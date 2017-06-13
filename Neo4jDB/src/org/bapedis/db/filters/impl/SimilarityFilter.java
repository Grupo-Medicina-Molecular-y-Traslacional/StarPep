/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;

/**
 *
 * @author loge
 */
public class SimilarityFilter implements Filter {
    protected final SimilarityFilterFactory factory;
    private String seq;
    private int k;

    public SimilarityFilter(SimilarityFilterFactory factory) {
        this.factory = factory;
    }
        
    @Override
    public String getDisplayName() {
        return "SimilarityFilter";
    }

    @Override
    public boolean accept(Peptide peptide) {
        return false;
    }

    public String getSeq() {
        return seq;
    }

    public void setSeq(String seq) {
        this.seq = seq;
    }

    public int getK() {
        return k;
    }

    public void setK(int k) {
        this.k = k;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }
    
}
