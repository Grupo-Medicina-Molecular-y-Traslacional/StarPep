/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

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
        id = "org.bapedis.core.ui.actions.MolecularDescriptorAction"
)
@ActionRegistration(
        displayName = "#CTL_MolecularDescriptorAction"
)
@ActionReference(path = "Menu/Tools", position = 200)
@NbBundle.Messages("CTL_MolecularDescriptorAction=Molecular Descriptor")
public class MolecularDescriptorAction extends ToolAction{
    
    public MolecularDescriptorAction() {
        super(AlgorithmCategory.MolecularDescriptor);
    }
    
}
