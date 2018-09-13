/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.layout.plugin;

import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.NetworkTag;


public abstract class AbstractLayoutFactory implements AlgorithmFactory, NetworkTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(AbstractLayoutFactory.class, "Layout.category");
    }
    
    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }    
}
