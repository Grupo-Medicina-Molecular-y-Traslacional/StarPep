/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.impl.SequenceSearch;
import org.bapedis.core.spi.algo.impl.SequenceSearchFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;

/**
 *
 * @author loge
 */
public class SeqAlignmentFilter implements Filter{
    private final SeqAlignmentFilterFactory factory;
    private final SequenceSearch searchAlgo;

    public SeqAlignmentFilter(SeqAlignmentFilterFactory factory) {
        this.factory = factory;
        searchAlgo = (SequenceSearch) new SequenceSearchFactory().createAlgorithm();        
    }   

    public SequenceSearch getSearchAlgorithm() {
        return searchAlgo;
    }
            
    @Override
    public String getDisplayName() {
        return "";
    }

    @Override
    public boolean accept(Peptide peptide) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }
    
}
