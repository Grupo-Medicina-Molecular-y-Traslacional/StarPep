/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.alg.FeatureExtractionTag;

/**
 *
 * @author beltran, loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 500)
public class AggregatedBomanIndexFactory implements AlgorithmFactory, FeatureExtractionTag 
{
    @Override
    public String getCategory() 
    {
        return NbBundle.getMessage( AggregatedBomanIndexFactory.class, "AggregatedBomanIndex.category" );
    }
    
    @Override
    public String getName() 
    {
        return NbBundle.getMessage( AggregatedBomanIndexFactory.class, "AggregatedBomanIndex.name" );
    }
    
    @Override
    public String getDescription() 
    {
        return NbBundle.getMessage( AggregatedBomanIndexFactory.class, "AggregatedBomanIndex.desc" );
    }
    
    @Override
    public AlgorithmSetupUI getSetupUI()
    {
        return null;
    }
    
    @Override
    public Algorithm createAlgorithm() 
    {
        return new AggregatedBomanIndex( this );
    }
    
}
