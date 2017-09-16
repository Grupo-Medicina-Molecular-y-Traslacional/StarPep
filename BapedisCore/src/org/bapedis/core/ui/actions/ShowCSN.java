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
        id = "org.bapedis.core.ui.actions.ShowCSN"
)
@ActionRegistration(
        displayName = "#CTL_ShowCSN",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 330)
})
public class ShowCSN extends AbstractAction {
    protected final ProjectManager pc;
    private final GraphWindowController graphWC;

    public ShowCSN() {
        String name = NbBundle.getMessage(ShowCSN.class, "CTL_ShowCSN");
        putValue(NAME, name);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (graphWC != null) {
            AttributesModel attrModel = pc.getAttributesModel();
            if (attrModel != null){
                attrModel.setMainGView(AttributesModel.CSN_VIEW);
                graphWC.openGraphWindow();
            }
        }
    }

//    @Override
//    public JMenuItem getPopupPresenter() {
//        JMenu main = new JMenu(NbBundle.getMessage(ShowPeptideNodes.class, "CTL_ShowGraph"));
//        List<? extends Action> actionsForPath = Utilities.actionsForPath("Actions/ShowDataFromLibrary/InWorkspace");
//        for (Action action : actionsForPath) {
//            main.add(action);
//        }
//        return main;
//    }
}
