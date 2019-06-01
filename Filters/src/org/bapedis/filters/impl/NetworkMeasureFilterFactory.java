/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service=FilterFactory.class, position = 150)
public class NetworkMeasureFilterFactory implements FilterFactory {
    protected final String name;
    protected final FilterSetupUI setupUI;

    public NetworkMeasureFilterFactory() {
        name = NbBundle.getMessage(NetworkMeasureFilter.class, "NetworkMeasureFilter.name");
        setupUI = new NetworkMeasureFilterSetupUI();
    }
        
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Filter createFilter() {
        return new NetworkMeasureFilter(this);
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }    
}
