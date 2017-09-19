/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;

/**
 *
 * @author loge
 */
public class PairwiseSequenceAlignment extends NetworkSimilarityBuilder {

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

    public PairwiseSequenceAlignment(AlgorithmFactory factory) {
        super(factory);        
        alignmentTypeIndex = 0;
        substitutionMatrixIndex = 7; // Blosum 62 by Henikoff & Henikoff
        similarityTypeIndex = 0;
        similarityScoreIndex = 0;
    }

    public int getAlignmentTypeIndex() {
        return alignmentTypeIndex;
    }

    public void setAlignmentTypeIndex(int alignmentType) {
        if (alignmentType < 0 || alignmentType >= Alignment_Type.length) {
            throw new IllegalArgumentException("Unknown value for alignment type");
        }
        this.alignmentTypeIndex = alignmentType;
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

    public void setSubstitutionMatrixIndex(int substitutionMatrix) {
        if (substitutionMatrix < 0 || substitutionMatrix >= Substitution_Matrix.length) {
            throw new IllegalArgumentException("Unknown value for substitution matrix");
        }
        this.substitutionMatrixIndex = substitutionMatrix;
    }

    private SubstitutionMatrix getSubstitutionMatrix() {
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
}
