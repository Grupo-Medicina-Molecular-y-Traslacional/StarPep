/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.LookAndFeel;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.db.ui.actions.ShowGraph"
)
@ActionRegistration(
        displayName = "#CTL_ShowGraph",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 310)
})
public class ShowGraph extends AbstractAction {

    private final GraphWindowController graphWC;

    public ShowGraph() {
        String name = NbBundle.getMessage(ShowGraph.class, "CTL_ShowGraph");
        putValue(NAME, name);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (graphWC != null) {
            graphWC.openGraphWindow();
        }
    }

//    @Override
//    public JMenuItem getPopupPresenter() {
//        JMenu main = new JMenu(NbBundle.getMessage(ShowPeptideNodes.class, "CTL_ShowGraph"));
//        List<? extends Action> actionsForPath = Utilities.actionsForPath("Actions/ShowDataFromLibrary/InWorkspace");
//        for (Action action : actionsForPath) {
//            main.add(action);
//        }
//        return main;
//    }
}