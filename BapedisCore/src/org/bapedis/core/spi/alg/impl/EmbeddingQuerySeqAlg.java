/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.FASTASEQ;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class EmbeddingQuerySeqAlg implements Algorithm, Cloneable {

    static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    static int DEFAULT_TOP_K_INDEX = 0;

    protected final EmbeddingQuerySeqFactory factory;
    protected LinkedHashMap<String, List<ProteinSequence>> selected, nonSelected;
    protected GraphModel graphModel;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    private List<Node> graphNodes;
    private AttributesModel newAttrModel;
    protected boolean stopRun;
    protected final GraphWindowController graphWC;
    protected final SequenceAlignmentModel alignmentModel;
    protected int topKIndex;
    private static AtomicInteger counter = new AtomicInteger(45120);

    public EmbeddingQuerySeqAlg(EmbeddingQuerySeqFactory factory) {
        this.factory = factory;
        selected = new LinkedHashMap<>();
        nonSelected = new LinkedHashMap<>();
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        alignmentModel = new SequenceAlignmentModel();
        alignmentModel.setPercentIdentity(30);
        topKIndex = DEFAULT_TOP_K_INDEX;
    }

    public int getTopKIndex() {
        return topKIndex;
    }

    public void setTopKIndex(int topKIndex) {
        this.topKIndex = topKIndex;
    }

    public void addDataSetFromFile(String ds, File inputFile) throws Exception {
        if (selected.containsKey(ds) || nonSelected.containsKey(ds)) {
            throw new Exception(NbBundle.getMessage(EmbeddingQuerySeqAlg.class, "EmbeddingAlgorithm.invalidDS", ds));
        } else {
            List<ProteinSequence> entries = FASTASEQ.load(inputFile);
            selected.put(ds, entries);
        }
    }

    public void remove(String ds) {
        moveFromTo(ds, selected, nonSelected);
    }

    private void moveFromTo(String ds, LinkedHashMap<String, List<ProteinSequence>> from,
            LinkedHashMap<String, List<ProteinSequence>> to) {
        if (!from.containsKey(ds)) {
            throw new IllegalArgumentException("Can not be removed: " + ds);
        }
        List<ProteinSequence> list = from.get(ds);
        from.remove(ds);
        to.put(ds, list);
    }

    public void recover(String ds) {
        moveFromTo(ds, nonSelected, selected);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        graphModel = pc.getGraphModel(workspace);
        stopRun = false;
        graphNodes = null;
    }

    @Override
    public void endAlgo() {
        if (newAttrModel != null && !stopRun) {
            // To refresh graph view
            GraphModel graphModel = pc.getGraphModel(workspace);
            Graph graph = graphModel.getGraphVisible();
            graph.clear();
            graphWC.refreshGraphView(workspace, graphNodes, null);

            final Workspace ws = workspace;
            final AttributesModel modelToRemove = pc.getAttributesModel(workspace);
            final AttributesModel modelToAdd = newAttrModel;
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
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
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        workspace = null;
        progressTicket = null;
        graphModel = null;
        newAttrModel = null;
        graphNodes = null;
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
        Graph mainGraph = graphModel.getGraph();
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        //Create new workspace
        newAttrModel = new AttributesModel(workspace);
        graphNodes = new LinkedList<>();

        // Target list of peptides
        List<Peptide> targetList = new LinkedList<>();
        if (attrModel != null) {
            //Target list
            for (Peptide peptide : attrModel.getPeptides()) {
                if (peptide.getGraphNode().getLabel().equals("Peptide")) {
                    targetList.add(peptide);
                }
            }

            //Add query peptides and graph nodes            
            List<Peptide> queryList = new LinkedList<>();
            Peptide peptide;
            Node node;
            String id, seq;
            for (Map.Entry<String, List<ProteinSequence>> entry : selected.entrySet()) {
                for (ProteinSequence protein : entry.getValue()) {
                    id = protein.getAccession().getID();
                    seq = protein.getSequenceAsString();

                    node = getOrAddGraphNode(graphModel, entry.getKey(), id);

                    peptide = new Peptide(node, mainGraph);
                    peptide.setAttributeValue(Peptide.ID, counter.getAndIncrement());
                    peptide.setAttributeValue(Peptide.SEQ, seq);
                    peptide.setAttributeValue(Peptide.LENGHT, seq.length());
                    peptide.setBiojavaSeq(protein);

                    queryList.add(peptide);
                }
            }

            if (!stopRun) {
                if (queryList.size() > 0) {
                    //Add query peptides to new attribute model
                    for (Peptide query : queryList) {
                        newAttrModel.addPeptide(query);
                        graphNodes.add(query.getGraphNode());
                    }
                } else {
                    for (Peptide query : targetList) {
                        newAttrModel.addPeptide(query);
                        graphNodes.add(query.getGraphNode());
                    }
                }

                //Removing non selected nodes
                for (Map.Entry<String, List<ProteinSequence>> entry : nonSelected.entrySet()) {
                    for (ProteinSequence protein : entry.getValue()) {
                        id = protein.getAccession().getID();
                        node = mainGraph.getNode(id);
                        if (node != null) {
                            mainGraph.removeNode(node);
                        }
                    }
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
    public Object clone() throws CloneNotSupportedException {
        EmbeddingQuerySeqAlg copy = (EmbeddingQuerySeqAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.selected = (LinkedHashMap<String, List<ProteinSequence>>) selected.clone();
        copy.nonSelected = (LinkedHashMap<String, List<ProteinSequence>>) nonSelected.clone();
        return copy;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true; 
    }

}
