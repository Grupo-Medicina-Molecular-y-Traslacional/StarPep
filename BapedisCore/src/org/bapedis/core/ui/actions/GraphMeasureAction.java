/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import org.bapedis.core.model.AlgorithmCategory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.GraphMeasureAction"
)
@ActionRegistration(
        displayName = "#CTL_GraphMeasureAction"
)
@ActionReference(path = "Menu/Tools", position = 200)
@Messages("CTL_GraphMeasureAction=Graph Measure")
public final class GraphMeasureAction extends ToolAction {

    public GraphMeasureAction() {
        super(AlgorithmCategory.GraphMeasure);
    }
   
}
