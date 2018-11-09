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
import org.bapedis.core.ui.PeptideViewerTopComponent;
import org.bapedis.core.ui.components.RemoveDescriptorPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.FeatureRemoving"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/delete_md.gif",
        displayName = "#CTL_FeatureRemoving"
)
@ActionReferences({@ActionReference(path = "Menu/Tools/MolecularDescriptor", position = 40),
                   @ActionReference(path = "Toolbars/MD", position = 40) })
@Messages("CTL_FeatureRemoving=Removing")
public final class FeatureRemovingAction extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public FeatureRemovingAction() {
        super(AttributesModel.class);
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWS = pc.getCurrentWorkspace();
        DialogDescriptor dd = new DialogDescriptor(new RemoveDescriptorPanel(currentWS), NbBundle.getMessage(FeatureRemovingAction.class, "RemoveDescriptorPanel.title"));
        dd.setOptions(new Object[]{DialogDescriptor.CLOSED_OPTION});
        DialogDisplayer.getDefault().notify(dd);
    }
}
