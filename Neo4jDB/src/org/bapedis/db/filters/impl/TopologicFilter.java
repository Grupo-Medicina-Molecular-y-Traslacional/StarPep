/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import java.util.Arrays;
import org.bapedis.core.model.Attribute;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.model.NeoNeighbor;
import org.bapedis.db.model.NeoNeighborsModel;
import org.bapedis.db.model.NeoPeptide;

/**
 *
 * @author loge
 */
public class TopologicFilter implements Filter {

    protected TopologicCondition topologicCond;
    protected IntegerFilterOperator degreeOp;
    protected String degreeValue;
    protected Attribute neighborCondAttr;
    protected FilterOperator neighborCondOp;
    protected String neighborCondValue;
    protected boolean neighborCondMatchCase;
    protected boolean neighborCondNegative;

    public enum TopologicCondition {

        RFLATIONSHIP, DEGREE
    }

    public TopologicFilter() {
        topologicCond = TopologicCondition.RFLATIONSHIP;
        degreeOp = IntegerFilterOperator.EQUALS;
    }

    public TopologicCondition getTopologicCondition() {
        return topologicCond;
    }

    public void setTopologicCondition(TopologicCondition topologicCond) {
        this.topologicCond = topologicCond;
    }

    public IntegerFilterOperator getDegreeOperator() {
        return degreeOp;
    }

    public void setDegreeOperator(IntegerFilterOperator degreeOp) {
        this.degreeOp = degreeOp;
    }

    public String getDegreeValue() {
        return degreeValue;
    }

    public void setDegreeValue(String degreeValue) {
        this.degreeValue = degreeValue;
    }

    public Attribute getNeighborCondAttribute() {
        return neighborCondAttr;
    }

    public void setNeighborCondAttribute(Attribute neighborCondAttr) {
        this.neighborCondAttr = neighborCondAttr;
    }

    public FilterOperator getNeighborCondOperator() {
        return neighborCondOp;
    }

    public void setNeighborCondOperator(FilterOperator neighborCondOp) {
        this.neighborCondOp = neighborCondOp;
    }

    public String getNeighborCondValue() {
        return neighborCondValue;
    }

    public void setNeighborCondValue(String neighborCondValue) {
        this.neighborCondValue = neighborCondValue;
    }

    public boolean isNeighborCondMatchCase() {
        return neighborCondMatchCase;
    }

    public void setNeighborCondMatchCase(boolean neighborCondMatchCase) {
        this.neighborCondMatchCase = neighborCondMatchCase;
    }

    public boolean isNeighborCondNegative() {
        return neighborCondNegative;
    }

    public void setNeighborCondNegative(boolean neighborCondNegative) {
        this.neighborCondNegative = neighborCondNegative;
    }

    @Override
    public String getDisplayName() {
        String condition = "";
        switch (topologicCond) {
            case RFLATIONSHIP:
                condition = "Relationship";
                break;
            case DEGREE:
                condition = "Degree " + degreeOp + " " + degreeValue;
                break;
        }
        String neighbor = neighborCondAttr + " " + neighborCondOp + " " + neighborCondValue;
        return condition + " - " + (neighborCondNegative ? "Not (" + neighbor + ")" : neighbor);
    }

    @Override
    public boolean accept(NeoPeptide peptide) {
        NeoNeighborsModel neoModel = peptide.getNeighbors();
        int count = 0;
        boolean accepted;
        for (NeoNeighbor neighbor : neoModel.getNeighbors()) {
            accepted = false;
            if (neighbor.getAttributes().contains(neighborCondAttr)) {
                Object objValue = neighbor.getAttributeValue(neighborCondAttr);
                if (String[].class.equals(neighborCondAttr.getType())) {
                    String strValue = Arrays.toString((String[]) objValue);
                    accepted = (neighborCondMatchCase) ? neighborCondOp.applyTo(strValue, neighborCondValue) : neighborCondOp.applyTo(strValue.toUpperCase(), neighborCondValue.toUpperCase());
                } else if (String.class.equals(neighborCondAttr.getType())) {
                    accepted = (neighborCondMatchCase) ? neighborCondOp.applyTo(objValue, neighborCondValue) : neighborCondOp.applyTo(objValue.toString().toUpperCase(), neighborCondValue.toUpperCase());
                } else { // The matchCase is ignored
                    accepted = neighborCondOp.applyTo(objValue, neighborCondValue);
                }
            }
            if (accepted) {
                count++;
            }
        }
        switch (topologicCond) {
            case RFLATIONSHIP:
                return count > 0;
            case DEGREE:
                return degreeOp.applyTo(count, degreeValue);
        }
        return false;
    }

}
