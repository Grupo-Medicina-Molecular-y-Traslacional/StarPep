/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import static javax.swing.Action.SMALL_ICON;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.openide.awt.ActionReferences;
import org.openide.util.ImageUtilities;
import org.bapedis.core.spi.alg.FeatureExtractionTag;

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
    @ActionReference(path = "Menu/Tools/MolecularDescriptor", position = 10)
})
@NbBundle.Messages("CTL_FeatureExtraction=Extraction")
public class FeatureExtractionAction extends ToolAction {
    
    public FeatureExtractionAction() {
        super(NbBundle.getMessage(FeatureExtractionAction.class, "CTL_FeatureExtraction"),
                FeatureExtractionTag.class);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/add_md.gif", false));
    }
          
}
