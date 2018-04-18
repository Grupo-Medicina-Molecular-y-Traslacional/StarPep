/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import org.bapedis.chemspace.spi.SimilarityMeasure;
import org.bapedis.chemspace.spi.SimilarityMeasureFactory;
import org.bapedis.chemspace.spi.SimilarityMeasureSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = SimilarityMeasureFactory.class)
public class TanimotoCoefficientFactory implements SimilarityMeasureFactory{

    @Override
    public String getName() {
        return NbBundle.getMessage(TanimotoCoefficientFactory.class, "TanimotoCoefficient.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(TanimotoCoefficientFactory.class, "TanimotoCoefficient.desc");
    }

    @Override
    public SimilarityMeasureSetupUI getSetupUI() {
        return null;
    }

    @Override
    public SimilarityMeasure createAlgorithm() {
        return new TanimotoCoefficient(this);
    }
    
}
