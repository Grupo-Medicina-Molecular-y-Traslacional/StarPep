/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.layout;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.services.ProjectManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Tools",
        id = "org.gephi.desktop.layout.LayoutAction"
)
@ActionRegistration(
        displayName = "#CTL_LayoutAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 100)
})
@Messages("CTL_LayoutAction=Graph Layout")
public final class LayoutAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        AlgorithmModel algoModel = pm.getAlgorithmModel();
        algoModel.setCategory(AlgorithmCategory.GraphLayout);

        TopComponent tc = WindowManager.getDefault().findTopComponent("AlgoExplorerTopComponent");
        tc.open();
        tc.requestActive();
    }
}
