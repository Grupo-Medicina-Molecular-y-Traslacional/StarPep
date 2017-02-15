/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */

public class TopologicFilterFactory {
    protected final String name;
    protected  FilterSetupUI setupUI;

    public TopologicFilterFactory() {
        name = NbBundle.getMessage(TopologicFilterFactory.class, "TopologicFilterFactory.name");
//        setupUI = new TopologicFilterSetupUI();
    }

    
    public String getName() {
        return name;
    }

    public Filter createFilter() {
        return new TopologicFilter();
    }

    public FilterSetupUI getSetupUI() {
        return setupUI;
    }

    public Class getFilterClass() {
        return TopologicFilter.class;
    }
    
}
