/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.alg.SimilarityTag;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class TanimotoCoefficientFactory implements SimilarityTag {

    @Override
    public String getName() {
        return NbBundle.getMessage(TanimotoCoefficientFactory.class, "TanimotoCoefficient.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(TanimotoCoefficientFactory.class, "TanimotoCoefficient.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public AbstractSimCoefficient createAlgorithm() {
        return new TanimotoCoefficient(this);
    }

    @Override
    public String getCategory() {
        return null;
    }

}
