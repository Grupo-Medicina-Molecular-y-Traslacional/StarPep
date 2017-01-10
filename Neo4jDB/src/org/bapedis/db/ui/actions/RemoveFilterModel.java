/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.ui.actions.WorkspaceContextSensitiveAction;
import org.bapedis.db.model.FilterModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.db.ui.actions.RemoveFilterModel"
)
@ActionRegistration(
        displayName = "#CTL_RemoveFilterModel",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilterModel", position = 200)
})
@NbBundle.Messages("CTL_RemoveFilterModel=Remove filter model")
public class RemoveFilterModel extends WorkspaceContextSensitiveAction<FilterModel> {

    public RemoveFilterModel() {
        super(FilterModel.class);
        putValue(NAME, NbBundle.getMessage(RemoveFilterModel.class, "CTL_RemoveFilterModel"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = NbBundle.getMessage(RemoveFilterModel.class, "RemoveFilterModel.dialog.confirm");
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {
            Workspace currentWs = pc.getCurrentWorkspace();
            FilterModel filterModel = currentWs.getLookup().lookup(FilterModel.class);
            currentWs.remove(filterModel);
            for(Workspace otherWs: pc.getWorkspaces()){
                if (otherWs != currentWs){
                    otherWs.remove(filterModel);
                }
            }
            pc.remove(filterModel);
        }
    }

}
