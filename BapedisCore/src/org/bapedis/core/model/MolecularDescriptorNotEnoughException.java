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
public class MolecularDescriptorNotEnoughException extends MolecularDescriptorException {

    public MolecularDescriptorNotEnoughException() {
        super(NbBundle.getMessage(MolecularDescriptorNotEnoughException.class, "MolecularDescriptorNotEnoughException.msg"),
                new NotifyDescriptor.Message(NbBundle.getMessage(MolecularDescriptorNotEnoughException.class, "MolecularDescriptorNotEnoughException.msgHtml"), NotifyDescriptor.ERROR_MESSAGE));
    }

}
