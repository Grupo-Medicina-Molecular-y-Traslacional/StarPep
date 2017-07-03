/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.db.ui.NeoGraphSceneDescription;
import org.bapedis.db.ui.NeoGraphPreViewDescription;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
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
    ,
    @ActionReference(path = "Actions/ShowDataFromLibrary/Peptides", position = 200)
})
public class ShowGraph extends AbstractAction implements Presenter.Popup {

    public ShowGraph() {
        String name = NbBundle.getMessage(ShowGraph.class, "CTL_ShowGraph");
        putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent graphTC = WindowManager.getDefault().findTopComponent("GraphTC");
        if (graphTC == null) {
            MultiViewDescription[] multiviews = new MultiViewDescription[2];
            multiviews[0] = new NeoGraphSceneDescription();
            multiviews[1] = new NeoGraphPreViewDescription();
            graphTC = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
            graphTC.setName("GraphTC");
            graphTC.setDisplayName(NbBundle.getMessage(ShowGraph.class, "CTL_GraphTC"));
        }
        graphTC.open();
        graphTC.requestActive();
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenu main = new JMenu(NbBundle.getMessage(ShowPeptideNodes.class, "CTL_ShowGraph"));
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Actions/ShowDataFromLibrary/InWorkspace");
        for (Action action : actionsForPath) {
            main.add(action);
        }
        return main;
    }

}
