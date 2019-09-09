/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.SingleQuerySeqSearchFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.SearchTag;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.SearchAction"
)
@ActionRegistration(
        displayName = "#CTL_SearchBy"
)
@ActionReference(path = "Menu/Tools", position = 20)
public class SearchAction extends ToolAction{
    
    public SearchAction() {
        super(NbBundle.getMessage(SearchAction.class, "CTL_SearchBy"),
                SearchTag.class);           
    }        
}
