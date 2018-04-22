/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class Batch {
    private final Peptide[] peptides;
    private int cursor;

    public Batch(int size) {
        peptides = new Peptide[size];
        cursor = 0;
    }
    
    public void addPeptide(Peptide peptide){
        peptides[cursor++] = peptide;
    }
    
    public Peptide getPeptide(int pos){
        return peptides[pos];
    }

    public Peptide[] getPeptides() {
        return peptides;
    }        
    
    public int getSize(){
        return peptides.length;
    }   
    
}
