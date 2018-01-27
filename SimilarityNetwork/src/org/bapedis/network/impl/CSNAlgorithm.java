/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.network.model.SeqClusteringModel;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.AllDescriptors;
import org.bapedis.core.spi.algo.impl.AllDescriptorsFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.network.spi.SimilarityMeasure;

/**
 *
 * @author loge
 */
public class CSNAlgorithm implements Algorithm {
    private final SeqClusteringModel seqClustering;
    private final AllDescriptors descriptorAlgo;
    private final FeatureSelectionModel featureModel;
    private SimilarityMeasure simMeasure;

    public CSNAlgorithm() {
        seqClustering = new SeqClusteringModel();
        descriptorAlgo = (AllDescriptors)new AllDescriptorsFactory().createAlgorithm(); 
        featureModel = new FeatureSelectionModel(null);
    }

    public SeqClusteringModel getSeqClustering() {
        return seqClustering;
    }   

    public AllDescriptors getDescriptorAlgo() {
        return descriptorAlgo;
    }        

    public FeatureSelectionModel getFeatureModel() {
        return featureModel;
    } 

    public SimilarityMeasure getSimMeasure() {
        return simMeasure;
    }

    public void setSimMeasure(SimilarityMeasure simMeasure) {
        this.simMeasure = simMeasure;
    }        

    @Override
    public void initAlgo(Workspace workspace) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endAlgo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlgorithmFactory getFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
