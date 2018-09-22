/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.bapedis.core.ui.PeptideViewerTopComponent;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author loge
 */
public class ExportToolbar extends AbstractAction implements Presenter.Toolbar{

    @Override
    public void actionPerformed(ActionEvent e) {
        
    }

    @Override
    public Component getToolbarPresenter() {
        final JPopupMenu popup = new JPopupMenu();

        List<? extends Action> actions = Utilities.actionsForPath("Actions/ExportPeptides");
        for (Action action : actions) {
            popup.add(action);
        }

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/export.png", false), popup);
        dropDownButton.setToolTipText(NbBundle.getMessage(ExportToolbar.class, "ExportToolbar.export.tooltiptext"));
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dropDownButton.isEnabled()) {
                    popup.show(dropDownButton, 0, dropDownButton.getHeight());
                }
            }
        });
        return dropDownButton;
        
    }
    
}
