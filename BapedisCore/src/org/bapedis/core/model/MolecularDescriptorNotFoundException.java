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
 * @author loge
 */
public class MolecularDescriptorNotFoundException extends MolecularDescriptorException {

    private final Peptide pept;
    private final MolecularDescriptor attr;

    public MolecularDescriptorNotFoundException(Peptide pept, MolecularDescriptor attr) {
        super(NbBundle.getMessage(MolecularDescriptorNotFoundException.class, "MolecularDescriptorNotFoundException.msg", pept.getId(), attr.getCategory(), attr.getDisplayName()),
                new NotifyDescriptor.Message(NbBundle.getMessage(MolecularDescriptorNotFoundException.class, "MolecularDescriptorNotFoundException.msgHtml", pept.getId(), attr.getCategory(), attr.getDisplayName()), NotifyDescriptor.ERROR_MESSAGE));
        this.pept = pept;
        this.attr = attr;
    }

    public Peptide getPeptide() {
        return pept;
    }

    public MolecularDescriptor getAttribute() {
        return attr;
    }        

}
