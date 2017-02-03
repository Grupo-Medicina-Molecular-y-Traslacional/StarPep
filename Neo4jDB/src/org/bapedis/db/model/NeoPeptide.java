/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.db.dao.NeoPeptideDAO;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NeoPeptide extends Peptide {

    protected final long neoId;
    protected EnumMap<AnnotationType, List<NeoNeighbor>> annotations;
    protected NeoPeptideDAO dao;

    public static String getPrefixName() {
        return NbBundle.getMessage(NeoPeptide.class, "NeoPeptide.prefix");
    }

    public NeoPeptide(long neoId, String id, String sequence, NeoPeptideDAO dao) {
        super(id, sequence);
        this.neoId = neoId;
        this.dao = dao;
        annotations = null;
    }

    public long getNeoId() {
        return neoId;
    }

    //Lazy loading
    protected void loadAnnotations() {
        annotations = new EnumMap<>(AnnotationType.class);
        for (AnnotationType aType : AnnotationType.values()) {
            annotations.put(aType, new LinkedList<NeoNeighbor>());
        }
        List<NeoNeighbor> neighbors = dao.getNeoNeighbors(this);
        for (NeoNeighbor neighbor : neighbors) {
            addNeighbor(neighbor);
        }
    }

    protected void addNeighbor(NeoNeighbor neoNeighbor) {
        AnnotationType aType = AnnotationType.valueOf(neoNeighbor.getLabel().toUpperCase());
        List<NeoNeighbor> neighbors = annotations.get(aType);
        neighbors.add(neoNeighbor);
    }

    public EnumMap<AnnotationType, List<NeoNeighbor>> getAnnotations() {
        if (annotations == null) {
            loadAnnotations();
        }
        return annotations;
    }

    public List<NeoNeighbor> getAnnotations(AnnotationType aType) {
        if (annotations == null) {
            loadAnnotations();
        }
        return annotations.get(aType);
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
