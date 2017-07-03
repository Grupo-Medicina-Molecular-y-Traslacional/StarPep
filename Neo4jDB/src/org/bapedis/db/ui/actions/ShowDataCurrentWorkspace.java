/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

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
public class ShowDataCurrentWorkspace extends AbstractAction {

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
    
}
