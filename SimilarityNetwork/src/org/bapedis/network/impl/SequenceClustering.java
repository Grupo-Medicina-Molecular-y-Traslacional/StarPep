/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.LinkedHashMap;
import java.util.Map;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
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
public class SequenceClustering {

    public static final int PID_MAX=90;
    public static final int PID_MIN=50;
    public static final int[] PID_REFS= new int[]{50, 70, 90};
    
    public static final int DEFAULT_ALIGNMENT_TYPE_INDEX=0; // Needleman-Wunsch
    public static final int DEFAULT_SUBSTITUTION_MATRIX_INDEX=7; // Blosum 62 by Henikoff & Henikoff
    public static final int DEFAULT_PID=70;
    
    public static final String[] Alignment_Type = new String[]{"Needleman-Wunsch", "Smith-Waterman"};
    public static final String[] Substitution_Matrix = new String[]{
        "Blosum 30 by Henikoff & Henikoff", "Blosum 35 by Henikoff & Henikoff", "Blosum 40 by Henikoff & Henikoff",
        "Blosum 45 by Henikoff & Henikoff", "Blosum 50 by Henikoff & Henikoff", "Blosum 55 by Henikoff & Henikoff",
        "Blosum 60 by Henikoff & Henikoff", "Blosum 62 by Henikoff & Henikoff", "Blosum 65 by Henikoff & Henikoff",
        "Blosum 70 by Henikoff & Henikoff", "Blosum 75 by Henikoff & Henikoff", "Blosum 80 by Henikoff & Henikoff",
        "Blosum 85 by Henikoff & Henikoff", "Blosum 90 by Henikoff & Henikoff", "Blosum 100 by Henikoff & Henikoff",
        "PAM 250 by Gonnet, Cohen & Benner", "PAM 250 by Dayhoff"};

    protected SubstitutionMatrix<AminoAcidCompound> substitutionMatrix;
    protected Alignments.PairwiseSequenceAlignerType alignerType;    
    
    protected AlgorithmProperty[] properties;
    protected int alignmentTypeIndex, substitutionMatrixIndex;
    protected int percentIdentity;
    protected boolean clustering;

    public SequenceClustering() {
        properties = new AlgorithmProperty[4];
        alignmentTypeIndex = DEFAULT_ALIGNMENT_TYPE_INDEX;
        alignerType = getAlignerType();
        substitutionMatrixIndex = DEFAULT_SUBSTITUTION_MATRIX_INDEX; 
        substitutionMatrix = getSubstitutionMatrix();
        percentIdentity = DEFAULT_PID;
        clustering = true;
    }

    public int getPercentIdentity() {
        return percentIdentity;
    }

    public void setPercentIdentity(int percentIdentity) {
        if (percentIdentity < PID_MIN || percentIdentity > PID_MAX){
            throw new IllegalArgumentException("Invalid value for percent identity. It should be between " + PID_MIN + " and " + PID_MAX);
        }
        this.percentIdentity = percentIdentity;
    }    

    public boolean isClustering() {
        return clustering;
    }

    public void setClustering(boolean clustering) {
        this.clustering = clustering;
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

    public Alignments.PairwiseSequenceAlignerType getAlignerType() {
        if (Alignment_Type[alignmentTypeIndex].equals("Needleman-Wunsch")) {
            return Alignments.PairwiseSequenceAlignerType.GLOBAL;
        } else if (Alignment_Type[alignmentTypeIndex].equals("Smith-Waterman")) {
            return Alignments.PairwiseSequenceAlignerType.LOCAL;
        }
        return null;
    }



    public void initAlgo(Workspace workspace) {
        Map<String, String> outMap = new LinkedHashMap<>();
        outMap.put("Alignment type", Alignment_Type[alignmentTypeIndex]);
        outMap.put("Substitution matrix", Substitution_Matrix[substitutionMatrixIndex]);
        outMap.put("Percent identity", String.valueOf(percentIdentity));

        int maxKeyLength = 0;
        for (String key : outMap.keySet()) {
            if (key.length() > maxKeyLength) {
                maxKeyLength = key.length();
            }
        }
        
        StringBuilder msg;
        for (Map.Entry<String, String> entry : outMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            msg = new StringBuilder(key);
            for (int i = key.length() + 1; i <= maxKeyLength; i++) {
                msg.append(' ');
            }
            msg.append(" : ");
            msg.append(value);
        }
    }
    
    private int getDenominatorValue(SequencePair<ProteinSequence, AminoAcidCompound> pair, Peptide peptide1, Peptide peptide2) {
        switch (getAlignerType()) {
            case LOCAL:
                return Math.min(peptide1.getSequence().length(), peptide2.getSequence().length());
            case GLOBAL:
                return pair.getLength();
        }
        return 0;
    }    

    private float computeSequenceIdentity(Peptide peptide1, Peptide peptide2) {
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        float score;
        if (peptide1.getSequence().equals(peptide2.getSequence())) {
            score = 1;
        } else {
            try {
                pair = Alignments.getPairwiseAlignment(peptide1.getBiojavaSeq(), peptide2.getBiojavaSeq(),
                        alignerType, gapPenalty, substitutionMatrix);
                score = ((float) pair.getNumIdenticals()) / getDenominatorValue(pair, peptide1, peptide2);
            } catch (CompoundNotFoundException ex) {
//                log.log(Level.SEVERE, "Compound Not Found Exception: {0}", ex.getMessage());
                Exceptions.printStackTrace(ex);
                score = -1;
            }
        }
        return score;
    }

}
