/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = DistanceFunction.class, position = 10)
public class Manhattan extends AbstractDistance {

    public Manhattan() {
        super("Manhattan");
    }

    @Override
    public double distance(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double val1, val2;
        double sum = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            sum += Math.abs(val1-val2);
        }
        return sum;     
    }
    
}
