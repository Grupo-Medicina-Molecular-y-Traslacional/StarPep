/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.invariants.AggregationOperators;
import org.bapedis.modamp.scales.HydrophilicityScale;

/**
 *
 * @author Cesar
 */
public class AggregatedHydrophilicity extends AverageHydrophilicity
{    
    final private AggregationOperators operators;
    
    public AggregatedHydrophilicity( AlgorithmFactory factory ) 
    {
        super( factory );
        
        operators = new AggregationOperators();
        
        HOPT810101_NAME = "Hydrophilicity(HOPT810101)";
        KUHL950101_NAME = "Hydrophilicity(KUHL950101)";
    }
    
    @Override
    protected void compute( Peptide peptide ) 
    {
        double[] lovis;
        if ( HOPT810101 ) 
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.hopp_Woods_hydrov_hash() );
            AggregationOperators.applyOperators( lovis, HOPT810101_NAME, peptide, this, operators, false );
        }
        
        if ( KUHL950101 )
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash() );
            AggregationOperators.applyOperators( lovis, KUHL950101_NAME, peptide, this, operators, true );
        }
        
        lovis = MD.gravyByAA( peptide.getSequence() );
        AggregationOperators.applyOperators( lovis, GRAVY, peptide, this, operators, false );
    }
}
