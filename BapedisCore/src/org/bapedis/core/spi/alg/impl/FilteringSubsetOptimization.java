/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class FilteringSubsetOptimization implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final FilteringSubsetOptimizationFactory factory;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    private final NotifyDescriptor emptyMDs;
    private boolean secondStage;

    //Algorithm
    protected FeatureSEFiltering filtering;
    protected FeatureSubsetOptimization subsetOptimization;
    private final boolean debug;

    public FilteringSubsetOptimization(FilteringSubsetOptimizationFactory factory) {
        this.factory = factory;

        filtering = (FeatureSEFiltering) (new FeatureSEFilteringFactory()).createAlgorithm();
        subsetOptimization = (FeatureSubsetOptimization) (new FeatureSubsetOptimizationFactory()).createAlgorithm();

        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(FilteringSubsetOptimizationFactory.class, "FeatureSubsetOptimization.emptyMDs.info"), NotifyDescriptor.ERROR_MESSAGE);

        secondStage = false;

        debug = true;
    }

    public FeatureSEFiltering getFilteringAlg() {
        return filtering;
    }

    public FeatureSubsetOptimization getSubsetOptimizationAlg() {
        return subsetOptimization;
    }

    public boolean isSecondStage() {
        return secondStage;
    }

    public void setSecondStage(boolean secondStage) {
        this.secondStage = secondStage;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        stopRun = false;

        filtering.setDebug(debug);
        subsetOptimization.setDebug(debug);
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
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
        if (attrModel != null && attrModel.getPeptides().size() > 0) {
            try {
                //----------Preprocessing all descriptors   
                List<MolecularDescriptor> allFeatures = new LinkedList<>();

                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        if (!stopRun) {
                            allFeatures.add(attr);
                            attr.resetSummaryStats(attrModel.getPeptides());
                        }
                    }
                }

                if (allFeatures.isEmpty()) {
                    DialogDisplayer.getDefault().notify(emptyMDs);
                    pc.reportError("There is not calculated molecular descriptors", workspace);
                    stopRun = true;
                }

                //First stage
                execute(filtering);

                //Second stage
                if (secondStage) {
                    execute(subsetOptimization);
                }

            } catch (MolecularDescriptorNotFoundException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                pc.reportError(ex.getMessage(), workspace);
            }

        }
    }

    protected void execute(Algorithm currentAlg) {
        if (!stopRun) {
            String taskName = NbBundle.getMessage(FilteringSubsetOptimization.class, "FilteringSubsetOptimization.workflow.taskName", currentAlg.getFactory().getName());
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
        FilteringSubsetOptimization copy = (FilteringSubsetOptimization) super.clone();
        copy.filtering = (FeatureSEFiltering) this.filtering.clone();
        copy.subsetOptimization = (FeatureSubsetOptimization) this.subsetOptimization.clone();
        return copy;
    }

}
