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
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
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
        id = "org.bapedis.db.ui.actions.RenameFilterModel"
)
@ActionRegistration(
        displayName = "#CTL_RenameFilterModel",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilterModel", position = 300)
})
@NbBundle.Messages("CTL_RenameFilterModel=Rename filter model")
public class RenameFilterModel extends WorkspaceContextSensitiveAction<FilterModel> {

    public RenameFilterModel() {
        super(FilterModel.class);
        putValue(NAME, NbBundle.getMessage(RenameFilterModel.class, "CTL_RenameFilterModel"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace ws = pc.getCurrentWorkspace();
        FilterModel filterModel = ws.getLookup().lookup(FilterModel.class);
        String name = filterModel.getName();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", getValue(NAME).toString());
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            filterModel.setName(name);
        }
    }

}
