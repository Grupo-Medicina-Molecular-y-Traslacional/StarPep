/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.bapedis.chemspace.distance;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author crjacas, loge
 */
@ServiceProvider(service = DistanceFunction.class, position = 40)
public class Canberra extends AbstractDistance
{
    public Canberra()
    {
        super( "Canberra" );
    }
    

    @Override
    public double distance(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException {
        double val1, val2;
        double sum = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            sum += Math.abs(val1-val2) / (Math.abs(val1) + Math.abs(val2));
        }
        return sum;
    }
}
