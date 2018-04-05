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
    public static final String PROPERTY_NAME = "WOM";
    
    public enum RepresentationOption{CS2D, CSN};
    public enum MolecularDescriptorOption{AVAILABLE, NEW};
    public enum FeatureFiltering{YES, NO};
    public enum FeatureWeighting{YES, NO};
    public enum NormalizationOption{Z_SCORE, MIN_MAX};    
        
    protected static final String[] normalizationText = new String[]{"Z-score", "Min-max"};
    
    protected RepresentationOption repOption;
    protected MolecularDescriptorOption mdOption;
    protected FeatureFiltering ffOption;
    protected FeatureWeighting fwOption;
    protected NormalizationOption normOption;

    public WizardOptionModel() {
        repOption = RepresentationOption.CS2D;
        mdOption = MolecularDescriptorOption.NEW;
        normOption = NormalizationOption.MIN_MAX;
        ffOption = FeatureFiltering.YES;
        fwOption = FeatureWeighting.NO;
    }

    public RepresentationOption getRepresentationOption() {
        return repOption;
    }

    public void setRepresentationOption(RepresentationOption repOption) {
        this.repOption = repOption;
    }

    public MolecularDescriptorOption getMolecularDescriptorOption() {
        return mdOption;
    }

    public void setMolecularDescriptorOption(MolecularDescriptorOption mdOption) {
        this.mdOption = mdOption;
    }

    public FeatureFiltering getFeatureFilteringOption() {
        return ffOption;
    }

    public void setFeatureFilteringOption(FeatureFiltering ffOption) {
        this.ffOption = ffOption;
    }        

    public FeatureWeighting getFeatureWeightingOption() {
        return fwOption;
    }

    public void setFeatureWeightingOption(FeatureWeighting fwOption) {
        this.fwOption = fwOption;
    }        

    public NormalizationOption getNormalizationOption() {
        return normOption;
    }

    public void setNormalizationOption(NormalizationOption normOption) {
        this.normOption = normOption;
    }

    

}
