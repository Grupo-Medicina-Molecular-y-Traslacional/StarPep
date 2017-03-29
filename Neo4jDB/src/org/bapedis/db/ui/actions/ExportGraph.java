/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.bapedis.db.ui.actions.ExportGraph"
)
@ActionRegistration(
        displayName = "#CTL_ExportGraph"
)
@ActionReference(path = "Menu/File/ExportData", position = 3433)
@Messages("CTL_ExportGraph=Graph")
public final class ExportGraph implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }
}
