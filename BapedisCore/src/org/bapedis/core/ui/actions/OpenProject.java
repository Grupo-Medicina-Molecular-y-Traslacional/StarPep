/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

//@ActionID(
//        category = "File",
//        id = "org.bapedis.core.ui.actions.OpenProject"
//)
//@ActionRegistration(
//        iconBase = "org/bapedis/core/resources/openProject.png",
//        displayName = "#CTL_OpenProject"
//)
//@ActionReferences({
//    @ActionReference(path = "Menu/File", position = 1300, separatorBefore = 1250),
//    @ActionReference(path = "Toolbars/File", position = 100)
//})
public final class OpenProject extends AbstractAction {

    public OpenProject() {
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }
}
