/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.filters.impl;

import java.util.StringTokenizer;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.model.AnnotationType;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class PDBFilter implements Filter {
    protected final PDBFilterFactory factory;

    protected boolean negative;

    public PDBFilter(PDBFilterFactory factory) {
        this.factory = factory;
        negative = false;
    }

    public boolean isNegative() {
        return negative;
    }

    public void setNegative(boolean negative) {
        this.negative = negative;
    }

    @Override
    public String getDisplayName() {
        String text = NbBundle.getMessage(PDBFilter.class, "PDBFilter.name");
        return negative ? "Not (" + text + ")" : text;
    }

    @Override
    public boolean accept(Peptide peptide) {
        boolean accepted = false;
        String[] crossRefs = peptide.getAnnotationValues(AnnotationType.CROSSREF);
        StringTokenizer tokenizer;
        String db;
        for (String crossRef : crossRefs) {
            tokenizer = new StringTokenizer(crossRef, ":");
            db = tokenizer.nextToken();
            if (db.equals("PDB")) {
                accepted = true;
                break;
            }
        }
        return negative ? !accepted : accepted;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

}
