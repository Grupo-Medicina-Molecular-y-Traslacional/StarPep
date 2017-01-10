/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.Peptide;
import org.bapedis.db.dao.NeoPeptideDAO;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NeoPeptide extends Peptide {

    protected final long neoId;
    protected NeoNeighborsModel neoNeighborsModel;
    protected NeoPeptideDAO dao;

    public static String getPrefixName(){
        return NbBundle.getMessage(NeoPeptide.class, "NeoPeptide.prefix");
    }    
    public NeoPeptide(long neoId, String displayName, String sequence, NeoPeptideDAO dao) {
        super(displayName, sequence);
        this.neoId = neoId;
        this.dao = dao;
    }

    public long getNeoId() {
        return neoId;
    }
    
    public NeoNeighborsModel getNeighbors(){
        return dao.getNeoNeighborsBy(this);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.neoId ^ (this.neoId >>> 32));
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
        final NeoPeptide other = (NeoPeptide) obj;
        return other.neoId == this.neoId;
    }        

}
