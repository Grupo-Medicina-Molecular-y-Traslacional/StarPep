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
public enum LongFilterOperator implements FilterOperator {
    EQUALS("=") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    return obj.equals(operand);
                }
            },
    NOT_EQUALS("<>") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    return !obj.equals(operand);
                }

            },
    LESS("<") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    long val1 = (long)obj;
                    long val2 = Long.parseLong(operand);
                    return val1 < val2;
                }

            }, 
    LESS_OR_EQUALS("<=") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    long val1 = (long)obj;
                    long val2 = Long.parseLong(operand);
                    return val1 <= val2;
                }

            }, 
    GREATER(">") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    long val1 = (long)obj;
                    long val2 = Long.parseLong(operand);
                    return val1 > val2;
                }

            }, 
    GREATER_OR_EQUALS(">=") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    long val1 = (long)obj;
                    long val2 = Long.parseLong(operand);
                    return val1 >= val2;
                }

            };
    private final String text;

    private LongFilterOperator(String text) {
        this.text = text;
    }

    @Override
    public boolean isValid(String operand) {
        try {
            Long.parseLong(operand);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    @Override
    public String toString() {
        return text; //To change body of generated methods, choose Tools | Templates.
    }
    
}
