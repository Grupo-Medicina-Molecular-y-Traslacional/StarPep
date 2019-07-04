/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Table;
import org.gephi.graph.impl.GraphStoreConfiguration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class GraphElementNavigatorModel {

    protected GraphElementType elementType;
    protected final GraphElementAvailableColumnsModel nodeAvailableColumnsModel;
    protected final GraphElementAvailableColumnsModel edgeAvailableColumnsModel;

    public GraphElementNavigatorModel() {
        // Node      
        Table nodeTable = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getNodeTable();
        nodeAvailableColumnsModel = new GraphElementAvailableColumnsModel(new String[]{ProjectManager.NODE_TABLE_PRO_NAME}, 
                new String[]{GraphStoreConfiguration.ELEMENT_TIMESET_COLUMN_ID});
        
        nodeAvailableColumnsModel.allKnownColumns.add(new GraphNodeDegreeDataColumn()); 
        
        nodeAvailableColumnsModel.addAvailableColumn(new GraphElementAttributeColumn(nodeTable.getColumn(ProjectManager.NODE_TABLE_PRO_NAME)));
        nodeAvailableColumnsModel.addAvailableColumn(new GraphElementAttributeColumn(nodeTable.getColumn(GraphStoreConfiguration.ELEMENT_LABEL_COLUMN_ID)));
        

        // Edge
        Table edgeTable = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getEdgeTable();
        
        String sourceName = NbBundle.getMessage(GraphEdgeAttributeColumn.class, "GraphElement.column.source");
        String targetName = NbBundle.getMessage(GraphEdgeAttributeColumn.class, "GraphElement.column.target");
        
        edgeAvailableColumnsModel = new GraphElementAvailableColumnsModel(new String[]{sourceName, GraphStoreConfiguration.ELEMENT_LABEL_COLUMN_ID, targetName}, 
                new String[]{GraphStoreConfiguration.ELEMENT_TIMESET_COLUMN_ID,
                             GraphStoreConfiguration.ELEMENT_ID_COLUMN_ID,
                             ProjectManager.EDGE_TABLE_PRO_XREF});
        
        edgeAvailableColumnsModel.availableColumns.add(new GraphEdgeAttributeColumn(sourceName, GraphEdgeAttributeColumn.Direction.Source));
        edgeAvailableColumnsModel.availableColumns.add(new GraphElementAttributeColumn(edgeTable.getColumn(GraphStoreConfiguration.ELEMENT_LABEL_COLUMN_ID)));
        edgeAvailableColumnsModel.availableColumns.add(new GraphEdgeAttributeColumn(targetName, GraphEdgeAttributeColumn.Direction.Target));
                
        elementType = GraphElementType.Node;
    }

    public GraphElementAvailableColumnsModel getNodeAvailableColumnsModel() {
        return nodeAvailableColumnsModel;
    }
    
    public GraphElementAvailableColumnsModel getEdgeAvailableColumnsModel() {
        return edgeAvailableColumnsModel;
    }    

    public GraphElementType getElementType() {
        return elementType;
    }

    public void setElementType(GraphElementType elementType) {
        this.elementType = elementType;
    }

}
