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
final public class Means extends AggregationOperatorDecorator
{    
    public Means( AggregationOperator operator ) 
    {
        super( operator );
    }
    
    @Override
    public void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, boolean applyMeans, List<String> operatorsList ) 
    {
        super.applyOperators( lovis, keyAttr, peptide, md, applyMeans, operatorsList ); //To change body of generated methods, choose Tools | Templates.
        
        double val;
        
        if ( applyMeans )
        {
            if ( operatorsList.contains( "P2" ) )
            {
                val = this.generalizedMean( lovis, 2 );
                peptide.setAttributeValue( md.getOrAddAttribute( "P2-" + keyAttr,
                                                                 "P2-" + keyAttr, Double.class, 0d ), val );
            }
            
            if ( applyMeans )
            {
                if ( operatorsList.contains( "P3" ) )
                {
                    val = this.generalizedMean( lovis, 3 );
                    peptide.setAttributeValue( md.getOrAddAttribute( "P3-" + keyAttr,
                                                                     "P3-" + keyAttr, Double.class, 0d ), val );
                }
                
                if ( operatorsList.contains( "HM" ) )
                {
                    val = this.generalizedMean( lovis, -1 );
                    peptide.setAttributeValue( md.getOrAddAttribute( "HM-" + keyAttr,
                                                                     "HM-" + keyAttr, Double.class, 0d ), val );
                }
            }
        }
    }
    
    final public double generalizedMean( double[] lovis, int pot ) 
    {
        int  n = lovis.length, nZeros = 0;        
        if ( n == 0 ) 
        {
            return 0;
        }
        
        double value = 0;
        for ( int i = 0; i < n; i++ )
        {
            if ( lovis[i] < 0 && ( pot % 2 != 0 ) )
            {
                return Double.NaN;
            }
            else if ( lovis[i] == 0 ) 
            {
                nZeros++;
            }
            else 
            {
                value = value + Math.pow( lovis[i], pot );
            }
        }
        
        if ( ( n - nZeros ) == 0 ) // all lovis are zeros
        {
            return 0;
        }
        
        if ( !Double.isNaN( value ) )
        {
            switch ( pot ) 
            {
                case -1: // harmonic mean
                    value = ( n - nZeros ) / value;
                    break;
                case 2: // quadratic mean
                    value = Math.sqrt( value / ( n - nZeros ) );
                    break;
                case 3: // potential mean
                    value = Math.cbrt( value / ( n - nZeros ) );
                    break;
                default:
                    value = Double.NaN;
                    break;
            }
        }
        
        return value;
    }
}
