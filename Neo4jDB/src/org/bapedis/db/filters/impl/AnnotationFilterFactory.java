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

/**
 *
 * @author loge
 */
public class AnnotationFilterFactory implements FilterFactory {
    protected String name;
    protected FilterSetupUI setupUI;

    public AnnotationFilterFactory() {
        name = NbBundle.getMessage(AnnotationFilterFactory.class, "AnnotationFilterFactory.name");
        setupUI = new AnnotationFilterSetupUI();
    }
    
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Filter createFilter() {
        return new AnnotationFilter();
    }

    @Override
    public FilterSetupUI getSetupUI() {
        return setupUI;
    }
    
}
