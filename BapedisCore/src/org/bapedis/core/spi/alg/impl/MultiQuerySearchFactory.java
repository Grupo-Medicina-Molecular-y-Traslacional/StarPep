/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.SearchTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 20)
public class MultiQuerySearchFactory implements SearchTag {
    private SequenceSearchSetupUI setupUI = new SequenceSearchSetupUI(new MultiQuerySearchPanel());

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
       return NbBundle.getMessage(SingleQuerySearch.class, "MultiQuerySearch.name");
    }

    @Override
    public String getDescription() {
       return NbBundle.getMessage(SingleQuerySearch.class, "MultiQuerySearch.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new MultiQuerySearch(this);
    }
    
}
