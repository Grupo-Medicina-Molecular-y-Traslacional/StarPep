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
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class AbstractCluster implements Algorithm {

    protected final AlgorithmFactory factory;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    protected Peptide[] peptides;
    protected Workspace workspace;
    protected final List<Cluster> clusterList;
    protected final ProjectManager pc;

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
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        ticket = null;
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
            Node node;
            for(Cluster c: clusterList){
                node = c.getCentroid().getGraphNode();
                node.setAttribute(ProjectManager.NODE_TABLE_PRO_CLUSTER, c.getId());
                for(Peptide p: c.getMembers()){
                    node = p.getGraphNode();
                    node.setAttribute(ProjectManager.NODE_TABLE_PRO_CLUSTER, c.getId());
                }
            }
        }
    }

    protected abstract void cluterize();

}
