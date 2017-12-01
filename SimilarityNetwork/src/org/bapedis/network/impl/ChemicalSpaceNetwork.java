/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetwork extends SimilarityNetworkAlgo {

    public static final int MIN_AVAILABLE_FEATURES = 2;
    protected final Set<String> descriptorKeys;
    protected final NotifyDescriptor emptyKeys, uselessFeatureWarning, notEnoughFeatures;
    protected int optionIndex;
    protected static int AllOption = 0;
    protected static int CustomizeOption = 1;
    protected static final String[] similarityMetrics = new String[]{"Tanimoto Coefficient"};
    protected static final String[] normalization = new String[]{"Z-score", "Min-max"};
    protected int metricIndex;
    protected int normalizationIndex;
    protected final List<MolecularDescriptor> featureList;

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
        descriptorKeys = new LinkedHashSet<>();
        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.features.notEnough", MIN_AVAILABLE_FEATURES), NotifyDescriptor.ERROR_MESSAGE);
        uselessFeatureWarning = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.uselessFeature.warning"), NotifyDescriptor.WARNING_MESSAGE);

        optionIndex = AllOption;
        metricIndex = 0;
        normalizationIndex = 0;
        featureList = new LinkedList<>();
    }

    public Set<String> getDescriptorKeys() {
        return descriptorKeys;
    }

    public int getOptionIndex() {
        return optionIndex;
    }

    public void setOptionIndex(int optionIndex) {
        this.optionIndex = optionIndex;
    }

    public int getMetricIndex() {
        return metricIndex;
    }

    public void setMetricIndex(int metricIndex) {
        this.metricIndex = metricIndex;
    }

    public int getNormalizationIndex() {
        return normalizationIndex;
    }

    public void setNormalizationIndex(int normalizationIndex) {
        this.normalizationIndex = normalizationIndex;
    }

    @Override
    public void initAlgo(Workspace workspace) {
        super.initAlgo(workspace);
        featureList.clear();
        // Fill the feature list
        if (optionIndex == AllOption) {
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                    featureList.add(desc);
                }
            }
        } else if (optionIndex == CustomizeOption) {
            if (descriptorKeys.isEmpty()) {
                DialogDisplayer.getDefault().notify(emptyKeys);
                pc.reportError("There is no molecular descriptor selected", workspace);
                cancel();
            } else {
                for (String key : descriptorKeys) {
                    if (!attrModel.hasMolecularDescriptors(key)) {
                        NotifyDescriptor notFound = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.key.notFound", key), NotifyDescriptor.ERROR_MESSAGE);
                        DialogDisplayer.getDefault().notify(notFound);
                        pc.reportError("Value not found for molecular descriptor: " + key, workspace);
                        cancel();
                        break;
                    }
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        featureList.add(attr);
                    }
                }
            }
        } else {
            throw new IllegalStateException("Unknown value for option index: " + optionIndex);
        }
        
        //Preprocess feature list
        if (!stopRun) {
            List<Peptide> peptides = attrModel.getPeptides();
            try {
                // Compute max, min, mean and std
                for (MolecularDescriptor attr : featureList) {
                    attr.resetSummaryStats(peptides);
                }
                // Remove constant attributes
                List<MolecularDescriptor> toRemove = new LinkedList<>();
                for (MolecularDescriptor attr : featureList) {
                    if (attr.getMax() == attr.getMin()) {
                        toRemove.add(attr);
                    }
                }
                if (toRemove.size() > 0) {
                    DialogDisplayer.getDefault().notify(uselessFeatureWarning);
                    pc.reportMsg("Some molecular features remain constant for all peptides and they will be ignored.", workspace);
                }
                featureList.removeAll(toRemove);
                for (MolecularDescriptor descriptor : toRemove) {
                    pc.reportMsg("Ignored: " + descriptor.getDisplayName(), workspace);
                }

                //Check feature list size
                if (featureList.size() < MIN_AVAILABLE_FEATURES) {
                    DialogDisplayer.getDefault().notify(notEnoughFeatures);
                    pc.reportError("There is not enough number of available molecular features", workspace);
                    cancel();
                }
            } catch (MolecularDescriptorNotFoundException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorND());
                pc.reportError(ex.getMessage(), workspace);
                cancel();
            }
        }

        //Output details
        if (!stopRun) {
            Map<String, List<MolecularDescriptor>> byCategory = featureList.parallelStream()
                    .collect(Collectors.groupingBy(MolecularDescriptor::getCategory));

            int maxKeyLength = 0;
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                if (key.length() > maxKeyLength) {
                    maxKeyLength = key.length();
                }
            }
            StringBuilder msg;
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                msg = new StringBuilder(key);
                for (int i = key.length() + 1; i <= maxKeyLength; i++) {
                    msg.append(' ');
                }
                msg.append(" : "); 
                msg.append(byCategory.containsKey(key)? byCategory.get(key).size(): 0);     
                pc.reportMsg(msg.toString(), workspace);
            }
            pc.reportMsg("\nTotal of available molecular features: " + featureList.size(), workspace);
            
            pc.reportMsg("\nSimilarity Metric: " + similarityMetrics[metricIndex], workspace);
            pc.reportMsg("Normalization: " + normalization[normalizationIndex], workspace);
        }

    }

    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) {
        try {
            switch (metricIndex) {
                case 0:
                    return calculateTanimoto(peptide1, peptide2);
            }
            throw new IllegalStateException("Unknown value for metric index: " + metricIndex);
        } catch (MolecularDescriptorNotFoundException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private float calculateTanimoto(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        // Evaluates the continuous Tanimoto coefficient
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;
        double val1, val2;
        for (MolecularDescriptor descriptor : featureList) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            ab += val1 * val2;
            a2 += val1 * val1;
            b2 += val2 * val2;
        }
        return (float) ab / (float) (a2 + b2 - ab);
    }

    private double normalizedValue(Peptide peptide, MolecularDescriptor attr) throws MolecularDescriptorNotFoundException {
        switch (normalizationIndex) {
            case 0:
                return Math.abs(attr.getNormalizedZscoreValue(peptide));
            case 1:
                return attr.getNormalizedMinMaxValue(peptide);
        }
        throw new IllegalStateException("Unknown value for normalization index: " + normalizationIndex);
    }

}
