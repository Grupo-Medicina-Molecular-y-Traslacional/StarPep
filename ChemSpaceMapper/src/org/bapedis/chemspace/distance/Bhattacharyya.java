/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author Loge
 */
public class Bhattacharyya extends AbstractDistance {

    public Bhattacharyya(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    double compute(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double val1, val2;
        double sum = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);            
            sum += Math.pow( Math.sqrt( Math.abs( val1 ) ) - Math.sqrt( Math.abs( val2 ) ), 2 );
        }
        return Math.sqrt(sum);
    }
    
}