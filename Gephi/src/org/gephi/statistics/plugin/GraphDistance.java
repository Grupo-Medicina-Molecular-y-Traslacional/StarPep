/*
 Copyright 2008-2011 Gephi
 Authors : Patick J. McSweeney <pjmcswee@syr.edu>, Sebastien Heymann <seb@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.statistics.plugin;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Map;
import java.util.Stack;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.*;
import org.openide.util.Lookup;

/**
 * Ref: Ulrik Brandes, A Faster Algorithm for Betweenness Centrality, in Journal
 * of Mathematical Sociology 25(2):163-177, (2001)
 *
 * @author pjmcswee
 * @author Jonny Wray
 */
public class GraphDistance implements Algorithm {

    public static final String BETWEENNESS = "betweenesscentrality";
    public static final String CLOSENESS = "closnesscentrality";
    public static final String HARMONIC_CLOSENESS = "harmonicclosnesscentrality";
    public static final String ECCENTRICITY = "eccentricity";

    protected GraphModel graphModel;
    protected final GraphDistanceBuilder factory;
    /**
     *
     */
    private double[] betweenness;
    /**
     *
     */
    private double[] closeness;
    private double[] harmonicCloseness;
    /**
     *
     */
    private double[] eccentricity;
    /**
     *
     */
    private int diameter;
    private int radius;
    /**
     *
     */
    private double avgDist;
    /**
     *
     */
    private int N;
    /**
     *
     */
    private final boolean isDirected;
    /**
     *
     */
    private ProgressTicket progress;
    /**
     *
     */
    private boolean isCanceled;
    private boolean isNormalized;

    /**
     * Gets the average shortest path length in the network
     *
     * @return average shortest path length for all nodes
     */
    public double getPathLength() {
        return avgDist;
    }

    /**
     * @return the diameter of the network
     */
    public double getDiameter() {
        return diameter;
    }

    /**
     * @return the radius of the network
     */
    public double getRadius() {
        return radius;
    }

    /**
     * Construct a GraphDistance calculator for the current graph model
     */
    public GraphDistance(GraphDistanceBuilder factory) {
        this.factory = factory;
        isDirected = false;
    }

    protected Map<String, double[]> calculateDistanceMetrics(Graph graph, HashMap<Node, Integer> indicies, boolean directed, boolean normalized) {
        int n = graph.getNodeCount();

        HashMap<String, double[]> metrics = new HashMap<>();

        double[] nodeEccentricity = new double[n];
        double[] nodeBetweenness = new double[n];
        double[] nodeCloseness = new double[n];
        double[] nodeHarmonicCloseness = new double[n];

        metrics.put(ECCENTRICITY, nodeEccentricity);
        metrics.put(CLOSENESS, nodeCloseness);
        metrics.put(HARMONIC_CLOSENESS, nodeHarmonicCloseness);
        metrics.put(BETWEENNESS, nodeBetweenness);

//        Progress.start(progress, graph.getNodeCount());
        int count = 0;

        int totalPaths = 0;
        for (Node s : graph.getNodes()) {
            Stack<Node> S = new Stack<>();

            LinkedList<Node>[] P = new LinkedList[n];
            double[] theta = new double[n];
            int[] d = new int[n];

            int s_index = indicies.get(s);

            setInitParametetrsForNode(s, P, theta, d, s_index, n);

            LinkedList<Node> Q = new LinkedList<>();
            Q.addLast(s);
            while (!Q.isEmpty()) {
                Node v = Q.removeFirst();
                S.push(v);
                int v_index = indicies.get(v);

                EdgeIterable edgeIter = getEdgeIter(graph, v, directed);

                for (Edge edge : edgeIter) {
                    Node reachable = graph.getOpposite(v, edge);

                    int r_index = indicies.get(reachable);
                    if (d[r_index] < 0) {
                        Q.addLast(reachable);
                        d[r_index] = d[v_index] + 1;
                    }
                    if (d[r_index] == (d[v_index] + 1)) {
                        theta[r_index] = theta[r_index] + theta[v_index];
                        P[r_index].addLast(v);
                    }
                }
            }
            double reachable = 0;
            for (int i = 0; i < n; i++) {
                if (d[i] > 0) {
                    avgDist += d[i];
                    nodeEccentricity[s_index] = (int) Math.max(nodeEccentricity[s_index], d[i]);
                    nodeCloseness[s_index] += d[i];
                    nodeHarmonicCloseness[s_index] += Double.isInfinite(d[i]) ? 0.0 : 1.0 / d[i];
                    diameter = Math.max(diameter, d[i]);
                    reachable++;
                }
            }

            radius = (int) Math.min(nodeEccentricity[s_index], radius);

            if (reachable != 0) {
                nodeCloseness[s_index] = (nodeCloseness[s_index] == 0) ? 0 : reachable / nodeCloseness[s_index];
                nodeHarmonicCloseness[s_index] = nodeHarmonicCloseness[s_index] / reachable;
            }

            totalPaths += reachable;

            double[] delta = new double[n];
            while (!S.empty()) {
                Node w = S.pop();
                int w_index = indicies.get(w);
                ListIterator<Node> iter1 = P[w_index].listIterator();
                while (iter1.hasNext()) {
                    Node u = iter1.next();
                    int u_index = indicies.get(u);
                    delta[u_index] += (theta[u_index] / theta[w_index]) * (1 + delta[w_index]);
                }
                if (w != s) {
                    nodeBetweenness[w_index] += delta[w_index];
                }
            }
            count++;
            if (isCanceled) {
                return metrics;
            }
//            Progress.progress(progress, count);
        }

        avgDist /= totalPaths;//mN * (mN - 1.0f);

        calculateCorrection(graph, indicies, nodeBetweenness, directed, normalized);

        return metrics;
    }

