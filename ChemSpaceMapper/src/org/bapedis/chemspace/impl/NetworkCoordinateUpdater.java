/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import javax.vecmath.Vector3f;
import static org.bapedis.chemspace.impl.NetworkThresholdUpdater.CHANGED_THRESHOLD;
import org.bapedis.chemspace.model.CoordinateSpace;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class NetworkCoordinateUpdater extends SwingWorker<Void, Void> {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    static final String UPDATED_POSITIONS = "updated_positions";
    private final AtomicBoolean stopRun;
    private final ProgressTicket ticket;
    private final CoordinateSpace xyzSpace;
    private final int xAxis, yAxis, zAxis;

    public NetworkCoordinateUpdater(CoordinateSpace xyzSpace, int xAxis, int yAxis, int zAxis) {
        this.xyzSpace = xyzSpace;
        this.xAxis = xAxis;
        this.yAxis = yAxis;
        this.zAxis = zAxis;
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(NetworkThresholdUpdater.class, "GraphNodePositionUpdater.task.name"), new Cancellable() {
            @Override
            public boolean cancel() {
                stopRun.set(true);
                return true;
            }
        });
        ticket.start();
    }

    @Override
    protected Void doInBackground() throws Exception {
        xyzSpace.updatePositions(xAxis, yAxis, zAxis);
        Vector3f[] positions = xyzSpace.getPositions();
        Peptide[] peptides = xyzSpace.getPeptides();
        ticket.switchToDeterminate(peptides.length);
        GraphModel graphModel = pc.getGraphModel();
        Graph graphVisible = graphModel.getGraphVisible();
        graphVisible.readLock();
        try {
            Node node;
            Vector3f p;
            for (int i = 0; i < positions.length && !stopRun.get(); i++) {
                p = positions[i];
                node = peptides[i].getGraphNode();
                node.setX((float) ((0.01 + p.getX()) * 1000) - 500);
                node.setY((float) ((0.01 + p.getY()) * 1000) - 500);
                node.setZ(0); // 2D  
                ticket.progress();
            }
        } finally {
            graphVisible.readUnlock();
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            firePropertyChange(UPDATED_POSITIONS, null, null);
            ticket.finish();
        }
    }

}
