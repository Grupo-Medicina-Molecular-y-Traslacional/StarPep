/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.bapedis.core.controller.ProjectController;
import org.bapedis.core.model.Project;
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
import static org.bapedis.db.ui.actions.Bundle.*;

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

    protected final ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        String name = FilterModel.getPrefixName() + " " + FilterModel.getCount();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(NewFilterModel.class, "NewFilterModel.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            FilterModel newFilterModel = new FilterModel(name);
            Project pj = pc.getProject();
            pj.add(newFilterModel);
            Workspace currentWs = pj.getCurrentWorkspace();
            FilterModel oldFilterModel = currentWs.getLookup().lookup(FilterModel.class);
            if (oldFilterModel != null) {
                currentWs.remove(oldFilterModel);
            }
            currentWs.add(newFilterModel);
        }
    }
}
