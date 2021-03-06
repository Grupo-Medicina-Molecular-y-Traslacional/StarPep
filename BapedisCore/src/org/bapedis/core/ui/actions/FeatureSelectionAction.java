/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.FeatureSelectionTag;

/**
 *
 * @author Home
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.FeatureSelection"
)
@ActionRegistration(        
        displayName = "#CTL_FeatureSelection",
        lazy=false
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools/MolecularDescriptor", position = 20)
})
public final class FeatureSelectionAction extends ToolAction {


    public FeatureSelectionAction() {
        super(NbBundle.getMessage(FeatureSelectionAction.class, "CTL_FeatureSelection"),
                FeatureSelectionTag.class);
    }

}
