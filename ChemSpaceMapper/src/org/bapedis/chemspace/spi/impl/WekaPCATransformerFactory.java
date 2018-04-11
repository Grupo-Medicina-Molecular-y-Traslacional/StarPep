/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.chemspace.spi.TwoDTransformer;
import org.bapedis.chemspace.spi.TwoDTransformerFactory;
import org.bapedis.chemspace.spi.TwoDTransformerSetupUI;

/**
 *
 * @author loge
 */
@ServiceProvider(service = TwoDTransformerFactory.class)
public class WekaPCATransformerFactory implements TwoDTransformerFactory{

    @Override
    public String getName() {
        return NbBundle.getMessage(WekaPCATransformer.class, "WekaPCATransformer.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(WekaPCATransformer.class, "WekaPCATransformer.desc");
    }

    @Override
    public TwoDTransformerSetupUI getSetupUI() {
        return null;
    }

    @Override
    public TwoDTransformer createAlgorithm() {
        return new WekaPCATransformer();
    }
    
}
