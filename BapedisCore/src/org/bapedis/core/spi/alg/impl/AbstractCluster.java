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
public abstract class AbstractCluster implements Algorithm, Cloneable {

    public static PeptideAttribute CLUSTER_ATTR = new PeptideAttribute("cluster", "Cluster", Integer.class, true, -1);
    public static final String CLUSTER_COLUMN = "cluster";
    protected final AlgorithmFactory factory;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    protected AttributesModel attrModel;
    protected Peptide[] peptides;
    protected Workspace workspace;
    protected Cluster[] clusters;
    protected final ProjectManager pc;
    private GraphVizSetting graphViz;
    protected GraphModel graphModel;

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
            attrModel = pc.getAttributesModel(workspace);
            if (attrModel != null) {
                peptides = attrModel.getPeptides().toArray(new Peptide[0]);
                attrModel.removeDisplayedColumn(CLUSTER_ATTR);
            }
        }
        stopRun = false;
        ticket = progressTicket;
        clusters = null;
        graphModel = pc.getGraphModel(workspace);
        graphViz = pc.getGraphVizSetting(workspace);
    }

    @Override
    public void endAlgo() {
        if (attrModel != null && !stopRun) {
            attrModel.addDisplayedColumn(CLUSTER_ATTR);

            //Add cluster column
            boolean fireEvent = false;
            Table nodeTable = graphModel.getNodeTable();
            if (!nodeTable.hasColumn(CLUSTER_COLUMN)) {
                nodeTable.addColumn(CLUSTER_COLUMN, "Cluster", Integer.class, null);
                fireEvent = true;
            }

            //Set default values            
            for (Node node : graphModel.getGraph().getNodes()) {
                node.setAttribute(CLUSTER_COLUMN, CLUSTER_ATTR.getDefaultValue());
            }
            for (Peptide peptide : attrModel.getPeptideMap().values()) {
                peptide.setAttributeValue(CLUSTER_ATTR, CLUSTER_ATTR.getDefaultValue());
            }

            //Set cluster values    
            if (clusters != null) {
                Node node;
                for (Cluster c : clusters) {
                    for (Peptide p : c.getMembers()) {
                        p.setAttributeValue(CLUSTER_ATTR, c.getId());
                        node = p.getGraphNode();
                        node.setAttribute(CLUSTER_COLUMN, c.getId());
                    }
                }
            }


            if (fireEvent) {
                graphViz.fireChangedGraphView();
            }
        }

        attrModel = null;
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
        if (peptides != null && peptides.length > 0) {
            List<Cluster> clusterList = cluterize();

            if (clusterList != null) {
                int index = 0;
                clusters = new Cluster[clusterList.size()];
                for (Cluster c : clusterList) {
                    c.setPercentage(c.getSize()*100/(double)peptides.length);
                    clusters[index++] = c;                    
                }

                Arrays.sort(clusters, new Comparator<Cluster>() {
                    @Override
                    public int compare(Cluster o1, Cluster o2) {
                        double p1 = o1.getPercentage();
                        double p2 = o2.getPercentage();
                        return p1 > p2 ? -1 : p1 < p2 ? 1 : 0;
                    }

                });
            }
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        AbstractCluster copy = (AbstractCluster) super.clone();
        return copy;
    }    

    protected abstract List<Cluster> cluterize();

}
