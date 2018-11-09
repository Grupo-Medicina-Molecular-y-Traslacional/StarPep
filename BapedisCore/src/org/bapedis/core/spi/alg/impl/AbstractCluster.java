/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.ClusterNavigatorModel;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class AbstractCluster implements Algorithm {

    protected static PeptideAttribute CLUSTER_ATTR = new PeptideAttribute("cluster", "Cluster", Integer.class);
    public static final String CLUSTER_COLUMN = "cluster";
    protected final AlgorithmFactory factory;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    protected Peptide[] peptides;
    protected Workspace workspace;
    protected Cluster[] clusters;
    protected final ProjectManager pc;
    private GraphVizSetting graphViz;
    protected GraphModel graphModel;
    protected ClusterNavigatorModel navModel;

    public AbstractCluster(AlgorithmFactory factory) {
        this.factory = factory;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public Peptide[] getPeptides() {
        return peptides;
    }

    public void setPeptides(Peptide[] peptides) {
        this.peptides = peptides;
    }

    public Cluster[] getClusters() {
        return clusters;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        if (peptides == null) {
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            if (attrModel != null) {
                peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            }
        }
        stopRun = false;
        ticket = progressTicket;
        clusters = null;
        graphModel = pc.getGraphModel(workspace);
        graphViz = pc.getGraphVizSetting(workspace);
        navModel = pc.getClusterNavModel(workspace);
        navModel.setRunning(true);
    }

    @Override
    public void endAlgo() {
        navModel.setRunning(false);
        navModel = null;
        workspace = null;
        peptides = null;
        ticket = null;
        graphModel = null;
        graphViz = null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return false;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public void run() {
        if (peptides != null) {
            List<Cluster> clusterList = cluterize();
            if (clusterList != null) {
                int index = 0;
                clusters = new Cluster[clusterList.size()];
                for (Cluster c : clusterList) {
                    clusters[index++] = c;
                }
                Arrays.sort(clusters, new Comparator<Cluster>() {
                    @Override
                    public int compare(Cluster o1, Cluster o2) {
                        double p1 = o1.getPercentageComp();
                        double p2 = o2.getPercentageComp();
                        return p1 > p2 ? -1 : p1 < p2 ? 1 : 0;
                    }

                });

                //Add cluster column
                boolean fireEvent = false;
                Table nodeTable = graphModel.getNodeTable();
                if (!nodeTable.hasColumn(CLUSTER_COLUMN)) {
                    nodeTable.addColumn(CLUSTER_COLUMN, "Cluster", Integer.class, null);
                    fireEvent = true;
                }

                Node node;
                for (Cluster c : clusterList) {
                    for (Peptide p : c.getMembers()) {
                        p.setAttributeValue(CLUSTER_ATTR, c.getId());
                        node = p.getGraphNode();
                        node.setAttribute(CLUSTER_COLUMN, c.getId());                        
                    }
                }

                navModel.setClusters(clusters);

                if (fireEvent) {
                    graphViz.fireChangedGraphTable();
                }
            }
        }
    }

    protected abstract List<Cluster> cluterize();

}
