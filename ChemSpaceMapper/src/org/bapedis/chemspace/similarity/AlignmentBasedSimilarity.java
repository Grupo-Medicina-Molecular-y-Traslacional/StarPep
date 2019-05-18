/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.impl.PairwiseSequenceAlignment;

/**
 *
 * @author loge
 */
public class AlignmentBasedSimilarity extends AbstractSimCoefficient{
    private SequenceAlignmentModel alignmentModel;

    public AlignmentBasedSimilarity(AlignmentBasedSimilarityFactory factory) {
        super(factory);
        alignmentModel = new SequenceAlignmentModel();
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel model) {
        this.alignmentModel = model;
    }                

    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) throws Exception {        
        return PairwiseSequenceAlignment.computeSequenceIdentity(peptide1.getBiojavaSeq(), peptide2.getBiojavaSeq(), alignmentModel);
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        AlignmentBasedSimilarity copy = (AlignmentBasedSimilarity) super.clone();
        copy.alignmentModel = (SequenceAlignmentModel)this.alignmentModel.clone();
        return copy;
    }       
}
