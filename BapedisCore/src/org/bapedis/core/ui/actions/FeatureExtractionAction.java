/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.MolecularDescriptorTag;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.FeatureExtraction"
)
@ActionRegistration(
        displayName = "#CTL_FeatureExtraction"
)
@ActionReference(path = "Menu/Tools", position = 200)
@NbBundle.Messages("CTL_FeatureExtraction=Molecular Descriptor")
public class FeatureExtractionAction extends ToolAction{
    
    public FeatureExtractionAction() {
        super(NbBundle.getMessage(FeatureExtractionAction.class, "CTL_FeatureExtraction"),
                MolecularDescriptorTag.class);
    }
          
}
