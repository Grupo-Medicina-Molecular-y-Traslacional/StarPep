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

public class Chebyshev extends AbstractDistance{

    public Chebyshev(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public double compute(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double val1, val2, d;
        double dist = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            d = Math.abs(val1-val2);
            if (dist < d){
                dist = d;
            }
        }
        return dist;           
    }
    
}
