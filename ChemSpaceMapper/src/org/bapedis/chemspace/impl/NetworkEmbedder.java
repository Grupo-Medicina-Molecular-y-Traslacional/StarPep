/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ForkJoinPool;
import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.chemspace.spi.SimilarityMeasure;
import org.bapedis.chemspace.spi.impl.TanimotoCoefficientFactory;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class NetworkEmbedder extends AbstractEmbedder {
    public static final int MAX_NODES=1000;
    public static final int MAX_EDGES=100000;
    
    private static final ForkJoinPool fjPool = new ForkJoinPool();    
    private SimilarityMeasure simMeasure;
    private SimilarityMatrix similarityMatrix;
    private float similarityThreshold;
    private NetworkType networkType;
    private CompressedModel compressedModel;

    public NetworkEmbedder(NetworkEmbedderFactory factory) {
        super(factory);
        simMeasure = new TanimotoCoefficientFactory().createAlgorithm();
        similarityThreshold = 0.7f;
        networkType = NetworkType.FULL;
        compressedModel = new CompressedModel();
    }

    public SimilarityMeasure getSimMeasure() {
        return simMeasure;
    }

    public void setSimMeasure(SimilarityMeasure simMeasure) {
        this.simMeasure = simMeasure;
    }

    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(float similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }        

    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }        

    public CompressedModel getCompressedModel() {
        return compressedModel;
    }

    public void setCompressedModel(CompressedModel compressedModel) {
        this.compressedModel = compressedModel;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        similarityMatrix = null;
    }

    @Override
    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
        //Set features to similarity measure
        simMeasure.setMolecularFeatures(features);

        // Setup Similarity Matrix Builder
        SimilarityMatrixkBuilder.setStopRun(stopRun);
        SimilarityMatrixkBuilder.setContext(peptides, simMeasure, ticket);

        // Compute new similarity matrix        
        SimilarityMatrixkBuilder task = new SimilarityMatrixkBuilder();
        int workunits = task.getWorkUnits();
        ticket.switchToDeterminate(workunits);
        fjPool.invoke(task);
        task.join();
        similarityMatrix = task.getSimilarityMatrix(); 
        
        switch(networkType){
            case FULL:
                createFullNetwork();
                break;
            case COMPRESSED:
                break;
        }
    }     
    
    private void createFullNetwork(){
        Peptide[] peptides = similarityMatrix.getPeptides();
        clearGraph(graphModel);   
        Edge graphEdge;
        Float score;
        String id;
        for (int i = 0; i < peptides.length - 1 && !stopRun; i++) {
            for (int j = i + 1; j < peptides.length && !stopRun; j++) {
                score = similarityMatrix.getValue(peptides[i], peptides[j]);
                if (score != null && score >= similarityThreshold) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        id = String.format("%s-%s", peptides[i].getId(), peptides[j].getId());
                        graphEdge = createGraphEdge(graphModel, id, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
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
    
    public static void clearGraph(GraphModel graphModel) {
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

    public static Edge createGraphEdge(GraphModel graphModel, String id, Node node1, Node node2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        
        // Create Edge
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

    @Override
    public void endAlgo() {
        super.endAlgo();
        SimilarityMatrixkBuilder.setContext(null, null, null);
        if (stopRun) { // Cancelled
            similarityMatrix = null;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        NetworkEmbedder copy = (NetworkEmbedder) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.compressedModel = (CompressedModel)compressedModel.clone();
        return copy;
    }
        
    @Override
    public boolean cancel() {
        super.cancel();
        SimilarityMatrixkBuilder.setStopRun(stopRun);
        return stopRun;
    }             
}
