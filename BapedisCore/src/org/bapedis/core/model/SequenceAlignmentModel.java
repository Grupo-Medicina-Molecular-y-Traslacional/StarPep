/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedHashMap;
import java.util.Map;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;

/**
 *
 * @author loge
 */
public class SequenceAlignmentModel {
    public static final int PID_MAX=90;
    public static final int PID_MIN=50;
    public static final int[] PID_REFS= new int[]{50, 70, 90};        
    public static final int DEFAULT_PID=70;    
    
    protected SubstitutionMatrix<AminoAcidCompound> substitutionMatrix;
    protected Alignments.PairwiseSequenceAlignerType alignerType;
    protected int percentIdentity;

    public SequenceAlignmentModel() {
        
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

    public SubstitutionMatrix<AminoAcidCompound> getSubstitutionMatrix() {
        return substitutionMatrix;
    }

    public void setSubstitutionMatrix(SubstitutionMatrix<AminoAcidCompound> substitutionMatrix) {
        this.substitutionMatrix = substitutionMatrix;
    }

    public Alignments.PairwiseSequenceAlignerType getAlignerType() {
        return alignerType;
    }

    public void setAlignerType(Alignments.PairwiseSequenceAlignerType alignerType) {
        this.alignerType = alignerType;
    }        

    @Override
    public String toString() {
        Map<String, String> outMap = new LinkedHashMap<>();
        outMap.put("Alignment type", alignerType.name());
        outMap.put("Substitution matrix", substitutionMatrix.getName());
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
    
    
}
