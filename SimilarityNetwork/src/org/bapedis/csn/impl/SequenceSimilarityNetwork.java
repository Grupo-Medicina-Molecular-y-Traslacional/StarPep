/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.csn.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
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
public class SequenceSimilarityNetwork extends SimilarityNetworkAlgo implements SimilarityMeasure{

    public static final String[] Alignment_Type = new String[]{"Needleman-Wunsch", "Smith-Waterman"};
    public static final String[] Substitution_Matrix = new String[]{
        "Blosum 30 by Henikoff & Henikoff", "Blosum 35 by Henikoff & Henikoff", "Blosum 40 by Henikoff & Henikoff",
        "Blosum 45 by Henikoff & Henikoff", "Blosum 50 by Henikoff & Henikoff", "Blosum 55 by Henikoff & Henikoff",
        "Blosum 60 by Henikoff & Henikoff", "Blosum 62 by Henikoff & Henikoff", "Blosum 65 by Henikoff & Henikoff",
        "Blosum 70 by Henikoff & Henikoff", "Blosum 75 by Henikoff & Henikoff", "Blosum 80 by Henikoff & Henikoff",
        "Blosum 85 by Henikoff & Henikoff", "Blosum 90 by Henikoff & Henikoff", "Blosum 100 by Henikoff & Henikoff",
        "PAM 250 by Gonnet, Cohen & Benner", "PAM 250 by Dayhoff"};
    public static final String[] Similarity_Type = new String[]{"Percent sequence identity", "Percent positive substitutions"};
    public static final String[][] Similarity_Score = new String[][]{{"Identities / (Length of shorter sequence)", "Identities / Columns"},
    {"Positives / (Length of shorter sequence)", "Positives / Columns"}};
    protected int alignmentTypeIndex, substitutionMatrixIndex, similarityTypeIndex, similarityScoreIndex;
    protected SubstitutionMatrix<AminoAcidCompound> substitutionMatrix;
    protected Alignments.PairwiseSequenceAlignerType alignerType;
    protected double threshold;

    public SequenceSimilarityNetwork(AlgorithmFactory factory) {
        super(factory);
        alignmentTypeIndex = 0;
        alignerType = getAlignerType();
        substitutionMatrixIndex = 7; // Blosum 62 by Henikoff & Henikoff
        substitutionMatrix = getSubstitutionMatrix();
        similarityTypeIndex = 0;
        similarityScoreIndex = 0;
        threshold = 0.7;
    }

    public int getAlignmentTypeIndex() {
        return alignmentTypeIndex;
    }

    public void setAlignmentTypeIndex(int alignmentTypeIndex) {
        if (alignmentTypeIndex < 0 || alignmentTypeIndex >= Alignment_Type.length) {
            throw new IllegalArgumentException("Unknown value for alignment type");
        }
        this.alignmentTypeIndex = alignmentTypeIndex;
        alignerType = getAlignerType();
    }

    public int getSubstitutionMatrixIndex() {
        return substitutionMatrixIndex;
    }

    public int getSimilarityTypeIndex() {
        return similarityTypeIndex;
    }

    public void setSimilarityTypeIndex(int similarityType) {
        if (similarityType < 0 || similarityType >= Similarity_Type.length) {
            throw new IllegalArgumentException("Unknown value for similarity type");
        }
        this.similarityTypeIndex = similarityType;
    }

    public int getSimilarityScoreIndex() {
        return similarityScoreIndex;
    }

    public void setSimilarityScoreIndex(int similarityScore) {
        if (similarityTypeIndex < 0 || similarityTypeIndex >= Similarity_Score.length) {
            throw new IllegalArgumentException("Unknown value for similarity type");
        }
        if (similarityScore < 0 || similarityScore >= Similarity_Score[similarityTypeIndex].length) {
            throw new IllegalArgumentException("Unknown value for similarity score");
        }
        this.similarityScoreIndex = similarityScore;
    }

    public void setSubstitutionMatrixIndex(int substitutionMatrixIndex) {
        if (substitutionMatrixIndex < 0 || substitutionMatrixIndex >= Substitution_Matrix.length) {
            throw new IllegalArgumentException("Unknown value for substitution matrix");
        }
        this.substitutionMatrixIndex = substitutionMatrixIndex;
        substitutionMatrix = getSubstitutionMatrix();
    }

    private SubstitutionMatrix<AminoAcidCompound> getSubstitutionMatrix() {
        switch (Substitution_Matrix[substitutionMatrixIndex]) {
            case "Blosum 30 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum30();
            case "Blosum 35 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum35();
            case "Blosum 40 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum40();
            case "Blosum 45 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum45();
            case "Blosum 50 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum50();
            case "Blosum 55 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum55();
            case "Blosum 60 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum60();
            case "Blosum 62 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum62();
            case "Blosum 65 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum65();
            case "Blosum 70 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum70();
            case "Blosum 75 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum75();
            case "Blosum 80 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum80();
            case "Blosum 85 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum85();
            case "Blosum 90 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum90();
            case "Blosum 100 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum100();
            case "PAM 250 by Gonnet, Cohen & Benner":
                return SubstitutionMatrixHelper.getGonnet250();
            case "PAM 250 by Dayhoff":
                return SubstitutionMatrixHelper.getPAM250();
        }
        return null;
    }

    private Alignments.PairwiseSequenceAlignerType getAlignerType() {
        if (Alignment_Type[alignmentTypeIndex].equals("Needleman-Wunsch")) {
            return Alignments.PairwiseSequenceAlignerType.GLOBAL;
        } else if (Alignment_Type[alignmentTypeIndex].equals("Smith-Waterman")) {
            return Alignments.PairwiseSequenceAlignerType.LOCAL;
        }
        return null;
    }
    
    private int getNumeratorValue(SequencePair<ProteinSequence, AminoAcidCompound> pair){
        switch (Similarity_Type[similarityTypeIndex]){
            case "Percent sequence identity":
                return pair.getNumIdenticals();
            case "Percent positive substitutions":
                return pair.getNumSimilars();
        }
        return 0;
    }
    
    private int getDenominatorValue(SequencePair<ProteinSequence, AminoAcidCompound> pair, Peptide peptide1, Peptide peptide2){
        switch(Similarity_Score[similarityTypeIndex][similarityScoreIndex]){
            case "Identities / (Length of shorter sequence)":
            case "Positives / (Length of shorter sequence)":
                return Math.min(peptide1.getSequence().length(), peptide2.getSequence().length());
            case "Identities / Columns":
            case "Positives / Columns":
                return pair.getLength();
        }       
        return 0;
    }
    

    @Override
    public double computeSimilarity(Peptide peptide1, Peptide peptide2) {
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        double score;
        if (peptide1.getSequence().equals(peptide2.getSequence())) {
            score = 1;
        } else {
            try {
                pair = Alignments.getPairwiseAlignment(peptide1.getBiojavaSeq(), peptide2.getBiojavaSeq(),
                        alignerType, gapPenalty, substitutionMatrix);
                score = ((double)getNumeratorValue(pair)) / getDenominatorValue(pair, peptide1, peptide2);
            } catch (CompoundNotFoundException ex) {
//                log.log(Level.SEVERE, "Compound Not Found Exception: {0}", ex.getMessage());
                Exceptions.printStackTrace(ex);
                score = -1;
            }
        }
        return score;
    }

    @Override
    protected SimilarityMeasure getSimilarityProvider() {
        return this;
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(double value) {
        this.threshold = value;
    }
}
