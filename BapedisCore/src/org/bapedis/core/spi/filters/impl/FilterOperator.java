/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

/**
 *
 * @author loge
 */
public interface FilterOperator {
    boolean applyTo(Object obj, String operand);
    boolean isValid(String operand);
}
