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
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
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
public class NonRedundantSetAlg implements Algorithm {

    private final ProjectManager pc;
    private List<Node> graphNodes;
    private final NonRedundantSetAlgFactory factory;
    protected final SequenceClustering clusteringAlg;
    private AttributesModel newAttrModel;
    private Workspace workspace;
    private ProgressTicket ticket;
    private boolean stopRun;
    protected final GraphWindowController graphWC;

    public NonRedundantSetAlg(NonRedundantSetAlgFactory factory) {
        this.factory = factory;
        this.clusteringAlg = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return clusteringAlg.getAlignmentModel();
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

        clusteringAlg.initAlgo(workspace, ticket);
        clusteringAlg.run();
        clusteringAlg.endAlgo();
        return clusteringAlg.getClusters();
    }

    @Override
    public void run() {
        if (!stopRun) {
            Cluster[] clusters = clusterize();
            //New model
            AttributesModel tmpAttrModel = pc.getAttributesModel(workspace);
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
