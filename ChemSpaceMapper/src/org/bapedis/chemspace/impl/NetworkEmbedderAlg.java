/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.IntStream;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class NetworkEmbedderAlg implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected Graph graph, mainGraph;
    protected int relType;
    protected ProgressTicket ticket;
    private final AtomicBoolean stopRun;
    private AbstractDistance distFunc;
    private double maxDistance;
    private double currentThreshold;

    public NetworkEmbedderAlg(AlgorithmFactory factory) {
        this.factory = factory;
        currentThreshold = 0.7;
        stopRun = new AtomicBoolean(false);
    }

    public AbstractDistance getDistanceFunction() {
        return distFunc;
    }

    public void setDistanceFunction(AbstractDistance distFunc) {
        this.distFunc = distFunc;
    }

    public double getSimilarityThreshold() {
        return currentThreshold;
    }

    public void setSimilarityThreshold(double threshold) {
        this.currentThreshold = threshold;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.ticket = progressTicket;
        stopRun.set(false);
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            graphModel = pc.getGraphModel(workspace);
            mainGraph = graphModel.getGraph();
            graph = graphModel.getGraphVisible();
            relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

            //Load features
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }
            distFunc.setFeatures(allFeatures);
        }
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        graphModel = null;
        graph = null;
    }

    @Override
    public boolean cancel() {
        stopRun.set(true);
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void run() {
        // Remove all edges..
        mainGraph.writeLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                mainGraph.clearEdges(node, relType);
            }
        } finally {
            mainGraph.writeUnlock();
            pc.getGraphVizSetting().fireChangedGraphView();
        }

        // task size
        ticket.switchToDeterminate(2 * peptides.length);

        // Create new edges...
        if (peptides != null && !stopRun.get()) {
            try {
                Arrays.stream(peptides).parallel().forEach(peptide -> {
                    if (!stopRun.get()) {
                        try {
                            computeHSPNeighbors(peptide);
                            ticket.progress();
                        } catch (MolecularDescriptorNotFoundException ex) {
                            DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                            Exceptions.printStackTrace(ex);
                            cancel();
                        }
                    }
                });
            } finally {
                pc.getGraphVizSetting().fireChangedGraphView();
            }
        }

        // Report max distance
        if (!stopRun.get()) {
            pc.reportMsg("Max distance: " + maxDistance, workspace);
        }

        //Compute similarity values
        IntStream.range(0, peptides.length).parallel().forEach(index -> {
            if (!stopRun.get()) {
                computeSimilarityValues(index);
                ticket.progress();
            }
        });

    }

    private void computeSimilarityValues(int index) {
        double distance, similarity;
        Node node1, node2;
        Edge graphEdge;
        node1 = peptides[index].getGraphNode();

        for (int j = index + 1; j < peptides.length && !stopRun.get(); j++) {
            node2 = peptides[j].getGraphNode();
            graphEdge = mainGraph.getEdge(node1, node2, relType);
            if (graphEdge != null) {
                distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                similarity = 1.0 - distance / maxDistance;

                graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, similarity);

                // Add the edge to the graph view
                if (similarity >= currentThreshold) {
                    graph.writeLock();
                    try {
                        graph.addEdge(graphEdge);
                    } finally {
                        graph.writeUnlock();
                    }
                }
            }
        }
    }

    private void computeHSPNeighbors(Peptide peptide) throws MolecularDescriptorNotFoundException {
        Node node1, node2, node3;
        Edge graphEdge;
        Peptide closestPeptide;
        double distance, maxValue;
        int cursor;

        if (!stopRun.get()) {
            CandidatePeptide[] candidates = new CandidatePeptide[peptides.length - 1];
            node1 = peptide.getGraphNode();
            cursor = 0;
            for (int j = 0; j < peptides.length; j++) {
                if (peptide != peptides[j]) {
                    node2 = peptides[j].getGraphNode();
                    graphEdge = mainGraph.getEdge(node1, node2, relType);
                    if (graphEdge != null) {
                        distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                    } else {
                        distance = distFunc.distance(peptide, peptides[j]);
                    }
                    candidates[cursor++] = new CandidatePeptide(peptides[j], distance);
                }
            }

            Arrays.parallelSort(candidates);
            maxValue = candidates[candidates.length - 1].getDistance();
            cursor = 0;
            while (cursor < candidates.length) {
                if (candidates[cursor] != null) {
                    //Create an edge to the closest peptide
                    closestPeptide = candidates[cursor].getPeptide();
                    node2 = closestPeptide.getGraphNode();
                    distance = candidates[cursor].getDistance();
                    mainGraph.writeLock();
                    try {
                        graphEdge = mainGraph.getEdge(node1, node2, relType);
                        if (graphEdge == null) {
                            graphEdge = createEdge(node1, node2);
                            mainGraph.addEdge(graphEdge);
                            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE, distance);
                        }
                    } finally {
                        mainGraph.writeUnlock();
                    }

                    // ignore elements in the forbidden area
                    for (int k = cursor + 1; k < candidates.length; k++) {
                        if (candidates[k] != null) {
                            node3 = candidates[k].getPeptide().getGraphNode();
                            graphEdge = mainGraph.getEdge(node2, node3, relType);
                            if (graphEdge != null) {
                                distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                            } else {
                                distance = distFunc.distance(closestPeptide, candidates[k].getPeptide());
                            }
                            if (distance < candidates[k].getDistance()) {
                                candidates[k] = null;
                            }
                        }
                    }
                }
                cursor++;
            }
            //Update max distance
            synchronized (this) {
                if (maxValue > maxDistance) {
                    maxDistance = maxValue;
                }
            }
        }
    }

    private Edge createEdge(Node node1, Node node2) {
        // Create an edge between two nodes
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        return graphEdge;
    }

}

class CandidatePeptide implements Comparable<CandidatePeptide> {

    private final double distance;
    private final Peptide peptide;

    public CandidatePeptide(Peptide peptide, double distance) {
        this.peptide = peptide;
        this.distance = distance;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(CandidatePeptide o) {
        if (distance < o.distance) {
            return -1;
        }
        if (distance > o.distance) {
            return 1;
        }
        return 0;
    }
}
