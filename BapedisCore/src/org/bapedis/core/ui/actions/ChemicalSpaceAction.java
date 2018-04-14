/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.spi.alg.ChemSpaceTag;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.ChemicalSpaceAction"
)
@ActionRegistration(
        displayName = "#CTL_ChemicalSpaceAction"
)
@ActionReference(path = "Menu/Tools", position = 400)
@NbBundle.Messages({"CTL_ChemicalSpaceAction=Chemical Space"})
public class ChemicalSpaceAction extends ToolAction {
    
    
    public ChemicalSpaceAction() {
        super(NbBundle.getMessage(ChemicalSpaceAction.class, "CTL_ChemicalSpaceAction"),
                ChemSpaceTag.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

}
