/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.controller.ProjectController;
import org.bapedis.core.model.Project;
import org.bapedis.core.model.Workspace;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.NewWorkspace"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/newWorkspace.png",
        displayName = "#CTL_NewWorkspace"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1000),
    @ActionReference(path = "Toolbars/Workspace", position = 100)
})
public final class NewWorkspace implements ActionListener {

    public NewWorkspace() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        String name = Workspace.getPrefixName() + " " + Workspace.getCount();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(NewWorkspace.class, "NewWorkspace.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
            Project pj = pc.getProject();
            Workspace ws = new Workspace(name);
            pj.add(ws);
            pj.setCurrentWorkspace(ws);
        }
    }

}
