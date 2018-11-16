/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.model;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class Vertex {
    private final Peptide peptide;
    private int vertexIndex;
    private int gain;
    private boolean locked;

    public Vertex(Peptide peptide) {
        this.peptide = peptide;
        gain = 0;
        locked = false;
    }

    public Peptide getPeptide() {
        return peptide;
    }   

    public void setVertexIndex(int vertexIndex) {
        this.vertexIndex = vertexIndex;
    }
    
    public int getVertexIndex() {
        return vertexIndex;
    }        

    public int getGain() {
        return gain;
    }

    public void setGain(int gain) {
        this.gain = gain;
    }   

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }        
}
