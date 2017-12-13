/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.io.impl.FileExporterUI;
import org.bapedis.core.io.impl.MDExporter;
import org.bapedis.core.io.impl.MetadataExporter;
import org.bapedis.core.project.ProjectManager;
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
        id = "org.bapedis.core.ui.actions.ExportMetadata"
)
@ActionRegistration(
        displayName = "#CTL_ExportMetadata"
)
@ActionReferences({
    @ActionReference(path = "Actions/ExportPeptides", position = 300),
    @ActionReference(path = "Menu/File/ExportData", position = 300)
})
@Messages("CTL_ExportMetadata=Metadata relationship (CSV format)")
public final class ExportMetadata extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final SetupDialog dialog;

    public ExportMetadata() {
        super(AttributesModel.class);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        AttributesModel attrModel = pm.getAttributesModel();
        FileExporterUI ui = new FileExporterUI("metadata", ".csv");
        if (dialog.setup(ui, ui, NbBundle.getMessage(ExportMetadata.class, "ExportMetadata.dialogTitle"))) {
            try {
                MetadataExporter exporter = new MetadataExporter(attrModel);
                exporter.exportTo(ui.getSelectedFile());
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
