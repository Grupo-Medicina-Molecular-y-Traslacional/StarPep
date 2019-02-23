/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jmol.model;

import java.util.ArrayList;
import java.util.Objects;
import java.util.StringTokenizer;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.StarPepAnnotationType;

/**
 *
 * @author loge
 */
public class StructureData {
    private final Peptide peptide;
    private final ArrayList<String> structures;
    
    public StructureData(Peptide peptide) {
        this.peptide = peptide;
        structures = new ArrayList<>();
        String[] crossRefs = peptide.getAnnotationValues(StarPepAnnotationType.CROSSREF);
        StringTokenizer tokenizer;
        String db, code;
        for (String crossRef : crossRefs) {
            tokenizer = new StringTokenizer(crossRef, ":");
            db = tokenizer.nextToken();
            if (db.equals("PDB")) {
                code = tokenizer.nextToken();
                structures.add(code.trim());
            }
        }  
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public String[] getStructures() {
        return structures.toArray(new String[0]);
    }       

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.peptide);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final StructureData other = (StructureData) obj;
        if (!Objects.equals(this.peptide, other.peptide)) {
            return false;
        }
        return true;
    }        
    
}
