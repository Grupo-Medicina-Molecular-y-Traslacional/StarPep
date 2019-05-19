/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class MolecularDescriptorConstantException extends MolecularDescriptorException {
    private final MolecularDescriptor attr;

    public MolecularDescriptorConstantException(MolecularDescriptor attr) {
        super(NbBundle.getMessage(MolecularDescriptorConstantException.class, "MolecularDescriptorConstantException.msg", attr.getDisplayName()),
                new NotifyDescriptor.Message(NbBundle.getMessage(MolecularDescriptorConstantException.class, "MolecularDescriptorConstantException.msgHtml", attr.getDisplayName()), NotifyDescriptor.ERROR_MESSAGE));        
        this.attr = attr;
    }
    
    public MolecularDescriptor getAttribute() {
        return attr;
    }        
}
