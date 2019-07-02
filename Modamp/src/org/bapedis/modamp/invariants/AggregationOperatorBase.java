/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.invariants;

import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.impl.AbstractMD;

/**
 *
 * @author Cesar
 */
final public class AggregationOperatorBase extends AggregationOperator
{
    @Override
    public void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, boolean applyMeans, List<String> operatorsList )
    {
        
    }    
}
