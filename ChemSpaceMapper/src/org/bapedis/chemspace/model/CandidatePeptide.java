/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author Loge
 */
public class CandidatePeptide implements Comparable<CandidatePeptide> {

    private final double distance;
    private final Peptide peptide;
    private final int index;

    public CandidatePeptide(Peptide peptide, double distance) {
        this(-1, peptide, distance);
    }

    public CandidatePeptide(int index, Peptide peptide, double distance) {
        this.index = index;
        this.peptide = peptide;
        this.distance = distance;
    }

    public int getIndex() {
        return index;
    }        
    
    public Peptide getPeptide() {
        return peptide;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(CandidatePeptide o) {
        if (distance < o.distance) {
            return -1;
        }
        if (distance > o.distance) {
            return 1;
        }
        return 0;
    }
}
