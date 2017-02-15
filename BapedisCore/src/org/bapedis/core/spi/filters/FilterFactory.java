/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters;

import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.filters.impl.FilterOperator;

/**
 *
 * @author loge
 */
public interface FilterFactory {
    PeptideAttribute[] getAttributes();
    Filter createFilter(PeptideAttribute attr, FilterOperator op, String val);
//    FilterSetupUI getSetupUI();
//    Class getFilterClass();
}