    private void setInitParametetrsForNode(Node s, LinkedList<Node>[] P, double[] theta, int[] d, int index, int n) {
        for (int j = 0; j < n; j++) {
            P[j] = new LinkedList<>();
            theta[j] = 0;
            d[j] = -1;
        }
        theta[index] = 1;
        d[index] = 0;
    }

    private EdgeIterable getEdgeIter(Graph graph, Node v, boolean directed) {
        EdgeIterable edgeIter;
        if (directed) {
            edgeIter = ((DirectedGraph) graph).getOutEdges(v);
        } else {
            edgeIter = graph.getEdges(v);
        }
        return edgeIter;
    }

    private void initializeAttributeColunms(GraphModel graphModel) {
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(ECCENTRICITY)) {
            nodeTable.addColumn(ECCENTRICITY, "Eccentricity", Double.class, new Double(0));
        }
        if (!nodeTable.hasColumn(CLOSENESS)) {
            nodeTable.addColumn(CLOSENESS, "Closeness Centrality", Double.class, new Double(0));
        }
        if (!nodeTable.hasColumn(HARMONIC_CLOSENESS)) {
            nodeTable.addColumn(HARMONIC_CLOSENESS, "Harmonic Closeness Centrality", Double.class, new Double(0));
        }
        if (!nodeTable.hasColumn(BETWEENNESS)) {
            nodeTable.addColumn(BETWEENNESS, "Betweenness Centrality", Double.class, new Double(0));
        }
    }

    public HashMap<Node, Integer> createIndiciesMap(Graph graph) {
        HashMap<Node, Integer> indicies = new HashMap<>();
        int index = 0;
        for (Node s : graph.getNodes()) {
            indicies.put(s, index);
            index++;
        }
        return indicies;
    }

    private void initializeStartValues() {
        betweenness = new double[N];
        eccentricity = new double[N];
        closeness = new double[N];
        harmonicCloseness = new double[N];
        diameter = 0;
        avgDist = 0;
        radius = Integer.MAX_VALUE;
    }

    private void calculateCorrection(Graph graph, HashMap<Node, Integer> indicies,
            double[] nodeBetweenness, boolean directed, boolean normalized) {

        int n = graph.getNodeCount();

        for (Node s : graph.getNodes()) {

            int s_index = indicies.get(s);

            if (!directed) {
                nodeBetweenness[s_index] /= 2;
            }
            if (normalized) {
                nodeBetweenness[s_index] /= directed ? (n - 1) * (n - 2) : (n - 1) * (n - 2) / 2;
            }
        }
    }

    private void saveCalculatedValues(Graph graph, HashMap<Node, Integer> indicies,
            double[] nodeEccentricity, double[] nodeBetweenness, double[] nodeCloseness, double[] nodeHarmonicCloseness) {
        for (Node s : graph.getNodes()) {
            int s_index = indicies.get(s);

            s.setAttribute(ECCENTRICITY, nodeEccentricity[s_index]);
            s.setAttribute(CLOSENESS, nodeCloseness[s_index]);
            s.setAttribute(HARMONIC_CLOSENESS, nodeHarmonicCloseness[s_index]);
            s.setAttribute(BETWEENNESS, nodeBetweenness[s_index]);
        }
    }

    public void setNormalized(boolean isNormalized) {
        this.isNormalized = isNormalized;
    }

    public boolean isNormalized() {
        return isNormalized;
    }

    public boolean isDirected() {
        return isDirected;
    }

//    private String createImageFile(TempDir tempDir, double[] pVals, String pName, String pX, String pY) {
//        //distribution of values
//        Map<Double, Integer> dist = new HashMap<>();
//        for (int i = 0; i < N; i++) {
//            Double d = pVals[i];
//            if (dist.containsKey(d)) {
//                Integer v = dist.get(d);
//                dist.put(d, v + 1);
//            } else {
//                dist.put(d, 1);
//            }
//        }
//
//        //Distribution series
//        XYSeries dSeries = ChartUtils.createXYSeries(dist, pName);
//
//        XYSeriesCollection dataset = new XYSeriesCollection();
//        dataset.addSeries(dSeries);
//
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                pName,
//                pX,
//                pY,
//                dataset,
//                PlotOrientation.VERTICAL,
//                true,
//                false,
//                false);
//        chart.removeLegend();
//        ChartUtils.decorateChart(chart);
//        ChartUtils.scaleChart(chart, dSeries, isNormalized);
//        return ChartUtils.renderChart(chart, pName + ".png");
//    }
    /**
     *
     * @return
     */
    @Override
    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    /**
     *
     * @param progressTicket
     */
    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    @Override
    public void initAlgo() {
        graphModel = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel();
        initializeAttributeColunms(graphModel);
    }

    @Override
    public void endAlgo() {
        graphModel = null;
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
        isCanceled = false;
        Graph graph = graphModel.getGraphVisible();
        
        graph.readLock();
        try {
            N = graph.getNodeCount();

            initializeStartValues();

            HashMap<Node, Integer> indicies = createIndiciesMap(graph);

            Map<String, double[]> metrics = calculateDistanceMetrics(graph, indicies, isDirected, isNormalized);

            eccentricity = metrics.get(ECCENTRICITY);
            closeness = metrics.get(CLOSENESS);
            harmonicCloseness = metrics.get(HARMONIC_CLOSENESS);
            betweenness = metrics.get(BETWEENNESS);

            saveCalculatedValues(graph, indicies, eccentricity, betweenness, closeness, harmonicCloseness);
        } finally {
            graph.readUnlock();
        }
    }
}