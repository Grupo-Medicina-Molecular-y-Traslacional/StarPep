/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
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
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.biojava.nbio.core.sequence.io.BufferedReaderBytesRead;
import org.biojava.nbio.core.sequence.io.GenericFastaHeaderParser;
import org.biojava.nbio.core.sequence.io.ProteinSequenceCreator;
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
public class EmbeddingAlgorithm implements Algorithm, Cloneable {

    static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final EmbeddingAlgorithmFactory factory;
    protected LinkedHashMap<String, List<ProteinSequence>> selected, nonSelected;
    protected GraphModel graphModel;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    private List<Node> graphNodes;
    private AttributesModel newAttrModel;
    protected boolean stopRun;
    protected final GraphWindowController graphWC;
    protected final SequenceAlignmentModel alignmentModel;
    private static AtomicInteger counter = new AtomicInteger(45120);

    public EmbeddingAlgorithm(EmbeddingAlgorithmFactory factory) {
        this.factory = factory;
        selected = new LinkedHashMap<>();
        nonSelected = new LinkedHashMap<>();
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        alignmentModel = new SequenceAlignmentModel();
        alignmentModel.setPercentIdentity(30);
    }

    public void addDataSetFromFile(String ds, File inputFile) throws Exception {
        if (selected.containsKey(ds) || nonSelected.containsKey(ds)) {
            throw new Exception(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.invalidDS", ds));
        } else {
            try {
                FileInputStream inStream = new FileInputStream(inputFile);
                InputStreamReader isr = new InputStreamReader(inStream);
                BufferedReaderBytesRead br = new BufferedReaderBytesRead(isr);
                GenericFastaHeaderParser headerParser = new GenericFastaHeaderParser<>();
                ProteinSequenceCreator sequenceCreator = new ProteinSequenceCreator(AminoAcidCompoundSet.getAminoAcidCompoundSet());
                List<ProteinSequence> entries = new LinkedList<>();
                String header = "";
                long sequenceIndex = 0;
                StringBuilder sb = new StringBuilder();
                long fileIndex = br.getBytesRead();
                String line = br.readLine();
                if (line.charAt(0) != '>') {
                    throw new IOException(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.invalidFirstLine"));
                }

                boolean keepGoing = true;
                do {
                    line = line.trim(); // nice to have but probably not needed
                    if (line.length() != 0) {
                        if (line.startsWith(">")) {//start of new fasta record
                            if (sb.length() > 0) {//i.e. if there is already a sequence before
                                //    logger.debug("Sequence index=" + sequenceIndex);                                
                                if (sb.length() > 100) {
                                    throw new Exception(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.invalidSeqLength", header));
                                }

                                try {
                                    @SuppressWarnings("unchecked")
                                    ProteinSequence sequence = (ProteinSequence) sequenceCreator.getSequence(sb.toString(), sequenceIndex);
                                    headerParser.parseHeader(header, sequence);
                                    entries.add(sequence);
                                } catch (CompoundNotFoundException e) {
                                    throw new CompoundNotFoundException(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.compoundNotFound", header, e.getMessage()));
                                }

                                sb.setLength(0); //this is faster, better memory utilization (same buffer)
                            }
                            header = line.substring(1);
                        } else if (line.startsWith(";")) {
                        } else {
                            //mark the start of the sequence with the fileIndex before the line was read
                            if (sb.length() == 0) {
                                sequenceIndex = fileIndex;
                            }
                            sb.append(line);
                        }
                    }
                    fileIndex = br.getBytesRead();
                    line = br.readLine();
                    if (line == null) {//i.e. EOF
                        String seq = sb.toString();
                        if (seq.length() == 0) {
                            pc.reportMsg("warning: Can't parse sequence. Got sequence of length 0!. Sequence index: " + sequenceIndex, pc.getCurrentWorkspace());
                            pc.reportMsg("header: " + header, pc.getCurrentWorkspace());
                        }
                        //    logger.debug("Sequence index=" + sequenceIndex + " " + fileIndex );
                        if (sb.length() > 100) {
                            throw new Exception(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.invalidSeqLength", header));
                        }

                        try {
                            @SuppressWarnings("unchecked")
                            ProteinSequence sequence = (ProteinSequence) sequenceCreator.getSequence(seq, sequenceIndex);
                            headerParser.parseHeader(header, sequence);
                            entries.add(sequence);
                        } catch (Exception e) {
                            throw new CompoundNotFoundException(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.compoundNotFound", header, e.getMessage()));
                        }
                        keepGoing = false;
                    }
                } while (keepGoing);

                selected.put(ds, entries);
            } catch (CompoundNotFoundException ex) {
                throw ex;
            } catch (Exception ex) {
                throw new Exception(NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.badInput", ex.getMessage()));
            }
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
    public boolean cancel() {
        stopRun = true;
        return true;
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
                    //Searching in target list                 
                    Peptide[] targets = targetList.toArray(new Peptide[0]);
                    // Stop if max rejects ocurred
                    double identityScore = 0.5;
                    double score;
                    int rejections = 0;
                    TreeSet<SequenceSearch.SequenceHit> hits;

                    for (Peptide query : queryList) {
                        if (!stopRun) {
                            // Sort by decreasing common words
                            Arrays.parallelSort(targets, new CommonKMersComparator(query.getSequence()));
                        }
                        hits = new TreeSet<>();
                        for (int i = 0; i < targets.length && !stopRun && rejections < SequenceSearch.MAX_REJECTS; i++) {
                            try {
                                score = PairwiseSequenceAlignment.computeSequenceIdentity(query.getBiojavaSeq(), targets[i].getBiojavaSeq(), alignmentModel);
                                if (score >= identityScore) {
                                    hits.add(new SequenceSearch.SequenceHit(targets[i], score));
                                } else {
                                    rejections++;
                                }
                            } catch (CompoundNotFoundException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }

                        if (!stopRun) {
                            SequenceSearch.SequenceHit hit;
                            for (Iterator<SequenceSearch.SequenceHit> it = hits.descendingIterator(); it.hasNext();) {
                                hit = it.next();
                                if (!newAttrModel.getPeptideMap().containsKey(hit.getPeptide().getId())) {
                                    newAttrModel.addPeptide(hit.getPeptide());
                                    graphNodes.add(hit.getPeptide().getGraphNode());
                                }
                            }
                        }
                    }

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
        EmbeddingAlgorithm copy = (EmbeddingAlgorithm) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.selected = (LinkedHashMap<String, List<ProteinSequence>>) selected.clone();
        copy.nonSelected = (LinkedHashMap<String, List<ProteinSequence>>) nonSelected.clone();
        return copy;
    }

}
