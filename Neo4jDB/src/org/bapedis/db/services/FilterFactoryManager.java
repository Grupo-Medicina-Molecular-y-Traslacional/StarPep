/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.services;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.filters.spi.FilterFactory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service=FilterFactoryManager.class)
public class FilterFactoryManager {
    protected final List<FilterFactory> builders;

    public FilterFactoryManager() {
        builders = new LinkedList<>();
        Collection<? extends FilterFactory> factories = Lookup.getDefault().lookupAll(FilterFactory.class);
        for(FilterFactory factory: factories){
            builders.add(factory);
        }
    }
    
    public FilterFactory getBuilder(Filter filter){
        for(FilterFactory factory: builders){
            if (factory.getFilterClass().equals(filter.getClass()))
                return factory;
        }
        return null;
    }
    
}
