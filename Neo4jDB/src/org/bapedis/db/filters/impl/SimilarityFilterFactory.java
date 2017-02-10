/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service=FilterFactory.class)
public class SimilarityFilterFactory implements FilterFactory {

    SimilarityFilterSetupUI setupUI;
    
    public SimilarityFilterFactory(){
//        setupUI = new SimilarityFilterSetupUI();
    }
            
    @Override
    public String getName() {
        return "Similarity filter";
    }

    @Override
    public Filter createFilter() {
        return new SimilarityFilter();
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Class getFilterClass() {
        return SimilarityFilter.class;
    }

}
