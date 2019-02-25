/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;

/**
 *
 * @author loge
 */
public enum StringFilterOperator implements FilterOperator {

    EQUALS("equal to") {

        @Override
        public boolean applyTo(Object obj, String operand, boolean matchCase) {
            return matchCase ? obj.equals(operand) : ((String) obj).toLowerCase().equals(operand.toLowerCase());
        }
    },
    NOT_EQUALS("not equal to") {
        @Override
        public boolean applyTo(Object obj, String operand, boolean matchCase) {
            return !matchCase ? obj.equals(operand) : ((String) obj).toLowerCase().equals(operand.toLowerCase());
        }
    },
    CONTAINS("contains") {

        @Override
        public boolean applyTo(Object obj, String operand, boolean matchCase) {
            return matchCase ? ((String) obj).contains(operand) : ((String) obj).toLowerCase().contains(operand.toLowerCase());
        }
    },
    STARTS_WITH("starts with") {

        @Override
        public boolean applyTo(Object obj, String operand, boolean matchCase) {
            return matchCase ? ((String) obj).startsWith(operand) : ((String) obj).toLowerCase().startsWith(operand.toLowerCase());
        }
    },
    ENDS_WITH("ends with") {

        @Override
        public boolean applyTo(Object obj, String operand, boolean matchCase) {
            return matchCase ? ((String) obj).endsWith(operand) : ((String) obj).toLowerCase().endsWith(operand.toLowerCase());
        }
    },
    REGEX("regex") {

        @Override
        public boolean applyTo(Object obj, String operand, boolean matchCase) {
            Pattern p = Pattern.compile(operand);
            Matcher m = p.matcher((String) obj);
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
        if (!operand.isEmpty() && text.equals("regex")){
            try{
                Pattern.compile(operand);
                return true;
            } catch(PatternSyntaxException ex){
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
                return false;
            }
        }
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
