/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.similarity.AbstractSimCoefficient;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.ui.components.JQuickHistogram;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class NetworkEmbedderAlg implements Algorithm, Cloneable {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected Peptide[] peptides;
    protected MolecularDescriptor[] features;

    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun;
//    private SimilarityMatrix similarityMatrix;
    private AtomicBoolean atomicRun;
    private AbstractSimCoefficient simCoefficient;
    private float currentThreshold;
    private final NetworkType networkType;
    private JQuickHistogram histogram;

    public NetworkEmbedderAlg(AlgorithmFactory factory) {
        this.factory = factory;
        networkType = NetworkType.Neighborhood;
        currentThreshold = 0.7f;
    }

    public AbstractSimCoefficient getSimCoefficient() {
        return simCoefficient;
    }

    public void setSimCoefficient(AbstractSimCoefficient simCoefficient) {
        this.simCoefficient = simCoefficient;
    }

    public float getSimilarityThreshold() {
        return currentThreshold;
    }

    public void setSimilarityThreshold(float threshold) {
        this.currentThreshold = threshold;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public JQuickHistogram getHistogram() {
        return histogram;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        stopRun = false;
        this.workspace = workspace;
        this.ticket = progressTicket;
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            graphModel = pc.getGraphModel(workspace);
            graph = graphModel.getGraphVisible();
            //Load features
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }
            features = allFeatures.toArray(new MolecularDescriptor[0]);
        }
        atomicRun = new AtomicBoolean(stopRun);
        simCoefficient.initAlgo(workspace, progressTicket);
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        features = null;
        graphModel = null;
        graph = null;
        atomicRun = null;
//        if (stopRun) { // Cancelled
//            similarityMatrix = null;
//        }
        simCoefficient.endAlgo();
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        atomicRun.set(stopRun);
        return stopRun;
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
        clearSimilarityEdges(graphModel);
        pc.getGraphVizSetting().fireChangedGraphView();
        if (peptides != null) {
            switch (networkType) {
                case Full:
                    // Compute new similarity matrix  
                    SimilarityMatrixBuilder task = new SimilarityMatrixBuilder(peptides);
                    task.setContext(simCoefficient, ticket, atomicRun);
                    int workunits = task.getWorkUnits();
                    ticket.switchToDeterminate(workunits);

                    fjPool.invoke(task);
                    task.join();
                    SimilarityMatrix simMatrix = task.getSimilarityMatrix();
                    histogram = simMatrix.getHistogram();
                    createFullNetwork(graphModel, simMatrix, ticket, atomicRun);
                    break;
                case Neighborhood:
                    histogram = new JQuickHistogram();
                    ticket.switchToDeterminate(peptides.length);
                    createNeighborhoodNetwork(graphModel, ticket, atomicRun);
                    break;
            }
            pc.getGraphVizSetting().fireChangedGraphView();
        }
    }

    protected void createFullNetwork(GraphModel graphModel, SimilarityMatrix simMatrix, ProgressTicket ticket, AtomicBoolean stopRun) {
        Edge graphEdge;
        Float score;
        ticket.switchToDeterminate(peptides.length - 1);
        for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                score = simMatrix.getValue(peptides[i], peptides[j]);
                if (score != null && score >= currentThreshold) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        graphEdge = createSimilarityEdge(graphModel, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
                        graph.writeLock();
                        try {
                            graph.addEdge(graphEdge);
                        } finally {
                            graph.writeUnlock();
                        }
                    }
                }
            }
            ticket.progress();
        }
    }

    protected void createNeighborhoodNetwork(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean stopRun) {
        Node node1, node2;
        Edge graphEdge;
        CandidatePeptide[] candidates = new CandidatePeptide[peptides.length - 1];
        Peptide closestPeptide;
        float similarity;
        int cursor;
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            cursor = 0;
            for (int j = 0; j < peptides.length; j++) {
                if (i != j) {
                    simCoefficient.setPeptidesToCompare(peptides[i], peptides[j]);
                    simCoefficient.run();
                    similarity = simCoefficient.getSimilarityValue();
                    candidates[cursor++] = new CandidatePeptide(peptides[j], similarity);
                }
            }

            Arrays.parallelSort(candidates);
            cursor = 0;
            while (cursor < candidates.length) {
                if (candidates[cursor] != null) {
                    //Create edge to the closest peptide
                    closestPeptide = candidates[cursor].getPeptide();
                    node2 = closestPeptide.getGraphNode();
                    if (graph.contains(node1) && graph.contains(node2)
                            && graph.getEdge(node1, node2) == null && graph.getEdge(node2, node1) == null) {
                        similarity = candidates[cursor].getSimilarity();
                        graphEdge = createSimilarityEdge(graphModel, node1, node2, similarity);
                        if (similarity >= currentThreshold) {
                            graph.writeLock();
                            try {
                                graph.addEdge(graphEdge);
                            } finally {
                                graph.writeUnlock();
                            }
                        }
                        histogram.addData(similarity);
                    }
                    // ignore elements in the forbidden area
                    for (int k = cursor + 1; k < candidates.length; k++) {
                        if (candidates[k] != null) {
                            simCoefficient.setPeptidesToCompare(closestPeptide, candidates[k].getPeptide());
                            simCoefficient.run();
                            similarity = simCoefficient.getSimilarityValue();
                            if (similarity > candidates[k].getSimilarity()) {
                                candidates[k] = null;
                            }
                        }
                    }
                }
                cursor++;
            }
            ticket.progress();
        }
    }

    protected void clearSimilarityEdges(GraphModel graphModel) {
        // Remove all similarity edges..
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                mainGraph.clearEdges(node, relType);
            }
        } finally {
            mainGraph.writeUnlock();
        }
    }

    protected Edge createSimilarityEdge(GraphModel graphModel, Node node1, Node node2, float similarity) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        // Create Edge
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            mainGraph.addEdge(graphEdge);
            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, similarity);
        } finally {
            mainGraph.writeUnlock();
        }

        return graphEdge;
    }
}

class CandidatePeptide implements Comparable<CandidatePeptide> {

    private final float similarity;
    private final Peptide peptide;

    public CandidatePeptide(Peptide peptide, float similarity) {
        this.peptide = peptide;
        this.similarity = similarity;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public float getSimilarity() {
        return similarity;
    }

    @Override
    public int compareTo(CandidatePeptide o) {
        if (similarity > o.similarity) {
            return -1;
        }
        if (similarity < o.similarity) {
            return 1;
        }
        return 0;
    }
}
