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

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.*;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

public class HarmonicCentrality extends AbstractCentrality {

    public static final String HARMONIC_CLOSENESS = "harmonicclosnesscentrality";
    public static final String HARMONIC_CLOSENESS_TITLE = "Harmonic Centrality (HC)";

    public static final String RANKING_BY_HARMONIC_CLOSENESS = "rankingByharmonicclosnesscentrality";
    public static final String RANKING_BY_HARMONIC_CLOSENESS_TITLE = "Ranking by HC";

    private final List<AlgorithmProperty> properties;

    private double[] harmonic;

    private GraphVizSetting graphViz;

    private int N;
    private final boolean isDirected;
    private boolean normalizedComp;

    /**
     * Construct a similarity-based centralities calculator for the current
     * graph model
     */
    public HarmonicCentrality(HarmonicCentralityFactory factory) {
        super(factory);
        isDirected = false;
        properties = new LinkedList<>();
        populateProperties();
        normalizedComp = true;
    }

    private void populateProperties() {
        final String CATEGORY = "Properties";
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(HarmonicCentrality.class, "Property.normalize.name"), CATEGORY, NbBundle.getMessage(HarmonicCentrality.class, "Property.normalize.desc"), "isNormalizedComp", "setNormalizedComp"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
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

        harmonic = new double[N];

        graphViz = pc.getGraphVizSetting(workspace);
    }

    @Override
    public AlgorithmProperty[] getProperties() {
//        return properties.toArray(new AlgorithmProperty[0]);
        return null;
    }

    @Override
    protected void calculateCentrality() {
        progress.switchToDeterminate(graph.getNodeCount());
        Arrays.stream(nodes).parallel().forEach(node -> {
            if (!isCanceled.get()) {
                calculateCentrality(node);
                progress.progress();
            }
        });
    }

    private void calculateCentrality(Node sourceNode) {
        double[] d = new double[graph.getNodeCount()];

        Set<Node> unvisitedNodes = new HashSet<>();
        Set<Node> visitedNodes = new HashSet<>();

        for (int i = 0; i < d.length; i++) {
            d[i] = Double.POSITIVE_INFINITY;
        }

        int s_index = (Integer) sourceNode.getAttribute(NODE_INDEX);
        d[s_index] = 0;

        unvisitedNodes.add(sourceNode);

        Node minDistanceNode;
        double minDistance;
        double dist;
        while (!unvisitedNodes.isEmpty() && !isCanceled.get()) {

            // find node with smallest distance value
            minDistance = Double.POSITIVE_INFINITY;
            minDistanceNode = null;
            for (Node node : unvisitedNodes) {
                int index = (Integer) node.getAttribute(NODE_INDEX);

                if (d[index] < minDistance) {
                    minDistance = d[index];
                    minDistanceNode = node;
                }
            }

            unvisitedNodes.remove(minDistanceNode);
            visitedNodes.add(minDistanceNode);

            int v_index = (Integer) minDistanceNode.getAttribute(NODE_INDEX);

            EdgeIterable edgeIter = getEdgeIter(graph, minDistanceNode, directed);

            for (Edge edge : edgeIter) {
                if (isCanceled.get()) {
                    edgeIter.doBreak();
                    return;
                }
                Node reachable = graph.getOpposite(minDistanceNode, edge);

                if (!visitedNodes.contains(reachable)) {
                    int r_index = (Integer) reachable.getAttribute(NODE_INDEX);

                    dist = d[v_index] + (1 - edge.getWeight());

                    if (dist < d[r_index]) {
                        d[r_index] = dist;
                        unvisitedNodes.add(reachable);
                    }
                }
            }
        }

        for (int i = 0; i < N; i++) {
            if (d[i] > 0) {
                harmonic[s_index] += Double.isInfinite(d[i]) ? 0.0 : 1.0 / d[i];
            }
        }
    }

    @Override
    public void endAlgo() {

        //Add betweenness centrality column
        boolean fireEvent = false;
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(HARMONIC_CLOSENESS)) {
            nodeTable.addColumn(HARMONIC_CLOSENESS, HARMONIC_CLOSENESS_TITLE, Double.class, new Double(0));
            nodeTable.addColumn(RANKING_BY_HARMONIC_CLOSENESS, RANKING_BY_HARMONIC_CLOSENESS_TITLE, Integer.class, -1);
            fireEvent = true;
        }

        //Set default values
        Double defaultValue = new Double(0);
        Integer defaultRank = new Integer(-1);
        for (Node node : graphModel.getGraph().getNodes()) {
            node.setAttribute(HARMONIC_CLOSENESS, defaultValue);
            node.setAttribute(RANKING_BY_HARMONIC_CLOSENESS, defaultRank);
        }

        //Save values
        if (!isCanceled.get()) {
            for (Node s : nodes) {
                int s_index = (Integer) s.getAttribute(NODE_INDEX);
                s.setAttribute(HARMONIC_CLOSENESS, harmonic[s_index]);
            }
            
            Arrays.parallelSort(nodes, new RankComparator(HARMONIC_CLOSENESS));
            for(int i=0; i<nodes.length; i++){
                nodes[i].setAttribute(RANKING_BY_HARMONIC_CLOSENESS, i+1);
            }            
        }

        super.endAlgo();
        harmonic = null;

        if (fireEvent) {
            graphViz.fireChangedGraphView();
        }
        graphViz = null;
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
    class NodeComparator implements Comparator<Node> {

        private final double[] d;

        public NodeComparator(double[] d) {
            this.d = d;
        }

        @Override
        public int compare(Node node1, Node node2) {
            int index1 = (Integer) node1.getAttribute(NODE_INDEX);
            int index2 = (Integer) node2.getAttribute(NODE_INDEX);

            return d[index1] == d[index2] ? 0 : (d[index1] < d[index2] ? -1 : 1);
        }

    }
}
