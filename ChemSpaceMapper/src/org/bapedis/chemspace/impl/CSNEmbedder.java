/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
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
public class CSNEmbedder extends DescriptorBasedEmbedder implements NetworkEmbedder {
    public static final int MAX_NODES=1000;
    public static final int MAX_EDGES=100000;
    
    private static final ForkJoinPool fjPool = new ForkJoinPool();   
    private SimilarityMatrixBuilder task;
    private AtomicBoolean atomicRun;
    private SimilarityMeasure simMeasure;
    private SimilarityMatrix similarityMatrix;
    private float similarityThreshold;
    private NetworkType networkType;
    private CompressedModel compressedModel;

    public CSNEmbedder(CSNEmbedderFactory factory) {
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

    @Override
    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    @Override
    public void setSimilarityThreshold(float similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }        

    @Override
    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

    @Override
    public NetworkType getNetworkType() {
        return networkType;
    }

    @Override
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }        

    @Override
    public CompressedModel getCompressedModel() {
        return compressedModel;
    }

    @Override
    public void setCompressedModel(CompressedModel compressedModel) {
        this.compressedModel = compressedModel;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        similarityMatrix = null;
        atomicRun = new AtomicBoolean(stopRun);
    }

    @Override
    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
        //Set features to similarity measure
        simMeasure.setMolecularFeatures(features);

        // Setup Similarity Matrix Builder
        task = new SimilarityMatrixBuilder(peptides);
        task.setContext(simMeasure, ticket, atomicRun);
        int workunits = task.getWorkUnits();
        ticket.switchToDeterminate(workunits);
        
        // Compute new similarity matrix        
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
    
    private  void createFullNetwork(){
        Peptide[] peptides = similarityMatrix.getPeptides();
        NetworkEmbedder.clearGraph(graphModel);   
        Edge graphEdge;
        Float score;
        String id;
        for (int i = 0; i < peptides.length - 1 && !stopRun; i++) {
            for (int j = i + 1; j < peptides.length && !stopRun; j++) {
                score = similarityMatrix.getValue(peptides[i], peptides[j]);
                if (score != null && score >= similarityThreshold) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        id = String.format("%s-%s", peptides[i].getId(), peptides[j].getId());
                        graphEdge = NetworkEmbedder.createGraphEdge(graphModel, id, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
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

    @Override
    public void endAlgo() {
        super.endAlgo();
        if (task != null)
        task.setContext(null, null, null);
        if (stopRun){ // Cancelled
            similarityMatrix = null;
        }
        task = null;
        atomicRun = null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        CSNEmbedder copy = (CSNEmbedder) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.compressedModel = (CompressedModel)compressedModel.clone();
        return copy;
    }
        
    @Override
    public boolean cancel() {
        super.cancel();
        atomicRun.set(stopRun);
        return stopRun;
    }             
}
