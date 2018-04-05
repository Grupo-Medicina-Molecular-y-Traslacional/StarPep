/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class TwoDEmbedder extends AbstractEmbedder{

    public TwoDEmbedder(TwoDEmbedderFactory factory) {
        super(factory);
    }

    @Override
    protected void embed(Peptide[] peptides, MolecularDescriptor[] features) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
