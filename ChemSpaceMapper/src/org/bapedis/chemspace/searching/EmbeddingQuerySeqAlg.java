/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import javax.swing.SwingUtilities;
import org.bapedis.chemspace.distance.AbstractDistance;
import static org.bapedis.chemspace.impl.MapperAlgorithm.INDEX_ATTR;
import org.bapedis.chemspace.util.WekaHeap;
import org.bapedis.chemspace.util.WekaHeapElement;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorException;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
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
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
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
    protected MD_OUTPUT_OPTION mdOption;
    private AlgorithmFactory distFactory;
    static final NotifyDescriptor ErrorWS = new NotifyDescriptor.Message(NbBundle.getMessage(EmbeddingQuerySeqAlg.class, "Newworkspace.exist"), NotifyDescriptor.ERROR_MESSAGE);
    static final NotifyDescriptor ErrorEmpty = new NotifyDescriptor.Message(NbBundle.getMessage(EmbeddingQuerySeqAlg.class, "EmbeddingAlgorithm.emptySeq"), NotifyDescriptor.ERROR_MESSAGE);
    private static final AtomicInteger COUNTER = new AtomicInteger(45120);
    private Color color;
    private int knn;
    private double threshold;
    private double maxDistance;

    public EmbeddingQuerySeqAlg(EmbeddingQuerySeqFactory factory) {
        this.factory = factory;
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        knn = -1;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getKnn() {
        return knn;
    }

    public void setKnn(int knn) {
        this.knn = knn;
    }

    @Override
    public void setFasta(String fasta) {
        this.fasta = fasta;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    @Override
    public String getFasta() {
        return fasta;
    }

    public double getMaxDistance() {
        return maxDistance;
    }

    public void setMaxDistance(double maxDistance) {
        this.maxDistance = maxDistance;
    }

    public AlgorithmFactory getDistFactory() {
        return distFactory;
    }

    public void setDistFactory(AlgorithmFactory distFactory) {
        this.distFactory = distFactory;
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
                List<String> targetIDs = new LinkedList<>();
                for (Peptide peptide : currentModel.getPeptides()) {
                    targetIDs.add(peptide.getID());
                }
                currentModel.getBridge().copyTo(newAttrModel, targetIDs);

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
                    pc.reportMsg("Loaded peptides: " + queries.size(), workspace);
                } catch (Exception ex) {
                    NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(nd);
                    cancel();
                }

                if (!stopRun && queries != null) {
                    GraphModel tmpGraphModel = pc.getGraphModel(tmpWS);
                    Graph tmpGraph = tmpGraphModel.getGraph();

                    //Add query peptides and graph nodes  
                    List<String> queryIDs = new LinkedList<>();

                    Peptide peptide;
                    Node node;
                    String id, seq;
                    for (ProteinSequence protein : queries) {
                        id = protein.getAccession().getID();
                        seq = protein.getSequenceAsString();

                        node = getOrAddGraphNode(tmpGraphModel, QUERY_LABEL, id);

                        peptide = new Peptide(node, tmpGraph);
                        peptide.setAttributeValue(Peptide.ID, id);
                        peptide.setAttributeValue(Peptide.SEQ, seq);
                        peptide.setAttributeValue(Peptide.LENGHT, seq.length());
                        peptide.setBiojavaSeq(protein);

                        queryIDs.add(peptide.getID());
                        tmpModel.addPeptide(peptide);
                    }
                    AllDescriptors alg = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
                    alg.setIncludeUseless(true);
                    alg.initAlgo(tmpWS, progressTicket);
                    alg.run();
                    alg.endAlgo();

                    // Update new attribute model...
                    Set<String> refkeys = newAttrModel.getMolecularDescriptorKeys();
                    Set<String> newKeys = tmpModel.getMolecularDescriptorKeys();
                    List<MolecularDescriptor> toRemove = new LinkedList<>();
                    for (String key : newKeys) {
                        if (refkeys.contains(key)) {
                            for (MolecularDescriptor md : tmpModel.getMolecularDescriptors(key)) {
                                if (!newAttrModel.getMolecularDescriptors(key).contains(md)) {
                                    toRemove.add(md);
                                }
                            }
                        } else {
                            for (MolecularDescriptor md : tmpModel.getMolecularDescriptors(key)) {
                                toRemove.add(md);
                            }
                        }
                    }
                    for (MolecularDescriptor md : toRemove) {
                        tmpModel.deleteAttribute(md);
                    }
                    tmpModel.getBridge().copyTo(newAttrModel, queryIDs);

                    // Load all descriptors
                    List<MolecularDescriptor> allFeatures = new LinkedList<>();
                    if (!stopRun) {
                        for (String key : newAttrModel.getMolecularDescriptorKeys()) {
                            for (MolecularDescriptor attr : newAttrModel.getMolecularDescriptors(key)) {
                                allFeatures.add(attr);
                            }
                        }
                    }

                    // Preprocessing all features. Computing max, min, mean and std            
                    if (!stopRun) {
                        pc.reportMsg("Preprocessing of features. Computing max, min, mean and std", workspace);
                        try {
                            MolecularDescriptor.preprocessing(allFeatures, newAttrModel.getPeptides());
                        } catch (MolecularDescriptorException ex) {
                            DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                            pc.reportError(ex.getMessage(), workspace);
                            cancel();
                        }
                    }

                    //Create descriptor matrix     
                    Peptide[] peptides = newAttrModel.getPeptides().toArray(new Peptide[0]);
                    double[][] descriptorMatrix = null;
                    if (!stopRun) {
                        pc.reportMsg("Descriptor matrix construction", workspace);
                        descriptorMatrix = new double[peptides.length][allFeatures.size()];
                        try {
                            int i, j;
                            i = 0;
                            for (Peptide p : peptides) {
                                j = 0;
                                for (MolecularDescriptor md : allFeatures) {
                                    descriptorMatrix[i][j] = normalizedValue(p, md);
                                    j++;
                                }
                                i++;
                            }
                        } catch (MolecularDescriptorNotFoundException ex) {
                            DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                            Exceptions.printStackTrace(ex);
                            cancel();
                        }
                    }

                    // Set peptide index attribute
                    int index = 0;
                    for (Peptide p : peptides) {
                        p.setAttributeValue(INDEX_ATTR, index++);
                    }

                    // Search KKN
                    Graph newVisGraph = pc.getGraphVisible(newWS);
                    GraphModel newGraphModel = pc.getGraphModel(newWS);
                    List<Node> graphNodes = new LinkedList<>();
                    List<Edge> graphEdges = new LinkedList<>();

                    int queryIndex = targetIDs.size();
                    AbstractDistance dist = (AbstractDistance) distFactory.createAlgorithm();
                    WekaHeap heap;
                    double distance;
                    int firstkNN;
                    int[] indices;
                    double[] distances;
                    Edge edge;
                    try {
                        for (int i = queryIndex; i < peptides.length && !stopRun; i++) {
                            heap = new WekaHeap(knn);
                            firstkNN = 0;
                            for (int j = 0; j < queryIndex && !stopRun; j++) {
                                dist.setContext(peptides[i], peptides[j], descriptorMatrix);
                                dist.run();
                                distance = dist.getDistance();
                                if (firstkNN < knn) {
                                    heap.put(j, distance);
                                    firstkNN++;
                                } else {
                                    WekaHeapElement temp = heap.peek();
                                    if (distance < temp.distance) {
                                        heap.putBySubstitute(j, distance);
                                    } else if (distance == temp.distance) {
                                        heap.putKthNearest(j, distance);
                                    }
                                }
                            }
                            indices = new int[heap.size() + heap.noOfKthNearest()];
                            distances = new double[heap.size() + heap.noOfKthNearest()];
                            int l = 1;
                            WekaHeapElement h;
                            while (heap.noOfKthNearest() > 0) {
                                h = heap.getKthNearest();
                                indices[indices.length - l] = h.index;
                                distances[indices.length - l] = h.distance;
                                l++;
                            }
                            while (heap.size() > 0) {
                                h = heap.get();
                                indices[indices.length - l] = h.index;
                                distances[indices.length - l] = h.distance;
                                l++;
                            }
                            for (int r = 0; r < indices.length; r++) {
                                edge = getOrAddGraphEdge(newGraphModel, peptides[i], peptides[indices[r]], distances[r]);
                                if (edge != null) {
                                    graphEdges.add(edge);
                                }
                            }
                        }
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    //Add nodes and edges
                    if (!stopRun) {
                        newVisGraph.addAllEdges(graphEdges);
                    }
                }
            }
        } else {
            DialogDisplayer.getDefault().notify(ErrorEmpty);
            cancel();
        }
    }

    protected double normalizedValue(Peptide peptide, MolecularDescriptor attr) throws MolecularDescriptorNotFoundException {
        switch (mdOption) {
            case Z_SCORE:
                return attr.getNormalizedZscoreValue(peptide);
            case MIN_MAX:
                return attr.getNormalizedMinMaxValue(peptide);
        }
        throw new IllegalArgumentException("Unknown value for normalization index: " + mdOption);
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
        graphNode.setColor(color);
        return graphNode;
    }

    protected Edge getOrAddGraphEdge(GraphModel graphModel, Peptide source, Peptide end, double distance) {
        // Create an edge between two nodes
        Graph mainGraph = graphModel.getGraph();
        Node node1 = source.getGraphNode();
        Node node2 = end.getGraphNode();
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        if (relType != -1) {
            Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
            graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

            //Set color
            graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
            graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
            graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
            graphEdge.setAlpha(0f);

            double similarity;
            if (distance <= maxDistance) {
                similarity = 1.0 - distance / maxDistance;
                graphEdge.setWeight(Math.round(similarity * 100.0) / 100.0);
            }else{
                graphEdge.setWeight(0);
            }
            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE, distance);

            mainGraph.addEdge(graphEdge);

            return graphEdge;
        }
        return null;
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

    public MD_OUTPUT_OPTION getMdOption() {
        return mdOption;
    }

    public void setMdOption(MD_OUTPUT_OPTION mdOption) {
        this.mdOption = mdOption;
    }

}
