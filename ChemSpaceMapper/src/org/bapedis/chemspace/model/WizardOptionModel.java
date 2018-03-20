/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

/**
 *
 * @author loge
 */
public class WizardOptionModel {

    public enum InputSequenceOption{AVAILABLE, NEW};
    public enum MolecularDescriptorOption{AVAILABLE, NEW};
    public enum NormalizationOption{Z_SCORE, MIN_MAX};
    public boolean featureFiltering;
        
    protected static final String[] normalizationText = new String[]{"Z-score", "Min-max"};
    
    protected InputSequenceOption seqOption;
    protected MolecularDescriptorOption mdOption;
    protected NormalizationOption normOption;

    public WizardOptionModel() {
        seqOption = InputSequenceOption.AVAILABLE;
        mdOption = MolecularDescriptorOption.NEW;
        normOption = NormalizationOption.MIN_MAX;
        featureFiltering = true;
    }

    public boolean isFeatureFiltering() {
        return featureFiltering;
    }

    public void setFeatureFiltering(boolean featureFiltering) {
        this.featureFiltering = featureFiltering;
    }

    public InputSequenceOption getInputSequenceOption() {
        return seqOption;
    }

    public void setInputSequenceOption(InputSequenceOption seqOption) {
        this.seqOption = seqOption;
    }

    public MolecularDescriptorOption getMolecularDescriptorOption() {
        return mdOption;
    }

    public void setMolecularDescriptorOption(MolecularDescriptorOption mdOption) {
        this.mdOption = mdOption;
    }

    public NormalizationOption getNormalizationOption() {
        return normOption;
    }

    public void setNormalizationOption(NormalizationOption normOption) {
        this.normOption = normOption;
    }

    

}
