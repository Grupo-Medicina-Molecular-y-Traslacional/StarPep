/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

//@ActionID(
//        category = "Tools",
//        id = "org.bapedis.core.ui.actions.AddMDToolBar"
//)
//@ActionRegistration(
//        displayName = "#CTL_AddMD"
//)
//@ActionReferences({
//    @ActionReference(path = "Toolbars/MD", position = 100)
//})
//@NbBundle.Messages("CTL_AddMD=Add molecular descriptors")
public class AddMDToolBar extends AbstractAction implements Presenter.Toolbar{

    public AddMDToolBar() {
    }        
    
    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        FeatureExtractionAction mdAction = new FeatureExtractionAction();
        JMenu menu = (JMenu) mdAction.getMenuPresenter();
        final JPopupMenu popup = menu.getPopupMenu();
        
        Image iconImage = ImageUtilities.loadImage("org/bapedis/core/resources/add_md.gif");
        ImageIcon icon = new ImageIcon(iconImage);

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(
                new BufferedImage(32, 32, BufferedImage.TYPE_BYTE_GRAY)), popup);

        dropDownButton.setIcon(icon);
        dropDownButton.setToolTipText(NbBundle.getMessage(AddMDToolBar.class, "CTL_AddMD"));
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.show(dropDownButton, 0, dropDownButton.getHeight());
            }
        });

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                dropDownButton.setSelected(false);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                dropDownButton.setSelected(false);
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

        return dropDownButton;

    }
    
}
