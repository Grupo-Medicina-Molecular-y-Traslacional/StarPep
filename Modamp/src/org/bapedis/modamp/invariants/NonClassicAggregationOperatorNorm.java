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
final public class NonClassicAggregationOperatorNorm extends NonClassicAggregationOperatorDecorator
{    
    public NonClassicAggregationOperatorNorm( NonClassicAggregationOperator operator ) 
    {
        super( operator );
    }
    
    @Override
    public void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, boolean applyMeans, List<String> operatorsList ) 
    {
        super.applyOperators( lovis, keyAttr, peptide, md, applyMeans, operatorsList );
        
        double val;
        if ( operatorsList.contains( "N1" ) )
        {
            val = MinkoskyNorm( lovis, 1 );
            peptide.setAttributeValue( md.getOrAddAttribute( keyAttr + "-N1",
                                                             keyAttr + "-N1", Double.class, 0d ), val );
        }
        
        if ( operatorsList.contains( "N2" ) )
        {
            val = MinkoskyNorm( lovis, 2 );
            peptide.setAttributeValue( md.getOrAddAttribute( keyAttr + "-N2",
                                                             keyAttr + "-N2", Double.class, 0d ), val );
        }
    }
    
    public double MinkoskyNorm( double[] lovis, int k ) 
    {
        double sum = 0;
        for ( int i = 0; i < lovis.length; i++ ) 
        {
            sum = sum + Math.pow( lovis[i], k );
        }
        
        if ( k == 1 ) 
        {
            return sum;
        } 
        else if ( k == 2 ) 
        {
            return Math.sqrt( sum );
        }
        
        return Double.NaN;
    }
}
