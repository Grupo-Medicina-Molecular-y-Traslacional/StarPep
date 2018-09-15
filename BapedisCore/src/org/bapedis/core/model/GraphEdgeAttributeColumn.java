/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class GraphEdgeAttributeColumn implements GraphElementDataColumn{
    public static enum Direction{Source, Target};
    protected final Direction direction;
    protected final String name;
    
    public GraphEdgeAttributeColumn( Direction direction){
        this((direction == Direction.Source)? NbBundle.getMessage(GraphEdgeAttributeColumn.class, "GraphElement.column.source"):
                                                NbBundle.getMessage(GraphEdgeAttributeColumn.class, "GraphElement.column.target"),
                direction);
    }

    public GraphEdgeAttributeColumn(String name, Direction direction) {
        this.name = name;
        this.direction = direction;
    }
    
    @Override
    public Class getColumnClass() {
        return String.class;
    }

    @Override
    public String getColumnName() {
        return name;                        
    }

    @Override
    public Object getValueFor(Element element) {
        return (direction == Direction.Source)?((Edge)element).getSource().getAttribute(ProjectManager.NODE_TABLE_PRO_NAME): ((Edge)element).getTarget().getAttribute(ProjectManager.NODE_TABLE_PRO_NAME);
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
