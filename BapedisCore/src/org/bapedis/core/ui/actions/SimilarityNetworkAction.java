/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.core.model.AlgorithmCategory;
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
        id = "org.bapedis.core.ui.actions.SimilarityNetworkAction"
)
@ActionRegistration(
        displayName = "#CTL_SimilarityNetworkAction"
)
@ActionReference(path = "Menu/Tools", position = 300)
@NbBundle.Messages({"CTL_SimilarityNetworkAction=Similarity Network"})
public class SimilarityNetworkAction extends ToolAction {
    
    
    public SimilarityNetworkAction() {
        super(AlgorithmCategory.SimilarityNetwork);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu menu = (JMenu) super.getMenuPresenter();
        JMenuItem newItem = new JMenuItem(new SimilarityNetworkWizardAction());
        menu.insert(newItem, 0);
        return menu;
    }

}
