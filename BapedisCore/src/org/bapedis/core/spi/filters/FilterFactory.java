/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters;

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
