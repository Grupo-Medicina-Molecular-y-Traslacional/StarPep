/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import org.bapedis.chemspace.model.Position;
import org.bapedis.chemspace.spi.ThreeDTransformer;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class WekaPCATransformer implements ThreeDTransformer{

    @Override
    public Position[] transform(Peptide[] peptides, MolecularDescriptor[] features) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
