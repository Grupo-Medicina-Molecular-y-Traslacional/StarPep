/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.invariants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.openide.util.Exceptions;

/**
 *
 * @author Cesar
 */
public class AggregationOperators 
{
    private static final String[] METHODS = { "variance", "skewness", "kurtosis", "standardDeviation", "variationCoefficient", "range" };
    
    private static final String[] ACRONYM = { "V", "S", "K", "SD", "VC", "RA" };
    
    public static void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, AggregationOperators operators )
    {
        Method[] declaredMethods = operators.getClass().getMethods();
        
        for ( int i = 0; i < METHODS.length; i++ )
        {        
            for ( Method method : declaredMethods ) 
            {
                if ( method.getName().equals( METHODS[i] ) ) 
                {
                    try 
                    {
                        double val = (double) method.invoke( operators, new Object[]{ lovis } );
                        peptide.setAttributeValue( md.getOrAddAttribute( ACRONYM[i] + "-" + keyAttr,
                                                                         ACRONYM[i] + "-" + keyAttr, Double.class, 0d ), val );
                    }
                    catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException ex )
                    {
                        Exceptions.printStackTrace( ex );
                    }
                }
            }
        }
    }    
    
    public double variance( double[] lovis ) 
    {
        int longitud = lovis.length;
        double sum = 0;
        int nZeros = 0;
        
        if (longitud <= 1) 
        {
            return 0;
        }
        
        double arithMean = arithmeticMean( lovis );
        
        for (int i = 0; i < longitud; i++) 
        {
            if ( lovis[i] == 0 ) 
            {
                nZeros++;
            }
            else 
            {
                sum = sum + Math.pow( lovis[i] - arithMean, 2 );
            }
        }
        
        if ( ( longitud - nZeros ) == 0 ) //all lovis are zeros
        {
            return 0;
        }
        
        return sum / (longitud - 1);
    }
    
    public double skewness( double[] lovis ) 
    {
        if ( lovis.length < 3 ) 
        {
            return 0;
        }
        
        double tcm_std = thirdCentralMoment( lovis );
        double result = ( ( lovis.length * tcm_std) / ( ( lovis.length - 1 ) * ( lovis.length - 2 ) ) );
        
        return result;
    }
    
    public double kurtosis( double[] lovis ) 
    {
        int n = lovis.length;        
        if (n < 4) 
        {
            return 0;
        }
        
        double M2 = 0, M4 = 0;
        double arithmeticMean = arithmeticMean( lovis );
        
        for (int i = 0; i < lovis.length; i++) 
        {
            M2 = M2 + Math.pow( lovis[i] - arithmeticMean, 2 );
            M4 = M4 + Math.pow( lovis[i] - arithmeticMean, 4 );
        }
        
        double stdDev4 = Math.pow( standardDeviation( lovis ), 4 );
        
        if (stdDev4 == 0) 
        {
            return 0;
        }
        
        return (n * (n + 1) * M4 - 3 * M2 * M2 * (n - 1))
                / ((n - 1) * (n - 2) * (n - 3) * stdDev4);
    }
    
    public double standardDeviation( double[] lovis ) 
    {
        return Math.sqrt( variance( lovis ) );
    }
    
    public double variationCoefficient( double[] lovis ) 
    {
        int longitud = lovis.length;
        if (longitud <= 0) 
        {
            return 0;
        }
        
        double ArithmeticMean  = arithmeticMean( lovis );        
        if (   ArithmeticMean == 0 ) 
        {
            return 0;
        }
        
        return standardDeviation( lovis ) / ArithmeticMean;
    }
    
    public double range( double[] lovis ) 
    {
        return XMax( lovis ) - XMin( lovis );
    }
    
    private double thirdCentralMoment( double[] lovis ) 
    {
        double sum = 0;        
        double arithMean = arithmeticMean( lovis );        
        double stdDev = standardDeviation( lovis );
        
        if (stdDev == 0) 
        {
            return 0;
        }
        
        for (int i = 0; i < lovis.length; i++) 
        {
            sum = sum + Math.pow( ( lovis[i] - arithMean ) / stdDev, 3 );
        }
        
        return sum;
    }
    
    private double arithmeticMean( double[] lovis ) 
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
    
    private double XMax( double[] lovis ) 
    {
        if ( lovis.length <= 0 ) 
        {
            return 0;
        }
        
        Double max = lovis[0];
        for ( int i = 1; i < lovis.length; i++ ) 
        {
            max = Math.max( max, lovis[i] );
        }
        
        return max;
    }
    
    private double XMin( double[] lovis )
    {
        if ( lovis.length <= 0 ) 
        {
            return 0;
        }
        
        Double min = lovis[0];
        for ( int i = 1; i < lovis.length; i++ ) 
        {
            min = Math.min( min, lovis[i] );
        }
        
        return min;
    }
}
