/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class PairwiseSequenceAlignment {

    public static float computeSequenceIdentity(ProteinSequence peptide1, ProteinSequence peptide2, SequenceAlignmentModel model) {
        Alignments.PairwiseSequenceAlignerType alignerType = model.getAlignerType();
        SubstitutionMatrix<AminoAcidCompound> substitutionMatrix = model.getSubstitutionMatrix();
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        float score;
        String seq1 = peptide1.getSequenceAsString();
        String seq2 = peptide2.getSequenceAsString();
        if (seq1.equals(seq2)) {
            score = 1;
        } else {
            pair = Alignments.getPairwiseAlignment(peptide1, peptide2,
                    alignerType, gapPenalty, substitutionMatrix);
            score = ((float) pair.getNumIdenticals()) / getDenominatorValue(pair, seq1, seq2, alignerType);
        }
        return score;
    }

    private static int getDenominatorValue(SequencePair<ProteinSequence, AminoAcidCompound> pair, String seq1, String seq2, Alignments.PairwiseSequenceAlignerType alignerType) {
        switch (alignerType) {
            case LOCAL:
                return Math.min(seq1.length(), seq2.length());
            case GLOBAL:
                return pair.getLength();
        }
        return 0;
    }
}
