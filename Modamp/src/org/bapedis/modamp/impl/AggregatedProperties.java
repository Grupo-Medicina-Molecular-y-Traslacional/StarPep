package org.bapedis.modamp.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.invariants.AggregationOperators;
import org.bapedis.modamp.invariants.Choquet;
import org.bapedis.modamp.scales.ChargeScale;
import org.bapedis.modamp.scales.HydrophilicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author beltran, loge, Cesar
 */
public class AggregatedProperties extends AbstractMD 
{
    private boolean mass;
    private boolean boman;
    private boolean charge;
    private boolean hydrophilicity;
    
    final private AggregationOperators operators;
    final private List<AlgorithmProperty> properties;
    
    public AggregatedProperties( AggregatedPropertiesFactory factory ) 
    {
        super( factory );
        
        mass = true;
        boman = true;
        charge = true;
        hydrophilicity = true;
        
        operators = new Choquet();
        properties = new LinkedList<>();
        
        try
        {
            properties.add(AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.mass.name"           ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.mass.desc"           ), "isMass"          , "setMass"           ) );
            properties.add(AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.boman.name"          ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.boman.desc"          ), "isBoman"         , "setBoman"          ) );
            properties.add(AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.charge.name"         ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.charge.desc"         ), "isCharge"        , "setCharge"         ) );
            properties.add(AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.hydrophilicity.name" ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.hydrophilicity.desc" ), "isHydrophilicity", "setHydrophilicity" ) );
        }
        catch ( NoSuchMethodException ex )
        {
            Exceptions.printStackTrace(ex);
        }
    }
    
    @Override
    public void initAlgo( Workspace workspace, ProgressTicket progressTicket )
    {
        super.initAlgo( workspace, progressTicket ); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void compute(Peptide peptide) 
    {
        double[] lovis;
        
        if ( isMass() )
        {
            lovis = MD.mwByAA( peptide.getSequence() );
            operators.applyAllOperators( lovis, "mw", peptide, this, true );
        }
        
        if ( isBoman() )
        {
            lovis = MD.bomanByAA( peptide.getSequence() );
            operators.applyAllOperators( lovis, "Boman", peptide, this, false );
        }
        
        if ( isCharge() )
        {
            lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.klein_hash() );
            operators.applyAllOperators( lovis, "NetCharge(KLEP840101)", peptide, this, false );
            
            lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.charton_ctc_hash() );
            operators.applyAllOperators( lovis, "NetCharge(CHAM830107)", peptide, this, true );
            
            lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.charton_ctdc_hash() );
            operators.applyAllOperators( lovis, "NetCharge(CHAM830108)", peptide, this, true );
        }
        
        if ( isHydrophilicity() )
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash() );
            operators.applyAllOperators( lovis, "Hydrophilicity(KUHL950101)", peptide, this, true );
            
            lovis = MD.gravyByAA( peptide.getSequence() );
            operators.applyAllOperators( lovis, "GRAVY", peptide, this, false );
        }
    }
    
    @Override
    public AlgorithmProperty[] getProperties()
    {
        return properties.toArray( new AlgorithmProperty[0] );
    }
    
    public boolean isMass() 
    {
        return mass;
    }
    
    public void setMass( Boolean mass )
    {
        this.mass = mass;
    }
    
    public boolean isBoman()
    {
        return boman;
    }
    
    public void setBoman( Boolean boman )
    {
        this.boman = boman;
    }
    
    public boolean isCharge()
    {
        return charge;
    }
    
    public void setCharge( Boolean charge )
    {
        this.charge = charge;
    }
    
    public boolean isHydrophilicity()
    {
        return hydrophilicity;
    }
    
    public void setHydrophilicity( Boolean hydrophilicity )
    {
        this.hydrophilicity = hydrophilicity;
    }
}
