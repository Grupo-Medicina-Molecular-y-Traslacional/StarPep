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
public class UnsupervisedFeatureSelection implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final UnsupervisedFeatureSelectionFactory factory;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    private final NotifyDescriptor emptyMDs;
    private boolean firstStage, secondStage;

    //Algorithm
    protected FeatureDiscretization preprocessing;
    protected FeatureSEFiltering filtering;
    protected FeatureSubsetOptimization subsetOptimization;

    public UnsupervisedFeatureSelection(UnsupervisedFeatureSelectionFactory factory) {
        this.factory = factory;

        preprocessing = (FeatureDiscretization) (new FeatureDiscretizationFactory()).createAlgorithm();
        filtering = (FeatureSEFiltering) (new FeatureSEFilteringFactory()).createAlgorithm();
        subsetOptimization = (FeatureSubsetOptimization) (new FeatureSubsetOptimizationFactory()).createAlgorithm();

        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(UnsupervisedFeatureSelection.class, "UnsupervisedFeatureSelection.emptyMDs.info"), NotifyDescriptor.ERROR_MESSAGE);

        firstStage = true;
        secondStage = true;
    }

    public FeatureDiscretization getPreprocessingAlg() {
        return preprocessing;
    }

    public FeatureSEFiltering getFilteringAlg() {
        return filtering;
    }

    public FeatureSubsetOptimization getSubsetOptimizationAlg() {
        return subsetOptimization;
    }

    public boolean isFirstStage() {
        return firstStage;
    }

    public void setFirstStage(boolean firstStage) {
        this.firstStage = firstStage;
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
        if (attrModel != null) {
            try {
                //----------Preprocessing all descriptors
                if (firstStage || secondStage) {
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

                    execute(preprocessing);
                }

                if (firstStage) {
                    execute(filtering);
                }

                if (secondStage) {
                    execute(subsetOptimization);
                }

            } catch (MolecularDescriptorNotFoundException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                pc.reportError(ex.getMessage(), workspace);
            }

        }
    }

    private void execute(Algorithm currentAlg) {
        if (!stopRun) {
            String taskName = NbBundle.getMessage(UnsupervisedFeatureSelection.class, "UnsupervisedFeatureSelection.workflow.taskName", currentAlg.getFactory().getName());
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
        UnsupervisedFeatureSelection copy = (UnsupervisedFeatureSelection) super.clone();
        copy.preprocessing = (FeatureDiscretization) this.preprocessing.clone();
        copy.filtering = (FeatureSEFiltering) this.filtering.clone();
        copy.subsetOptimization = (FeatureSubsetOptimization) this.subsetOptimization.clone();
        return copy;
    }

}
