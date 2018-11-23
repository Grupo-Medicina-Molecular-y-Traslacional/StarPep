/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.SMALL_ICON;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.ui.components.MetadataNetworkPanel;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.ShowMetadataNetwork"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/metadataNet.png",
        displayName = "#CTL_ShowMetadataNetwork"
)
@ActionReferences({
    @ActionReference(path = "Toolbars/Network", position = 10)
})
public class ShowMetadataNetwork extends AbstractAction {

    private final GraphWindowController graphWC;
    private final ProjectManager pc;

    public ShowMetadataNetwork() {
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        putValue(NAME, NbBundle.getMessage(ShowMetadataNetwork.class, "ShowMetadataNetwork.name"));
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/metadataNet.png", false));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ShowMetadataNetwork.class, "ShowMetadataNetwork.desc"));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphVizSetting graphViz = pc.getGraphVizSetting();
        MetadataNetworkPanel metadataPanel = new MetadataNetworkPanel(graphViz, pc.getCurrentWorkspace());
        DialogDescriptor dd = new DialogDescriptor(metadataPanel, NbBundle.getMessage(ShowMetadataNetwork.class, "ShowMetadataNetwork.metadataPanel.title"));
        dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION});
        if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
            try {
                graphWC.openGraphWindow();
                for (MetadataNetworkPanel.MetadataRadioButton mrb : metadataPanel.getMetadataOptions()) {
                    if (mrb.getRadioButton().isSelected()) {
                        graphViz.addDisplayedMetadata(mrb.getAnnotationType());
                    } else {
                        graphViz.removeDisplayedMetadata(mrb.getAnnotationType());
                    }
                }
            } finally {
                graphViz.fireChangedGraphView();
            }
        }
    }

}
