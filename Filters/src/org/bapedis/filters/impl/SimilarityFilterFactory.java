/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;

/**
 *
 * @author loge
 */
public class SimilarityFilterFactory implements FilterFactory  {

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
        return new SimilarityFilter(this);
    }


    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }


}
