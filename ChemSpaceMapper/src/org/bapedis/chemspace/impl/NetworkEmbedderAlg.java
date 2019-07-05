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
import javax.vecmath.Vector3f;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.model.CoordinateSpace;
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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.DistanceMatrix;

/**
 *
 * @author loge
 */
public class NetworkEmbedderAlg implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    public static final int FULL_MAX_NODES = 1000;

    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected Graph graph, mainGraph;
    protected int relType;
    protected ProgressTicket ticket;
    private final AtomicBoolean stopRun;
    private CoordinateSpace xyzSpace;
    private AbstractDistance distFunc;
    private double maxDistance;
    private double currentThreshold;
    private ChartPanel densityChart;
    private NetworkType networkType;

    public NetworkEmbedderAlg(AlgorithmFactory factory) {
        this.factory = factory;
        currentThreshold = 0.0;
        stopRun = new AtomicBoolean(false);
        networkType = NetworkType.HSP;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public AbstractDistance getDistanceFunction() {
        return distFunc;
    }

    public void setDistanceFunction(AbstractDistance distFunc) {
        this.distFunc = distFunc;
    }

    public CoordinateSpace getXyzSpace() {
        return xyzSpace;
    }

    public void setXyzSpace(CoordinateSpace xyzSpace) {
        this.xyzSpace = xyzSpace;
    }

    public double getSimilarityThreshold() {
        return currentThreshold;
    }

    public void setSimilarityThreshold(double threshold) {
        this.currentThreshold = threshold;
    }

    public ChartPanel getDensityChart() {
        return densityChart;
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
        maxDistance = 0;
        densityChart = null;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        graphModel = null;
        graph = null;
        pc.getGraphVizSetting().fireChangedGraphView();
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
        }

        switch (networkType) {
            case FULL:
                createFullNetwork();
                break;
            case HSP:
                createHSPNetwork();
                break;
            default:
                throw new IllegalStateException("Unknown network type: " + networkType);
        }

        // Report max distance
        if (!stopRun.get()) {
            pc.reportMsg("Max distance: " + maxDistance, workspace);
        }

        //Compute similarity values
        computeSimilarityRelationships();

        //Update node positions
        updateNodePositions();

        densityChart = new ChartPanel(createXYLineChart("Network Density", createDensityDataSet()));
        ticket.progress();
    }

    private void createFullNetwork() {
        // Setup Similarity Matrix Builder
        DistanceMatrixBuilder task = new DistanceMatrixBuilder(peptides);
        task.setContext(distFunc, ticket, stopRun);
        int workunits = task.getWorkUnits();
        ticket.switchToDeterminate(workunits + peptides.length - 1);

        // Compute new distance matrix        
        fjPool.invoke(task);
        task.join();
        DistanceMatrix distanceMatrix = task.getDistanceMatrix();

        //Create full network
        Node node1, node2;
        Edge graphEdge;
        double distance;
        for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                distance = distanceMatrix.getValue(peptides[i], peptides[j]);
                node2 = peptides[j].getGraphNode();
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
                if (distance > maxDistance) {
                    maxDistance = distance;
                }                
            }
            ticket.progress();
        }
    }

    private void createHSPNetwork() {
        // task size
        ticket.switchToDeterminate(3 * peptides.length + 1);

        // Create new edges...
        if (peptides != null && !stopRun.get()) {
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
        }
    }

    private void updateNodePositions() {
        Vector3f[] positions = xyzSpace.getPositions();
        graph.readLock();
        try {
            Node node;
            Vector3f p;
            for (int i = 0; i < positions.length && !stopRun.get(); i++) {
                p = positions[i];
                node = peptides[i].getGraphNode();
                node.setX((float) ((0.01 + p.getX()) * 1000) - 500);
                node.setY((float) ((0.01 + p.getY()) * 1000) - 500);
                node.setZ(0); // 2D  
                ticket.progress();
            }
        } finally {
            graph.readUnlock();
        }
    }

    private void computeSimilarityRelationships() {
        double distance, similarity;
        Node node1, node2;
        Edge graphEdge;
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                node2 = peptides[j].getGraphNode();
                graphEdge = mainGraph.getEdge(node1, node2, relType);
                if (graphEdge != null) {
                    distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                    similarity = 1.0 - distance / maxDistance;
                    graphEdge.setWeight(similarity);

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
            ticket.progress();
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

    private JFreeChart createXYLineChart(String chartTitle, XYSeriesCollection dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle, // chart title
                "Similarity threshold", // domain axis label
                "Value", // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
                false, // include legend
                false, // tooltips?
                false // URLs?
        );

        return chart;
    }

    private XYSeriesCollection createDensityDataSet() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries serieDensity = new XYSeries("Density");

        for (double threshold = 0; threshold < 1.05 && !stopRun.get(); threshold += 0.05) {
            serieDensity.add(threshold, calculateDensity(threshold));
        }

        dataset.addSeries(serieDensity);
        return dataset;
    }

    private float calculateDensity(double threshold) {
        float n = peptides.length;
        float edgeCount = 0;
        Node node1, node2;
        Edge graphEdge;
        double similarity;
        int relType = graphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length; j++) {
                node2 = peptides[j].getGraphNode();
                graphEdge = mainGraph.getEdge(node1, node2, relType);
                if (graphEdge != null) {
                    similarity = graphEdge.getWeight();
                    if (similarity >= threshold) {
                        edgeCount++;
                    }
                }
            }
        }
        return 2 * edgeCount / (n * (n - 1));
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
