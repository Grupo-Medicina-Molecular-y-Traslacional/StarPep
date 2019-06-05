/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class AbstractClusterizer implements Algorithm, Cloneable {

    public static PeptideAttribute CLUSTER_ATTR = new PeptideAttribute("cluster", "Cluster", Integer.class, true, -1);
    protected final AlgorithmFactory factory;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    protected AttributesModel attrModel;
    protected Peptide[] peptides;
    protected MolecularDescriptor[] features;
    protected Workspace workspace;
    protected Cluster[] clusters;
    protected final ProjectManager pc;
    private GraphVizSetting graphViz;
    protected GraphModel graphModel;
    protected boolean preprocessing;

    public AbstractClusterizer(AlgorithmFactory factory) {
        this.factory = factory;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        preprocessing = true;
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

    public boolean isPreprocessing() {
        return preprocessing;
    }

    public void setPreprocessing(boolean preprocessing) {
        this.preprocessing = preprocessing;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        stopRun = false;
        ticket = progressTicket;
        clusters = null;
        graphModel = pc.getGraphModel(workspace);
        graphViz = pc.getGraphVizSetting(workspace);  
        attrModel = pc.getAttributesModel(workspace);
        if (peptides == null) {            
            if (attrModel != null) {
                peptides = attrModel.getPeptides().toArray(new Peptide[0]);
                attrModel.removeDisplayedColumn(CLUSTER_ATTR);
                //Load features
                List<MolecularDescriptor> allFeatures = new LinkedList<>();
                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        allFeatures.add(attr);
                    }
                }
                features = allFeatures.toArray(new MolecularDescriptor[0]);

                if (preprocessing) {
                    try {
                        MolecularDescriptor.preprocessing(allFeatures, attrModel.getPeptides());
                    } catch (MolecularDescriptorException ex) {
                        DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                        pc.reportError(ex.getMessage(), workspace);
                        cancel();
                    }
                }
            }
        }
    }

    @Override
    public void endAlgo() {
        if (attrModel != null && !stopRun) {
            attrModel.addDisplayedColumn(CLUSTER_ATTR);

            //Set default values            
            for (Peptide peptide : attrModel.getPeptideMap().values()) {
                peptide.setAttributeValue(CLUSTER_ATTR, CLUSTER_ATTR.getDefaultValue());
            }

            //Set cluster values    
            if (clusters != null) {
                Node graphNode;
                for (Cluster c : clusters) {
                    for (Peptide p : c.getMembers()) {
                        p.setAttributeValue(CLUSTER_ATTR, c.getId());
                        graphNode = p.getGraphNode();
                        graphNode.setAttribute(CLUSTER_ATTR.getDisplayName(), c.getId());
                    }
                }
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
        if (peptides != null && peptides.length > 0 && !stopRun) {
            List<Cluster> clusterList = cluterize();

            if (clusterList != null) {
                int index = 0;
                clusters = new Cluster[clusterList.size()];
                for (Cluster c : clusterList) {
                    c.setPercentage(c.getSize() * 100 / (double) peptides.length);
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
        AbstractClusterizer copy = (AbstractClusterizer) super.clone();
        return copy;
    }

    protected abstract List<Cluster> cluterize();

}
