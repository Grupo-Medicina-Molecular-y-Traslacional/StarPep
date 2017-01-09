/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author loge
 */
public enum StringFilterOperator implements FilterOperator {

    EQUALS("equal to") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    return obj.equals(operand);
                }
            },
    NOT_EQUALS("not equal to"){
                @Override
                public boolean applyTo(Object obj, String operand) {
                    return !obj.equals(operand);
                }    
    },
    CONTAINS("contains") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    return ((String)obj).contains(operand);
                }
            },
    STARTS_WITH("starts with") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    return ((String)obj).startsWith(operand);
                }
            },
    ENDS_WITH("ends with") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    return ((String)obj).endsWith(operand);
                }
            },
    REGEX("regex") {

                @Override
                public boolean applyTo(Object obj, String operand) {
                    Pattern p = Pattern.compile(operand);
                    Matcher m = p.matcher((String)obj);
                    return m.matches();
                }
            };

    private final String text;

    private StringFilterOperator(String text) {
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

//    public static String[] getTextRepresentations() {
//        List<String> textRepresentations = new LinkedList<String>();
//
//        for (StringFilterOperator filterOperator : values()) {
//            textRepresentations.add(filterOperator.text);
//        }
//
//        return textRepresentations.toArray(new String[0]);
//    }
//
//    public static StringFilterOperator fromTextRepresentation(String text) {
//        for (StringFilterOperator filterOperator : values()) {
//            if (filterOperator.text.equals(text)) {
//                return filterOperator;
//            }
//        }
//        return null;
//    }
}
