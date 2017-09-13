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
import org.bapedis.core.io.impl.MDExporter;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.ExportMD"
)
@ActionRegistration(
        displayName = "#CTL_ExportMD"
)
@ActionReferences({
    @ActionReference(path = "Actions/ExportPeptides", position = 200),
    @ActionReference(path = "Menu/File/ExportData", position = 200)
})
@Messages("CTL_ExportMD=Molecular descriptors (CSV format)")
public final class ExportMD extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final SetupDialog dialog;

    public ExportMD() {
        super(AttributesModel.class);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        AttributesModel attrModel = pm.getAttributesModel();
        FileExporterUI ui = new FileExporterUI(".csv");
        if (dialog.setup(ui, ui, NbBundle.getMessage(ExportMD.class, "ExportMD.dialogTitle"))) {
            try {
                MDExporter exporter = new MDExporter(attrModel);
                exporter.exportTo(ui.getSelectedFile());
            } catch (Exception ex) {
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message("Error: " + ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE));
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
