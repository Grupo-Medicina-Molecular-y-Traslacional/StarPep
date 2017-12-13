/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

/**
 *
 * @author loge
 */
//@ActionID(
//        category = "File",
//        id = "org.bapedis.core.ui.actions.SaveProject"
//)
//@ActionRegistration(
//        iconBase = "org/bapedis/core/resources/saveProject.png",
//        displayName = "#CTL_SaveProject"
//)
//@ActionReferences({
//    @ActionReference(path = "Menu/File", position = 1400),
//    @ActionReference(path = "Toolbars/File", position = 200)
//})
public class SaveProject extends AbstractAction {

    public SaveProject() {
        setEnabled(false);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
    }
    
}
