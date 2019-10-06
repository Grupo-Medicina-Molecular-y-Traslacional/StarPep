package org.bapedis.modamp.impl;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.AminoacidPropertiesGetter;
import org.bapedis.modamp.EAminoacidProperty;
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
import org.bapedis.modamp.invariants.NonClassicAggregationOperatorNorm;
import org.bapedis.modamp.EAminoacidLocal;
import org.bapedis.modamp.LocalTool;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Cesar
 */
public class IndicesBasedOperators extends AbstractMD 
{
    private Integer maxK;
    private AminoacidPropertiesGetter aminoacidProperties;
    
    private boolean z1;
    private boolean z2;
    private boolean z3;
    private boolean ptt;
    private boolean eps;
    private boolean scm;
    private boolean scv;
    private boolean pie;
    private boolean pah;
    private boolean pbs;
    private boolean isa;
    private boolean gcp1;
    private boolean gcp2;
    private boolean boman;
    private boolean charge;
    private boolean hydrophilicity;
    
    private boolean total;
    private boolean aliphatic;
    private boolean alphaHelixFavoring;
    private boolean apolar;
    private boolean aromatic;
    private boolean betaSheetFavoring;
    private boolean betaTurnFavoring;
    private boolean negativelyChargedPolar;
    private boolean positivelyChargedPolar;
    private boolean unchargedPolar;
    private boolean unfolding;
    
    private boolean manhattanNorm;
    private boolean euclideanNorm;
    private boolean arithmeticMeanOperator;
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
    final private List<EAminoacidLocal> localsList;
    
    final private ClassicAggregationOperator classicOperators;
    final private NonClassicAggregationOperator nonClassicOperators;
    
    final private List<AlgorithmProperty> properties;
    
