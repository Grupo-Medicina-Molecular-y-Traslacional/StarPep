/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
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
    private final NotifyDescriptor errorND;
    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    protected boolean stopRun;
    protected ProgressTicket ticket;
    private final NotifyDescriptor emptyMDs;
    private boolean firstStage, secondStage;
    static final DecimalFormat DF = new DecimalFormat("0.0##");

    //Algorithm
    protected FeatureSEFiltering filtering;
    protected FeatureSubsetOptimization subsetOptimization;

    public FilteringSubsetOptimization(FilteringSubsetOptimizationFactory factory) {
        this.factory = factory;

        filtering = (FeatureSEFiltering) (new FeatureSEFilteringFactory()).createAlgorithm();
        subsetOptimization = (FeatureSubsetOptimization) (new FeatureSubsetOptimizationFactory()).createAlgorithm();
        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(FilteringSubsetOptimization.class, "FilteringSubsetOptimization.errorND"), NotifyDescriptor.ERROR_MESSAGE);
        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(FilteringSubsetOptimizationFactory.class, "FeatureSubsetOptimization.emptyMDs.info"), NotifyDescriptor.ERROR_MESSAGE);

        firstStage = true;
        secondStage = true;
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
        filtering.cancel();
        subsetOptimization.cancel();
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
                if (!filtering.isValid()) {
                    DialogDisplayer.getDefault().notify(errorND);
                    return;
                }
                
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
                if (firstStage) {
                    execute(filtering);
                }

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

    public static void printTop5Buttom3(MolecularDescriptor[] rankedFeatures, Workspace workspace) {
        //Print top 5 bottom 3
        FeatureSEFiltering.pc.reportMsg("Top 5", workspace);
        int top5 = 0;
        for (int i = 0; i < rankedFeatures.length && top5 < 5; i++) {
            if (rankedFeatures[i] != null) {
                FeatureSEFiltering.pc.reportMsg(rankedFeatures[i].getDisplayName() + " - score: " + DF.format(rankedFeatures[i].getScore()), workspace);
                top5++;
            }
        }
        FeatureSEFiltering.pc.reportMsg("...", workspace);
        FeatureSEFiltering.pc.reportMsg("Bottom 3", workspace);
        Stack<MolecularDescriptor> stack = new Stack<>();
        int bottom3 = 0;
        for (int i = rankedFeatures.length - 1; i >= 0 && bottom3 < 3; i--) {
            if (rankedFeatures[i] != null) {
                stack.push(rankedFeatures[i]);
                bottom3++;
            }
        }
        MolecularDescriptor descriptor;
        while (!stack.isEmpty()) {
            descriptor = stack.pop();
            FeatureSEFiltering.pc.reportMsg(descriptor.getDisplayName() + " - score: " + DF.format(descriptor.getScore()), workspace);
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
