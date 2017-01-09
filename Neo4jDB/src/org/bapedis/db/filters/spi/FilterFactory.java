/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.spi;

/**
 *
 * @author loge
 */
public interface FilterFactory {
    String getName();
    Filter createFilter();
    FilterSetupUI getSetupUI();
    Class getFilterClass();
}
