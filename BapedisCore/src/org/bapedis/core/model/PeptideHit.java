/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author Loge
 */
public class PeptideHit implements Comparable<PeptideHit>{
    private final Peptide peptide;
    private final double score;

    public PeptideHit(Peptide peptide, double score) {
        this.peptide = peptide;
        this.score = score;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public double getScore() {
        return score;
    }  
    
    @Override
    public int compareTo(PeptideHit other) {
        double diff = getScore() - other.getScore();
        if (diff > 0) {
            return 1;
        } else if (diff < 0) {
            return -1;
        }
        return 0;
    }    
}
