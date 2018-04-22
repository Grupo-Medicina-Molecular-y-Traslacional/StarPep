/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
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
        switch(embedder.getNetworkType()){
            case FULL:
                embedder.createFullNetwork(graphModel, stopRun);
            case COMPRESSED:
                embedder.createCompressedNetwork(graphModel, stopRun);
        }
        
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
            pc.getGraphViz().fireChangedGraphView();
            ticket.finish();
        }
    }

}
