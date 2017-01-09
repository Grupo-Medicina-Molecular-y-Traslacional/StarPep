/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

/**
 *
 * @author loge
 */
public class FilterHelper {

    public static FilterOperator[] getOperators(Class type) {
        if (Integer.class.equals(type)) {
            return IntegerFilterOperator.values();
        } else if (Long.class.equals(type)) {
            return LongFilterOperator.values();
        } else if (String[].class.equals(type)) {
            return StringArrayFilterOperator.values();
        }
        return StringFilterOperator.values();
    }
}
