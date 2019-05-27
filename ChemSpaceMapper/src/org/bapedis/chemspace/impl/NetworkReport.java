/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.io.File;
import java.io.IOException;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractClusterizer;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.OSUtil;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.entity.StandardEntityCollection;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Lookup;

/**
 *
 * @author Loge
 */
public class NetworkReport implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final NetworkReportFactory factory;
    private boolean stopRun = false;
    private Workspace workspace;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected Graph mainGraph;

    private JFreeChart densityChart, modularityChart;

    public NetworkReport(NetworkReportFactory factory) {
        this.factory = factory;
    }       

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            graphModel = pc.getGraphModel(workspace);
            mainGraph = graphModel.getGraph();
        }
        densityChart = null;
        modularityChart = null;
    }

    @Override
    public void endAlgo() {
        workspace = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
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
    public void run() {

        densityChart = createXYLineChart(createDensityDataSet());
        modularityChart = createXYLineChart(createModularityDataSet());
        
    }

    private JFreeChart createXYLineChart(XYSeriesCollection dataset) {

        JFreeChart chart = ChartFactory.createXYLineChart(
                "", // chart title
                "Similarity threshold", // domain axis label
                "Measure value", // range axis label
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

        for (float threshold = 0; threshold < 1.05 && !stopRun; threshold += 0.05) {
            serieDensity.add(threshold, calculateDensity(threshold));
        }

        dataset.addSeries(serieDensity);
        return dataset;
    }

    private XYSeriesCollection createModularityDataSet() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries serieModularity = new XYSeries("Modularity");

        for (float threshold = 0; threshold < 1.05 && !stopRun; threshold += 0.05) {
            serieModularity.add(threshold, calculateModularity(threshold));
        }

        dataset.addSeries(serieModularity);
        return dataset;
    }

    private float calculateDensity(float threshold) {
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
                    similarity = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                    if (similarity >= threshold) {
                        edgeCount++;
                    }
                }
            }
        }
        return 2 * edgeCount / (n * (n - 1));
    }

    private float calculateModularity(float threshold) {
        float edgeCount = 0;
        float sum = 0;
        Node node1, node2;
        int cluster1, cluster2;
        float degree1, degree2;
        Edge graphEdge;
        double similarity;
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Edge count
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length; j++) {
                node2 = peptides[j].getGraphNode();
                graphEdge = mainGraph.getEdge(node1, node2, relType);
                if (graphEdge != null) {
                    similarity = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                    if (similarity >= threshold) {
                        edgeCount++;
                    }
                }

            }
        }

        //Summatory
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length; j++) {
                node2 = peptides[j].getGraphNode();
                graphEdge = mainGraph.getEdge(node1, node2, relType);
                similarity = (graphEdge != null) ? (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY) : -1;
                cluster1 = (int) node1.getAttribute(AbstractClusterizer.CLUSTER_COLUMN);
                cluster2 = (int) node2.getAttribute(AbstractClusterizer.CLUSTER_COLUMN);
                if (cluster1 > -1 && cluster2 > -1 && cluster1 == cluster2) {
                    degree1 = calculateDegree(node1, relType, threshold);
                    degree2 = calculateDegree(node2, relType, threshold);
                    sum += ((similarity >= threshold ? 1 : 0) - (degree1 * degree2) / (2 * edgeCount));
                }

            }
        }

        return (sum / (2 * edgeCount));
    }

    private float calculateDegree(Node graphNode, int relType, float threshold) {
        float degree = 0;
        EdgeIterable relationships = mainGraph.getEdges(graphNode, relType);
        double similarity;
        for (Edge graphEdge : relationships) {
            similarity = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
            if (similarity >= threshold) {
                degree++;
            }
        }
        return degree;
    }

    public String getMeasureReport() {
        String report = "<b> Density </b>"
                + "<br />" + renderChart(densityChart, "Density")
                + "<br /><br />"
                + "<b> Modularity </b>"
                + "<br />" + renderChart(modularityChart, "Modularity");

        return report;
    }

    public String renderChart(JFreeChart chart, String name) {
        String imageFile = "";
        if (chart != null) {
            try {
                final ChartRenderingInfo info = new ChartRenderingInfo(new StandardEntityCollection());
                File file = OSUtil.createTempFile(name, "-chart");;
                imageFile = "<IMG SRC=\"file:" + file.getAbsolutePath() + "\" " + "WIDTH=\"600\" HEIGHT=\"400\" BORDER=\"0\" USEMAP=\"#chart\"></IMG>";
                ChartUtilities.saveChartAsPNG(file, chart, 600, 400, info);
            } catch (IOException e) {
            }
        }
        return imageFile;
    }

}
