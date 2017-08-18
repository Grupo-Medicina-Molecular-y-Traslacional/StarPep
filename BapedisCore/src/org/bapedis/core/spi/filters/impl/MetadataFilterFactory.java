/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service=FilterFactory.class)
public class MetadataFilterFactory implements FilterFactory {
    protected final String name;
    protected final FilterSetupUI setupUI;

    public MetadataFilterFactory() {
        name = NbBundle.getMessage(AttributeFilterFactory.class, "MetadataFilterFactory.name");
        setupUI = new MetadataFilterSetupUI();
    }    
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Filter createFilter() {
        return new MetadataFilter(this);
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }
    
}
