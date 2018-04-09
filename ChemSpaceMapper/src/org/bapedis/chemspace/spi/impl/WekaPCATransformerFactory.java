/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import org.bapedis.chemspace.spi.ThreeDTransformer;
import org.bapedis.chemspace.spi.ThreeDTransformerFactory;
import org.bapedis.chemspace.spi.ThreeDTransformerSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = ThreeDTransformerFactory.class)
public class WekaPCATransformerFactory implements ThreeDTransformerFactory{

    @Override
    public String getName() {
        return NbBundle.getMessage(WekaPCATransformer.class, "WekaPCATransformer.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(WekaPCATransformer.class, "WekaPCATransformer.desc");
    }

    @Override
    public ThreeDTransformerSetupUI getSetupUI() {
        return null;
    }

    @Override
    public ThreeDTransformer createAlgorithm() {
        return new WekaPCATransformer();
    }
    
}
