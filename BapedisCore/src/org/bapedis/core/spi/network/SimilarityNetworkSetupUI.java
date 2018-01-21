/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.network;

import org.bapedis.core.spi.algo.Algorithm;
import org.openide.WizardDescriptor;

/**
 *
 * @author loge
 */
public interface SimilarityNetworkSetupUI {
    
    WizardDescriptor.Panel<WizardDescriptor> getWizardPanels(Algorithm algorithm);
    
    void finishWizard(Algorithm algorithm);
}
