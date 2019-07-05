/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.FeatureDiscretizationTag;
import org.bapedis.core.spi.alg.FeatureSubsetOptimizationTag;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class TwoStageUnsupervisedSelection implements Algorithm, Cloneable {
    
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final TwoStageUnsupervisedSelectionFactory factory;
    
    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    protected boolean stopRun;
    protected ProgressTicket ticket;  
    
    //Algorithm
    protected final Algorithm preprocessing, filtering, subsetOptimization;

    public TwoStageUnsupervisedSelection(TwoStageUnsupervisedSelectionFactory factory) {
        this.factory = factory;
        
        preprocessing = ((FeatureDiscretizationTag)Lookup.getDefault().lookup(FeatureDiscretizationTag.class)).createAlgorithm();
        filtering = ((FeatureDiscretizationTag)Lookup.getDefault().lookup(FeatureDiscretizationTag.class)).createAlgorithm();
        subsetOptimization = ((FeatureSubsetOptimizationTag)Lookup.getDefault().lookup(FeatureSubsetOptimizationTag.class)).createAlgorithm();
    }

    public Algorithm getPreprocessingAlg() {
        return preprocessing;
    }

    public Algorithm getFilteringAlg() {
        return filtering;
    }

    public Algorithm getSubsetOptimizationAlg() {
        return subsetOptimization;
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
        execute(preprocessing);
        execute(filtering);
        execute(subsetOptimization);
    }
    
    private void execute(Algorithm currentAlg) {
        if (!stopRun) {
            String taskName = NbBundle.getMessage(TwoStageUnsupervisedSelection.class, "TwoStageUnsupervisedSelection.workflow.taskName", currentAlg.getFactory().getName());
            ticket.progress(taskName);
            ticket.switchToIndeterminate();
            pc.reportMsg(taskName, workspace);

            currentAlg.initAlgo(workspace, ticket);
            currentAlg.run();
            currentAlg.endAlgo();
        }
    }    
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        TwoStageUnsupervisedSelection copy = (TwoStageUnsupervisedSelection) super.clone();
        
        return copy;
    }    
    
}
