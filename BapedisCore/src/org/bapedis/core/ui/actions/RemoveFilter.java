/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.model.FilterModel;
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
        id = "org.bapedis.core.ui.actions.RemoveFilter"
)
@ActionRegistration(
        displayName = "#CTL_RemoveFilter",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilter", position = 200)
})
public class RemoveFilter extends GlobalContextSensitiveAction<Filter> {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public RemoveFilter() {
        super(Filter.class);
        String name = NbBundle.getMessage(RemoveFilter.class, "CTL_RemoveFilter");
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
            Collection<? extends Filter> context = lkpResult.allInstances();
            if (!context.isEmpty()) {
                FilterModel filterModel = pc.getFilterModel();
                filterModel.remove(context.toArray(new Filter[0]));
            }
        }
    }

}
