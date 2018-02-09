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
 * @author loge
 */
@ServiceProvider(service = FilterFactory.class, position = 400)
public class NonRedundantSetFilterFactory implements FilterFactory {

    private final NonRedundantSetFilterSetupUI setupUI = new NonRedundantSetFilterSetupUI();
    
    
    @Override
    public String getName() {
        return NbBundle.getMessage(NonRedundantSetFilter.class, "NonRedundantSetFilter.name");
    }

    @Override
    public Filter createFilter() {
        return new NonRedundantSetFilter(this);
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }
    
}
