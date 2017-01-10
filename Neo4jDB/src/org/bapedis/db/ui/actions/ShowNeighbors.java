/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import static javax.swing.Action.NAME;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.ui.actions.WorkspaceContextSensitiveAction;
import org.bapedis.db.services.NeoPeptideManager;
import org.bapedis.db.model.NeoNeighborsModel;
import org.bapedis.db.model.NeoPeptideModel;
import org.bapedis.db.ui.NeoPeptideModelTopComponent;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.db.ui.actions.ShowNeighbors"
)
@ActionRegistration(
        displayName = "#CTL_ShowNeighbors",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 110)})
public class ShowNeighbors extends WorkspaceContextSensitiveAction<NeoPeptideModel>  implements Presenter.Menu {

    protected JCheckBoxMenuItem menuItem;

    public ShowNeighbors() {
        super(NeoPeptideModel.class);
        putValue(NAME, NbBundle.getMessage(ShowNeighbors.class, "CTL_ShowNeighbors"));
        menuItem = new JCheckBoxMenuItem(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        final NeoPeptideModelTopComponent tc = (NeoPeptideModelTopComponent) WindowManager.getDefault().findTopComponent("NeoPeptideModelTopComponent");
        tc.showNeighborsPanel(menuItem.isSelected());
        final NeoPeptideManager npc = Lookup.getDefault().lookup(NeoPeptideManager.class);
        npc.setLoadNeighbors(menuItem.isSelected());
        final Workspace workspace = pc.getCurrentWorkspace();
        if (menuItem.isSelected()) {
            tc.setNeighborBusyLabel();
            tc.open();
            tc.requestActive();
            tc.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    try {
                        npc.setNeoNeighborsTo(workspace);
                    } catch (Exception ex) {
                        ex.printStackTrace();
                        tc.setErrorLabel();
                    } finally {
                        tc.setCursor(Cursor.getDefaultCursor());
                    }
                }
            });
        } else {
            NeoNeighborsModel oldModel = workspace.getLookup().lookup(NeoNeighborsModel.class);
            if (oldModel != null) {
                workspace.remove(oldModel);
            }
        }
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return menuItem;
    }

}
