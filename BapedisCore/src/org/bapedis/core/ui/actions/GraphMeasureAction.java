/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.services.ProjectManager;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.GraphMeasureAction"
)
@ActionRegistration(
        displayName = "#CTL_GraphMeasureAction"
)
@ActionReference(path = "Menu/Tools", position = 100)
@Messages("CTL_GraphMeasureAction=Graph Measure")
public final class GraphMeasureAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectManager pm = Lookup.getDefault().lookup(ProjectManager.class);
        AlgorithmModel algoModel = pm.getAlgorithmModel();
        algoModel.setCategory(AlgorithmCategory.GraphMeasure);

        TopComponent tc = WindowManager.getDefault().findTopComponent("AlgoExplorerTopComponent");
        tc.open();
        tc.requestActive();
    }
}
