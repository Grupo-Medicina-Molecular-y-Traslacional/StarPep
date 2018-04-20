/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.chemspace.spi.impl.AlignmentBasedSimilarity;
import org.bapedis.chemspace.spi.impl.AlignmentBasedSimilarityFactory;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author loge
 */
public class SSNEmbedder extends AbstractEmbedder implements NetworkEmbedder{
    private AlignmentBasedSimilarity similarityMeasure;
    private SequenceAlignmentModel alignmentModel;
    private SimilarityMatrix similarityMatrix;
    private float similarityThreshold;
    private NetworkType networkType;
    private CompressedModel compressedModel;
    
    public SSNEmbedder(AlgorithmFactory factory) {
        super(factory);
        alignmentModel = new SequenceAlignmentModel();
        similarityMeasure = (AlignmentBasedSimilarity)new AlignmentBasedSimilarityFactory().createAlgorithm();
        similarityThreshold = 0.7f;
        networkType = NetworkType.FULL;
        compressedModel = new CompressedModel();        
    }
    
    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    @Override
    public NetworkType getNetworkType() {
        return networkType;
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
    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }
    
}
