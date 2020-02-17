/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.MultiQuery;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.FASTASEQ;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class EmbeddingQuerySeqAlg implements Algorithm, Cloneable, MultiQuery {

    public static String QUERY_LABEL = "Query";
    static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected final EmbeddingQuerySeqFactory factory;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    private AttributesModel newAttrModel;
    protected boolean stopRun;
    protected final GraphWindowController graphWC;
    private String fasta;
    static final NotifyDescriptor ErrorWS = new NotifyDescriptor.Message(NbBundle.getMessage(EmbeddingQuerySeqAlg.class, "Newworkspace.exist"), NotifyDescriptor.ERROR_MESSAGE);
    private static final AtomicInteger COUNTER = new AtomicInteger(45120);

    public EmbeddingQuerySeqAlg(EmbeddingQuerySeqFactory factory) {
        this.factory = factory;
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    }

    @Override
    public void setFasta(String fasta) {
        this.fasta = fasta;
    }

    @Override
    public String getFasta() {
        return fasta;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        if (newAttrModel != null && !stopRun) {
            // To refresh graph view

            final Workspace newWS = newAttrModel.getOwnerWS();
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            pc.add(newWS);
                            pc.setCurrentWorkspace(newWS);
                        } finally {
                            pc.getGraphVizSetting(newWS).fireChangedGraphView();
                        }
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        workspace = null;
        progressTicket = null;
        newAttrModel = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        if (fasta != null && !fasta.isEmpty()) {
            Workspace newWS = pc.createWorkspace();
            if (newWS != null) {
                //Create new attr model
                newAttrModel = new AttributesModel(newWS);
                newWS.add(newAttrModel);

                //Copy target peptides and graph
                AttributesModel currentModel = pc.getAttributesModel(workspace);
                List<String> peptideIDs = new LinkedList<>();
                for (Peptide peptide : currentModel.getPeptides()) {
                    peptideIDs.add(peptide.getID());
                }
                currentModel.getBridge().copyTo(newAttrModel, peptideIDs);

                // Target list of peptides
                List<Peptide> targetList = new LinkedList<>();
                for (Peptide peptide : newAttrModel.getPeptides()) {
                    targetList.add(peptide);
                }

                //Add query peptides to new attribute model
                Workspace tmpWS = new Workspace("tmp");
                AttributesModel tmpModel = new AttributesModel(tmpWS);
                tmpWS.add(tmpModel);
                List<ProteinSequence> queries = null;
                try {
                    queries = FASTASEQ.load(fasta);
                } catch (Exception ex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    cancel();
                }

                if (!stopRun && queries != null) {
                    GraphModel graphModel = pc.getGraphModel(tmpWS);
                    Graph mainGraph = graphModel.getGraph();

                    //Add query peptides and graph nodes  
                    peptideIDs.clear();
                    List<Peptide> queryList = new LinkedList<>();
                    List<Node> graphNodes = new LinkedList<>();
                    Peptide peptide;
                    Node node;
                    String id, seq;
                    for (ProteinSequence protein : queries) {
                        id = protein.getAccession().getID();
                        seq = protein.getSequenceAsString();

                        node = getOrAddGraphNode(graphModel, QUERY_LABEL, id);

                        peptide = new Peptide(node, mainGraph);
                        peptide.setAttributeValue(Peptide.ID, id);
                        peptide.setAttributeValue(Peptide.SEQ, seq);
                        peptide.setAttributeValue(Peptide.LENGHT, seq.length());
                        peptide.setBiojavaSeq(protein);

                        peptideIDs.add(peptide.getID());
                        queryList.add(peptide);
                        graphNodes.add(node);
                        tmpModel.addPeptide(peptide);
                    }
                    AllDescriptors alg = (AllDescriptors)new AllDescriptorsFactory().createAlgorithm();
                    alg.initAlgo(tmpWS, progressTicket);
                    alg.run();
                    alg.endAlgo();
                    tmpModel.getBridge().copyTo(newAttrModel, peptideIDs);
                    //graphModel.getGraphVisible().addAllNodes(graphNodes);
                }
            }
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

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EmbeddingQuerySeqAlg copy = (EmbeddingQuerySeqAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.
        if (fasta != null) {
            copy.fasta = String.copyValueOf(fasta.toCharArray());
        }
        return copy;
    }

}
