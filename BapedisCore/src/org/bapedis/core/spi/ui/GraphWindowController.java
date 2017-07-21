/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.ui;

import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public interface GraphWindowController {

    void openGraphWindow();

    void selectNode(Node node);

    void closeGraphWindow();
}
