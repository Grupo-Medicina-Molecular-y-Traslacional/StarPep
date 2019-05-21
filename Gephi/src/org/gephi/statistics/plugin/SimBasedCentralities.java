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
import java.util.List;
import java.util.Map;
import java.util.Stack;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.*;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class SimBasedCentralities extends AbstractCentrality {

    public static final String CLOSENESS = "closnesscentrality";
    public static final String HARMONIC_CLOSENESS = "harmonicclosnesscentrality";
    public static final String ECCENTRICITY = "eccentricity";

    private final List<AlgorithmProperty> properties;

    private double[] closeness;
    private double[] harmonic;
    private double[] eccentricity;

    private GraphVizSetting graphViz;

    private int N;
    private final boolean isDirected;
    private boolean normalizedComp;


    /**
     * Construct a similarity-based centralities calculator for the current graph model
     */
    public SimBasedCentralities(SimBasedCentralitiesFactory factory) {
        super(factory);
        isDirected = false;
        properties = new LinkedList<>();
        populateProperties();
        normalizedComp = true;
    }

    private void populateProperties() {
        final String CATEGORY = "Properties";
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.normalize.name"), CATEGORY, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.normalize.desc"), "isNormalizedComp", "setNormalizedComp"));
//            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.harmonic.name"), CATEGORY, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.harmonic.desc"), "isBetweennessComp", "setBetweennessComp"));
//            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.closeness.name"), CATEGORY, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.closeness.desc"), "isClosenessComp", "setClosenessComp"));
//            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.eccentricity.name"), CATEGORY, NbBundle.getMessage(SimBasedCentralities.class, "SimBasedCentralities.eccentricity.desc"), "isEccentricityComp", "setEccentricityComp"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    protected Map<String, double[]> calculateDistanceMetrics(Graph graph, HashMap<Node, Integer> indicies, boolean directed, boolean normalized) {
        int n = graph.getNodeCount();

        HashMap<String, double[]> metrics = new HashMap<>();

        double[] nodeEccentricity = new double[n];
        double[] nodeCloseness = new double[n];
        double[] nodeHarmonicCloseness = new double[n];

        metrics.put(ECCENTRICITY, nodeEccentricity);
        metrics.put(CLOSENESS, nodeCloseness);
        metrics.put(HARMONIC_CLOSENESS, nodeHarmonicCloseness);

        progress.switchToDeterminate(graph.getNodeCount());

        int totalPaths = 0;
        NodeIterable nodesIterable = graph.getNodes();
        for (Node s : nodesIterable) {
            Stack<Node> S = new Stack<>();

            LinkedList<Node>[] P = new LinkedList[n];
            double[] theta = new double[n];
            int[] d = new int[n];

            int s_index = indicies.get(s);

            //setInitParametetrsForNode(s, P, theta, d, s_index, n)
            for (int j = 0; j < n; j++) {
                P[j] = new LinkedList<>();
                theta[j] = 0;
                d[j] = -1;
            }
            theta[s_index] = 1;
            d[s_index] = 0;

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

            if (isCanceled) {
                nodesIterable.doBreak();
                return metrics;
            }
            progress.progress();
        }

        

        return metrics;
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
            nodeTable.addColumn(HARMONIC_CLOSENESS, "Harmonic Centrality", Double.class, new Double(0));
        }
    }

    private void saveCalculatedValues(Graph graph, HashMap<Node, Integer> indicies,
            double[] nodeEccentricity, double[] nodeCloseness, double[] nodeHarmonicCloseness) {
        for (Node s : graph.getNodes()) {
            int s_index = indicies.get(s);

            s.setAttribute(ECCENTRICITY, nodeEccentricity[s_index]);
            s.setAttribute(CLOSENESS, nodeCloseness[s_index]);
            s.setAttribute(HARMONIC_CLOSENESS, nodeHarmonicCloseness[s_index]);
        }
    }

    public boolean isNormalizedComp() {
        return normalizedComp;
    }

    public void setNormalizedComp(Boolean normalizedComp) {
        this.normalizedComp = normalizedComp;
    }

    public boolean isDirected() {
        return isDirected;
    }

    /**
     *
     * @return
     */
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);

        N = graph.getNodeCount();

        eccentricity = new double[N];
        closeness = new double[N];
        harmonic = new double[N];

        graphViz = pc.getGraphVizSetting(workspace);
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

    @Override
    protected void calculateCentrality(int s_index, Stack<Node> stack, LinkedList<Node>[] list, int[] d, double[] theta) {
        double reachable = 0;
        for (int i = 0; i < N; i++) {
            if (d[i] > 0) {
                eccentricity[s_index] = (int) Math.max(eccentricity[s_index], d[i]);
                closeness[s_index] += d[i];
                harmonic[s_index] += Double.isInfinite(d[i]) ? 0.0 : 1.0 / d[i];
                reachable++;
            }
        }


        if (reachable != 0) {
            closeness[s_index] = (closeness[s_index] == 0) ? 0 : reachable / closeness[s_index];
            harmonic[s_index] = harmonic[s_index] / reachable;
        }
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

    @Override
    public void endAlgo() {
        super.endAlgo(); //To change body of generated methods, choose Tools | Templates.
        
        
    }
}
