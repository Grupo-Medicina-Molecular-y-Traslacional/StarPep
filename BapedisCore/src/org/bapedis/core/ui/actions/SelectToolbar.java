/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.AbstractAction;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.awt.ToolbarPool;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

//@ActionID(
//        category = "View",
//        id = "org.bapedis.core.ui.actions.SelectToolbar"
//)
//@ActionRegistration(
//        displayName = "#CTL_SelectToolbar"
//)
//@ActionReference(path = "Menu/View", position = 650)
//@Messages("CTL_SelectToolbar=Toolbar")
public final class SelectToolbar extends AbstractAction implements Presenter.Menu {

    public SelectToolbar() {        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // TODO implement action body
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu menu = new JMenu(NbBundle.getMessage(SelectToolbar.class, "CTL_SelectToolbar"));
        JCheckBoxMenuItem item;        
        for(final org.openide.awt.Toolbar tb: ToolbarPool.getDefault().getToolbars()){                        
            item = new JCheckBoxMenuItem(tb.getName());
            item.setSelected(tb.isVisible());
            item.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    tb.setVisible(!tb.isVisible());                    
                }
            });
            menu.add(item);
        }
        return menu;
    }
}
