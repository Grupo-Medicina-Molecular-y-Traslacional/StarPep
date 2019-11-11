/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.centrality;

import java.util.Arrays;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Sebastien Heymann
 */
public class WeightedDegree extends AbstractCentrality {

    public static final String WDEGREE = "totalstrength";
    public static final String WDEGREE_TITLE = "Total Strength (TS)";

    public static final String RANKING_BY_WDEGREE = "rankingBytotalstrength";
    public static final String RANKING_BY_WDEGREE_TITLE = "Ranking by TS";

    public static final String WINDEGREE = "internalstrength";
    public static final String WINDEGREE_TITLE = "Internal Strength (IS)";

    public static final String RANKING_BY_WINDEGREE = "rankingByinternalstrength";
    public static final String RANKING_BY_WINDEGREE_TITLE = "Ranking by IS";

    public static final String WOUTDEGREE = "externalstrength";
    public static final String WOUTDEGREE_TITLE = "External Strength (ES)";

    public static final String RANKING_BY_WOUTDEGREE = "rankingByexternalstrength";
    public static final String RANKING_BY_WOUTDEGREE_TITLE = "Ranking by ES";

    private GraphVizSetting graphViz;
    protected AlgorithmProperty[] properties;
    private boolean total, internal, external;
    private int relType;
    double[] intStrength, extStrength, totalStrength;

    public WeightedDegree(AlgorithmFactory factory) {
        super(factory);
        properties = new AlgorithmProperty[3];

        try {
            String CATEGORY = "Properties";
            total = true;
            properties[0] = AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(WeightedDegree.class, "Property.total.name"), CATEGORY, NbBundle.getMessage(WeightedDegree.class, "Property.total.desc"), "isTotal", "setTotal");

            internal = true;
            properties[1] = AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(WeightedDegree.class, "Property.internal.name"), CATEGORY, NbBundle.getMessage(WeightedDegree.class, "Property.internal.desc"), "isInternal", "setInternal");

            external = true;
            properties[2] = AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(WeightedDegree.class, "Property.external.name"), CATEGORY, NbBundle.getMessage(WeightedDegree.class, "Property.external.desc"), "isExternal", "setExternal");
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
            properties = null;
        }
    }

    public boolean isTotal() {
        return total;
    }

    public void setTotal(Boolean total) {
        this.total = total;
    }

    public boolean isInternal() {
        return internal;
    }

    public void setInternal(Boolean internal) {
        this.internal = internal;
    }

    public boolean isExternal() {
        return external;
    }

    public void setExternal(Boolean external) {
        this.external = external;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);

        intStrength = new double[nodes.length];
        extStrength = new double[nodes.length];
        totalStrength = new double[nodes.length];
        
        relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        graphViz = pc.getGraphVizSetting(workspace);
    }

    @Override
    public void endAlgo() {
        boolean fireEvent = false;
        Table nodeTable = graphModel.getNodeTable();
        if (internal && !nodeTable.hasColumn(WINDEGREE)) {
            nodeTable.addColumn(WINDEGREE, WINDEGREE_TITLE, Double.class, 0.0);
            nodeTable.addColumn(RANKING_BY_WINDEGREE, RANKING_BY_WINDEGREE_TITLE, Integer.class, -1);
        }
        if (external && !nodeTable.hasColumn(WOUTDEGREE)) {
            nodeTable.addColumn(WOUTDEGREE, WOUTDEGREE_TITLE, Double.class, 0.0);
            nodeTable.addColumn(RANKING_BY_WOUTDEGREE, RANKING_BY_WOUTDEGREE_TITLE, Integer.class, -1);
        }
        if (total && !nodeTable.hasColumn(WDEGREE)) {
            nodeTable.addColumn(WDEGREE, WDEGREE_TITLE, Double.class, 0.0);
            nodeTable.addColumn(RANKING_BY_WDEGREE, RANKING_BY_WDEGREE_TITLE, Integer.class, -1);
        }

        //Set default values
        Double defaultValue = new Double(0);
        for (Node node : graphModel.getGraph().getNodes()) {
            if (nodeTable.hasColumn(WINDEGREE)) {
                node.setAttribute(WINDEGREE, defaultValue);
            }
            if (nodeTable.hasColumn(WOUTDEGREE)) {
                node.setAttribute(WOUTDEGREE, defaultValue);
            }
            if (nodeTable.hasColumn(WDEGREE)) {
                node.setAttribute(WDEGREE, defaultValue);
            }
        }

        if (!isCanceled.get()) {
            for (int i = 0; i < nodes.length; i++) {
                if (internal) {
                    nodes[i].setAttribute(WINDEGREE, intStrength[i]);
                }
                if (external) {
                    nodes[i].setAttribute(WOUTDEGREE, extStrength[i]);
                }
                if (total) {
                    nodes[i].setAttribute(WDEGREE, totalStrength[i]);
                }
            }

            if (internal) {
                Arrays.parallelSort(nodes, new RankComparator(WINDEGREE));
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i].setAttribute(RANKING_BY_WINDEGREE, i + 1);
                }
            }
            if (external) {
                Arrays.parallelSort(nodes, new RankComparator(WOUTDEGREE));
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i].setAttribute(RANKING_BY_WOUTDEGREE, i + 1);
                }
            }
            if (total) {
                Arrays.parallelSort(nodes, new RankComparator(WDEGREE));
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i].setAttribute(RANKING_BY_WDEGREE, i + 1);
                }
            }
        }

        super.endAlgo();

        intStrength = null;
        extStrength = null;
        totalStrength = null;

        if (fireEvent) {
            graphViz.fireChangedGraphView();
        }
        graphViz = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return properties;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    protected void calculateCentrality() {
        Table nodeTable = graphModel.getNodeTable();
        Node oppositeNode;
        progress.switchToDeterminate(nodes.length);
        Node n;
        for (int i = 0; i < nodes.length; i++) {
            n = nodes[i];
            if (isCanceled.get()) {
                return;
            }
            double totalInWeight = 0;
            double totalOutWeight = 0;
            double totalWeight = 0;
            int comunity = -1;
            if (nodeTable.hasColumn(PeptideAttribute.CLUSTER_ATTR.getId())) {
                comunity = (int) n.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId());
            }

            for (Edge e : graph.getEdges(n, relType)) {
                oppositeNode = graph.getOpposite(n, e);
                if (comunity != -1 && nodeTable.hasColumn(PeptideAttribute.CLUSTER_ATTR.getId())) {
                    if (comunity == (int) oppositeNode.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId())) {
                        totalInWeight += e.getWeight();
                        totalWeight += e.getWeight();
                    } else {
                        totalOutWeight += e.getWeight();
                        totalWeight += e.getWeight();
                    }
                } else {
                    totalWeight += e.getWeight();
                }
            }
            if (internal) {
                intStrength[i] = totalInWeight;
            }
            if (external) {
                extStrength[i] = totalOutWeight;
            }
            if (total) {
                totalStrength[i] = totalWeight;
            }
            progress.progress();
        }

    }
}
