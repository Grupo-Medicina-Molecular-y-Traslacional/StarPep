/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import javax.swing.JMenuItem;
import org.bapedis.core.spi.alg.GephiTag;
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
        id = "org.bapedis.core.ui.actions.GephiAction"
)
@ActionRegistration(
        displayName = "#CTL_Gephi"
)
@ActionReference(path = "Menu/Tools", position = 100)
@NbBundle.Messages("CTL_Gephi=Based on Gephi")
public class GephiAction extends ToolAction {
    
    public GephiAction() {
        super(NbBundle.getMessage(GephiAction.class, "CTL_Gephi"),
                GephiTag.class);        
        main.insert(new ShowGraph(), 0);
    }     
    
}
