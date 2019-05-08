/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.chemspace.spi.SimilarityCoefficient;
import org.bapedis.chemspace.spi.SimilarityCoefficientFactory;
import org.bapedis.chemspace.spi.SimilarityCoefficientSetupUI;

/**
 *
 * @author loge
 */
@ServiceProvider(service = SimilarityCoefficientFactory.class)
public class TanimotoCoefficientFactory implements SimilarityCoefficientFactory{

    @Override
    public String getName() {
        return NbBundle.getMessage(TanimotoCoefficientFactory.class, "TanimotoCoefficient.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(TanimotoCoefficientFactory.class, "TanimotoCoefficient.desc");
    }

    @Override
    public SimilarityCoefficientSetupUI getSetupUI() {
        return null;
    }

    @Override
    public SimilarityCoefficient createAlgorithm() {
        return new TanimotoCoefficient(this);
    }
    
}
