/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

/**
 *
 * @author loge
 */
public class NewNodesAction extends AbstractAction implements Presenter.Menu {

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu main = new JMenu(NbBundle.getMessage(NewNodesAction.class, "NewNodesAction.name"));
        return main;
    }
    
}
