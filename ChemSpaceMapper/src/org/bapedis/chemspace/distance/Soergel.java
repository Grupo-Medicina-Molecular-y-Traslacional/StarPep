/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.bapedis.chemspace.distance;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author crjacas, loge
 */

public class Soergel extends AbstractDistance
{
    public Soergel( AlgorithmFactory factory  )
    {
       super(factory);
    }

    @Override
    public double compute(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double val1, val2;
        double num = 0, den = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            num += Math.abs(val1-val2);
            den += Math.max(val1, val2);
        }
        return den == 0 ? 0 : Math.abs( num / den );
    }
}
