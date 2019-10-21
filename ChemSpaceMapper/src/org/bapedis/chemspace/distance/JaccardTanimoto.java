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
public class JaccardTanimoto extends AbstractDistance {

    public JaccardTanimoto(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    double compute(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double ab = 0.0;
        double a2 = 0.0;
        double b2 = 0.0;
        double val1, val2;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            ab += val1 * val2;
            a2 += val1 * val1;
            b2 += val2 * val2;
        }
        double sc = ab / (a2 + b2 - ab);
        sc = Math.round(sc * 100.0);
        sc /= 100.0;
        assert sc >= 0 && sc <= 1 : "sc: " + sc;
        return 1.0 - sc;
    }

}
