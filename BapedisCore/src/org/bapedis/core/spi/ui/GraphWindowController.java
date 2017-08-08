/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.ui;

import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
public interface GraphWindowController {

    TopComponent getGraphWindow();
    
    void openGraphWindow();
    
    void selectEdge(Edge edge);

    void selectNode(Node node);
    
    void centerOnNode(Node node);

    void closeGraphWindow();
}
