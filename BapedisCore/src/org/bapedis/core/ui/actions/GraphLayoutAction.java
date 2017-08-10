/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import static javax.swing.Action.NAME;
import org.bapedis.core.model.AlgorithmCategory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.GraphLayoutAction"
)
@ActionRegistration(
        displayName = "#CTL_GraphLayoutAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 100)
})
@Messages("CTL_GraphLayoutAction=Graph Layout")
public final class GraphLayoutAction extends ToolAction {

    public GraphLayoutAction() {
        super(AlgorithmCategory.GraphLayout);
        putValue(NAME, Bundle.CTL_GraphLayoutAction());
    }
}
