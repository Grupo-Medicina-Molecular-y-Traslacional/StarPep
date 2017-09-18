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
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.ChemSpaceAction"
)
@ActionRegistration(
        displayName = "#CTL_ChemSpaceAction"
)
@ActionReference(path = "Menu/Tools", position = 300)
@Messages("CTL_ChemSpaceAction=Chemical Space Networks")
public class ChemSpaceAction extends AbstractAction implements Presenter.Menu{

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu main = new JMenu(NbBundle.getMessage(ChemSpaceAction.class, "CTL_ChemSpaceAction"));
        main.add(new NetworkSimilarityAction().getMenuPresenter());
        main.add(new GraphMeasureAction().getMenuPresenter());
        return main;
    }
    
}
