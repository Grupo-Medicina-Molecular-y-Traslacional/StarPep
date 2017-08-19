/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.filters.impl;

import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;

/**
 *
 * @author loge
 */
public class MetadataFilter implements Filter {

    protected final MetadataFilterFactory factory;
    protected AnnotationType annotationType;
    protected FilterOperator operator;
    protected String value;
    protected boolean matchCase;
    protected boolean negative;

    public MetadataFilter(MetadataFilterFactory factory) {
        this.factory = factory;
        operator = StringFilterOperator.values()[0];
    }

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
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

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public boolean isMatchCase() {
        return matchCase;
    }

    public void setMatchCase(boolean matchCase) {
        this.matchCase = matchCase;
    }

    @Override
    public String getDisplayName() {
        String text = annotationType.getDisplayName() + " " + operator + " " + value;
        return negative ? "Not (" + text + ")" : text;
    }

    @Override
    public boolean accept(Peptide peptide) {
        boolean accepted = false;
        String[] annotationValues = peptide.getAnnotationValues(annotationType);
        for (String annotationValue : annotationValues) {
            accepted = operator.applyTo(annotationValue, value, matchCase);
            if (accepted) {
                break;
            }
        }
         return negative ? !accepted : accepted;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

}