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
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.MolecularDescriptorTag;
import org.openide.awt.ActionReferences;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.FeatureExtraction"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/add_md.gif",
        displayName = "#CTL_FeatureExtraction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools/MolecularDescriptor", position = 20)
})
@NbBundle.Messages("CTL_FeatureExtraction=Extraction")
public class FeatureExtractionAction extends ToolAction {
    
    public FeatureExtractionAction() {
        super(NbBundle.getMessage(FeatureExtractionAction.class, "CTL_FeatureExtraction"),
                MolecularDescriptorTag.class);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/add_md.gif", false));
    }
          
}
