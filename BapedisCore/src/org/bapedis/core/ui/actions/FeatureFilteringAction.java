/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.AlgorithmExecutor;
import org.bapedis.core.ui.PeptideViewerTopComponent;
import org.bapedis.core.ui.components.FeatureFilterPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

//@ActionID(
//        category = "Tools",
//        id = "org.bapedis.core.ui.actions.FeatureFiltering"
//)
//@ActionRegistration(
//        iconBase = "org/bapedis/core/resources/filter_md.png",
//        displayName = "#CTL_FeatureFiltering"
//)
//@ActionReference(path = "Menu/Tools/MolecularDescriptor", position = 30)
//@Messages("CTL_FeatureFiltering=Filtering")
public final class FeatureFilteringAction extends WorkspaceContextSensitiveAction<AttributesModel> {
    protected final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final AlgorithmExecutor executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);

    public FeatureFilteringAction() {
        super(AttributesModel.class);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        FeatureFilterPanel panel = new FeatureFilterPanel(currentWS);
        DialogDescriptor dd = new DialogDescriptor(panel, NbBundle.getMessage(FeatureFilteringAction.class, "FeatureFilterPanel.title"));
        dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION});
        panel.setDialogDescriptor(dd);
        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
            if (currentWS.isBusy()) {
                DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
            } else {
                executor.execute(panel.getAlgorithm());
            }
        }
    }
}
