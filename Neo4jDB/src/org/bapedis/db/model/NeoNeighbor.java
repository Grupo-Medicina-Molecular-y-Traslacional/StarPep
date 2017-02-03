/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.Arrays;

/**
 *
 * @author loge
 */
public class NeoNeighbor {
    protected final long neoId;
    protected final String label;    
    protected final String name;
    protected final String[] xref;
    
    public NeoNeighbor(long neoId, String label, String name, String[] xref) {
        this.neoId = neoId;
        this.label = label;
        this.name = name;
        this.xref = xref;
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

    public String[] getXref() {
        return xref;
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
        return this.neoId == other.neoId;
    }
    
    
}
