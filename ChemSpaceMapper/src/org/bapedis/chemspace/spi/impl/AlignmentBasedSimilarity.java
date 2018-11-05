/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import org.bapedis.chemspace.spi.SimilarityMeasure;
import org.bapedis.chemspace.spi.SimilarityMeasureFactory;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.impl.PairwiseSequenceAlignment;

/**
 *
 * @author loge
 */
public class AlignmentBasedSimilarity implements SimilarityMeasure{

    private final AlignmentBasedSimilarityFactory factory;
    private SequenceAlignmentModel alignmentModel;

    public AlignmentBasedSimilarity(AlignmentBasedSimilarityFactory factory) {
        this.factory = factory;
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel model) {
        this.alignmentModel = model;
    }        
        
    @Override
    public SimilarityMeasureFactory getFactory() {
        return factory;
    }

    @Override
    public void setMolecularFeatures(MolecularDescriptor[] features) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) throws Exception {        
        return PairwiseSequenceAlignment.computeSequenceIdentity(peptide1.getBiojavaSeq(), peptide2.getBiojavaSeq(), alignmentModel);
    }
    
}
