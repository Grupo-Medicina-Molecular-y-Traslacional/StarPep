/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import java.util.Iterator;
import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class AttributeFilter implements Filter {

    protected final AttributeFilterFactory factory;
    protected boolean negative;
    protected PeptideAttribute attr;
    protected FilterOperator operator;
    protected String value;
    protected boolean matchCase;

    public AttributeFilter(AttributeFilterFactory factory) {
        this.factory = factory;
        matchCase = false;
    }

    public AttributeFilter(PeptideAttribute attr, FilterOperator operator, String value) {
        AttributeFilterFactory foundFactory = null;
        for (Iterator<? extends FilterFactory> it = Lookup.getDefault().lookup(ProjectManager.class).getFilterFactoryIterator(); it.hasNext(); ) {
            FilterFactory f = it.next();
            if (f instanceof AttributeFilterFactory) {
                foundFactory = (AttributeFilterFactory) f;
                break;
            }
        }
        this.factory = foundFactory;
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
    public String getHTMLDisplayName() {
        return null;
    }
        
    @Override
    public String getDisplayName() {
        String text = attr + " " + operator + " " + value;
        return negative ? "Not (" + text + ")" : text;
    }

    @Override
    public boolean accept(Peptide peptide) {
        boolean accepted = false;
        Object objValue = peptide.getAttributeValue(attr);
        if (objValue.getClass().isArray()) {
            Object[] array = (Object[]) objValue;
            for (Object obj : array) {
                accepted = operator.applyTo(obj, value, matchCase);
                if (accepted) {
                    break;
                }
            }
        } else {
            accepted = operator.applyTo(objValue, value, matchCase);
        }
        return negative ? !accepted : accepted;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

    @Override
    public Algorithm getPreprocessing(Peptide[] targets) {
        return null;
    }

}
