/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class CSNAlgorithmFactory implements AlgorithmFactory {

    private final CSNAlgorithmPanel setupUI = new CSNAlgorithmPanel();
    
    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.SimilarityNetwork;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        CSNAlgorithm csnAlgo = new CSNAlgorithm(this);
        CSNWizardSetupUI wizSetupUI = new CSNWizardSetupUI(csnAlgo);
        WizardDescriptor wiz = wizSetupUI.getWizardDescriptor();
        
        //The image in the left sidebar of the wizard is set like this:
        //wiz.putProperty(WizardDescriptor.PROP_IMAGE, ImageUtilities.loadImage("org/demo/wizard/banner.PNG", true));
        if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
           return csnAlgo;            
        }
        return null;
    }

}
