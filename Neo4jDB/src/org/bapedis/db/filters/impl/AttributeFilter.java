/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import java.util.Arrays;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.model.NeoPeptide;

/**
 *
 * @author loge
 */
public class AttributeFilter implements Filter {

    protected boolean negative;
    protected PeptideAttribute attr;
    protected FilterOperator operator;
    protected String value;
    protected boolean matchCase;

    public PeptideAttribute getAttribute() {
        return attr;
    }

    public void setAttribute(PeptideAttribute attr) {
        this.attr = attr;
    }

    public FilterOperator getOperator() {
        return operator;
    }

    public void setOperator(FilterOperator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isMatchCase() {
        return matchCase;
    }

    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    @Override
    public String getDisplayName() {
        String text = attr + " " + operator + " " + value;
        return negative ? "Not (" + text + ")" : text;
    }

    @Override
    public boolean accept(NeoPeptide peptide) {
        boolean accepted = false;
        if (peptide.getAttributes().contains(attr)) {
            Object objValue = peptide.getAttributeValue(attr);
            if (String[].class.equals(attr.getType())) {
                String strValue = Arrays.toString((String[]) objValue);
                accepted = (matchCase) ? operator.applyTo(strValue, value) : operator.applyTo(strValue.toUpperCase(), value.toUpperCase());
            } else if (String.class.equals(attr.getType())) {
                accepted = (matchCase) ? operator.applyTo(objValue, value) : operator.applyTo(objValue.toString().toUpperCase(), value.toUpperCase());
            } else { // The matchCase is ignored
                accepted = operator.applyTo(objValue, value);
            }
        }
        return negative ? !accepted : accepted;
    }

}
