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
import org.gephi.graph.api.Edge;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
public class SelectEdgeOnGraph extends AbstractAction {

    private final Edge edge;

    public SelectEdgeOnGraph(Edge edge) {
        this.edge = edge;
        putValue(NAME, NbBundle.getMessage(SelectEdgeOnGraph.class, "SelectOnGraph.name"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        if (graphWC != null) {
            TopComponent tc = graphWC.getGraphWindow();
            if (tc.isOpened()) {
                graphWC.selectEdge(edge);
            }
        }
    }

}
