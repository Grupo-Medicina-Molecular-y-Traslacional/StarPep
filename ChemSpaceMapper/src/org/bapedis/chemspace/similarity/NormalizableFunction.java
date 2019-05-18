/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author Loge
 */
public abstract class NormalizableFunction extends AbstractSimCoefficient {

    protected int normalizationIndex;
    
    public NormalizableFunction(AlgorithmFactory factory) {
        super(factory);
        normalizationIndex = 1;
    }

    protected double normalizedValue(Peptide peptide, MolecularDescriptor attr) throws MolecularDescriptorNotFoundException {
        switch (normalizationIndex) {
            case 0:
                return Math.abs(attr.getNormalizedZscoreValue(peptide));
            case 1:
                return attr.getNormalizedMinMaxValue(peptide);
        }
        throw new IllegalArgumentException("Unknown value for normalization index: " + normalizationIndex);
    }     
        
    
}
