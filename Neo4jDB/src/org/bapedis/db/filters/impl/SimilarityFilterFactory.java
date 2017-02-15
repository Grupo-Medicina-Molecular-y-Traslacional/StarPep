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
public class SimilarityFilterFactory  {

    SimilarityFilterSetupUI setupUI;
    
    public SimilarityFilterFactory(){
//        setupUI = new SimilarityFilterSetupUI();
    }
            

    public String getName() {
        return "Similarity filter";
    }

    public Filter createFilter() {
        return new SimilarityFilter();
    }


    public FilterSetupUI getSetupUI() {
        return setupUI;
    }

    public Class getFilterClass() {
        return SimilarityFilter.class;
    }

}
