/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;

/**
 *
 * @author loge
 */
public class SequenceAlignmentModel implements Cloneable{
    
    public static final String GLOBAL_ALIGNMENT_TEXT = "Global (Needleman-Wunsch)";
    public static final String LOCAL_ALIGNMENT_TEXT = "Local (Smith-Waterman)";

    public static final String[] ALIGNMENT_TYPE = new String[]{LOCAL_ALIGNMENT_TEXT, GLOBAL_ALIGNMENT_TEXT};
    public static final String[] SUBSTITUTION_MATRIX = new String[]{
        "Blosum 30 by Henikoff & Henikoff", "Blosum 35 by Henikoff & Henikoff", "Blosum 40 by Henikoff & Henikoff",
        "Blosum 45 by Henikoff & Henikoff", "Blosum 50 by Henikoff & Henikoff", "Blosum 55 by Henikoff & Henikoff",
        "Blosum 60 by Henikoff & Henikoff", "Blosum 62 by Henikoff & Henikoff", "Blosum 65 by Henikoff & Henikoff",
        "Blosum 70 by Henikoff & Henikoff", "Blosum 75 by Henikoff & Henikoff", "Blosum 80 by Henikoff & Henikoff",
        "Blosum 85 by Henikoff & Henikoff", "Blosum 90 by Henikoff & Henikoff", "Blosum 100 by Henikoff & Henikoff",
        "PAM 250 by Gonnet, Cohen & Benner", "PAM 250 by Dayhoff"};

    public static final int DEFAULT_ALIGNMENT_TYPE_INDEX = 0; // LOCAL_ALIGNMENT_TEXT
    public static final int DEFAULT_SUBSTITUTION_MATRIX_INDEX = 7; // Blosum 62 by Henikoff & Henikoff
    
    public static final int PID_MAX=90;
    public static final int PID_MIN=50;
    public static final int[] PID_REFS= new int[]{50, 70, 90};        
    public static final int DEFAULT_PID=70;    
    
    protected int percentIdentity;
    protected int alignmentTypeIndex, substitutionMatrixIndex;    

    public SequenceAlignmentModel() {
        percentIdentity = DEFAULT_PID;
        alignmentTypeIndex = DEFAULT_ALIGNMENT_TYPE_INDEX;
        substitutionMatrixIndex = DEFAULT_SUBSTITUTION_MATRIX_INDEX;
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
    
    public float getIndentityScore(){
        return percentIdentity / 100.f;
    } 

    public int getAlignmentTypeIndex() {
        return alignmentTypeIndex;
    }

    public void setAlignmentTypeIndex(int alignmentTypeIndex) {
        if (alignmentTypeIndex < 0 || alignmentTypeIndex >= ALIGNMENT_TYPE.length){
            throw new IllegalArgumentException("Invalid value for alignment type index. It should be between " + 0 + " and " + (ALIGNMENT_TYPE.length-1));
        }
        this.alignmentTypeIndex = alignmentTypeIndex;
    }        

    public int getSubstitutionMatrixIndex() {
        return substitutionMatrixIndex;
    }

    public void setSubstitutionMatrixIndex(int substitutionMatrixIndex) {
        if (substitutionMatrixIndex < 0 || substitutionMatrixIndex >= SUBSTITUTION_MATRIX.length){
            throw new IllegalArgumentException("Invalid value for substitution matrix index. It should be between " + 0 + " and " + (SUBSTITUTION_MATRIX.length-1));
        }        
        this.substitutionMatrixIndex = substitutionMatrixIndex;
    }
    
    public SubstitutionMatrix<AminoAcidCompound> getSubstitutionMatrix() {
        switch (SUBSTITUTION_MATRIX[substitutionMatrixIndex]) {
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
        switch(ALIGNMENT_TYPE[alignmentTypeIndex]){
            case LOCAL_ALIGNMENT_TEXT:
                return Alignments.PairwiseSequenceAlignerType.LOCAL;
            case GLOBAL_ALIGNMENT_TEXT:
                return Alignments.PairwiseSequenceAlignerType.GLOBAL;                    
        }
        return null;
    }        

    @Override
    public String toString() {
        Map<String, String> outMap = new LinkedHashMap<>();
        outMap.put("Alignment type", ALIGNMENT_TYPE[alignmentTypeIndex]);
        outMap.put("Substitution matrix", SUBSTITUTION_MATRIX[substitutionMatrixIndex]);
        outMap.put("Percent identity", String.valueOf(percentIdentity));

        int maxKeyLength = 0;
        for (String key : outMap.keySet()) {
            if (key.length() > maxKeyLength) {
                maxKeyLength = key.length();
            }
        }
        
        StringBuilder msg = new StringBuilder();
        for (Map.Entry<String, String> entry : outMap.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            msg.append(key);
            for (int i = key.length() + 1; i <= maxKeyLength; i++) {
                msg.append(' ');
            }
            msg.append(" : ");
            msg.append(value);
            msg.append("\n");
        }        
        return msg.toString(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        SequenceAlignmentModel copyModel = new SequenceAlignmentModel();
        copyModel.setAlignmentTypeIndex(getAlignmentTypeIndex());
        copyModel.setSubstitutionMatrixIndex(getSubstitutionMatrixIndex());
        copyModel.setPercentIdentity(getPercentIdentity());
        return copyModel;        
    }
    
    
    
    
}
