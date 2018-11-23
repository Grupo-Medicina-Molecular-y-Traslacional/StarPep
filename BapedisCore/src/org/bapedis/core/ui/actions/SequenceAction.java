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
import org.bapedis.core.spi.alg.SequenceTag;
import org.bapedis.core.spi.alg.impl.SequenceSearchFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.SequenceAction"
)
@ActionRegistration(
        displayName = "#CTL_Sequence"
)
@ActionReference(path = "Menu/Tools", position = 30)
public class SequenceAction extends ToolAction{
    
    public SequenceAction() {
        super(NbBundle.getMessage(SequenceAction.class, "CTL_Sequence"),
                SequenceTag.class);           
    }        
}
