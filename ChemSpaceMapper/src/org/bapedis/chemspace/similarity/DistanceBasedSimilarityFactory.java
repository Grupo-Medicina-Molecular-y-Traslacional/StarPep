/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.SimilarityTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class DistanceBasedSimilarityFactory implements SimilarityTag {

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(DistanceBasedSimilarity.class,"DistanceBasedSimilarity.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(DistanceBasedSimilarity.class,"DistanceBasedSimilarity.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;                
    }

    @Override
    public Algorithm createAlgorithm() {
        return new DistanceBasedSimilarity(this);
    }
    
}
