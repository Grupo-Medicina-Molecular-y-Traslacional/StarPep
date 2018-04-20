/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import javax.vecmath.Vector2f;
import org.bapedis.chemspace.model.JitterModel;
import org.bapedis.chemspace.model.TwoDSpace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
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
public class JitterLevelUpdater extends SwingWorker<Void, Void> {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    static final String CHANGED_LEVEL = "changed_level";
    private final TwoDSpace twoDSpace;
    private final int level;
    private final AtomicBoolean stopRun;
    private final ProgressTicket ticket;

    public JitterLevelUpdater(TwoDSpace twoDSpace, int level) {
        this.twoDSpace = twoDSpace;
        this.level = level;
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(FNThresholdUpdater.class, "JitterLevelUpdater.task.name"), new Cancellable() {
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
        Graph graph = graphModel.getGraphVisible();

        if (level > 0) {
            JitterModel jitterModel = twoDSpace.getJitterModel();
            jitterModel.setLevel(level);
            Vector2f[] positions = TwoDEmbedder.jittering(twoDSpace);
            TwoDEmbedder.setGraphNodePositions(graph, twoDSpace.getPeptides(), positions);
        } else if (level == 0) {
            TwoDEmbedder.setGraphNodePositions(graph, twoDSpace.getPeptides(), twoDSpace.getPositions());
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
            firePropertyChange(CHANGED_LEVEL, null, level);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            ticket.finish();
        }
    }

}
