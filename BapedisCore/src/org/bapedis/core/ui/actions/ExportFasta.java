/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.io.impl.FastaExporterUI;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.ExportFasta"
)
@ActionRegistration(
        displayName = "#CTL_ExportFasta"
)
@ActionReference(path = "Menu/File/ExportData", position = 3333)
@Messages("CTL_ExportFasta=Fasta")
public final class ExportFasta extends WorkspaceContextSensitiveAction<AttributesModel> {
     protected final SetupDialog dialog;
     
    public ExportFasta() {
        super(AttributesModel.class);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FastaExporterUI ui = new FastaExporterUI();
        if(dialog.setup(ui.getPanel(), ui, NbBundle.getMessage(ExportFasta.class, "ExportFasta.dialogTitle"))){
            
        }        
    }
}
