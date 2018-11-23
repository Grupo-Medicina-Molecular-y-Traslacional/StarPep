/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.List;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.MetadataSelectorPanel;
import org.bapedis.core.ui.components.SetupDialog;
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
/**
 *
 * @author loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.EditQuery"
)
@ActionRegistration(
        displayName = "#CTL_EditQuery",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditQuery", position = 100)
})
public class EditQuery extends GlobalContextSensitiveAction<Metadata> {

    protected final ProjectManager pc;
    protected final SetupDialog dialog;
    protected final String dialogTitle;

    public EditQuery() {
        super(Metadata.class);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        String name = NbBundle.getMessage(RemoveQuery.class, "CTL_EditQuery");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/edit.png", false));
        putValue(SHORT_DESCRIPTION, name);

        dialog = new SetupDialog();
        dialogTitle = NbBundle.getMessage(AddFilter.class, "MetadataSelector.title");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            Collection<? extends Metadata> context = lkpResult.allInstances();
            if (!context.isEmpty()) {
                Metadata metadata = context.iterator().next();
                StarPepAnnotationType type = metadata.getAnnotationType();
                MetadataSelectorPanel panel = new MetadataSelectorPanel(type);
                if (dialog.setup(panel, panel, dialogTitle)) {
                    QueryModel queryModel = pc.getQueryModel();
                    List<Metadata> selectedMetada = panel.getSelectedMetadata();
                    queryModel.addAll(selectedMetada);
                }
            }
        }
    }

}
