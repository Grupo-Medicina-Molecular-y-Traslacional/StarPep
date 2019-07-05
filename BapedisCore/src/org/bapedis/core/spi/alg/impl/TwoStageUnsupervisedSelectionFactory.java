/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.FeatureSelectionTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = FeatureSelectionTag.class, position = 0)
public class TwoStageUnsupervisedSelectionFactory implements FeatureSelectionTag {

    TwoStageUnsupervisedSelectionSetupUI setupUI = new TwoStageUnsupervisedSelectionSetupUI();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(TwoStageUnsupervisedSelection.class, "TwoStageUnsupervisedSelection.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(TwoStageUnsupervisedSelection.class, "TwoStageUnsupervisedSelection.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new TwoStageUnsupervisedSelection(this);
    }
    
}
