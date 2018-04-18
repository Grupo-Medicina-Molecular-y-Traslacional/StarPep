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
import org.bapedis.core.task.ProgressTicket;

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
    public boolean cancel() {
        super.cancel();
        SimilarityMatrixkBuilder.setStopRun(stopRun);
        return stopRun;
    }       

}
