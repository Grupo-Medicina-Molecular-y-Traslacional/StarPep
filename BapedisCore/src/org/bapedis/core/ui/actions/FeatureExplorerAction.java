/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.ui.PeptideViewerTopComponent;
import org.bapedis.core.ui.components.MolecularFeaturesPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.FeatureExplorer"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/select_md.png",
        displayName = "#CTL_FeatureExplorer"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools/MolecularDescriptor", position = 10),
    @ActionReference(path = "Toolbars/MD", position = 150)
})
@Messages("CTL_FeatureExplorer=Explorer")
public final class FeatureExplorerAction extends WorkspaceContextSensitiveAction<AttributesModel> {

    public FeatureExplorerAction() {
        super(AttributesModel.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributesModel currentModel = pc.getAttributesModel();
        DialogDescriptor dd = new DialogDescriptor(new MolecularFeaturesPanel(currentModel), NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.MolecularFeaturesPanel.title"));
        dd.setOptions(new Object[]{DialogDescriptor.CLOSED_OPTION});
        DialogDisplayer.getDefault().notify(dd);
    }
}
