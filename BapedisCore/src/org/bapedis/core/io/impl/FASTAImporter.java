/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.FASTASEQ;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class FASTAImporter {

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final static GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    private static AtomicInteger counter = new AtomicInteger(45120);

    public void importFASTA(File file, String label, Workspace workspace) {
        AttributesModel newAttrModel = new AttributesModel(workspace);
        AttributesModel oldModel = pc.getAttributesModel(workspace);

        GraphModel graphModel = pc.getGraphModel(workspace);
        Graph mainGraph = graphModel.getGraph();
        List<Node> graphNodes = new LinkedList<>();

        SwingWorker sw = new SwingWorker() {
            private final AtomicBoolean stopRun = new AtomicBoolean(false);
            private final ProgressTicket ticket = new ProgressTicket(NbBundle.getMessage(FASTAImporter.class, "FASTAImporter.task.name"), new Cancellable() {
                @Override
                public boolean cancel() {
                    stopRun.set(true);
                    return true;
                }
            });

            @Override
            protected Object doInBackground() throws Exception {
                if (oldModel != null) {
                    List<Integer> peptideIDs = new LinkedList<>();
                    for(Peptide peptide: oldModel.getPeptides()){
                        peptideIDs.add(peptide.getId());
                        graphNodes.add(peptide.getGraphNode());
                    }
                    oldModel.getBridge().copyTo(newAttrModel, peptideIDs);
                }
                List<ProteinSequence> entries = FASTASEQ.load(file);
                ticket.start(entries.size());
                Peptide peptide;
                Node node;
                String id, seq;
                for (ProteinSequence protein : entries) {
                    id = protein.getAccession().getID();
                    seq = protein.getSequenceAsString();

                    node = getOrAddGraphNode(graphModel, label, id);

                    peptide = new Peptide(node, mainGraph);
                    peptide.setAttributeValue(Peptide.ID, counter.getAndIncrement());
                    peptide.setAttributeValue(Peptide.SEQ, seq);
                    peptide.setAttributeValue(Peptide.LENGHT, seq.length());
                    peptide.setBiojavaSeq(protein);

                    newAttrModel.addPeptide(peptide);
                    graphNodes.add(node);
                    ticket.progress();
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    if (oldModel != null){
                        workspace.remove(oldModel);                        
                    }
                    workspace.add(newAttrModel);
                    pc.setCurrentWorkspace(workspace);
                    graphWC.refreshGraphView(workspace, graphNodes, null);
                    pc.getGraphVizSetting(workspace).fireChangedGraphView();
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    ticket.finish();
                }
            }
        };
        sw.execute();

        try {

        } catch (Exception ex) {
            NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(nd);
        }
    }

    private Node getOrAddGraphNode(GraphModel graphModel, String label, String id) {
        Graph mainGraph = graphModel.getGraph();
        Node graphNode = mainGraph.getNode(id);
        if (graphNode == null) {
            GraphFactory graphFactory = graphModel.factory();
            graphNode = graphFactory.newNode(id);

            graphNode.setLabel(label);
            graphNode.setSize(ProjectManager.GRAPH_NODE_SIZE);
            graphNode.setAttribute(ProjectManager.NODE_TABLE_PRO_NAME, id);

            //Set random position
            graphNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
            graphNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

            //Set color
            graphNode.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
            graphNode.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
            graphNode.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
            graphNode.setAlpha(1f);

            mainGraph.addNode(graphNode);
        }
        return graphNode;
    }

}
