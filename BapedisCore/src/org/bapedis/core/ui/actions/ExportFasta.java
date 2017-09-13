/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.io.impl.FastaExporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.io.impl.FileExporterUI;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.ExportFasta"
)
@ActionRegistration(
        displayName = "#CTL_ExportFasta"
)
@ActionReference(path = "Actions/ExportPeptides", position = 100)
@Messages("CTL_ExportFasta=Fasta")
public final class ExportFasta extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final SetupDialog dialog;

    public ExportFasta() {
        super(AttributesModel.class);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        AttributesModel attrModel = pm.getAttributesModel();
        FileExporterUI ui = new FileExporterUI(".fasta");
        if (dialog.setup(ui, ui, NbBundle.getMessage(ExportFasta.class, "ExportFasta.dialogTitle"))) {
            try {
                FastaExporter exporter = new FastaExporter(attrModel);                
                exporter.exportTo(ui.getSelectedFile());
            } catch (Exception ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
