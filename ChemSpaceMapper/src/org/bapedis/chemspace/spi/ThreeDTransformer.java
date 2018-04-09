/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi;

import org.bapedis.chemspace.model.Position;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface ThreeDTransformer {
    
   public Position[] transform(Peptide[] peptides, MolecularDescriptor[] features);
}
