/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.db.ui.actions.ShowDataNewWorkspace"
)
@ActionRegistration(
        displayName = "#CTL_ShowDataNewWorkspace"
)
@ActionReferences({
    @ActionReference(path = "Actions/ShowDataFromLibrary/InWorkspace", position = 200)
})
public class ShowDataNewWorkspace extends ShowDataAction {

    @Override
    public void actionPerformed(ActionEvent e) {
       ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
       workspace = new Workspace();
       super.actionPerformed(e);
       pc.add(workspace);
       pc.setCurrentWorkspace(workspace);
    }
}
