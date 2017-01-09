/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import org.bapedis.core.controller.ProjectController;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.ui.actions.GlobalContextSensitiveAction;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.model.FilterModel;
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
        id = "org.bapedis.db.ui.actions.RemoveFilter"
)
@ActionRegistration(
        displayName = "#CTL_RemoveFilter",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilter", position = 100)
})
@NbBundle.Messages("CTL_RemoveFilter=Remove filter")
public class RemoveFilter extends GlobalContextSensitiveAction<Filter> {
    protected final ProjectController pc;

    public RemoveFilter() {
        super(Filter.class);
        pc = Lookup.getDefault().lookup(ProjectController.class);
        String name = NbBundle.getMessage(RemoveFilter.class, "CTL_RemoveFilter");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/db/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends Filter> context = lkpResult.allInstances();
        if (!context.isEmpty()){
            Filter filter = context.iterator().next();
            Workspace workspace = pc.getProject().getCurrentWorkspace();
            FilterModel filterModel = workspace.getLookup().lookup(FilterModel.class);
            filterModel.removeFilter(filter);
        }
    }

}
