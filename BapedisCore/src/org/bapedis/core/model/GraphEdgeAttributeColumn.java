/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.openide.util.NbBundle;

/**
 *
 * @author Home
 */
public class GraphEdgeAttributeColumn implements GraphElementDataColumn{
    public static enum Direction{Source, Targe};
    protected final Direction direction;

    public GraphEdgeAttributeColumn(Direction direction) {
        this.direction = direction;
    }
    
    @Override
    public Class getColumnClass() {
        return String.class;
    }

    @Override
    public String getColumnName() {
        return (direction == Direction.Source)? NbBundle.getMessage(GraphEdgeAttributeColumn.class, "GraphElement.column.source"):
                                                NbBundle.getMessage(GraphEdgeAttributeColumn.class, "GraphElement.column.target");                        
    }

    @Override
    public Object getValueFor(Element element) {
        return (direction == Direction.Source)?((Edge)element).getSource().getAttribute("name"): ((Edge)element).getTarget().getAttribute("name");
    }

    @Override
    public void setValueFor(Element element, Object value) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isEditable() {
        return false;
    }

    @Override
    public Column getColumn() {
        return null;
    }
    
}
