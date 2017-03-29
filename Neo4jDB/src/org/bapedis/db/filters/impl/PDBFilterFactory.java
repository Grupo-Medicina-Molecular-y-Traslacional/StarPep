/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = FilterFactory.class)
public class PDBFilterFactory implements FilterFactory {
    protected final PDBFilterSetupUI setupUI;

    public PDBFilterFactory() {
        setupUI = new PDBFilterSetupUI();
    }
    
    @Override
    public String getName() {
        return NbBundle.getMessage(PDBFilterFactory.class, "PDBFilterFactory.name");
    }

    @Override
    public Filter createFilter() {
        return new PDBFilter();
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }
    
}
