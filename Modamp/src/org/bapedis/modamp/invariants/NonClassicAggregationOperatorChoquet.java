/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.invariants;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.impl.AbstractMD;

/**
 *
 * @author Cesar
 */
final public class NonClassicAggregationOperatorChoquet extends NonClassicAggregationOperatorDecorator
{    
    public NonClassicAggregationOperatorChoquet( NonClassicAggregationOperator operator )
    {
        super( operator );
    }
    
    @Override
    public void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, boolean applyMeans, List<String> operatorsList )
    {
        super.applyOperators( lovis, keyAttr, peptide, md, applyMeans, operatorsList ); //To change body of generated methods, choose Tools | Templates.
        
        if ( applyMeans )
        {
            compute( lovis, keyAttr, peptide, md, operatorsList );
        }
    }
    
    private enum PARAMETER_NAMES 
    {
        ALFA_SINGLETON_METHOD, SINGLETON_METHOD, L_VALUE
    }
    
    private enum SINGLETON_METHODS 
    {
        AGGREGATED_OBJECTS_1, AGGREGATED_OBJECTS_2
    }
    
    private void compute( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, List<String> operatorsList )
    {        
        if ( operatorsList.contains( "CHOQUET" ) )
        {
            for ( String key : defaultChoquet )
            {
                String[] values = key.split( "//" );
                
                HashMap<NonClassicAggregationOperatorChoquet.PARAMETER_NAMES, Object> parameters = new HashMap<>();
                parameters.put(NonClassicAggregationOperatorChoquet.PARAMETER_NAMES.L_VALUE              , Float.parseFloat( values[0] ) );            
                parameters.put(NonClassicAggregationOperatorChoquet.PARAMETER_NAMES.SINGLETON_METHOD     , values[1]  );
                parameters.put(NonClassicAggregationOperatorChoquet.PARAMETER_NAMES.ALFA_SINGLETON_METHOD, Float.parseFloat( values[2] )    );
                
                String[] params2String = new String[]{ "" };
                double val = compute( lovis, parameters, params2String );
                peptide.setAttributeValue( md.getOrAddAttribute( "CHOQUET[" + params2String[0] + "]-" + keyAttr,
                                                                 "CHOQUET[" + params2String[0] + "]-" + keyAttr, Double.class, 0d ), val );
            }
        }
    }
    
    private double compute( double[] lovis, HashMap<PARAMETER_NAMES, Object> parameters, String[] outParams2String )
    {
        if ( lovis == null )
        {
            return Double.NaN;
        }
        
        return compute( lovis.length > 0 ? lovis.clone() : new double[0],
                        SINGLETON_METHODS.valueOf( (String) parameters.get( PARAMETER_NAMES.SINGLETON_METHOD ) ),
                        parameters, outParams2String );
    }
    
    private double compute( double[] lovis_, SINGLETON_METHODS method, HashMap<PARAMETER_NAMES, Object> parameters, String[] outParams2String )
    {
        if ( lovis_ == null || lovis_.length == 0 )
        {
            return Double.NaN;
        }
        
        double[] lovis   = new double[ lovis_.length ];        
        for ( int i = 0; i < lovis_.length; i++ )
        {
            if ( ( lovis[ i ] = lovis_[ i ] ) < 0 )
            {
                lovis = null;                
                return Double.NaN;
            }
        }
        Arrays.sort( lovis );
        
        int dim = lovis.length;
        double[] singleton = computeSingletonMeasures( method, dim, parameters, lovis, outParams2String ); // singleton measures --> from minimum to maximun value
        
        double[] summationLU = new double[ dim ]; // summation from minumum to maximum value
        double[] summationUL = new double[ dim ]; // summation from maximum to minumum value
        
        for ( int i = dim, j = 1; i >= 1; i--, j++ )
        {
            summationLU[ j - 1 ] = ( j - 1 == 0 ) ? singleton[ j - 1 ] : summationLU[ j - 2 ] + singleton[ j - 1 ];            
            summationUL[ i - 1 ] = ( j - 1 == 0 ) ? singleton[ i - 1 ] : summationUL[ i     ] + singleton[ i - 1 ];
        }
        
        double value   = 0;
        float  L_value = (Float) parameters.get( PARAMETER_NAMES.L_VALUE );
        
        double A = computeFuzzyValue( dim, dim, singleton, summationLU, summationUL, L_value );
        for ( int i = dim; i >= 1; i-- )
        {
            double A_MINUS_1 = computeFuzzyValue( dim, i - 1, singleton, summationLU, summationUL, L_value );
            
            value += ( lovis[i - 1] * ( A - A_MINUS_1 ) );
            
            A = A_MINUS_1;
        }
        
        lovis = null;
        outParams2String[0] = L_value + outParams2String[0];
        return value;
    }
    
    private double computeFuzzyValue( int dim, int dimA, double[] singleton, double[] summationLU, double[] summationUL, double L )
    {
        if ( dimA == 0 )
        {
            return 0d;
        }
        else if ( dim == dimA )
        {
            return 1d;
        }
        else if ( L == -1 )
        {
            return singleton[ dimA - 1 ];
        }
        else if ( L > -1 && L <= 0 )
        {
            double num = ( 1 + L ) * summationLU[ dimA - 1 ] * ( 1 + L * singleton[ dimA - 1 ] );
            double den =   1 + L   * summationLU[ dimA - 1 ];
            
            return ( num / den ) - ( L * singleton[ dimA - 1 ] );
        }
        else if ( L > 0 )
        {
            double num = L * ( dimA - 1 ) * summationLU[ dimA - 1 ]   * ( 1 - summationLU[ dimA - 1 ] );
            double den = ( ( dim - dimA ) * summationUL[ dimA     ] ) + ( L * ( dimA - 1 ) * summationLU[ dimA - 1 ] );
            
            return ( num / den ) + summationLU[ dimA - 1 ];
        }
        
        return Double.NaN;
    }
    
    private double[] computeSingletonMeasures( SINGLETON_METHODS method, int dim, HashMap<PARAMETER_NAMES, Object> parameters, double[] lovis, String[] outParams2String )
    {
        switch ( method ) 
        {
            case AGGREGATED_OBJECTS_1:
                return AGGREGATED_OBJECTS_1_METHOD( dim, parameters, lovis, outParams2String );
                
            case AGGREGATED_OBJECTS_2:
                return AGGREGATED_OBJECTS_2_METHOD( dim, parameters, lovis, outParams2String );
        }
        
        outParams2String[0] = outParams2String[0] + ";NONE";
        return new double[ dim ];
    }
    
    private double[] AGGREGATED_OBJECTS_1_METHOD( int dim, HashMap<PARAMETER_NAMES, Object> parameters, double[] lovis, String[] outParams2String ) 
    {
        float alfa = (Float) parameters.get( PARAMETER_NAMES.ALFA_SINGLETON_METHOD );
        
        double[] singleton = new double[ dim ];
        if ( dim > 0 )
        {
            //the lovis vector is already in ascending order
            
            double den = 0d;
            for ( int i = 1; i <= dim ; i++ )
            {
                den += Math.pow( lovis[ i - 1 ], alfa );
            }
            
            if ( den != 0 )
            {
                for ( int i = lovis.length; i >= 1; i-- )
                {
                    singleton[ i - 1 ] = Math.pow( lovis[ i - 1 ], alfa ) / den;
                }
            }
        }
        
        outParams2String[0] = outParams2String[0] + ";AO1;" + alfa;
        return singleton;
    }
    
    private double[] AGGREGATED_OBJECTS_2_METHOD( int dim, HashMap<PARAMETER_NAMES, Object> parameters, double[] lovis, String[] outParams2String ) 
    {
        float alfa = (Float) parameters.get( PARAMETER_NAMES.ALFA_SINGLETON_METHOD );
        
        double[] singleton = new double[ dim ];
        if ( dim > 0 )
        {
            //the lovis vector is already in ascending order
            
            double den = 0d;
            for ( int i = 1; i <= dim ; i++ )
            {
                den += Math.pow( Math.abs( 1d - lovis[ i - 1 ] ), alfa );
            }
            
            if ( den != 0 )
            {
                for ( int i = lovis.length; i >= 1; i-- )
                {
                    singleton[ i - 1 ] = Math.pow( Math.abs( 1d - lovis[ i - 1 ] ), alfa ) / den;
                }
            }
        }
        
        outParams2String[0] = outParams2String[0] + ";AO2;" + alfa;
        return singleton;
    }
    
    final private String[] defaultChoquet = { 
                                                "-0.75//AGGREGATED_OBJECTS_2//0.6",
                                                "-0.75//AGGREGATED_OBJECTS_1//0.3",
                                                "-0.25//AGGREGATED_OBJECTS_2//0.6",
                                                "0.75//AGGREGATED_OBJECTS_2//0.6",
                                                "0.75//AGGREGATED_OBJECTS_1//0.2",
                                                "0.25//AGGREGATED_OBJECTS_1//0.9",
                                                "0.25//AGGREGATED_OBJECTS_2//0.6",
                                                "0.5//AGGREGATED_OBJECTS_1//0.2",
                                                "0.75//AGGREGATED_OBJECTS_2//0.5",
                                                "0.75//AGGREGATED_OBJECTS_1//0.8",
                                                "0.5//AGGREGATED_OBJECTS_1//0.9",
                                                "0.25//AGGREGATED_OBJECTS_1//0.8",
                                                "-0.25//AGGREGATED_OBJECTS_2//1.0",
                                                "-0.25//AGGREGATED_OBJECTS_2//0.8",
                                                "0.5//AGGREGATED_OBJECTS_2//0.6",
                                                "-0.25//AGGREGATED_OBJECTS_1//0.8",
                                                "-0.75//AGGREGATED_OBJECTS_1//0.9",
                                                "0.5//AGGREGATED_OBJECTS_2//0.9",
                                                "0.75//AGGREGATED_OBJECTS_1//0.9",
                                                "-0.75//AGGREGATED_OBJECTS_2//0.9",
                                                "-0.75//AGGREGATED_OBJECTS_1//1.0",
                                                "0.5//AGGREGATED_OBJECTS_2//0.8",
                                                "0.25//AGGREGATED_OBJECTS_2//0.5",
                                                "-0.75//AGGREGATED_OBJECTS_2//0.7",
                                                "-0.75//AGGREGATED_OBJECTS_2//1.0",
                                                "-0.5//AGGREGATED_OBJECTS_1//0.3",
                                                "-0.75//AGGREGATED_OBJECTS_2//0.0",
                                                "-0.75//AGGREGATED_OBJECTS_1//0.2",
                                                "-0.5//AGGREGATED_OBJECTS_2//0.0",
                                                "0.5//AGGREGATED_OBJECTS_1//0.8",
                                                "0.25//AGGREGATED_OBJECTS_1//0.2",
                                                "-0.5//AGGREGATED_OBJECTS_1//0.2",
                                                "0.5//AGGREGATED_OBJECTS_2//0.5"
                                            };
}
