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

 Contributor(s): Thomas Aynaud <taynaud@gmail.com>

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.bapedis.clustering.impl;

import java.util.*;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Peptide;
import static org.bapedis.core.model.PeptideAttribute.CLUSTER_ATTR;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractClusterizer;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author pjmcswee
 */
// Vincent D. Blondel, Jean-Loup Guillaume, Renaud Lambiotte, Etienne Lefebvre  
// Fast unfolding of communities in large networks (2008)
public class Modularity implements Algorithm {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final ModularityFactory factory;
    protected GraphModel graphModel;
    protected Peptide[] peptides;
    protected Graph graph;
    protected Node[] nodes;
    protected int relType;
    protected boolean isCanceled;
    protected Workspace workspace;
    protected ProgressTicket progress;
    private CommunityStructure structure;
    private int[] comStructure;
    private double modularity;
    private double modularityResolution;
    private boolean isRandomized;
    private boolean useWeight;
    private double resolution;
    private GraphVizSetting graphViz;
    private AttributesModel attrModel;
    private final List<AlgorithmProperty> properties;

    public Modularity(ModularityFactory factory) {
        this.factory = factory;
        isRandomized = false;
        useWeight = true;
        resolution = 1.;
        properties = new LinkedList<>();
        populateProperties();
    }
    
    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(KMeans.class, "Modularity.useWeight.name"), AbstractClusterizer.PRO_CATEGORY, NbBundle.getMessage(KMeans.class, "Modularity.useWeight.desc"), "getUseWeight", "setUseWeight"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }      

    public Peptide[] getPeptides() {
        return peptides;
    }

    public void setRandom(boolean isRandomized) {
        this.isRandomized = isRandomized;
    }

    public boolean getRandom() {
        return isRandomized;
    }

    public void setUseWeight(Boolean useWeight) {
        this.useWeight = useWeight;
    }

    public boolean getUseWeight() {
        return useWeight;
    }

    public void setResolution(double resolution) {
        this.resolution = resolution;
    }

    public double getResolution() {
        return resolution;
    }

    @Override
    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progress = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        if (peptides == null) {
            if (attrModel != null) {
                peptides = attrModel.getPeptides().toArray(new Peptide[0]);
                attrModel.removeDisplayedColumn(CLUSTER_ATTR);

                nodes = new Node[peptides.length];
                int i = 0;
                for (Peptide peptide : peptides) {
                    nodes[i++] = peptide.getGraphNode();
                }
            }
        }        
        isCanceled = false;
        graphModel = pc.getGraphModel(workspace);
        graph = graphModel.getGraphVisible();
        graphViz = pc.getGraphVizSetting(workspace);
        relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
    }

    @Override
    public void endAlgo() {
        if (attrModel != null && !isCanceled) {
            attrModel.addDisplayedColumn(CLUSTER_ATTR);

            boolean fireEvent = false;
            Table nodeTable = graphModel.getNodeTable();
            if (!nodeTable.hasColumn(CLUSTER_ATTR.getId())) {
                nodeTable.addColumn(CLUSTER_ATTR.getId(), CLUSTER_ATTR.getDisplayName(), CLUSTER_ATTR.getType(), Origin.DATA, CLUSTER_ATTR.getDefaultValue(), true);
                fireEvent = true;
            }

            //Set default values           
            for (Peptide peptide : attrModel.getPeptideMap().values()) {
                peptide.setAttributeValue(CLUSTER_ATTR, CLUSTER_ATTR.getDefaultValue());
            }
            for (Node node : graphModel.getGraph().getNodes()) {
                node.setAttribute(CLUSTER_ATTR.getId(), CLUSTER_ATTR.getDefaultValue());
            }

            //Set cluster values   
            Node graphNode;
            int community;
            for (Peptide p : peptides) {
                graphNode = p.getGraphNode();
                int n_index = structure.map.get(graphNode);
                community = comStructure[n_index];
                p.setAttributeValue(CLUSTER_ATTR, community);                
                graphNode.setAttribute(CLUSTER_ATTR.getId(), community);
            }
            
            if (fireEvent) {
                graphViz.fireChangedGraphView();
            }            
        }
        attrModel = null;
        peptides = null;
        nodes = null;
        graph = null;
        graphModel = null;
        workspace = null;
        progress = null;
        structure = null;
        comStructure = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        if (nodes != null) {
            graph.readLock();
            try {
                structure = new CommunityStructure();
                comStructure = new int[nodes.length];

                if (nodes.length > 0) {//Fixes issue #713 Modularity Calculation Throws Exception On Empty Graph
                    HashMap<String, Double> computedModularityMetrics = computeModularity(structure, comStructure, resolution, isRandomized, useWeight);
                    modularity = computedModularityMetrics.get("modularity");
                    modularityResolution = computedModularityMetrics.get("modularityResolution");
                } else {
                    modularity = 0;
                    modularityResolution = 0;
                }
            } finally {
                graph.readUnlock();
            }
        }
    }

    protected HashMap<String, Double> computeModularity(CommunityStructure theStructure, int[] comStructure,
            double currentResolution, boolean randomized, boolean weighted) {
        isCanceled = false;
        Random rand = new Random();

        double totalWeight = theStructure.graphWeightSum;
        double[] nodeDegrees = theStructure.weights.clone();

        HashMap<String, Double> results = new HashMap<>();

        if (isCanceled) {
            return results;
        }
        boolean someChange = true;
        while (someChange) {
            someChange = false;
            boolean localChange = true;
            while (localChange) {
                localChange = false;
                int start = 0;
                if (randomized) {
                    start = Math.abs(rand.nextInt()) % theStructure.N;
                }
                int step = 0;
                for (int i = start; step < theStructure.N; i = (i + 1) % theStructure.N) {
                    step++;
                    Community bestCommunity = updateBestCommunity(theStructure, i, currentResolution);
                    if ((theStructure.nodeCommunities[i] != bestCommunity) && (bestCommunity != null)) {
                        theStructure.moveNodeTo(i, bestCommunity);
                        localChange = true;
                    }
                    if (isCanceled) {
                        return results;
                    }
                }
                someChange = localChange || someChange;
                if (isCanceled) {
                    return results;
                }
            }

            if (someChange) {
                theStructure.zoomOut();
            }
        }

        fillComStructure(theStructure, comStructure);
        double[] degreeCount = fillDegreeCount(theStructure, comStructure, nodeDegrees, weighted);

        double computedModularity = finalQ(comStructure, degreeCount, theStructure, totalWeight, 1., weighted);
        double computedModularityResolution = finalQ(comStructure, degreeCount, theStructure, totalWeight, currentResolution, weighted);

        results.put("modularity", computedModularity);
        results.put("modularityResolution", computedModularityResolution);

        return results;
    }

    private Community updateBestCommunity(CommunityStructure theStructure, int node_id, double currentResolution) {
        double best = 0.;
        Community bestCommunity = null;
        Set<Community> iter = theStructure.nodeConnectionsWeight[node_id].keySet();
        for (Community com : iter) {
            double qValue = q(node_id, com, theStructure, currentResolution);
            if (qValue > best) {
                best = qValue;
                bestCommunity = com;
            }
        }
        return bestCommunity;
    }

    private int[] fillComStructure(CommunityStructure theStructure, int[] comStructure) {
        int count = 0;

        for (Community com : theStructure.communities) {
            for (Integer node : com.nodes) {
                Community hidden = theStructure.invMap.get(node);
                for (Integer nodeInt : hidden.nodes) {
                    comStructure[nodeInt] = count;
                }
            }
            count++;
        }
        return comStructure;
    }

    private double[] fillDegreeCount(CommunityStructure theStructure, int[] comStructure, double[] nodeDegrees, boolean weighted) {
        double[] degreeCount = new double[theStructure.communities.size()];

        for (Node node : nodes) {
            int index = theStructure.map.get(node);
            degreeCount[comStructure[index]] += nodeDegrees[index];
        }
        return degreeCount;
    }

    private double finalQ(int[] struct, double[] degrees,
            CommunityStructure theStructure, double totalWeight, double usedResolution, boolean weighted) {

        double res = 0;
        double[] internal = new double[degrees.length];
        for (Node n : nodes) {
            int n_index = theStructure.map.get(n);
            for (Edge edge : graph.getEdges(n, relType)) {
                Node neighbor = graph.getOpposite(n, edge);
                if (n == neighbor) {
                    continue;
                }
                int neigh_index = theStructure.map.get(neighbor);
                if (struct[neigh_index] == struct[n_index]) {
                    if (weighted) {
                        internal[struct[neigh_index]] += edge.getWeight();
                    } else {
                        internal[struct[neigh_index]]++;
                    }
                }
            }
        }
        for (int i = 0; i < degrees.length; i++) {
            internal[i] /= 2.0;
            res += usedResolution * (internal[i] / totalWeight) - Math.pow(degrees[i] / (2 * totalWeight), 2);//HERE
        }
        return res;
    }

    public double getModularity() {
        return modularity;
    }

