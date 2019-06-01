/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.impl.FilterOperator;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Node;

/**
 *
 * @author Loge
 */
public class NetworkMeasureFilter implements Filter {

    protected final NetworkMeasureFilterFactory factory;
    protected boolean negative;
    protected Column attr;
    protected FilterOperator operator;
    protected String value;

    public NetworkMeasureFilter(NetworkMeasureFilterFactory factory) {
        this.factory = factory;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    public Column getAttribute() {
        return attr;
    }

    public void setAttribute(Column attr) {
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

    @Override
    public String getHTMLDisplayName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        if (attr == null || operator == null || value == null) {
            return "Invalid filter";
        }
        String text = attr.getTitle() + " " + operator + " " + value;
        return negative ? "Not (" + text + ")" : text;
    }

    @Override
    public Algorithm getPreprocessing(Peptide[] targets) {
        return null;
    }

    @Override
    public boolean accept(Peptide peptide) {
        if (attr == null || operator == null || value == null) {
            return false;
        }
        Node graphNode = peptide.getGraphNode();
        Object objValue = graphNode.getAttribute(attr);
        boolean accepted = operator.applyTo(objValue, value, true);        
        return negative ? !accepted : accepted;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

}
