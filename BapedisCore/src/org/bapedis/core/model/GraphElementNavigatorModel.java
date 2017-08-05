/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author loge
 */
public class GraphElementNavigatorModel {
    protected GraphElementType visualElement;
    protected final GraphElementAvailableColumnsModel nodeAvailableColumnsModel;
    
    public GraphElementNavigatorModel() {
        nodeAvailableColumnsModel = new GraphElementAvailableColumnsModel();
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
