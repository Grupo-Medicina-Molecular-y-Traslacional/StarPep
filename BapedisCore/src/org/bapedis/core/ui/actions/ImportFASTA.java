/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.bapedis.core.io.impl.FASTAImporter;
import org.bapedis.core.io.impl.FASTAImporterUI;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.ImportFASTA"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/open.png",
        displayName = "#CTL_ImportFASTA"
)
@ActionReferences({
    @ActionReference(path = "Actions/ImportPeptides", position = 190),
    @ActionReference(path = "Menu/File/ImportData", position = 10),
    @ActionReference(path = "Toolbars/File", position = 290)
})
@NbBundle.Messages("CTL_ImportFASTA=Import peptides (FASTA format)")
public class ImportFASTA extends AbstractAction {

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final SetupDialog dialog;

    public ImportFASTA() {
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            FASTAImporterUI ui = new FASTAImporterUI();
            if (dialog.setup(ui, ui, NbBundle.getMessage(ImportFASTA.class, "ImportFASTA.dialogTitle"))) {
                try {
                    Workspace workspace;
                    if (ui.isCurrentWorkspace()) {
                        workspace = currentWS;
                    } else {
                        workspace = new Workspace(ui.getWorkspaceName());
                        pc.add(workspace);
                    }
                    FASTAImporter importer = new FASTAImporter();
                    importer.importFASTA(ui.getSelectedFile(), ui.getLabelOfNodes(),
                            workspace);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

}
