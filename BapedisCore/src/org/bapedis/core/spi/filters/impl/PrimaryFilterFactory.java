/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import org.bapedis.core.model.PeptideAttribute;
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
public class PrimaryFilterFactory implements FilterFactory {

    public PrimaryFilterFactory() {
    }
    
    
    @Override
    public Filter createFilter(PeptideAttribute attr, FilterOperator op, String val) {
        return new PrimaryFilter(attr, op, val);
    }

    @Override
    public PeptideAttribute[] getAttributes() {
        return new PeptideAttribute[]{
            new PeptideAttribute("id", "ID", String.class),
            new PeptideAttribute("seq", "Sequence", String.class),
            new PeptideAttribute("length", "Lenght", Integer.class)
        };
    }

    
}
