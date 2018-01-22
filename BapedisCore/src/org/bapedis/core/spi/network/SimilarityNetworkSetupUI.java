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
    
    /**
     * Collection of wizard panels. For each step in the wizard, a panel has to be provided
     * @return a <code>array</code> of wizard panels
     */    
    WizardDescriptor.Panel<WizardDescriptor>[] getWizardPanels(Algorithm algorithm);
    
    /**
     * The finish button of the wizard has been clicked
     * Save properties from wizard to algorithm
     */     
    void finishedWizard(WizardDescriptor wiz, Algorithm algorithm);
}
