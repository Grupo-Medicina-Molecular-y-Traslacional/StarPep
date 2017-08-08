/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SelectNodeOnGraph extends AbstractAction {

    private final Node node;

    public SelectNodeOnGraph(Node node) {
        this.node = node;
        putValue(NAME, NbBundle.getMessage(SelectNodeOnGraph.class, "SelectOnGraph.name"));        
    }    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        if (graphWC != null){
            graphWC.openGraphWindow();
            graphWC.selectNode(node);
        }
    }
    
}
