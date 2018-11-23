/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.NetworkTag;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.NetworkAction"
)
@ActionRegistration(
        displayName = "#CTL_Network"
)
@ActionReference(path = "Menu/Tools", position = 60)
public class NetworkAction extends ToolAction {
    
    public NetworkAction() {
        super(NbBundle.getMessage(NetworkAction.class, "CTL_Network"),
                NetworkTag.class);        
        main.insert(new ShowMetadataNetwork(), 0);
        main.insert(new ChemicalSpaceAction(), 1);
//        main.insert(new LoadHSP(), 2);
    }     
    
}
