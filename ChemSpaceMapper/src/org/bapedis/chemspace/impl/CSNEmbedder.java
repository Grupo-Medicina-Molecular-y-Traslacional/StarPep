/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.chemspace.spi.SimilarityMeasure;
import org.bapedis.chemspace.spi.impl.TanimotoCoefficientFactory;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class CSNEmbedder extends DescriptorBasedEmbedder implements NetworkEmbedder {
    public static final int MAX_NODES=1000;
    public static final int MAX_EDGES=100000;
    
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
        networkType = NetworkType.NONE;
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
        SimilarityMatrixBuilder task = new SimilarityMatrixBuilder(peptides);
        task.setContext(simMeasure, ticket, atomicRun);
        int workunits = task.getWorkUnits();        
        ticket.switchToDeterminate(workunits);
        
        // Compute new similarity matrix        
        fjPool.invoke(task);
        task.join();
        similarityMatrix = task.getSimilarityMatrix(); 
        
        runEmbed(graphModel, ticket, atomicRun);
    }     
    
               

    @Override
    public void endAlgo() {
        super.endAlgo();

        if (stopRun){ // Cancelled
            similarityMatrix = null;
        }
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
