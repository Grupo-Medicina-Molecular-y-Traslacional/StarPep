/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.services.ProjectManager;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class GraphElementNavigatorModel {
    protected GraphElementType visualElement;
    protected final GraphElementAvailableColumnsModel nodeAvailableColumnsModel;
    
    public GraphElementNavigatorModel() {
        nodeAvailableColumnsModel = new GraphElementAvailableColumnsModel();
        Table table = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getNodeTable();
        nodeAvailableColumnsModel.addAvailableColumn(new GraphElementAttributeColumn(table.getColumn("label")));
        visualElement = GraphElementType.Node;
    }

    public GraphElementAvailableColumnsModel getNodeAvailableColumnsModel() {
        return nodeAvailableColumnsModel;
    }

    public GraphElementType getVisualElement() {
        return visualElement;
    }

    public void setVisualElement(GraphElementType visualElement) {
        this.visualElement = visualElement;
    }
    
}
