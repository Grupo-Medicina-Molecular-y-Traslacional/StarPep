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
@ActionReference(path = "Menu/Tools", position = 20)
@NbBundle.Messages("CTL_Sequence=Sequence search")
public class SequenceAction extends AbstractAction{
    private final ActionListener actionListener;
    
    public SequenceAction() {
        AlgorithmFactory seqSearchfactory = Lookup.getDefault().lookup(SequenceSearchFactory.class);               
        actionListener = seqSearchfactory != null ?ToolAction.createActionListener(seqSearchfactory, SequenceTag.class): null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (actionListener != null){
            actionListener.actionPerformed(e);
        }
    }
    
    
}
