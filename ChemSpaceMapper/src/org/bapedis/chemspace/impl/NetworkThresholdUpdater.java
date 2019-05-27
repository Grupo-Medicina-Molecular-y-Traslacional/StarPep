/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
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
 * @author Loge
 */
public class NetworkThresholdUpdater extends SwingWorker<Void, Void> {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    static final String CHANGED_THRESHOLD = "changed_threshold";
    private final AtomicBoolean stopRun;
    private final ProgressTicket ticket;
    private final double newThreshold, currentThreshold;

    public NetworkThresholdUpdater(double newThreshold, double currentThreshold) {
        this.newThreshold = newThreshold;
        this.currentThreshold = currentThreshold;
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(NetworkThresholdUpdater.class, "NetworkThresholdUpdater.task.name"), new Cancellable() {
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
        applySimilarityThreshold();
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            firePropertyChange(CHANGED_THRESHOLD, null, newThreshold);
            pc.getGraphVizSetting().fireChangedGraphView();
            ticket.finish();
        }
    }
    
    public void applySimilarityThreshold() {
        AttributesModel attrModel = pc.getAttributesModel();
        Peptide[] peptides = attrModel.getPeptides().toArray(new Peptide[0]);
        GraphModel graphModel = pc.getGraphModel();        
        Graph mainGraph = graphModel.getGraph();
        Graph graph = graphModel.getGraphVisible();
        
        Node node1, node2;
        double similarity;
        int relType = graph.getModel().getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        if (newThreshold < currentThreshold) { // to add edges 
            Edge graphEdge;
            for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
                node1 = peptides[i].getGraphNode();
                for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                    node2 = peptides[j].getGraphNode();
                    if (graph.contains(node1) && graph.contains(node2)) {
                        graphEdge = mainGraph.getEdge(node2, node1, relType);
                        if (graphEdge != null) {
                            similarity = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                            if (similarity >= newThreshold && similarity < currentThreshold) {
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
            }
        } else if (newThreshold > currentThreshold) { // to remove edges
            graph.writeLock();
            try {
                for (Edge edge : graph.getEdges()) {
                    similarity = (double) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                    if (similarity < newThreshold) {
                        graph.removeEdge(edge);
                    }
                }
            } finally {
                graph.writeUnlock();
            }
        }
    }
    

}
