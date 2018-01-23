/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.network.wizard.CSNWizardPanel2;
import org.bapedis.network.wizard.CSNWizardPanel3;
import org.bapedis.network.wizard.CSNWizardPanel1;
import org.bapedis.network.wizard.CSNWizardPanel4;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.network.SimilarityNetworkSetupUI;
import org.openide.WizardDescriptor;

/**
 *
 * @author loge
 */
public class CSNSetupUI implements SimilarityNetworkSetupUI {

    @Override
    public WizardDescriptor.Panel<WizardDescriptor>[] getWizardPanels(Algorithm algorithm) {
        CSNAlgorithm csnAlgo = (CSNAlgorithm) algorithm;
        WizardDescriptor.Panel<WizardDescriptor>[] panels = new WizardDescriptor.Panel[4];
        panels[0] = new CSNWizardPanel1(csnAlgo);
        panels[1] = new CSNWizardPanel2(csnAlgo);
        panels[2] = new CSNWizardPanel3(csnAlgo);
        panels[3] = new CSNWizardPanel4(csnAlgo);
        
        return panels;        
    }

    @Override
    public void finishedWizard(WizardDescriptor wiz, Algorithm algorithm) {
        CSNAlgorithm csnAlgo = (CSNAlgorithm) algorithm;
        //
    }
    
}
