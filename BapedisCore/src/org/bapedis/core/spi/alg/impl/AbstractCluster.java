/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Peptide;
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

    public static final String CLUSTER_COLUMN = "cluster";
    protected final AlgorithmFactory factory;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    protected Peptide[] peptides;
    protected Workspace workspace;
    protected final List<Cluster> clusterList;
    protected final ProjectManager pc;
    private GraphVizSetting graphViz;
    protected GraphModel graphModel;

    public AbstractCluster(AlgorithmFactory factory) {
        this.factory = factory;
        clusterList = new LinkedList<>();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public Peptide[] getPeptides() {
        return peptides;
    }

    public void setPeptides(Peptide[] peptides) {
        this.peptides = peptides;
    }

    public List<Cluster> getClusterList() {
        return clusterList;
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
        clusterList.clear();
        graphModel = pc.getGraphModel(workspace);
        graphViz = pc.getGraphVizSetting(workspace);
    }

    @Override
    public void endAlgo() {
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
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public void run() {
        if (peptides != null) {
            cluterize();

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
                    node = p.getGraphNode();
                    node.setAttribute(CLUSTER_COLUMN, c.getId());
                }
            }

            if (fireEvent) {
                graphViz.fireChangedGraphTable();
            }
        }
    }

    protected abstract void cluterize();

}
