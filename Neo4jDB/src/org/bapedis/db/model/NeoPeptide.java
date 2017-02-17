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
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NeoPeptide extends Peptide {

    protected final long neoId;
    protected EnumMap<AnnotationType, List<NeoNeighbor>> annotations;

    public static String getPrefixName() {
        return NbBundle.getMessage(NeoPeptide.class, "NeoPeptide.prefix");
    }

    public NeoPeptide(long neoId, List<NeoNeighbor> neighbors) {
        this.neoId = neoId;
        annotations = new EnumMap<>(AnnotationType.class);
        for (AnnotationType aType : AnnotationType.values()) {
            annotations.put(aType, new LinkedList<NeoNeighbor>());
        }
        for (NeoNeighbor neighbor : neighbors) {
            AnnotationType aType = AnnotationType.valueOf(neighbor.getLabel().toUpperCase());
            annotations.get(aType).add(neighbor);
        }
    }

    public long getNeoId() {
        return neoId;
    }

    public List<NeoNeighbor> getAnnotations(AnnotationType aType) {
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
