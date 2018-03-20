/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.AllDescriptors;
import org.bapedis.core.spi.algo.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.algo.impl.FeatureSelectionAlgo;
import org.bapedis.core.spi.algo.impl.FeatureSelectionFactory;
import org.bapedis.core.spi.algo.impl.SequenceClustering;
import org.bapedis.core.spi.algo.impl.SequenceClusteringFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;

/**
 *
 * @author loge
 */
public class MapperAlgorithm implements Algorithm {

    public static final String RUNNING = "running";

    private final MapperAlgorithmFactory factory;

    protected Workspace workspace;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun, running;

    // Algorithms
    private final SequenceClustering seqClustering;    
    private final AllDescriptors featureExtraction;
    private final FeatureSelectionAlgo featureSelection;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        running = false;

        // Algorithms
        seqClustering = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        featureExtraction = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();        
        featureSelection = (FeatureSelectionAlgo) new FeatureSelectionFactory().createAlgorithm();
                
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {

    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        graphModel = null;
        graph = null;
        ticket = null;

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
        if (attrModel != null) {
        }

    }

}
