/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JButton;
import javax.swing.SwingWorker;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.network.model.SimilarityMatrix;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ApplyCutoffValue extends SwingWorker<Void, Void> {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final GraphModel graphModel;
    private final CSNAlgorithm csnAlgo;    
    private final AtomicBoolean stopRun;
    private final ProgressTicket ticket;
    private final JButton actionButton;

    public ApplyCutoffValue(CSNAlgorithm csnAlgo, JButton actionButton) {
        this.csnAlgo = csnAlgo;
        graphModel = pc.getGraphModel();
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(ApplyCutoffValue.class, "ApplyCutoffValue.task.name"), new Cancellable() {
            @Override
            public boolean cancel() {
                stopRun.set(true);
                return true;
            }
        });
        actionButton.setEnabled(false);
        this.actionButton = actionButton;
    }

    private void clearGraph() {
        // Remove all similarity edges..
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                mainGraph.clearEdges(node, relType);
            }
        } finally {
            mainGraph.writeUnlock();
        }
    }

    @Override
    protected Void doInBackground() throws Exception {
        ticket.start();
               
        Edge graphEdge;
        Float score;
        
        Graph graph = graphModel.getGraphVisible();
        float cutoff = csnAlgo.getCutoffValue()  / 100.f;
        SimilarityMatrix matrix = csnAlgo.getSimilarityMatrix();
        Peptide[] peptides = matrix.getPeptides();
        clearGraph();        
        for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                score = matrix.getValue(peptides[i], peptides[j]);
                if (score != null && score >= cutoff) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        graphEdge = createGraphEdge(peptides[i], peptides[j], score);
                        graph.writeLock();
                        try {
                            graph.addEdge(graphEdge);
                        } finally {
                            graph.writeUnlock();
                        }
                    }
                }
            }
        }
        return null;
    }

    private Edge createGraphEdge(Peptide peptide1, Peptide peptide2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        String id = String.format("%s-%s", peptide1.getId(), peptide2.getId());

        // Create Edge
        Edge graphEdge = graphModel.factory().newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            mainGraph.addEdge(graphEdge);
            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);
        } finally {
            mainGraph.writeUnlock();
        }

        return graphEdge;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            pc.getGraphViz().fireChangedGraphView();
            ticket.finish();
            actionButton.setEnabled(true);
        }
    }

}
