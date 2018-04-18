/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi;

import javax.vecmath.Vector2f;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface TwoDTransformer {
   
   public TwoDTransformerFactory getFactory();
   
   public Vector2f[] transform(Peptide[] peptides, MolecularDescriptor[] features);
}
