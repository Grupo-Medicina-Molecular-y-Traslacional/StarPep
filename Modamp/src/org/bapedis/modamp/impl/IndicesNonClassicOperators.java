package org.bapedis.modamp.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.invariants.ClassicAggregationOperator;
import org.bapedis.modamp.invariants.NonClassicAggregationOperatorBase;
import org.bapedis.modamp.invariants.NonClassicAggregationOperatorChoquet;
import org.bapedis.modamp.invariants.NonClassicAggregationOperatorGOWAWA;
import org.bapedis.modamp.invariants.NonClassicAggregationOperatorMeans;
import org.bapedis.modamp.invariants.NonClassicAggregationOperatorStatistics;
import org.bapedis.modamp.scales.ChargeScale;
import org.bapedis.modamp.scales.HydrophilicityScale;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.bapedis.modamp.invariants.NonClassicAggregationOperator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Cesar
 */
public class IndicesNonClassicOperators extends AbstractMD 
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
    
    private boolean autocorrelationOperator;
    private boolean gravitationalOperator;
    private boolean totalSumOperator;
    private boolean electroTopStateOperator;
    
    final private List<String> classicOperatorsList;
    final private List<String> nonClassicOperatorsList;
    
    final private ClassicAggregationOperator classicOperators;
    final private NonClassicAggregationOperator nonClassicOperators;
    
    final private List<AlgorithmProperty> properties;
    
    public IndicesNonClassicOperators( IndicesNonClassicOperatorsFactory factory ) 
    {
        super( factory );
        
        mass = true;
        boman = true;
        charge = true;
        hydrophilicity = true;
        
        classicOperatorsList = new LinkedList<>();
        nonClassicOperatorsList = new LinkedList<>();
        
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
        
        setAutocorrelationOperator( true );
        setGravitationalOperator( true );
        setTotalSumOperator( true );
        setElectroTopStateOperator( true );
        
        nonClassicOperators = new NonClassicAggregationOperatorChoquet( new NonClassicAggregationOperatorGOWAWA( 
                                                                        new NonClassicAggregationOperatorStatistics( 
                                                                        new NonClassicAggregationOperatorMeans( 
                                                                        new NonClassicAggregationOperatorBase() ) ) ) );
        
        classicOperators = new ClassicAggregationOperator( nonClassicOperators );
        
        properties = new LinkedList<>();
        
        try
        {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.p2.name"      ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.p2.desc"      ), "isQuadraticMeanOperator"      , "setQuadraticMeanOperator"       ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.p3.name"      ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.p3.desc"      ), "isPotentialMeanOperator"      , "setPotentialMeanOperator"       ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.hm.name"      ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.hm.desc"      ), "isHarmonicMeanOperator"       , "setHarmonicMeanOperator"        ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.v.name"       ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.v.desc"       ), "isVarianceOperator"           , "setVarianceOperator"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.s.name"       ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.s.desc"       ), "isSkewnessOperator"           , "setSkewnessOperator"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.k.name"       ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.k.desc"       ), "isKurtosisOperator"           , "setKurtosisOperator"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.sd.name"      ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.sd.desc"      ), "isStandardDeviationOperator"  , "setStandardDeviationOperator"   ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.vc.name"      ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.vc.desc"      ), "isVariationCoefficientOpertor", "setVariationCoefficientOpertor" ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.ra.name"      ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.ra.desc"      ), "isRangeOpertor"               , "setRangeOpertor"                ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.i50.name"     ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.i50.desc"     ), "isI50Opertor"                 , "setI50Opertor"                  ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.gowawa.name"  ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.gowawa.desc"  ), "isGowawaOperator"             , "setGowawaOperator"              ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.choquet.name" ), "Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.choquet.desc" ), "isChoquetOperator"            , "setChoquetOperator"             ) );
            
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.ac.name"  ), "Classic Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.ac.desc"  ), "isAutocorrelationOperator" , "setAutocorrelationOperator" ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.gv.name"  ), "Classic Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.gv.desc"  ), "isGravitationalOperator"   , "setGravitationalOperator"   ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.ts.name"  ), "Classic Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.ts.desc"  ), "isTotalSumOperator"        , "setTotalSumOperator"        ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.es.name"  ), "Classic Aggregation Operators", NbBundle.getMessage(IndicesNonClassicOperators.class, "AggregationOperators.es.desc"  ), "isElectroTopStateOperator" , "setElectroTopStateOperator" ) );
            
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.mass.name"           ), "Chemical Properties", NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.mass.desc"           ), "isMass"          , "setMass"           ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.boman.name"          ), "Chemical Properties", NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.boman.desc"          ), "isBoman"         , "setBoman"          ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.charge.name"         ), "Chemical Properties", NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.charge.desc"         ), "isCharge"        , "setCharge"         ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.hydrophilicity.name" ), "Chemical Properties", NbBundle.getMessage(IndicesNonClassicOperators.class, "IndicesNonClassicOperators.hydrophilicity.desc" ), "isHydrophilicity", "setHydrophilicity" ) );
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
            nonClassicOperators.applyOperators( lovis, "mw", peptide, this, true, nonClassicOperatorsList );            
            classicOperators   .applyOperators( lovis, "mw", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
        }
        
        if ( isBoman() )
        {
            lovis = MD.bomanByAA( peptide.getSequence() );
            nonClassicOperators.applyOperators( lovis, "Boman", peptide, this, false, nonClassicOperatorsList );            
            classicOperators   .applyOperators( lovis, "Boman", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
        }
        
        if ( isCharge() )
        {
            lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.klein_hash() );
            nonClassicOperators.applyOperators( lovis, "NetCharge(KLEP840101)", peptide, this, false, nonClassicOperatorsList );            
            classicOperators   .applyOperators( lovis, "NetCharge(KLEP840101)", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
        }
        
        if ( isHydrophilicity() )
        {
            lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash() );
            nonClassicOperators.applyOperators( lovis, "Hydrophilicity(KUHL950101)", peptide, this, true, nonClassicOperatorsList );
            classicOperators   .applyOperators( lovis, "Hydrophilicity(KUHL950101)", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            
            lovis = MD.gravyByAA( peptide.getSequence() );
            nonClassicOperators.applyOperators( lovis, "GRAVY", peptide, this, false, nonClassicOperatorsList );
            classicOperators   .applyOperators( lovis, "GRAVY", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
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
            nonClassicOperatorsList.add( "P2" );
        }
        else
        {
            nonClassicOperatorsList.remove( "P2" );
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
            nonClassicOperatorsList.add( "P3" );
        }
        else
        {
            nonClassicOperatorsList.remove( "P3" );
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
            nonClassicOperatorsList.add( "HM" );
        }
        else
        {
            nonClassicOperatorsList.remove( "HM" );
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
            nonClassicOperatorsList.add( "GOWAWA" );
        }
        else
        {
            nonClassicOperatorsList.remove( "GOWAWA" );
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
            nonClassicOperatorsList.add( "CHOQUET" );
        }
        else
        {
            nonClassicOperatorsList.remove( "CHOQUET" );
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
            nonClassicOperatorsList.add( "V" );
        }
        else
        {
            nonClassicOperatorsList.remove( "V" );
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
            nonClassicOperatorsList.add( "S" );
        }
        else
        {
            nonClassicOperatorsList.remove( "S" );
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
            nonClassicOperatorsList.add( "K" );
        }
        else
        {
            nonClassicOperatorsList.remove( "K" );
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
            nonClassicOperatorsList.add( "SD" );
        }
        else
        {
            nonClassicOperatorsList.remove( "SD" );
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
            nonClassicOperatorsList.add( "VC" );
        }
        else
        {
            nonClassicOperatorsList.remove( "VC" );
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
            nonClassicOperatorsList.add( "RA" );
        }
        else
        {
            nonClassicOperatorsList.remove( "RA" );
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
            nonClassicOperatorsList.add( "i50" );
        }
        else
        {
            nonClassicOperatorsList.remove( "i50" );
        }
    }
    
    public boolean isAutocorrelationOperator() 
    {
        return autocorrelationOperator;
    }
    
    public void setAutocorrelationOperator( Boolean autocorrelationOperator )
    {
        this.autocorrelationOperator = autocorrelationOperator;
        
        if ( autocorrelationOperator )
        {
            classicOperatorsList.add( "AC" );
        }
        else
        {
            classicOperatorsList.remove( "AC" );
        }
    }
    
    public boolean isGravitationalOperator()
    {
        return gravitationalOperator;
    }
    
    public void setGravitationalOperator( Boolean gravitationalOperator )
    {
        this.gravitationalOperator = gravitationalOperator;
        
        if ( gravitationalOperator )
        {
            classicOperatorsList.add( "GV" );
        }
        else
        {
            classicOperatorsList.remove( "GV" );
        }
    }
    
    public boolean isTotalSumOperator() 
    {
        return totalSumOperator;
    }
    
    public void setTotalSumOperator( Boolean totalSumOperator )
    {
        this.totalSumOperator = totalSumOperator;
        
        if ( totalSumOperator )
        {
            classicOperatorsList.add( "TS" );
        }
        else
        {
            classicOperatorsList.remove( "TS" );
        }
    }
    
    public boolean isElectroTopStateOperator()
    {
        return electroTopStateOperator;
    }
    
    public void setElectroTopStateOperator( Boolean electroTopStateOperator )
    {
        this.electroTopStateOperator = electroTopStateOperator;
        
        if ( electroTopStateOperator )
        {
            classicOperatorsList.add( "ES" );
        }
        else
        {
            classicOperatorsList.remove( "ES" );
        }
    }
}
