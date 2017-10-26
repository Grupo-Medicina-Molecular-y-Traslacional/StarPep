/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.core.ui.actions.ShowChemicalSpace"
)
@ActionRegistration(
        displayName = "#CTL_ShowChemicalSpace",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 330)
})
public class ShowChemicalSpace extends WorkspaceContextSensitiveAction<AttributesModel> {
    protected final ProjectManager pc;
    private final GraphWindowController graphWC;

    public ShowChemicalSpace() {
        super(AttributesModel.class);
        String name = NbBundle.getMessage(ShowChemicalSpace.class, "CTL_ShowChemicalSpace");
        putValue(NAME, name);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (graphWC != null) {
            AttributesModel attrModel = pc.getAttributesModel();
            if (attrModel != null){
                GraphModel graphModel = pc.getGraphModel();
                GraphView csnView = attrModel.getCsnView();
                if (graphModel.getVisibleView() != csnView){
                    graphModel.setVisibleView(csnView);
                }
                attrModel.setMainGView(AttributesModel.CSN_VIEW);
                graphWC.openGraphWindow();
            }
        }
    }
}
