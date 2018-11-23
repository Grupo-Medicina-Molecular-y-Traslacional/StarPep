/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.RemoveQuery"
)
@ActionRegistration(
        displayName = "#CTL_RemoveQuery",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditQuery", position = 200)
})
public class RemoveQuery extends GlobalContextSensitiveAction<Metadata> {

    protected final ProjectManager pc;

    public RemoveQuery() {
        super(Metadata.class);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        String name = NbBundle.getMessage(RemoveQuery.class, "CTL_RemoveQuery");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            Collection<? extends Metadata> context = lkpResult.allInstances();
            if (!context.isEmpty()) {
                QueryModel queryModel = pc.getQueryModel();
                queryModel.remove(context.toArray(new Metadata[0]));
            }
        }
    }

}
