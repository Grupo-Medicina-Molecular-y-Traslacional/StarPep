/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.action;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import org.bapedis.core.ui.components.SetupDialog;
import org.bapedis.db.Neo4jDB;
import org.bapedis.db.ui.DBDirUI;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

@ActionID(
        category = "File",
        id = "org.bapedis.db.action.ExportGraphDb"
)
@ActionRegistration(
        displayName = "#CTL_ExportGraphDb"
)
@ActionReference(path = "Menu/File/ExportData", position = 4429)
@Messages("CTL_ExportGraphDb=Graph Database (Neo4j format)")
public final class ExportGraphDb implements ActionListener {

    protected final SetupDialog dialog = new SetupDialog();

    @Override
    public void actionPerformed(ActionEvent e) {
        DBDirUI ui = new DBDirUI();
        if (dialog.setup(ui, ui, NbBundle.getMessage(ExportGraphDb.class, "ExportGraphDb.dialogTitle"))) {
            try {               
                Neo4jDB.extractDatabase(ui.getSelectedFile());
            } catch (IOException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        }        
    }
}
