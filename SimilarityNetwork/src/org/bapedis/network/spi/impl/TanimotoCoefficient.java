/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.spi.impl;

import java.util.List;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.network.spi.SimilarityMeasure;

/**
 *
 * @author loge
 */
public class TanimotoCoefficient implements SimilarityMeasure {
    protected List<MolecularDescriptor> featureList;
    protected int normalizationIndex = 1;
    
    @Override
    public void setMolecularDescriptors(List<MolecularDescriptor> featureList) {
        this.featureList = featureList;
    }
    
    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
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
