/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import org.bapedis.core.spi.filters.impl.FilterOperator;

/**
 *
 * @author loge
 */
public enum StringArrayFilterOperator implements FilterOperator {

    CONTAINS("contains") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    String arrayAsString = (String) obj;
                    return arrayAsString.contains(operand);
                }
            };

    private final String text;

    private StringArrayFilterOperator(String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

    @Override
    public boolean isValid(String operand) {
        return !operand.isEmpty();
    }
    
}
