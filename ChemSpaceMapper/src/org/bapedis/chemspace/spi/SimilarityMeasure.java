/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi;

import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface SimilarityMeasure {

    void setMolecularFeatures(MolecularDescriptor[] features);
    
    float computeSimilarity(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException;
    
}
