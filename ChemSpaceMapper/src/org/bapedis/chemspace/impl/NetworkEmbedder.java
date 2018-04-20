/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilarityMatrix;

/**
 *
 * @author loge
 */
public interface NetworkEmbedder {

    public float getSimilarityThreshold();
    
    public void setSimilarityThreshold(float similarityThreshold);
            
    public void setNetworkType(NetworkType networkType);

    public NetworkType getNetworkType();

    public CompressedModel getCompressedModel();

    public void setCompressedModel(CompressedModel compressedModel);

    public SimilarityMatrix getSimilarityMatrix();
}
