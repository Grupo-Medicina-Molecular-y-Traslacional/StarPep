/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ShowGraph extends WorkspaceContextSensitiveAction<AttributesModel> {

    private final GraphWindowController graphWC;

    public ShowGraph() {
        super(AttributesModel.class);
        String name = NbBundle.getMessage(ShowGraph.class, "CTL_GraphVisualization");
        putValue(NAME, name);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (graphWC != null) {
            graphWC.openGraphWindow();
        }
    }

}
