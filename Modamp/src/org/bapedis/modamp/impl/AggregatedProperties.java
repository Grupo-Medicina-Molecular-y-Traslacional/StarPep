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
    
    private boolean quadraticMeanOperator;
    private boolean potentialMeanOperator;
    private boolean harmonicMeanOperator;
    private boolean varianceOperator;
    private boolean skewnessOperator;
    private boolean kurtosisOperator;
    private boolean standardDeviationOperator;
    private boolean variationCoefficientOpertor;
    private boolean rangeOpertor;
    private boolean i50Opertor;
    private boolean gowawaOperator;
    private boolean choquetOperator;
    
    final private List<String> operatorsList;
    
    final private AggregationOperators operators;
    final private List<AlgorithmProperty> properties;
    
    public AggregatedProperties( AggregatedPropertiesFactory factory ) 
    {
        super( factory );
        
        mass = true;
        boman = true;
        charge = true;
        hydrophilicity = true;
        
        operatorsList = new LinkedList<>();
        setQuadraticMeanOperator( true );
        setPotentialMeanOperator( true );
        setHarmonicMeanOperator( true );
        setVarianceOperator( true );
        setSkewnessOperator( true );
        setKurtosisOperator( true );
        setStandardDeviationOperator( true );
        setVariationCoefficientOpertor( true );
        setRangeOpertor( true );
        setI50Opertor( true );
        setGowawaOperator( true );
        setChoquetOperator( true );
        
        operators = new Choquet();
        properties = new LinkedList<>();
        
        try
        {
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.p2.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.p2.desc"  ), "isQuadraticMeanOperator"      , "setQuadraticMeanOperator"       ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.p3.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.p3.desc"  ), "isPotentialMeanOperator"      , "setPotentialMeanOperator"       ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.hm.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.hm.desc"  ), "isHarmonicMeanOperator"       , "setHarmonicMeanOperator"        ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.v.name"   ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.v.desc"   ), "isVarianceOperator"           , "setVarianceOperator"            ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.s.name"   ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.s.desc"   ), "isSkewnessOperator"           , "setSkewnessOperator"            ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.k.name"   ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.k.desc"   ), "isKurtosisOperator"           , "setKurtosisOperator"            ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.sd.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.sd.desc"  ), "isStandardDeviationOperator"  , "setStandardDeviationOperator"   ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.vc.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.vc.desc"  ), "isVariationCoefficientOpertor", "setVariationCoefficientOpertor" ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.ra.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.ra.desc"  ), "isRangeOpertor"               , "setRangeOpertor"                ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.i50.name" ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.i50.desc" ), "isI50Opertor"                 , "setI50Opertor"                  ) );
            
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.gowawa.name"  ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.gowawa.desc"  ), "isGowawaOperator"  , "setGowawaOperator"  ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.choquet.name" ), "Aggregation Operators", NbBundle.getMessage( AggregatedProperties.class, "AggregationOperators.choquet.desc" ), "isChoquetOperator" , "setChoquetOperator" ) );
            
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.mass.name"           ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.mass.desc"           ), "isMass"          , "setMass"           ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.boman.name"          ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.boman.desc"          ), "isBoman"         , "setBoman"          ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.charge.name"         ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.charge.desc"         ), "isCharge"        , "setCharge"         ) );
            properties.add( AlgorithmProperty.createProperty( this, Boolean.class, NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.hydrophilicity.name" ), "Chemical Properties", NbBundle.getMessage( AggregatedProperties.class, "AggregatedProperties.hydrophilicity.desc" ), "isHydrophilicity", "setHydrophilicity" ) );
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
            operators.applyAllOperators( lovis, "mw", peptide, this, true, operatorsList );
        }
        
        if ( isBoman() )
        {
            lovis = MD.bomanByAA( peptide.getSequence() );
            operators.applyAllOperators( lovis, "Boman", peptide, this, false, operatorsList );
        }
        
        if ( isCharge() )
        {
            lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.klein_hash() );
            operators.applyAllOperators( lovis, "NetCharge(KLEP840101)", peptide, this, false, operatorsList );
            
            /*lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.charton_ctc_hash() );
            operators.applyAllOperators( lovis, "NetCharge(CHAM830107)", peptide, this, true, operatorsList );
            
            lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.charton_ctdc_hash() );
            operators.applyAllOperators( lovis, "NetCharge(CHAM830108)", peptide, this, true, operatorsList );*/
        }
        
        if ( isHydrophilicity() )
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash() );
            operators.applyAllOperators( lovis, "Hydrophilicity(KUHL950101)", peptide, this, true, operatorsList );
            
            lovis = MD.gravyByAA( peptide.getSequence() );
            operators.applyAllOperators( lovis, "GRAVY", peptide, this, false, operatorsList );
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
    
    public boolean isQuadraticMeanOperator()
    {
        return quadraticMeanOperator;
    }
    
    public void setQuadraticMeanOperator( Boolean quadraticMeanOperator )
    {
        this.quadraticMeanOperator = quadraticMeanOperator;
        
        if ( quadraticMeanOperator )
        {
            operatorsList.add( "P2" );
        }
        else
        {
            operatorsList.remove( "P2" );
        }
    }
    
    public boolean isPotentialMeanOperator()
    {
        return potentialMeanOperator;
    }
    
    public void setPotentialMeanOperator( Boolean potentialMeanOperator )
    {
        this.potentialMeanOperator = potentialMeanOperator;
        
        if ( potentialMeanOperator )
        {
            operatorsList.add( "P3" );
        }
        else
        {
            operatorsList.remove( "P3" );
        }
    }
    
    public boolean isHarmonicMeanOperator() 
    {
        return harmonicMeanOperator;
    }
    
    public void setHarmonicMeanOperator( Boolean harmonicMeanOperator )
    {
        this.harmonicMeanOperator = harmonicMeanOperator;
        
        if ( harmonicMeanOperator )
        {
            operatorsList.add( "HM" );
        }
        else
        {
            operatorsList.remove( "HM" );
        }
    }
    
    public boolean isGowawaOperator() 
    {
        return gowawaOperator;
    }
    
    public void setGowawaOperator( Boolean gowawaOperator )
    {
        this.gowawaOperator = gowawaOperator;
        
        if ( gowawaOperator )
        {
            operatorsList.add( "GOWAWA" );
        }
        else
        {
            operatorsList.remove( "GOWAWA" );
        }
    }
    
    public boolean isChoquetOperator()
    {
        return choquetOperator;
    }
    
    public void setChoquetOperator( Boolean choquetOperator )
    {
        this.choquetOperator = choquetOperator;
        
        if ( choquetOperator )
        {
            operatorsList.add( "CHOQUET" );
        }
        else
        {
            operatorsList.remove( "CHOQUET" );
        }
    }
    
    public boolean isVarianceOperator() 
    {
        return varianceOperator;
    }
    
    public void setVarianceOperator( Boolean varianceOperator )
    {
        this.varianceOperator = varianceOperator;
        
        if ( varianceOperator )
        {
            operatorsList.add( "V" );
        }
        else
        {
            operatorsList.remove( "V" );
        }
    }
    
    public boolean isSkewnessOperator()
    {
        return skewnessOperator;
    }
    
    public void setSkewnessOperator( Boolean skewnessOperator )
    {
        this.skewnessOperator = skewnessOperator;
        
        if ( skewnessOperator )
        {
            operatorsList.add( "S" );
        }
        else
        {
            operatorsList.remove( "S" );
        }
    }
    
    public boolean isKurtosisOperator()
    {
        return kurtosisOperator;
    }
    
    public void setKurtosisOperator( Boolean kurtosisOperator )
    {
        this.kurtosisOperator = kurtosisOperator;
        
        if ( kurtosisOperator )
        {
            operatorsList.add( "K" );
        }
        else
        {
            operatorsList.remove( "K" );
        }
    }
    
    public boolean isStandardDeviationOperator() 
    {
        return standardDeviationOperator;
    }
    
    public void setStandardDeviationOperator( Boolean standardDeviationOperator )
    {
        this.standardDeviationOperator = standardDeviationOperator;
        
        if ( standardDeviationOperator )
        {
            operatorsList.add( "SD" );
        }
        else
        {
            operatorsList.remove( "SD" );
        }
    }
    
    public boolean isVariationCoefficientOpertor() 
    {
        return variationCoefficientOpertor;
    }
    
    public void setVariationCoefficientOpertor( Boolean variationCoefficientOpertor )
    {
        this.variationCoefficientOpertor = variationCoefficientOpertor;
        
        if ( variationCoefficientOpertor )
        {
            operatorsList.add( "VC" );
        }
        else
        {
            operatorsList.remove( "VC" );
        }
    }
    
    public boolean isRangeOpertor() 
    {
        return rangeOpertor;
    }
    
    public void setRangeOpertor( Boolean rangeOpertor )
    {
        this.rangeOpertor = rangeOpertor;
        
        if ( rangeOpertor )
        {
            operatorsList.add( "RA" );
        }
        else
        {
            operatorsList.remove( "RA" );
        }
    }
    
    public boolean isI50Opertor() 
    {
        return i50Opertor;
    }
    
    public void setI50Opertor( Boolean i50Opertor )
    {
        this.i50Opertor = i50Opertor;
        
        if ( i50Opertor )
        {
            operatorsList.add( "i50" );
        }
        else
        {
            operatorsList.remove( "i50" );
        }
    }
}
