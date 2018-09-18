/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.GraphModel;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NetworkThresholdUpdater extends SwingWorker<Void, Void> {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    static final String CHANGED_THRESHOLD = "changed_threshold";
    private final NetworkEmbedder embedder;
    private final AtomicBoolean stopRun;
    private final ProgressTicket ticket;

    public NetworkThresholdUpdater(NetworkEmbedder embedder) {
        this.embedder = embedder;
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(NetworkThresholdUpdater.class, "FNThresholdUpdater.task.name"), new Cancellable() {
            @Override
            public boolean cancel() {
                stopRun.set(true);
                return true;
            }
        });
    }

    @Override
    protected Void doInBackground() throws Exception {
        ticket.start();
        GraphModel graphModel = pc.getGraphModel();
        embedder.runEmbed(graphModel, ticket, stopRun);

//            if (newThreshold < oldThreshold) { // to add edges 
//            Edge graphEdge;
//            String id;
//            Peptide[] peptides = matrix.getPeptides();
//            for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
//                for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
//                    score = matrix.getValue(peptides[i], peptides[j]);
//                    if (score != null && score >= newThreshold && score < oldThreshold) {
//                        if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
//                            id = String.format("%s-%s", peptides[i].getId(), peptides[j].getId());
//                            graphEdge = mainGraph.getEdge(id);
//                            if (graphEdge == null) {
//                                graphEdge = NetworkEmbedder.createGraphEdge(graphModel, id, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
//                            }
//                            graph.writeLock();
//                            try {
//                                graph.addEdge(graphEdge);
//                            } finally {
//                                graph.writeUnlock();
//                            }
//                        }
//                    }
//                }
//            }
//        } else if (newThreshold > oldThreshold) { // to remove edges
//            graph.writeLock();
//            try {
//                for (Edge edge : graph.getEdges()) {
//                    score = (Float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
//                    if (score < newThreshold) {
//                        graph.removeEdge(edge);
//                    }
//                }
//            } finally {
//                graph.writeUnlock();
//            }
//        }
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
            firePropertyChange(CHANGED_THRESHOLD, null, embedder.getSimilarityThreshold());
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            pc.getGraphVizSetting().fireChangedGraphView();
            ticket.finish();
        }
    }

}