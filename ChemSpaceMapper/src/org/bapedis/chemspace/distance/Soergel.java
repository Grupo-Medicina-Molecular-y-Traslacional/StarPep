/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.bapedis.chemspace.distance;

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
    public double compute() {
        double val1, val2;
        double num = 0, den = 0;
        for (int j=0; j < descriptorMatrix[index1].length; j++) {
            val1 = descriptorMatrix[index1][j];
            val2 = descriptorMatrix[index2][j];
            
            num += Math.abs(val1-val2);
            den += Math.max(val1, val2);
        }
        return den == 0 ? 0 : Math.abs( num / den );
    }
}
