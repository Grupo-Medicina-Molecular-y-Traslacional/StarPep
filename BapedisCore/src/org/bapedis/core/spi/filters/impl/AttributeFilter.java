/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.filters.Filter;

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
    
    public AttributeFilter(){
        matchCase = false;
    }

    public AttributeFilter(PeptideAttribute attr, FilterOperator operator, String value) {
        this.attr = attr;
        this.operator = operator;
        this.value = value;
        matchCase = false;
    }

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
    public boolean accept(Peptide peptide) {
        Object objValue = peptide.getAttributeValue(attr);
        boolean accepted = operator.applyTo(objValue, value, matchCase);
//        accepted = (matchCase) ? operator.applyTo(objValue, value) : operator.applyTo(objValue.toString().toUpperCase(), value.toUpperCase());
        return negative ? !accepted : accepted;
    }

}
