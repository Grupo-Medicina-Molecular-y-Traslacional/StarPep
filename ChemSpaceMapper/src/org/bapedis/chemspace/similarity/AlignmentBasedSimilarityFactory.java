/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.SimilarityTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class AlignmentBasedSimilarityFactory implements SimilarityTag {

    private final AlignmentBasedSimilarityPanel setupUI = new AlignmentBasedSimilarityPanel();
    
    @Override
    public String getName() {
        return NbBundle.getMessage(AlignmentBasedSimilarityFactory.class, "AlignmentBasedSimilarity.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(AlignmentBasedSimilarityFactory.class, "AlignmentBasedSimilarity.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public AbstractSimCoefficient createAlgorithm() {
        return new AlignmentBasedSimilarity(this);
    }

    @Override
    public String getCategory() {
        return null;
    }
    
}
