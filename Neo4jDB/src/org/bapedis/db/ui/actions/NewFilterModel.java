/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.model.FilterModel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Edit",
        id = "org.bapedis.db.ui.actions.NewFilterModel"
)
@ActionRegistration(
        displayName = "#CTL_NewFilterModel",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilterModel", position = 100)
})
@Messages("CTL_NewFilterModel=New filter model")
public final class NewFilterModel extends AbstractAction {

    public NewFilterModel() {
        String name = NbBundle.getMessage(NewFilterModel.class, "CTL_NewFilterModel");
        putValue(NAME, name);
    }

    protected final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = FilterModel.getPrefixName() + " " + FilterModel.getCount();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(NewFilterModel.class, "NewFilterModel.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            FilterModel newFilterModel = new FilterModel(name);
            pc.add(newFilterModel);
            Workspace currentWs = pc.getCurrentWorkspace();
            FilterModel oldFilterModel = currentWs.getLookup().lookup(FilterModel.class);
            if (oldFilterModel != null) {
                currentWs.remove(oldFilterModel);
            }
            currentWs.add(newFilterModel);
        }
    }
}
