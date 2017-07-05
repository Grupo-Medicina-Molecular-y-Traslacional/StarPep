/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
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
        id = "org.bapedis.db.ui.actions.ShowDataCurrentWorkspace"
)
@ActionRegistration(
        displayName = "#CTL_ShowCurrentWorkspace"
)
@ActionReferences({
    @ActionReference(path = "Actions/ShowDataFromLibrary/InWorkspace", position = 100)
})
public class ShowDataCurrentWorkspace extends ShowDataAction {

    @Override
    public void actionPerformed(ActionEvent e) {
       ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
       workspace = pc.getCurrentWorkspace();
       super.actionPerformed(e);
    }

}
