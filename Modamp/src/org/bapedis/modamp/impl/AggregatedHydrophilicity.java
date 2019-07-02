/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.invariants.AggregationOperators;
import org.bapedis.modamp.invariants.Choquet;
import org.bapedis.modamp.scales.HydrophilicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

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
        
        operators = new Choquet();
    }
    
    @Override
    protected void populateProperties() 
    {
        GRAVY = false;
        GRAVY_NAME = "GRAVY";
        HOPT810101 = true;
        HOPT810101_NAME = "Hydrophilicity(HOPT810101)";
        KUHL950101 = true;
        KUHL950101_NAME = "Hydrophilicity(KUHL950101)";
        
        try
        {
            properties.add(AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedHydrophilicity.class, "AggregatedHydrophilicity.HOPT810101.name" ), PRO_CATEGORY, NbBundle.getMessage( AggregatedHydrophilicity.class, "AggregatedHydrophilicity.HOPT810101.desc" ), "isHOPT810101", "setHOPT810101" ) );
            properties.add(AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedHydrophilicity.class, "AggregatedHydrophilicity.KUHL950101.name" ), PRO_CATEGORY, NbBundle.getMessage( AggregatedHydrophilicity.class, "AggregatedHydrophilicity.KUHL950101.desc" ), "isKUHL950101", "setKUHL950101" ) );
        }
        catch ( NoSuchMethodException ex )
        {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) 
    {
        HOPT810101 = false;
        KUHL950101 = false;
        
        super.initAlgo( workspace, progressTicket );
        
        HOPT810101 = true;
        KUHL950101 = true;
    }
    
    @Override
    protected void compute( Peptide peptide ) 
    {
        double[] lovis;
        if ( HOPT810101 ) 
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.hopp_Woods_hydrov_hash() );
            operators.applyAllOperators( lovis, HOPT810101_NAME, peptide, this, false );
        }
        
        if ( KUHL950101 )
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash() );
            operators.applyAllOperators( lovis, KUHL950101_NAME, peptide, this, true );
        }
        
        lovis = MD.gravyByAA( peptide.getSequence() );
        operators.applyAllOperators( lovis, GRAVY_NAME, peptide, this, false );
    }
}
