/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import java.util.StringTokenizer;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.db.model.AnnotationType;
import org.bapedis.db.model.NeoPeptide;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class PDBFilter implements Filter {

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(PDBFilter.class, "PDBFilter.name");
    }

    @Override
    public boolean accept(Peptide peptide) {
        NeoPeptide neoPeptide = (NeoPeptide) peptide;
        String[] crossRefs = neoPeptide.getAnnotationValues(AnnotationType.CROSSREF);
        StringTokenizer tokenizer;
        String db;
        for (String crossRef : crossRefs) {
            tokenizer = new StringTokenizer(crossRef, ":");
            db = tokenizer.nextToken();
            if (db.equals("PDB")) {
                return true;
            }
        }
        return false;
    }

}
