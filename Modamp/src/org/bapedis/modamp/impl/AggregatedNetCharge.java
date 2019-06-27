/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.invariants.AggregationOperators;
import org.bapedis.modamp.scales.ChargeScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Cesar
 */
public class AggregatedNetCharge extends AbstractMD 
{
    final private AggregationOperators operators;
    
    protected final String Z5, Z7, Z9;
    protected boolean KLEP840101, CHAM830107, CHAM830108;
    protected final String KLEP840101_NAME, CHAM830107_NAME, CHAM830108_NAME;
    private final List<AlgorithmProperty> properties;
    
    public AggregatedNetCharge( AlgorithmFactory factory )
    {
        super( factory );
        
        KLEP840101 = true;
        CHAM830107 = true;
        CHAM830108 = true;
        KLEP840101_NAME = "NetCharge(KLEP840101)";
        CHAM830107_NAME = "NetCharge(CHAM830107)";
        CHAM830108_NAME = "NetCharge(CHAM830108)";
        Z5 = "Z(pH=5)";
        Z7 = "Z(pH=7)";
        Z9 = "Z(pH=9)";
        properties = new LinkedList<>();
        
        populateProperties();
        operators = new AggregationOperators();
    }
    
    private void populateProperties() 
    {
        try 
        {
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedNetCharge.class, "AggregatedNetCharge.KLEP840101.name" ), PRO_CATEGORY, NbBundle.getMessage( AggregatedNetCharge.class, "AggregatedNetCharge.KLEP840101.desc" ), "isKLEP840101", "setKLEP840101" ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedNetCharge.class, "AggregatedNetCharge.CHAM830107.name" ), PRO_CATEGORY, NbBundle.getMessage( AggregatedNetCharge.class, "AggregatedNetCharge.CHAM830107.desc" ), "isCHAM830107", "setCHAM830107" ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedNetCharge.class, "AggregatedNetCharge.CHAM830108.name" ), PRO_CATEGORY, NbBundle.getMessage( AggregatedNetCharge.class, "AggregatedNetCharge.CHAM830108.desc" ), "isCHAM830108", "setCHAM830108" ) );
        }
        catch ( NoSuchMethodException ex )
        {
            Exceptions.printStackTrace( ex );
        }
    }
    
    public boolean isKLEP840101() 
    {
        return KLEP840101;
    }
    
    public void setKLEP840101( Boolean KLEP840101 )
    {
        this.KLEP840101 = KLEP840101;
    }
    
    public boolean isCHAM830107() 
    {
        return CHAM830107;
    }
    
    public void setCHAM830107( Boolean CHAM830107 ) 
    {
        this.CHAM830107 = CHAM830107;
    }
    
    public boolean isCHAM830108() 
    {
        return CHAM830108;
    }
    
    public void setCHAM830108( Boolean CHAM830108 ) 
    {
        this.CHAM830108 = CHAM830108;
    }
    
    @Override
    public void initAlgo( Workspace workspace, ProgressTicket progressTicket ) 
    {
        super.initAlgo( workspace, progressTicket ); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void compute( Peptide peptide ) 
    {
        String seq = peptide.getSequence();
        
        double[] lovis;
        if ( KLEP840101 ) 
        {
            lovis = MD.sumAndAvgByAA( seq, ChargeScale.klein_hash() );
            AggregationOperators.applyOperators( lovis, KLEP840101_NAME, peptide, this, operators, false );
        }
        
        if ( CHAM830107 )
        {
            lovis = MD.sumAndAvgByAA( seq, ChargeScale.charton_ctc_hash() );
            AggregationOperators.applyOperators( lovis, CHAM830107_NAME, peptide, this, operators, true );
        }
        
        if ( CHAM830108 )
        {
            lovis = MD.sumAndAvgByAA( seq, ChargeScale.charton_ctdc_hash() );
            AggregationOperators.applyOperators( lovis, CHAM830108_NAME, peptide, this, operators, true );
        }
    }
    
    @Override
    public AlgorithmProperty[] getProperties() 
    {
        return properties.toArray( new AlgorithmProperty[0] );
    }
}
