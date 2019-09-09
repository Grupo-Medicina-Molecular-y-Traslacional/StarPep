/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.ui.components.SingleQueryPanel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.alg.SearchTag;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 10)
public class SingleQuerySeqSearchFactory implements SearchTag{
    private SequenceSearchSetupUI setupUI = new SequenceSearchSetupUI();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
       return NbBundle.getMessage(SingleQuerySeqSearch.class, "SingleQuerySeqSearch.name");
    }

    @Override
    public String getDescription() {
       return NbBundle.getMessage(SingleQuerySeqSearch.class, "SingleQuerySeqSearch.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new SingleQuerySeqSearch(this);
    }
    
}
