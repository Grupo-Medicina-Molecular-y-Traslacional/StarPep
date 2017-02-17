/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.impl.StringFilterOperator;
import org.bapedis.db.model.AnnotationType;

/**
 *
 * @author loge
 */
public class AnnotationFilter implements Filter {
    protected AnnotationType annotationType;
    protected StringFilterOperator operator;
    protected String value;

    public AnnotationType getAnnotationType() {
        return annotationType;
    }

    public void setAnnotationType(AnnotationType annotationType) {
        this.annotationType = annotationType;
    }

    public StringFilterOperator getOperator() {
        return operator;
    }

    public void setOperator(StringFilterOperator operator) {
        this.operator = operator;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    
    @Override
    public String getDisplayName() {
        return annotationType.getDisplayName() + operator + value;
    }

    @Override
    public boolean accept(Peptide peptide) {
        return true;
    }
    
}
