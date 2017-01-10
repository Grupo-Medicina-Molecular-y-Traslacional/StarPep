/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.model.Workspace;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.RemoveOtherWorkspaces"
)
@ActionRegistration(
        displayName = "#CTL_RemoveOtherWorkspaces",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/File/RemoveWorkspace", position = 200),
    @ActionReference(path = "Actions/RemoveWorkspace", position = 200)
})
public class RemoveOtherWorkspaces extends AbstractAction implements LookupListener {

    protected ProjectManager pc;
    protected Lookup.Result<Workspace> lkpResult;

    public RemoveOtherWorkspaces() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        lkpResult = pc.getLookup().lookupResult(Workspace.class);
        lkpResult.addLookupListener(this);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/removeWorkspace.png", false));
        putValue(NAME, NbBundle.getMessage(RemoveCurrentWorkspace.class, "CTL_RemoveOtherWorkspaces"));
        setEnabled(pc.getLookup().lookupAll(Workspace.class).size() > 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = NbBundle.getMessage(RemoveOtherWorkspaces.class, "RemoveOtherWorkspaces.dialog.confirm");
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {
            Collection<? extends Workspace> workspaces = pc.getLookup().lookupAll(Workspace.class);
            for (Workspace ws : workspaces) {
                if (pc.getCurrentWorkspace() != ws) {
                    pc.remove(ws);
                }
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        setEnabled(lkpResult.allInstances().size() > 1);
    }

}
