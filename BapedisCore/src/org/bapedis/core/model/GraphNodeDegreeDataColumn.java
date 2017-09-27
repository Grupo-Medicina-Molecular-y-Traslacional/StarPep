/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.services.ProjectManager;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class GraphNodeDegreeDataColumn implements GraphElementDataColumn{

    @Override
    public Class getColumnClass() {
        return Integer.class;
    }

    @Override
    public String getColumnName() {
        return NbBundle.getMessage(GraphNodeDegreeDataColumn.class, "GraphNodeDegreeDataColumn.name");
    }

    @Override
    public Object getValueFor(Element element) {
        Graph graph = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getGraphVisible();
        return graph.getDegree((Node)element);
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
