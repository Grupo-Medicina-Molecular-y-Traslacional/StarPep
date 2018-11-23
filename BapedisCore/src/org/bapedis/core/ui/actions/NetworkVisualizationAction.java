/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.core.ui.actions.NetworkVisualization"
)
@ActionRegistration(
        displayName = "#CTL_NetworkVisualization"
)
@ActionReferences({
    @ActionReference(path = "Menu/Window", position = 243)
})
public class NetworkVisualizationAction extends AbstractAction {

    private final GraphWindowController graphWC;

    public NetworkVisualizationAction() {
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (graphWC != null) {
            graphWC.openGraphWindow();
        }
    }

}
