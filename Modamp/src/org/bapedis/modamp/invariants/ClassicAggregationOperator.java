/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.invariants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.openide.util.Exceptions;

/**
 *
 * @author Cesar
 */
public class ClassicAggregationOperator implements IAggregationOperator
{
    private int maxK = 1;
    
    final private NonClassicAggregationOperator nonClassicOperator;
    
    private final String[] METHODS = { "autocorrelation", "gravitational", "totalSumLagK", "electroTopologicalState" };    
    private final String[] ACRONYM = { "AC", "GV", "TS", "ES" };
    
    public ClassicAggregationOperator( NonClassicAggregationOperator nonClassicOperator )
    {
        this.nonClassicOperator = nonClassicOperator;
    }
    
    public void applyOperators( double[] lovis, String keyAttr, Peptide peptide, AbstractMD md, boolean applyMeans, List<String> nonClassicOperatorsList, List<String> classicOperatorsList )
    {
        if ( !classicOperatorsList.isEmpty() )
        {
            int[][] distanceMatrix = maxK == 1 ? peptide.calcAdjancencyMtrix() : computeFloydAPSP( peptide.calcAdjancencyMtrix() );
            
            ArrayList<Double> elements = new ArrayList<>( peptide.getLength() * 2 );            
            for ( int i = 0; i < METHODS.length; i++ )
            {
                try 
                {
                    if ( classicOperatorsList.contains( ACRONYM[i] ) )
                    {
                        if ( i >= 0 && i <= 2 )
                        {
                            for ( int t = 1; t <= maxK; t++ )
                            {
                                Method method = this.getClass().getMethod( METHODS[i], double[].class, Peptide.class, Integer.class, int[][].class, ArrayList.class );
                                
                                String tmp = keyAttr + "-" + ACRONYM[i] + "[" + t + "]";                                
                                double[] newLovis = (double[]) method.invoke( this, new Object[]{ lovis, peptide, t, distanceMatrix, elements } );
                                
                                nonClassicOperator.applyOperators( newLovis, tmp, peptide, md, applyMeans, nonClassicOperatorsList );
                                newLovis = null;
                            }
                        }
                        else
                        {
                            Method method = this.getClass().getMethod( METHODS[i], double[].class, Peptide.class, int[][].class );
                            
                            String tmp = keyAttr + "-" + ACRONYM[i];                                
                            double[] newLovis = (double[]) method.invoke( this, new Object[]{ lovis, peptide, distanceMatrix } );
                            
                            nonClassicOperator.applyOperators( newLovis, tmp, peptide, md, applyMeans, nonClassicOperatorsList );
                            newLovis = null;
                        }
                    }
                }
                catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex )
                {
                    Exceptions.printStackTrace( ex );
                }
            }
            
            elements.clear();
            elements = null;
            distanceMatrix = null;
        }
    }
    
    final public double[] autocorrelation( double[] a, Peptide peptide, Integer k, int[][] distanceMatrix, ArrayList<Double> elements )
    {
        elements.clear();
        
        int n = peptide.getLength();
        for ( int i = 0; i < n; i++ ) 
        {
            for ( int j = i; j < n; j++ ) 
            {
                if ( distanceMatrix[i][j] == k ) 
                {
                    elements.add( a[i] * a[j] );
                }
            }
        }
        
        double[] result = new double[ elements.size() ];
        for (int i = 0; i < elements.size(); i++) 
        {
            result[i] = elements.get(i);
        }        
        return result;
    }
    
    final public double[] gravitational( double[] a, Peptide peptide, Integer k, int[][] distanceMatrix, ArrayList<Double> elements )
    {
        elements.clear();
        
        int n = peptide.getLength();
        for ( int i = 0; i < n; i++ ) 
        {
            for ( int j = i + 1; j < n; j++ ) 
            {
                if ( distanceMatrix[i][j] == k ) 
                {
                    elements.add( ( a[i] * a[j] ) / k );
                }
            }
        }
        
        double[] result = new double[ elements.size() ];
        for ( int i = 0; i < elements.size(); i++ ) 
        {
            result[i] = elements.get(i);
        }        
        return result;
    }
    
    public double[] totalSumLagK( double[] a, Peptide peptide, Integer k, int[][] distanceMatrix, ArrayList<Double> elements )
    {
        elements.clear();
        
        int n = peptide.getLength();
        for ( int i = 0; i < n; i++ ) 
        {
            for ( int j = i + 1; j < n; j++ ) 
            {
                if ( distanceMatrix[i][j] == k ) 
                {
                    elements.add( a[i] + a[j] );
                }
            }
        }
        
        double[] result = new double[ elements.size() ];
        for ( int i = 0; i < elements.size(); i++ ) 
        {
            result[i] = elements.get(i);
        }        
        return result;
    }
    
    public double[] electroTopologicalState( double[] lovis, Peptide peptide, int[][] distanceMatrix ) 
    {
        int longitud = peptide.getLength();
        
        double[] Si = new double[ longitud ];
        double Li = 0;
        
        for ( int i = 0; i < longitud; i++ ) 
        {
            Li = lovis[i];
            double sum = 0;
            
            for ( int j = 0; j < longitud; j++ )
            {
                int Dij = distanceMatrix[i][j] + 1;
                sum = sum + ( Li - lovis[j] ) / Math.pow( Dij, 2 );
            }
            Si[i] = Li + sum;
        }
        
        return Si;
    }
    
    private int[][] computeFloydAPSP( int costMatrix[][] )
    {
        int i;
        int j;
        int k;
        int nrow = costMatrix.length;
        int[][] distMatrix = new int[nrow][nrow];
        //logger.debug("Matrix size: " + n);
        for (i = 0; i < nrow; i++) {
            for (j = 0; j < nrow; j++) {
                if (costMatrix[i][j] == 0) {
                    distMatrix[i][j] = 999999999;
                } else {
                    distMatrix[i][j] = 1;
                }
            }
        }
        for (i = 0; i < nrow; i++) {
            distMatrix[i][i] = 0;
            // no self cycle
        }
        for (k = 0; k < nrow; k++) {
            for (i = 0; i < nrow; i++) {
                for (j = 0; j < nrow; j++) {
                    if (distMatrix[i][k] + distMatrix[k][j] < distMatrix[i][j]) {
                        distMatrix[i][j] = distMatrix[i][k] + distMatrix[k][j];
                        //P[i][j] = k;        // k is included in the shortest path
                    }
                }
            }
        }
        return distMatrix;
    }
    
    public void setMaxK( int maxK )
    {
        this.maxK = maxK;
    }
}
