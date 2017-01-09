/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.filters.spi.FilterFactory;
import org.bapedis.db.filters.spi.FilterSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service=FilterFactory.class)
public class TopologicFilterFactory implements FilterFactory{
    protected final String name;
    protected final FilterSetupUI setupUI;

    public TopologicFilterFactory() {
        name = NbBundle.getMessage(TopologicFilterFactory.class, "TopologicFilterFactory.name");
        setupUI = new TopologicFilterSetupUI();
    }

    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Filter createFilter() {
        return new TopologicFilter();
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Class getFilterClass() {
        return TopologicFilter.class;
    }
    
}
