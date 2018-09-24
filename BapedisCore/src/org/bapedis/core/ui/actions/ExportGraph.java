/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.io.impl.GraphMLExportPanel;
import org.bapedis.core.io.impl.GraphMLExporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.ExportGraph"
)
@ActionRegistration(
        displayName = "#CTL_ExportGraph"
)
@ActionReference(path = "Menu/File/ExportData", position = 400)
@Messages("CTL_ExportGraph=Graph (GraphML format)")
public final class ExportGraph extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final SetupDialog dialog;

    public ExportGraph() {
        super(AttributesModel.class);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            GraphMLExporter exporter = new GraphMLExporter();
            GraphMLExportPanel ui = new GraphMLExportPanel();
            ui.setup(exporter);
            if (dialog.setup(ui, ui, NbBundle.getMessage(ExportGraph.class, "ExportGraph.dialogTitle"))) {
                try {
                    ui.unsetup(exporter);
                    exporter.exportTo(ui.getSelectedFile());
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
