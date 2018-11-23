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
import org.bapedis.core.io.impl.MDExporterPanel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import static org.bapedis.core.ui.actions.ExportGraph.pc;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDisplayer;
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

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final SetupDialog dialog;

    public ExportMD() {
        super(AttributesModel.class);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            AttributesModel attrModel = pc.getAttributesModel();
            MDExporter exporter = new MDExporter(attrModel);
            MDExporterPanel ui = new MDExporterPanel();
            ui.setup(exporter);
            if (dialog.setup(ui, ui, NbBundle.getMessage(ExportMD.class, "ExportMD.dialogTitle"))) {
                try {
                    ui.unsetup(exporter);
                    exporter.exportTo(ui.getSelectedFile());
                } 
                catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }        
    }
}
