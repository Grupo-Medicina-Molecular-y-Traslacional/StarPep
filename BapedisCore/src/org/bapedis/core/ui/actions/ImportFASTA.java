/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import org.bapedis.core.io.impl.FASTAImporter;
import org.bapedis.core.io.impl.FASTAImporterUI;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import static org.bapedis.core.ui.actions.NewWorkspace.ErrorWS;
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
        displayName = "#CTL_ImportFASTA"
)
@ActionReferences({
    @ActionReference(path = "Actions/ImportPeptides", position = 190),
    @ActionReference(path = "Menu/File/ImportData", position = 10)
})
@NbBundle.Messages("CTL_ImportFASTA=Peptide sequences (FASTA format)")
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
                    String name = ui.getWorkspaceName();
                    boolean exist = false;
                    for (Iterator<? extends Workspace> it = pc.getWorkspaceIterator(); it.hasNext();) {
                        Workspace ws = it.next();
                        if (ws.getName().equals(name)) {
                            exist = true;
                        }
                    }
                    if (exist) {
                        DialogDisplayer.getDefault().notify(ErrorWS);
                    } else {
                        Workspace workspace = new Workspace(name);                        
                        FASTAImporter importer = new FASTAImporter();
                        importer.importFASTA(ui.getSelectedFile(), ui.getLabelOfNodes(),
                                workspace);
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

}
