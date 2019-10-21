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
public class AngularSeparation extends AbstractDistance {

    public AngularSeparation(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    double compute(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double val1, val2;
        double num = 0;
        double den1 = 0, den2 = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            num += val1 * val2;
            den1 += val1 * val1;
            den2 += val2 * val2;
        }
        double sc = den1 == 0 || den2 == 0 ? 0 : (num / Math.sqrt(den1 * den2));
        sc = Math.round(sc * 100.0);
        sc /= 100.0;        
        assert sc >= 0 && sc <= 1: "sc: " + sc;
        return 1 - sc;
    }

}
