/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.gephi.graph.api.Column;
import org.gephi.graph.api.Element;

/**
 *
 * @author loge
 */
public class GraphElementAttributeColumn<T extends Element> implements GraphElementDataColumn<T>{
    private final Column column;

    public GraphElementAttributeColumn(Column column) {
        this.column = column;
    }

    @Override
    public Class getColumnClass() {
        return column.getTypeClass();
    }

    @Override
    public String getColumnName() {
        return column.getTitle();
    }

    @Override
    public Object getValueFor(T element) {
        return element.getAttribute(column);
    }

    @Override
    public void setValueFor(T element, Object value) {
        element.setAttribute(column, value);
    }

    @Override
    public boolean isEditable() {
        return !column.isReadOnly();
    }

    @Override
    public Column getColumn() {
        return column;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 89 * hash + (this.column != null ? this.column.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphElementAttributeColumn other = (GraphElementAttributeColumn) obj;
        return this.column == other.column || (this.column != null && this.column.equals(other.column));
    }    

    
}
