/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.NodeOperation;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ShowPropertiesAction extends AbstractAction {
    private final AbstractNode node;

    public ShowPropertiesAction(AbstractNode node) {
        this.node = node;
        putValue(NAME, NbBundle.getMessage(AddToQueryModel.class, "ShowProperties.name"));
    }        
    
    @Override
    public void actionPerformed(ActionEvent e) {
        NodeOperation.getDefault().showProperties(node);
    }
    
}
