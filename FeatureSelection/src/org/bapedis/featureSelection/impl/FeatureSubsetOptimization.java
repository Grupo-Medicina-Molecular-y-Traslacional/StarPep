/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.impl;

import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import static org.bapedis.featureSelection.impl.FeatureSEFiltering.pc;

/**
 *
 * @author Loge
 */
public class FeatureSubsetOptimization implements Algorithm{

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    
    protected final FeatureSubsetOptimizationFactory factory;

    public FeatureSubsetOptimization(FeatureSubsetOptimizationFactory factory) {
        this.factory = factory;
    }        
    
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        peptides = attrModel.getPeptides();
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        peptides = null;
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
        
    }
    
}
