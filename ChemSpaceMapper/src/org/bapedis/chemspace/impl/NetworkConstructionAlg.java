/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.text.DecimalFormat;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.vecmath.Vector3f;
import org.bapedis.chemspace.model.CoordinateSpace;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
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
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class NetworkConstructionAlg implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    static final DecimalFormat DF = new DecimalFormat("0.0##");

    public static final int FULL_MAX_NODES = 1000;

    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected Graph graph, mainGraph;
    protected int relType;
    protected ProgressTicket ticket;
    protected final AtomicBoolean stopRun;
    protected CoordinateSpace xyzSpace;
    protected AlgorithmFactory distFactory;
    protected double currentThreshold;
    protected ChartPanel densityChart;
    protected final double[] densityValues;
    protected double[][] descriptorMatrix;
    private boolean twoDMap;
    private double maxDistance;

    public NetworkConstructionAlg(AlgorithmFactory factory) {
        this.factory = factory;
        currentThreshold = pc.getGraphVizSetting().getSimilarityThreshold();
        stopRun = new AtomicBoolean(false);
        densityValues = new double[101];
        twoDMap = true;
    }

    public boolean is2DMap() {
        return twoDMap;
    }

    public void set2DMap(boolean twoDMap) {
        this.twoDMap = twoDMap;
    }    

    public double[][] getDescriptorMatrix() {
        return descriptorMatrix;
    }

    public void setDescriptorMatrix(double[][] descriptorMatrix) {
        this.descriptorMatrix = descriptorMatrix;
    }
    
    public double[] getDensityValues() {
        return densityValues;
    }

    public AlgorithmFactory getDistanceFactory() {
        return distFactory;
    }

    public void setDistanceFactory(AlgorithmFactory distanceFactory) {
        this.distFactory = distanceFactory;
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

    public double getMaxDistance() {
        return maxDistance;
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
        }
        for (int i = 0; i < densityValues.length; i++) {
            densityValues[i] = 0;
        }
        densityChart = null;
        maxDistance = 0;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        graphModel = null;
        graph = null;
        descriptorMatrix = null;
        pc.getGraphVizSetting().setSimilarityThreshold(currentThreshold);
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
        if (peptides != null && peptides.length > 0) {
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

            maxDistance = createNetwork();

            // Report max distance
            if (!stopRun.get()) {
                pc.reportMsg("Max distance: " + String.format("%.2f", maxDistance), workspace);
            }

            //Compute similarity values
            computeSimilarityRelationships();

            //Populate density array
            populateDensityArray();

            StringBuilder xAxis = new StringBuilder("similarity_threshold = [");
            StringBuilder yAxis = new StringBuilder("network_density = [");
            for (int t = 0; t <= 100; t++) {
                xAxis.append(t / 100.0);
                yAxis.append(densityValues[t]);

                if (t < 100) {
                    xAxis.append(",");
                    yAxis.append(",");
                }
            }
            xAxis.append("];");
            yAxis.append("];");

            pc.reportMsg(xAxis.toString(), workspace);
            pc.reportMsg(yAxis.toString(), workspace);

            // Update similarity edges
            pc.reportMsg("Threshold: " + String.format("%.2f", currentThreshold), workspace);
            updateSimilarityEdges();

            if (twoDMap) {
                //Update node positions
                updateNodePositions();
            }

            densityChart = new ChartPanel(createXYLineChart("", createDensityDataSet()));
            ticket.progress();
        }
    }

    protected abstract double createNetwork();

    protected void updateNodePositions() {
        Vector3f[] positions = xyzSpace.getPositions();
        graph.readLock();
        try {
            Node node;
            Vector3f p;
            for (int i = 0; i < positions.length && !stopRun.get(); i++) {
                p = positions[i];
                node = peptides[i].getGraphNode();
                node.setX((float) ((0.01 + p.x) * 1000) - 500);
                node.setY((float) ((0.01 + p.y) * 1000) - 500);
                node.setZ(0); // 2D  
                ticket.progress();
            }
        } finally {
            graph.readUnlock();
        }
    }

    protected void updateSimilarityEdges() {
        double similarity;
        Node node1, node2;
        Edge graphEdge;
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                node2 = peptides[j].getGraphNode();
                graphEdge = mainGraph.getEdge(node1, node2, relType);
                if (graphEdge != null) {
                    similarity = graphEdge.getWeight();
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
    }

    protected void computeSimilarityRelationships() {
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
                    graphEdge.setWeight(Math.round(similarity * 100.0) / 100.0);
                }
            }
            ticket.progress();
        }
    }

    protected Edge createEdge(Node node1, Node node2) {
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

    protected JFreeChart createXYLineChart(String chartTitle, XYSeriesCollection dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                chartTitle, // chart title
                "Similarity threshold", // domain axis label
                "Network density", // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
                false, // include legend
                false, // tooltips?
                false // URLs?
        );

        return chart;
    }

    protected XYSeriesCollection createDensityDataSet() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries serieDensity = new XYSeries("Density");

        for (int t = 0; t <= 100 && !stopRun.get(); t++) {
            serieDensity.add(t / 100.0, densityValues[t]);
        }

        dataset.addSeries(serieDensity);
        return dataset;
    }

    protected void populateDensityArray() {
        long[] edgeCount = new long[101];
        int n = peptides.length;
        Node node1, node2;
        Edge graphEdge;
        int threshold;
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length; j++) {
                node2 = peptides[j].getGraphNode();
                graphEdge = mainGraph.getEdge(node1, node2, relType);
                if (graphEdge != null) {
                    threshold = (int) Math.round(graphEdge.getWeight() * 100);
                    for (int t = 0; t <= threshold; t++) {
                        edgeCount[t]++;
                    }
                }
            }
        }
        for (int t = 0; t < densityValues.length; t++) {
            densityValues[t] = 2.0 * edgeCount[t] / (n * (n - 1));
        }
    }
}