//    public String getReport() {
//        //Distribution series
//        Map<Integer, Integer> sizeDist = new HashMap<>();
//        for (Node n : structure.graph.getNodes()) {
//            Integer v = (Integer) n.getAttribute(COMMUNITY_ATTR);
//            if (!sizeDist.containsKey(v)) {
//                sizeDist.put(v, 0);
//            }
//            sizeDist.put(v, sizeDist.get(v) + 1);
//        }
//
//        XYSeries dSeries = ChartUtils.createXYSeries(sizeDist, "Size Distribution");
//
//        XYSeriesCollection dataset1 = new XYSeriesCollection();
//        dataset1.addSeries(dSeries);
//
//        JFreeChart chart = ChartFactory.createXYLineChart(
//                "Size Distribution",
//                "Modularity Class",
//                "Size (number of nodes)",
//                dataset1,
//                PlotOrientation.VERTICAL,
//                true,
//                false,
//                false);
//        chart.removeLegend();
//        ChartUtils.decorateChart(chart);
//        ChartUtils.scaleChart(chart, dSeries, false);
//        String imageFile = ChartUtils.renderChart(chart, "communities-size-distribution.png");
//
//        NumberFormat f = new DecimalFormat("#0.000");
//
//        String report = "<HTML> <BODY> <h1>Modularity Report </h1> "
//                + "<hr>"
//                + "<h2> Parameters: </h2>"
//                + "Randomize:  " + (isRandomized ? "On" : "Off") + "<br>"
//                + "Use edge weights:  " + (useWeight ? "On" : "Off") + "<br>"
//                + "Resolution:  " + (resolution) + "<br>"
//                + "<br> <h2> Results: </h2>"
//                + "Modularity: " + f.format(modularity) + "<br>"
//                + "Modularity with resolution: " + f.format(modularityResolution) + "<br>"
//                + "Number of Communities: " + structure.communities.size()
//                + "<br /><br />" + imageFile
//                + "<br /><br />" + "<h2> Algorithm: </h2>"
//                + "Vincent D Blondel, Jean-Loup Guillaume, Renaud Lambiotte, Etienne Lefebvre, <i>Fast unfolding of communities in large networks</i>, in Journal of Statistical Mechanics: Theory and Experiment 2008 (10), P1000<br />"
//                + "<br /><br />" + "<h2> Resolution: </h2>"
//                + "R. Lambiotte, J.-C. Delvenne, M. Barahona <i>Laplacian Dynamics and Multiscale Modular Structure in Networks 2009<br />"
//                + "</BODY> </HTML>";
//
//        return report;
//    }
    private double q(int node, Community community, CommunityStructure theStructure, double currentResolution) {
        Float edgesToFloat = theStructure.nodeConnectionsWeight[node].get(community);
        double edgesTo = 0;
        if (edgesToFloat != null) {
            edgesTo = edgesToFloat.doubleValue();
        }
        double weightSum = community.weightSum;
        double nodeWeight = theStructure.weights[node];
        double qValue = currentResolution * edgesTo - (nodeWeight * weightSum) / (2.0 * theStructure.graphWeightSum);
        if ((theStructure.nodeCommunities[node] == community) && (theStructure.nodeCommunities[node].size() > 1)) {
            qValue = currentResolution * edgesTo - (nodeWeight * (weightSum - nodeWeight)) / (2.0 * theStructure.graphWeightSum);
        }
        if ((theStructure.nodeCommunities[node] == community) && (theStructure.nodeCommunities[node].size() == 1)) {
            qValue = 0.;
        }
        return qValue;
    }

    class CommunityStructure {

        HashMap<Community, Float>[] nodeConnectionsWeight;
        HashMap<Community, Integer>[] nodeConnectionsCount;
        HashMap<Node, Integer> map;
        Community[] nodeCommunities;
        double[] weights;
        double graphWeightSum;
        List<ModEdge>[] topology;
        List<Community> communities;
        int N;
        HashMap<Integer, Community> invMap;

        CommunityStructure() {
            N = nodes.length;
            invMap = new HashMap<>();
            nodeConnectionsWeight = new HashMap[N];
            nodeConnectionsCount = new HashMap[N];
            nodeCommunities = new Community[N];
            map = new HashMap<>();
            topology = new ArrayList[N];
            communities = new ArrayList<>();
            int index = 0;
            weights = new double[N];

            for (Node node : nodes) {
                map.put(node, index);
                nodeCommunities[index] = new Community(this);

                nodeConnectionsWeight[index] = new HashMap<>();
                nodeConnectionsCount[index] = new HashMap<>();
                weights[index] = 0;
                nodeCommunities[index].seed(index);
                Community hidden = new Community(structure);
                hidden.nodes.add(index);
                invMap.put(index, hidden);
                communities.add(nodeCommunities[index]);
                index++;
                if (isCanceled) {
                    return;
                }
            }

            for (Node node : map.keySet()) {
                int node_index = map.get(node);
                topology[node_index] = new ArrayList<>();

                Set<Node> uniqueNeighbors = new HashSet<>(graph.getNeighbors(node, relType).toCollection());
                for (Node neighbor : uniqueNeighbors) {
                    if (node == neighbor) {
                        continue;
                    }
                    int neighbor_index = map.get(neighbor);
                    float weight = 0;

                    //Sum all parallel edges weight:
                    for (Edge edge : graph.getEdges(node, neighbor, relType)) {
                        if (useWeight) {
                            weight += edge.getWeight();
                        } else {
                            weight += 1;
                        }
                    }

                    //Finally add a single edge with the summed weight of all parallel edges:
                    //Fixes issue #1419 Getting null pointer error when trying to calculate modularity
                    weights[node_index] += weight;
                    ModEdge me = new ModEdge(node_index, neighbor_index, weight);
                    topology[node_index].add(me);
                    Community adjCom = nodeCommunities[neighbor_index];

                    nodeConnectionsWeight[node_index].put(adjCom, weight);
                    nodeConnectionsCount[node_index].put(adjCom, 1);

                    Community nodeCom = nodeCommunities[node_index];
                    nodeCom.connectionsWeight.put(adjCom, weight);
                    nodeCom.connectionsCount.put(adjCom, 1);

                    nodeConnectionsWeight[neighbor_index].put(nodeCom, weight);
                    nodeConnectionsCount[neighbor_index].put(nodeCom, 1);

                    adjCom.connectionsWeight.put(nodeCom, weight);
                    adjCom.connectionsCount.put(nodeCom, 1);

                    graphWeightSum += weight;
                }

                if (isCanceled) {
                    return;
                }
            }
            graphWeightSum /= 2.0;
        }

        public void addNodeTo(int node, Community to) {
            to.add(node);
            nodeCommunities[node] = to;

            for (ModEdge e : topology[node]) {
                int neighbor = e.target;

                ////////
                //Remove Node Connection to this community
                Float neighEdgesTo = nodeConnectionsWeight[neighbor].get(to);
                if (neighEdgesTo == null) {
                    nodeConnectionsWeight[neighbor].put(to, e.weight);
                } else {
                    nodeConnectionsWeight[neighbor].put(to, neighEdgesTo + e.weight);
                }
                Integer neighCountEdgesTo = nodeConnectionsCount[neighbor].get(to);
                if (neighCountEdgesTo == null) {
                    nodeConnectionsCount[neighbor].put(to, 1);
                } else {
                    nodeConnectionsCount[neighbor].put(to, neighCountEdgesTo + 1);
                }

                ///////////////////
                Community adjCom = nodeCommunities[neighbor];
                Float wEdgesto = adjCom.connectionsWeight.get(to);
                if (wEdgesto == null) {
                    adjCom.connectionsWeight.put(to, e.weight);
                } else {
                    adjCom.connectionsWeight.put(to, wEdgesto + e.weight);
                }

                Integer cEdgesto = adjCom.connectionsCount.get(to);
                if (cEdgesto == null) {
                    adjCom.connectionsCount.put(to, 1);
                } else {
                    adjCom.connectionsCount.put(to, cEdgesto + 1);
                }

                Float nodeEdgesTo = nodeConnectionsWeight[node].get(adjCom);
                if (nodeEdgesTo == null) {
                    nodeConnectionsWeight[node].put(adjCom, e.weight);
                } else {
                    nodeConnectionsWeight[node].put(adjCom, nodeEdgesTo + e.weight);
                }

                Integer nodeCountEdgesTo = nodeConnectionsCount[node].get(adjCom);
                if (nodeCountEdgesTo == null) {
                    nodeConnectionsCount[node].put(adjCom, 1);
                } else {
                    nodeConnectionsCount[node].put(adjCom, nodeCountEdgesTo + 1);
                }

                if (to != adjCom) {
                    Float comEdgesto = to.connectionsWeight.get(adjCom);
                    if (comEdgesto == null) {
                        to.connectionsWeight.put(adjCom, e.weight);
                    } else {
                        to.connectionsWeight.put(adjCom, comEdgesto + e.weight);
                    }

                    Integer comCountEdgesto = to.connectionsCount.get(adjCom);
                    if (comCountEdgesto == null) {
                        to.connectionsCount.put(adjCom, 1);
                    } else {
                        to.connectionsCount.put(adjCom, comCountEdgesto + 1);
                    }

                }
            }
        }

        public void removeNodeFromItsCommunity(int node) {
            Community community = nodeCommunities[node];
            for (ModEdge e : topology[node]) {
                int neighbor = e.target;

                ////////
                //Remove Node Connection to this community
                Float edgesTo = nodeConnectionsWeight[neighbor].get(community);
                Integer countEdgesTo = nodeConnectionsCount[neighbor].get(community);
                if (countEdgesTo - 1 == 0) {
                    nodeConnectionsWeight[neighbor].remove(community);
                    nodeConnectionsCount[neighbor].remove(community);
                } else {
                    nodeConnectionsWeight[neighbor].put(community, edgesTo - e.weight);
                    nodeConnectionsCount[neighbor].put(community, countEdgesTo - 1);
                }

                ///////////////////
                //Remove Adjacency Community's connection to this community
                Community adjCom = nodeCommunities[neighbor];
                Float oEdgesto = adjCom.connectionsWeight.get(community);
                Integer oCountEdgesto = adjCom.connectionsCount.get(community);
                if (oCountEdgesto - 1 == 0) {
                    adjCom.connectionsWeight.remove(community);
                    adjCom.connectionsCount.remove(community);
                } else {
                    adjCom.connectionsWeight.put(community, oEdgesto - e.weight);
                    adjCom.connectionsCount.put(community, oCountEdgesto - 1);
                }

                if (node == neighbor) {
                    continue;
                }

                if (adjCom != community) {
                    Float comEdgesto = community.connectionsWeight.get(adjCom);
                    Integer comCountEdgesto = community.connectionsCount.get(adjCom);
                    if (comCountEdgesto - 1 == 0) {
                        community.connectionsWeight.remove(adjCom);
                        community.connectionsCount.remove(adjCom);
                    } else {
                        community.connectionsWeight.put(adjCom, comEdgesto - e.weight);
                        community.connectionsCount.put(adjCom, comCountEdgesto - 1);
                    }
                }

                Float nodeEgesTo = nodeConnectionsWeight[node].get(adjCom);
                Integer nodeCountEgesTo = nodeConnectionsCount[node].get(adjCom);
                if (nodeCountEgesTo - 1 == 0) {
                    nodeConnectionsWeight[node].remove(adjCom);
                    nodeConnectionsCount[node].remove(adjCom);
                } else {
                    nodeConnectionsWeight[node].put(adjCom, nodeEgesTo - e.weight);
                    nodeConnectionsCount[node].put(adjCom, nodeCountEgesTo - 1);
                }

            }
            community.remove(node);
        }

        public void moveNodeTo(int node, Community to) {
            removeNodeFromItsCommunity(node);
            addNodeTo(node, to);
        }

        public void zoomOut() {
            int M = communities.size();
            ArrayList<ModEdge>[] newTopology = new ArrayList[M];
            int index = 0;
            nodeCommunities = new Community[M];
            nodeConnectionsWeight = new HashMap[M];
            nodeConnectionsCount = new HashMap[M];
            HashMap<Integer, Community> newInvMap = new HashMap<>();
            for (int i = 0; i < communities.size(); i++) {//Community com : mCommunities) {
                Community com = communities.get(i);
                nodeConnectionsWeight[index] = new HashMap<>();
                nodeConnectionsCount[index] = new HashMap<>();

                newTopology[index] = new ArrayList<>();
                nodeCommunities[index] = new Community(com);
                Set<Community> iter = com.connectionsWeight.keySet();
                double weightSum = 0;

                Community hidden = new Community(structure);
                for (Integer nodeInt : com.nodes) {
                    Community oldHidden = invMap.get(nodeInt);
                    hidden.nodes.addAll(oldHidden.nodes);
                }
                newInvMap.put(index, hidden);
                for (Community adjCom : iter) {
                    int target = communities.indexOf(adjCom);
                    float weight = com.connectionsWeight.get(adjCom);
                    if (target == index) {
                        weightSum += 2. * weight;
                    } else {
                        weightSum += weight;
                    }
                    ModEdge e = new ModEdge(index, target, weight);
                    newTopology[index].add(e);
                }
                weights[index] = weightSum;
                nodeCommunities[index].seed(index);

                index++;
            }
            communities.clear();

            for (int i = 0; i < M; i++) {
                Community com = nodeCommunities[i];
                communities.add(com);
                for (ModEdge e : newTopology[i]) {
                    nodeConnectionsWeight[i].put(nodeCommunities[e.target], e.weight);
                    nodeConnectionsCount[i].put(nodeCommunities[e.target], 1);
                    com.connectionsWeight.put(nodeCommunities[e.target], e.weight);
                    com.connectionsCount.put(nodeCommunities[e.target], 1);
                }

            }

            N = M;
            topology = newTopology;
            invMap = newInvMap;
        }
    }

    class ModEdge {

        int source;
        int target;
        float weight;

        public ModEdge(int s, int t, float w) {
            source = s;
            target = t;
            weight = w;
        }
    }

    class Community {

        double weightSum;
        CommunityStructure structure;
        List<Integer> nodes;
        HashMap<Community, Float> connectionsWeight;
        HashMap<Community, Integer> connectionsCount;

        public int size() {
            return nodes.size();
        }

        public Community(Community com) {
            structure = com.structure;
            connectionsWeight = new HashMap<>();
            connectionsCount = new HashMap<>();
            nodes = new ArrayList<>();
            //mHidden = pCom.mHidden;
        }

        public Community(CommunityStructure structure) {
            this.structure = structure;
            connectionsWeight = new HashMap<>();
            connectionsCount = new HashMap<>();
            nodes = new ArrayList<>();
        }

        public void seed(int node) {
            nodes.add(node);
            weightSum += structure.weights[node];
        }

        public boolean add(int node) {
            nodes.add(node);
            weightSum += structure.weights[node];
            return true;
        }

        public boolean remove(int node) {
            boolean result = nodes.remove((Integer) node);
            weightSum -= structure.weights[node];
            if (nodes.isEmpty()) {
                structure.communities.remove(this);
            }
            return result;
        }

    }
}
