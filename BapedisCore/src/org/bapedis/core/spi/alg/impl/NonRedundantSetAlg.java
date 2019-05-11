/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NonRedundantSetAlg implements Algorithm, Cloneable {

    private final ProjectManager pc;
    protected final PeptideDAO dao;
    private List<Node> graphNodes;
    private final NonRedundantSetAlgFactory factory;
    protected SequenceClustering clusteringAlg;
    private AttributesModel tmpAttrModel, newAttrModel;
    private Workspace workspace;
    private ProgressTicket ticket;
    private boolean stopRun, workspaceInput;
    protected final GraphWindowController graphWC;

    public NonRedundantSetAlg(NonRedundantSetAlgFactory factory) {
        this.factory = factory;
        this.clusteringAlg = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        dao = Lookup.getDefault().lookup(PeptideDAO.class);
        workspaceInput = false;
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return clusteringAlg.getAlignmentModel();
    }

    public boolean isWorkspaceInput() {
        return workspaceInput;
    }

    public void setWorkspaceInput(boolean workspaceInput) {
        this.workspaceInput = workspaceInput;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.ticket = progressTicket;
        graphNodes = null;
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        // Set new Model
        if (newAttrModel != null && !stopRun) {
            // To refresh graph view
            GraphModel graphModel = pc.getGraphModel(workspace);
            Graph graph = graphModel.getGraphVisible();
            graph.clear();
            graphWC.refreshGraphView(workspace, graphNodes, null);

            final Workspace ws = workspace;
            final AttributesModel modelToRemove = pc.getAttributesModel(workspace);
            final AttributesModel modelToAdd = newAttrModel;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        //To change attribute model
                        ws.remove(modelToRemove);
                        ws.add(modelToAdd);
                    } finally {
                        pc.getGraphVizSetting(ws).fireChangedGraphView();
                    }
                }
            });

        }
        workspace = null;
        graphNodes = null;
        ticket = null;
        newAttrModel = null;
        tmpAttrModel = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        if (clusteringAlg != null) {
            return clusteringAlg.cancel();
        }
        return stopRun;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    private Cluster[] clusterize() {
        String msg = NbBundle.getMessage(NonRedundantSetAlg.class, "NonRedundantSetAlg.task.clusterize");
        pc.reportMsg(msg, workspace);
        ticket.progress(msg);

        //set peptides
        if (workspaceInput) {
            tmpAttrModel = pc.getAttributesModel(workspace);
        } else {
            tmpAttrModel = dao.getPeptides(new QueryModel(workspace), pc.getGraphModel(workspace), pc.getAttributesModel(workspace));            
        }

        Peptide[] peptides = tmpAttrModel.getPeptides().toArray(new Peptide[0]);
        clusteringAlg.setPeptides(peptides);

        //Clusterize
        if (!stopRun) {
            clusteringAlg.initAlgo(workspace, ticket);
            clusteringAlg.run();
            clusteringAlg.endAlgo();
            return clusteringAlg.getClusters();
        }
        return null;
    }

    @Override
    public void run() {
        if (!stopRun) {
            Cluster[] clusters = clusterize();
            if (clusters != null) {
                //New model
                graphNodes = new LinkedList<>();
                newAttrModel = new AttributesModel(workspace);
                tmpAttrModel.getBridge().copyTo(newAttrModel, null);
                Peptide peptide;
                for (Cluster cluster : clusters) {
                    peptide = cluster.getCentroid();
                    newAttrModel.addPeptide(peptide);
                    graphNodes.add(peptide.getGraphNode());
                }
            }
        }
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        NonRedundantSetAlg copy = (NonRedundantSetAlg) super.clone();
        copy.clusteringAlg = (SequenceClustering)this.clusteringAlg.clone();
        return copy;
    }     

}
