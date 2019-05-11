/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.bapedis.chemspace.impl.MapperAlgorithm.pc;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.spi.SimilarityCoefficient;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NetworkEmbedderAlg implements Algorithm, Cloneable {

    public static final int MAX_NODES = 1000;
    public static final int MAX_EDGES = 100000;

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected final NotifyDescriptor notEnoughFeatures;

    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun;
    private SimilarityMatrix similarityMatrix;
    private AtomicBoolean atomicRun;
    private SimilarityCoefficient simCoefficient;
    private float similarityThreshold;
    private NetworkType networkType;

    public NetworkEmbedderAlg(AlgorithmFactory factory) {
        this.factory = factory;
        networkType = NetworkType.FULL;
        similarityThreshold = 0.7f;
        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.notEnoughHTML"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public SimilarityCoefficient getSimCoefficient() {
        return simCoefficient;
    }

    public void setSimCoefficient(SimilarityCoefficient simCoefficient) {
        this.simCoefficient = simCoefficient;
    }        

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(float similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        stopRun = false;
        this.workspace = workspace;
        this.ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            graphModel = pc.getGraphModel(workspace);
            graph = graphModel.getGraphVisible();
        }
        atomicRun = new AtomicBoolean(stopRun);
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        graphModel = null;
        graph = null;
        atomicRun = null;
        if (stopRun) { // Cancelled
            similarityMatrix = null;
        }
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        atomicRun.set(stopRun);
        return stopRun;
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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void run() {
        if (attrModel != null) {
            List<Peptide> peptides = attrModel.getPeptides();

            // Setup Similarity Matrix Builder    
            
            //Set features to similarity measure
            //simMeasure.setMolecularFeatures(workspace, features);
        
            SimilarityMatrixBuilder task = new SimilarityMatrixBuilder(peptides.toArray(new Peptide[0]));
            task.setContext(simCoefficient, ticket, atomicRun);
            int workunits = task.getWorkUnits();
            ticket.switchToDeterminate(workunits);

            // Compute new similarity matrix        
            fjPool.invoke(task);
            task.join();
            similarityMatrix = task.getSimilarityMatrix();

            // Populate feature list 
            Set<String> descriptorKeys = attrModel.getMolecularDescriptorKeys();
            List<MolecularDescriptor> features = new LinkedList<>();
            for (String key : descriptorKeys) {
                for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                    features.add(desc);
                }
            }
            
            // Preprocessing and validate molecular features
            preprocessing(features, peptides);
            
            switch (getNetworkType()) {
                case FULL:
                    createFullNetwork(graphModel, ticket, atomicRun);
                    break;
                case HSP:
                    createHSPNetwork(graphModel, ticket, atomicRun);
                    break;
            }
            //embed(peptides.toArray(new Peptide[0]), features.toArray(new MolecularDescriptor[0]));
        }
    }
    
    private void preprocessing(List<MolecularDescriptor> features, List<Peptide> peptides) {
        // Check feature list size
        if (features.size() < ProjectManager.MIN_AVAILABLE_FEATURES) {
            DialogDisplayer.getDefault().notify(notEnoughFeatures);
            pc.reportError(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.notEnough"), workspace);
            cancel();
        }

        // try/catch for molecular not found exception handling
        try {
            // Preprocessing of feature list. Compute max, min, mean and std
            for (MolecularDescriptor attr : features) {
                attr.resetSummaryStats(peptides);
            }

            // Validate molecular features
            for (MolecularDescriptor attr : features) {
                if (attr.getMax() == attr.getMin()) {
                    NotifyDescriptor invalidFeature = new NotifyDescriptor.Message(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.invalidFeatureHTML", attr.getDisplayName()), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(invalidFeature);
                    pc.reportError(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.invalidFeature", attr.getDisplayName()), workspace);
                    cancel();
                }
            }
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorND());
            pc.reportError(ex.getMessage(), workspace);
            cancel();
        }
    } 
    
    
    

    protected void createFullNetwork(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean stopRun) {
        clearSimilarityEdges(graphModel);
        Peptide[] peptides = similarityMatrix.getPeptides();
        Edge graphEdge;
        Float score;
        ticket.switchToDeterminate(peptides.length - 1);
        for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                score = getSimilarityMatrix().getValue(peptides[i], peptides[j]);
                if (score != null && score >= similarityThreshold) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        graphEdge = createSimilarityEdge(graphModel, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
                        graph.writeLock();
                        try {
                            graph.addEdge(graphEdge);
                        } finally {
                            graph.writeUnlock();
                        }
                    }
                }
            }
            ticket.progress();
        }
    }

    protected void createHSPNetwork(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean stopRun) {
        clearSimilarityEdges(graphModel);
        Peptide[] peptides = similarityMatrix.getPeptides();

        Node node1, node2;
        Edge graphEdge;
        Float score;
        CandidatePeptide[] candidates = new CandidatePeptide[peptides.length];
        Peptide closestPeptide;
        double dist;
        int cursor;
        for (int i = 0; i < peptides.length; i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = 0; j < peptides.length; j++) {
                if (i != j) {
                    score = similarityMatrix.getValue(peptides[i], peptides[j]);
                    dist = score == null ? Double.MAX_VALUE : 1 - score;
                } else {
                    dist = 0;
                }
                candidates[j] = new CandidatePeptide(dist, peptides[j]);
            }

            Arrays.parallelSort(candidates);
            cursor = 0;
            while (cursor < candidates.length) {
                if (candidates[cursor] != null && candidates[cursor].getDistance() > 0 && candidates[cursor].getDistance() < Double.MAX_VALUE) {
                    //Create edge to the closest peptide
                    closestPeptide = candidates[cursor].getPeptide();
                    node2 = closestPeptide.getGraphNode();
                    if (graph.contains(node1) && graph.contains(node2)
                            && graph.getEdge(node1, node2) == null && graph.getEdge(node2, node1) == null) {
                        score = similarityMatrix.getValue(peptides[i], closestPeptide);
                        graphEdge = createSimilarityEdge(graphModel, node1, node2, score);
                        graph.writeLock();
                        try {
                            graph.addEdge(graphEdge);
                        } finally {
                            graph.writeUnlock();
                        }
                    }
                    // ignore elements in the forbidden area
                    for (int k = cursor + 1; k < candidates.length; k++) {
                        if (candidates[k] != null && candidates[k].getDistance() > 0 && candidates[k].getDistance() < Double.MAX_VALUE) {
                            score = similarityMatrix.getValue(candidates[k].getPeptide(), closestPeptide);
                            if (score != null && score > similarityMatrix.getValue(candidates[k].getPeptide(), peptides[i])) {
                                candidates[k] = null;
                            }
                        }
                    }
                }
                cursor++;
            }
            ticket.progress();
        }
    }

    protected void clearSimilarityEdges(GraphModel graphModel) {
        // Remove all similarity edges..
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                mainGraph.clearEdges(node, relType);
            }
        } finally {
            mainGraph.writeUnlock();
        }
    }

    protected Edge createSimilarityEdge(GraphModel graphModel, Node node1, Node node2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        // Create Edge
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            mainGraph.addEdge(graphEdge);
            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);
        } finally {
            mainGraph.writeUnlock();
        }

        return graphEdge;
    }
}

class CandidatePeptide implements Comparable<CandidatePeptide> {

    private final double distance;
    private final Peptide peptide;

    public CandidatePeptide(double distance, Peptide peptide) {
        this.distance = distance;
        this.peptide = peptide;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(CandidatePeptide o) {
        if (distance < o.distance) {
            return -1;
        }
        if (distance > o.distance) {
            return 1;
        }
        return 0;
    }

}