    public IndicesBasedOperators( IndicesBasedOperatorsFactory factory ) 
    {
        super( factory );
        
        try 
        {
            aminoacidProperties = new AminoacidPropertiesGetter();
        }
        catch ( IOException ex )
        {
            Exceptions.printStackTrace(ex);
        }
        
        classicOperatorsList = new LinkedList<>();
        nonClassicOperatorsList = new LinkedList<>();
        localsList = new LinkedList<>();
        
        ptt = true;
        gcp1 = true;
        gcp2 = true;
        eps = true;
        scm = true;
        scv = true;
        pie = true;
        pah = true;
        pbs = true;
        isa = true;
        z1 = true;
        z2 = true;
        z3 = true;
        boman = true;
        charge = true;
        hydrophilicity = true;
        
        setTotal( true );
        setAliphatic( true );
        setAlphaHelixFavoring( true );
        setApolar( true );
        setAromatic( true );
        setBetaSheetFavoring( true );
        setBetaTurnFavoring( true );
        setNegativelyChargedPolar( true );
        setPositivelyChargedPolar( true );
        setUnchargedPolar( true );
        setUnfolding( true );
        
        setManhattanNorm( true );
        setEuclideanNorm( true );
        setArithmeticMeanOperator( true );
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
        setGowawaOperator( false );
        setChoquetOperator( false );
        
        setAutocorrelationOperator( false );
        setGravitationalOperator( false );
        setTotalSumOperator( false );
        setElectroTopStateOperator( false );
        
        nonClassicOperators = new NonClassicAggregationOperatorChoquet( new NonClassicAggregationOperatorGOWAWA( 
                                                                        new NonClassicAggregationOperatorStatistics( 
                                                                        new NonClassicAggregationOperatorMeans( 
                                                                        new NonClassicAggregationOperatorNorm(
                                                                        new NonClassicAggregationOperatorBase() ) ) ) ) );
        
        classicOperators = new ClassicAggregationOperator( nonClassicOperators );
        
        setMaxK( 1 );
        
        properties = new LinkedList<>();        
        try
        {
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.ptt.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.ptt.desc"            ), "isPtt"           , "setPtt"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.gcp1.name"           ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.gcp1.desc"           ), "isGcp1"          , "setGcp1"           ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.gcp2.name"           ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.gcp2.desc"           ), "isGcp2"          , "setGcp2"           ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.eps.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.eps.desc"            ), "isEps"           , "setEps"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.scm.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.scm.desc"            ), "isScm"           , "setScm"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.scv.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.scv.desc"            ), "isScv"           , "setScv"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.pie.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.pie.desc"            ), "isPie"           , "setPie"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.pah.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.pah.desc"            ), "isPah"           , "setPah"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.pbs.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.pbs.desc"            ), "isPbs"           , "setPbs"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.isa.name"            ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.isa.desc"            ), "isIsa"           , "setIsa"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.z1.name"             ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.z1.desc"             ), "isZ1"            , "setZ1"             ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.z2.name"             ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.z2.desc"             ), "isZ2"            , "setZ2"             ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.z3.name"             ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.z3.desc"             ), "isZ3"            , "setZ3"             ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.boman.name"          ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.boman.desc"          ), "isBoman"         , "setBoman"          ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.charge.name"         ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.charge.desc"         ), "isCharge"        , "setCharge"         ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.hydrophilicity.name" ), "Chemical Properties", NbBundle.getMessage( IndicesBasedOperators.class, "IndicesBasedOperators.hydrophilicity.desc" ), "isHydrophilicity", "setHydrophilicity" ) );
            
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.total.name"     ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.total.desc"     ), "isTotal"                 , "setTotal"                  ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.aliphatic.name" ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.aliphatic.desc" ), "isAliphatic"             , "setAliphatic"              ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.fah.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.fah.desc"       ), "isAlphaHelixFavoring"    , "setAlphaHelixFavoring"     ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.apolar.name"    ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.apolar.desc"    ), "isApolar"                , "setApolar"                 ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.aromatic.name"  ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.aromatic.desc"  ), "isAromatic"              , "setAromatic"               ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.fbs.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.fbs.desc"       ), "isBetaSheetFavoring"     , "setBetaSheetFavoring"      ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.fbt.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.fbt.desc"       ), "isBetaTurnFavoring"      , "setBetaTurnFavoring"       ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ncp.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ncp.desc"       ), "isNegativelyChargedPolar", "setNegativelyChargedPolar" ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.pcp.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.pcp.desc"       ), "isPositivelyChargedPolar", "setPositivelyChargedPolar" ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.unp.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.unp.desc"       ), "isUnchargedPolar"        , "setUnchargedPolar"         ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.unf.name"       ), "Total and Chemical Fragments", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.unf.desc"       ), "isUnfolding"             , "setUnfolding"              ) );
            
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.n1.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.n1.desc"      ), "isManhattanNorm"              , "setManhattanNorm"               ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.n2.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.n2.desc"      ), "isEuclideanNorm"              , "setEuclideanNorm"               ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.am.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.am.desc"      ), "isArithmeticMeanOperator"     , "setArithmeticMeanOperator"      ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.p2.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.p2.desc"      ), "isQuadraticMeanOperator"      , "setQuadraticMeanOperator"       ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.p3.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.p3.desc"      ), "isPotentialMeanOperator"      , "setPotentialMeanOperator"       ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.hm.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.hm.desc"      ), "isHarmonicMeanOperator"       , "setHarmonicMeanOperator"        ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.v.name"       ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.v.desc"       ), "isVarianceOperator"           , "setVarianceOperator"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.s.name"       ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.s.desc"       ), "isSkewnessOperator"           , "setSkewnessOperator"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.k.name"       ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.k.desc"       ), "isKurtosisOperator"           , "setKurtosisOperator"            ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.sd.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.sd.desc"      ), "isStandardDeviationOperator"  , "setStandardDeviationOperator"   ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.vc.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.vc.desc"      ), "isVariationCoefficientOpertor", "setVariationCoefficientOpertor" ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ra.name"      ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ra.desc"      ), "isRangeOpertor"               , "setRangeOpertor"                ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.i50.name"     ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.i50.desc"     ), "isI50Opertor"                 , "setI50Opertor"                  ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.gowawa.name"  ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.gowawa.desc"  ), "isGowawaOperator"             , "setGowawaOperator"              ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.choquet.name" ), "Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.choquet.desc" ), "isChoquetOperator"            , "setChoquetOperator"             ) );
            
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.maxk.name" ), "Classic Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.maxk.desc" ), "getMaxK"                   , "setMaxK"                    ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ac.name"   ), "Classic Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ac.desc"   ), "isAutocorrelationOperator" , "setAutocorrelationOperator" ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.gv.name"   ), "Classic Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.gv.desc"   ), "isGravitationalOperator"   , "setGravitationalOperator"   ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ts.name"   ), "Classic Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.ts.desc"   ), "isTotalSumOperator"        , "setTotalSumOperator"        ) );
            properties.add(AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.es.name"   ), "Classic Aggregation Operators", NbBundle.getMessage( IndicesBasedOperators.class, "AggregationOperators.es.desc"   ), "isElectroTopStateOperator" , "setElectroTopStateOperator" ) );
            
            
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
    protected void compute( Peptide peptide ) 
    {
        double[] lovis;
        
        for ( EAminoacidLocal local : localsList )
        {
            if ( isPtt() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.ptt, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-ppt", peptide, this, true, nonClassicOperatorsList );            
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-ppt", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isGcp1() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.gcp1, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-gcp1", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-gcp1", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isGcp2() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.gcp2, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-gcp2", peptide, this, false, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-gcp2", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isEps() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.eps, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-eps", peptide, this, false, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-eps", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isScm() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.side_chain_mass, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-scm", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-scm", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isScv() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.side_chain_volume, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-scv", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-scv", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isPie() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.pie, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-pie", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-pie", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isPah() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.pah, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-pah", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-pah", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isPbs() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.pbs, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-pbs", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-pbs", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isIsa() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.isa, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-isa", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-isa", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isZ1() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.z1, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-z1", peptide, this, false, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-z1", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isZ2() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.z2, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-z2", peptide, this, false, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-z2", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isZ3() )
            {
                lovis = aminoacidProperties.getAminoacidPropertyValues( peptide.getSequence(), EAminoacidProperty.z3, local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-z3", peptide, this, false, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-z3", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
                      
            if ( isBoman() )
            {
                lovis = MD.bomanByAA( peptide.getSequence(), local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-Boman", peptide, this, false, nonClassicOperatorsList );            
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-Boman", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isCharge() )
            {
                lovis = MD.sumAndAvgByAA( peptide.getSequence(), ChargeScale.klein_hash(), local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-NetCharge(KLEP840101)", peptide, this, false, nonClassicOperatorsList );            
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-NetCharge(KLEP840101)", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
            
            if ( isHydrophilicity() )
            {
                lovis = MD.gravyByAA( peptide.getSequence(), HydrophilicityScale.kuhn_hydrov_hash(), local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-Hydrophilicity(KUHL950101)", peptide, this, true, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-Hydrophilicity(KUHL950101)", peptide, this, true, nonClassicOperatorsList, classicOperatorsList );
                
                lovis = MD.gravyByAA( peptide.getSequence(), local );
                nonClassicOperators.applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-GRAVY", peptide, this, false, nonClassicOperatorsList );
                classicOperators   .applyOperators( lovis, LocalTool.getAcronymAccordingToLocal( local ) + "-GRAVY", peptide, this, false, nonClassicOperatorsList, classicOperatorsList );
            }
        }
    }
    
    @Override
    public AlgorithmProperty[] getProperties()
    {
        return properties.toArray( new AlgorithmProperty[0] );
    }
    
    public boolean isPtt() 
    {
        return ptt;
    }
    
    public void setPtt( Boolean ptt )
    {
        this.ptt = ptt;
    }
    
    public boolean isGcp1()
    {
        return gcp1;
    }
    
    public void setGcp1( Boolean gcp1 )
    {
        this.gcp1 = gcp1;
    }
    
    public boolean isGcp2()
    {
        return gcp2;
    }
    
    public void setGcp2( Boolean gcp2 )
    {
        this.gcp2 = gcp2;
    }
    
    public boolean isEps() 
    {
        return eps;
    }
    
    public void setEps( Boolean eps )
    {
        this.eps = eps;
    }
    
    public boolean isScm()
    {
        return scm;
    }
    
    public void setScm( Boolean scm )
    {
        this.scm = scm;
    }
    
    public boolean isScv()
    {
        return scv;
    }
    
    public void setScv( Boolean scv )
    {
        this.scv = scv;
    }
    
    public boolean isPie()
    {
        return pie;
    }
    
    public void setPie( Boolean pie )
    {
        this.pie = pie;
    }
    
    public boolean isPah() 
    {
        return pah;
    }
    
    public void setPah( Boolean pah )
    {
        this.pah = pah;
    }
    
    public boolean isPbs()
    {
        return pbs;
    }
    
    public void setPbs( Boolean pbs )
    {
        this.pbs = pbs;
    }
    
    public boolean isIsa() 
    {
        return isa;
    }
    
    public void setIsa( Boolean isa )
    {
        this.isa = isa;
    }
    
    public boolean isZ1()
    {
        return z1;
    }
    
    public void setZ1( Boolean z1 )
    {
        this.z1 = z1;
    }
    
    public boolean isZ2()
    {
        return z2;
    }
    
    public void setZ2( Boolean z2 ) 
    {
        this.z2 = z2;
    }
    
    public boolean isZ3() 
    {
        return z3;
    }
    
    public void setZ3( Boolean z3 )
    {
        this.z3 = z3;
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
    
    public boolean isTotal() 
    {
        return total;
    }
    
    public void setTotal( Boolean total )
    {
        this.total = total;
        
        if ( total )
        {
            localsList.add( EAminoacidLocal.Total );
        }
        else
        {
            localsList.remove( EAminoacidLocal.Total );
        }
    }
    
    public boolean isAliphatic() 
    {
        return aliphatic;
    }
    
    public void setAliphatic( Boolean aliphatic )
    {
        this.aliphatic = aliphatic;
        
        if ( aliphatic )
        {
            localsList.add( EAminoacidLocal.Aliphatic );
        }
        else
        {
            localsList.remove( EAminoacidLocal.Aliphatic );
        }
    }
    
    public boolean isAlphaHelixFavoring() 
    {
        return alphaHelixFavoring;
    }
    
    public void setAlphaHelixFavoring( Boolean alphaHelixFavoring )
    {
        this.alphaHelixFavoring = alphaHelixFavoring;
        
        if ( alphaHelixFavoring )
        {
            localsList.add( EAminoacidLocal.AlphaHelixFavoring );
        }
        else
        {
            localsList.remove( EAminoacidLocal.AlphaHelixFavoring );
        }
    }
    
    public boolean isApolar()
    {
        return apolar;
    }
    
    public void setApolar( Boolean apolar )
    {
        this.apolar = apolar;
        
        if ( apolar )
        {
            localsList.add( EAminoacidLocal.Apolar );
        }
        else
        {
            localsList.remove( EAminoacidLocal.Apolar );
        }
    }
    
    public boolean isAromatic()
    {
        return aromatic;
    }
    
    public void setAromatic( Boolean aromatic )
    {
        this.aromatic = aromatic;
        
        if ( aromatic )
        {
            localsList.add( EAminoacidLocal.Aromatic );
        }
        else
        {
            localsList.remove( EAminoacidLocal.Aromatic );
        }
    }
    
    public boolean isBetaSheetFavoring() 
    {
        return betaSheetFavoring;
    }
    
    public void setBetaSheetFavoring( Boolean betaSheetFavoring )
    {
        this.betaSheetFavoring = betaSheetFavoring;
        
        if ( betaSheetFavoring )
        {
            localsList.add( EAminoacidLocal.BetaSheetFavoring );
        }
        else
        {
            localsList.remove( EAminoacidLocal.BetaSheetFavoring );
        }
    }
    
    public boolean isBetaTurnFavoring()
    {
        return betaTurnFavoring;
    }
    
    public void setBetaTurnFavoring( Boolean betaTurnFavoring )
    {
        this.betaTurnFavoring = betaTurnFavoring;
        
        if ( betaTurnFavoring )
        {
            localsList.add( EAminoacidLocal.BetaTurnFavoring );
        }
        else
        {
            localsList.remove( EAminoacidLocal.BetaTurnFavoring );
        }
    }
    
    public boolean isNegativelyChargedPolar() 
    {
        return negativelyChargedPolar;
    }
    
    public void setNegativelyChargedPolar( Boolean negativelyChargedPolar )
    {
        this.negativelyChargedPolar = negativelyChargedPolar;
        
        if ( negativelyChargedPolar )
        {
            localsList.add( EAminoacidLocal.NegativelyChargedPolar );
        }
        else
        {
            localsList.remove( EAminoacidLocal.NegativelyChargedPolar );
        }
    }
    
    public boolean isPositivelyChargedPolar() 
    {
        return positivelyChargedPolar;
    }
    
    public void setPositivelyChargedPolar( Boolean positivelyChargedPolar )
    {
        this.positivelyChargedPolar = positivelyChargedPolar;
        
        if ( positivelyChargedPolar )
        {
            localsList.add( EAminoacidLocal.PositivelyChargedPolar );
        }
        else
        {
            localsList.remove( EAminoacidLocal.PositivelyChargedPolar );
        }
    }
    
    public boolean isUnchargedPolar()
    {
        return unchargedPolar;
    }
    
    public void setUnchargedPolar( Boolean unchargedPolar )
    {
        this.unchargedPolar = unchargedPolar;
        
        if ( unchargedPolar )
        {
            localsList.add( EAminoacidLocal.UnchargedPolar );
        }
        else
        {
            localsList.remove( EAminoacidLocal.UnchargedPolar );
        }
    }
    
    public boolean isUnfolding() 
    {
        return unfolding;
    }
    
    public void setUnfolding( Boolean unfolding )
    {
        this.unfolding = unfolding;
        
        if ( unfolding )
        {
            localsList.add( EAminoacidLocal.Unfolding );
        }
        else
        {
            localsList.remove( EAminoacidLocal.Unfolding );
        }
    }
    
    public boolean isManhattanNorm() 
    {
        return manhattanNorm;
    }
    
    public void setManhattanNorm( Boolean manhattanNorm ) 
    {
        this.manhattanNorm = manhattanNorm;
        
        if ( manhattanNorm )
        {
            nonClassicOperatorsList.add( "N1" );
        }
        else
        {
            nonClassicOperatorsList.remove( "N1" );
        }
    }
    
    public boolean isEuclideanNorm()
    {
        return euclideanNorm;
    }
    
    public void setEuclideanNorm( Boolean euclideanNorm ) 
    {
        this.euclideanNorm = euclideanNorm;
        
        if ( euclideanNorm )
        {
            nonClassicOperatorsList.add( "N2" );
        }
        else
        {
            nonClassicOperatorsList.remove( "N2" );
        }
    }
    
    public boolean isArithmeticMeanOperator() 
    {
        return arithmeticMeanOperator;
    }
    
    public void setArithmeticMeanOperator( Boolean arithmeticMeanOperator )
    {
        this.arithmeticMeanOperator = arithmeticMeanOperator;
        
        if ( arithmeticMeanOperator )
        {
            nonClassicOperatorsList.add( "AM" );
        }
        else
        {
            nonClassicOperatorsList.remove( "AM" );
        }
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
    
    public Integer getMaxK()
    {
        return maxK;
    }
    
    public void setMaxK( Integer maxK )
    {
        this.maxK = maxK;
        classicOperators.setMaxK( maxK );
    }
}
