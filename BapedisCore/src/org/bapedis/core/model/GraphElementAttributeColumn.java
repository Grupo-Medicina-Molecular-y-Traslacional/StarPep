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
public class GraphElementAttributeColumn implements GraphElementDataColumn{
    private final Column column;
    private final String name;

    public GraphElementAttributeColumn(Column column) {
        this(column.getTitle(), column);
    }
        
    public GraphElementAttributeColumn(String name, Column column) {
        this.name = name;
        this.column = column;
    }

    @Override
    public Class getColumnClass() {
        return column.getTypeClass();
    }

    @Override
    public String getColumnName() {
        return name;
    }

    @Override
    public Object getValueFor(Element element) {
        return element.getAttribute(column);
    }

    @Override
    public void setValueFor(Element element, Object value) {
        element.setAttribute(column, value);
    }

    @Override
    public boolean isEditable() {
        return false;
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
