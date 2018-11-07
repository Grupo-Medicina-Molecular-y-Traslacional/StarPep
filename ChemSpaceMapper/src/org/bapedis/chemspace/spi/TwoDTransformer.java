/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi;

import org.bapedis.chemspace.model.TwoDSpace;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;

/**
 *
 * @author loge
 */
public interface TwoDTransformer {
   
   TwoDTransformerFactory getFactory();
         
    TwoDSpace transform(Workspace workspace, Peptide[] peptides, MolecularDescriptor[] features);
}
