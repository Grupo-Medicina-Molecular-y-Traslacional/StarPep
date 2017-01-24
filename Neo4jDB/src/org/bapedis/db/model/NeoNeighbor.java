/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.HashSet;

/**
 *
 * @author loge
 */
public class NeoNeighbor {
    protected final long neoId;
    protected final String label;    
    protected final String name;
    protected final HashSet<NeoPeptide> sourcePeptides;
    
    public NeoNeighbor(long neoId, String label, String name) {
        this.neoId = neoId;
        this.label = label;
        this.name = name;
        sourcePeptides = new HashSet<>();
    }

    public long getNeoId() {
        return neoId;
    }  

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }        
    
    public void addSourcePeptide(NeoPeptide neoPeptide){
        sourcePeptides.add(neoPeptide);
    }
    
    public boolean containsSourcePeptide(NeoPeptide neoPeptide){
        return sourcePeptides.contains(neoPeptide);
    }
    
    public NeoPeptide[] getSourcePeptides(){
        return sourcePeptides.toArray(new NeoPeptide[0]);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (int) (this.neoId ^ (this.neoId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NeoNeighbor other = (NeoNeighbor) obj;
        return this.neoId != other.neoId;
    }
    
    
}
