/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.NotifyDescriptor;

/**
 *
 * @author Loge
 */
public class MolecularDescriptorException extends Exception {
    protected final NotifyDescriptor errorND;

    public MolecularDescriptorException(String msg, NotifyDescriptor errorND) {
        super(msg);
        this.errorND = errorND;
    }
        
    public MolecularDescriptorException(String msg) {
        super(msg);
        this.errorND = new NotifyDescriptor.Message(msg, NotifyDescriptor.ERROR_MESSAGE);;
    } 

    public NotifyDescriptor getErrorNotifyDescriptor() {
        return errorND;
    }
    
}
