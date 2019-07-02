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
abstract public class AggregationOperatorDecorator extends AggregationOperator
{
    protected AggregationOperator operator;
    
    public AggregationOperatorDecorator( AggregationOperator operator )
    {
        this.operator = operator;
    }
    
    @Override
    public void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, boolean applyMeans, List<String> operatorsList ) 
    {
        if ( operator != null )
        {
            operator.applyOperators( lovis, keyAttr, peptide, md, applyMeans, operatorsList );
        }
    }
    
    final protected double arithmeticMean( double[] lovis ) 
    {
        double sum = 0;
        for (int i = 0; i < lovis.length; i++) 
        {
            sum += lovis[i];
        }
        
        if ( (lovis.length > 0) && (sum != 0) ) 
        {
            return sum / lovis.length;
        }
        
        return 0;
    }
}
