/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.LinkedHashSet;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetwork extends SimilarityNetworkAlgo {

    protected final Set<String> descriptorKeys;
    protected final boolean normalize;
    protected final NotifyDescriptor emptyKeys, notFound;
    protected int optionIndex;
    protected int metricIndex;
    protected int normalizationIndex;

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
        normalize = true;
        descriptorKeys = new LinkedHashSet<>();
        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
        notFound = new NotifyDescriptor.Message(NbBundle.getMessage(ChemicalSpaceNetwork.class, "ChemicalSpaceNetwork.key.notFound"), NotifyDescriptor.ERROR_MESSAGE);
        
        optionIndex = 0;
        metricIndex = 0;
        normalizationIndex = 0;
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
    public void initAlgo() {
        super.initAlgo();
//        if (selectedKeys.isEmpty()) {
//            DialogDisplayer.getDefault().notify(emptyKeys);
//            cancel();
//        } else {
//            for (String key : selectedKeys) {
//                if (!stopRun) {
//                    if (attrModel.hasMolecularDescriptors(key)) {
//                        Peptide[] peptides = attrModel.getPeptides();
//                        double val, max, min;
//                        for (PeptideAttribute attr : attrModel.getMolecularDescriptors(key)) {
//                            max = Double.MIN_VALUE;
//                            min = Double.MAX_VALUE;
//                            for (int i = 0; i < peptides.length && !stopRun; i++) {
//                                if (peptides[i].getAttributeValue(attr) != null) {
//                                    val = PeptideAttribute.convertToDouble(peptides[i].getAttributeValue(attr));
//                                    if (val < min) {
//                                        min = val;
//                                    }
//                                    if (val > max) {
//                                        max = val;
//                                    }
//                                } else {
//                                    DialogDisplayer.getDefault().notify(notFound);
//                                    cancel();
//                                }
//                            }
//                            attr.setMaxValue(max);
//                            attr.setMinValue(min);
//                        }
//                    } else {
//                        DialogDisplayer.getDefault().notify(notFound);
//                        cancel();
//                    }
//                }
//            }
//        }
    }

    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) {
        // Evaluates the continuous Tanimoto coefficient
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;
        double val1, val2;
//        for (String key : featureSelectionModel.getDescriptorKeys()) {
//            for (MolecularDescriptor descriptor : attrModel.getMolecularDescriptors(key)) {
//                val1 = normalize ? descriptor.normalize(peptide1.getAttributeValue(descriptor)) : PeptideAttribute.convertToDouble(peptide1.getAttributeValue(descriptor));
//                val2 = normalize ? descriptor.normalize(peptide2.getAttributeValue(descriptor)) : PeptideAttribute.convertToDouble(peptide2.getAttributeValue(descriptor));
//                ab += val1 * val2;
//                a2 += val1 * val1;
//                b2 += val2 * val2;
//            }
//        }
        return (float) ab / (float) (a2 + b2 - ab);
    }

}
