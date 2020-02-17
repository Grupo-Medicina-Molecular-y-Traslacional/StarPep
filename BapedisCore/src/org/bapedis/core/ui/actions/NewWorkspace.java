/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.model.Workspace;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.NewWorkspace"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/newWorkspace.png",
        displayName = "#CTL_NewWorkspace"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 220),
    @ActionReference(path = "Toolbars/Workspace", position = 50)
})
public final class NewWorkspace implements ActionListener {

    private final ProjectManager pc;

    public NewWorkspace() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        Workspace ws = pc.createWorkspace();
        if (ws != null) {
            pc.add(ws);
            pc.setCurrentWorkspace(ws);
        }
    }

}
