/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.Iterator;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.data.PeptideDAO;

/**
 *
 * @author loge
 */
public class MDExporter implements Exporter {

    protected final AttributesModel attrModel;
    protected final char separator = ',';

    public MDExporter(AttributesModel attrModel) {
        this.attrModel = attrModel;
    }

    @Override
    public void exportTo(File file) throws Exception {
        PrintWriter pw = new PrintWriter(file);
        try {
            //Write header
            pw.format("\"%s\"",PeptideDAO.ID.getDisplayName());
            for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
                PeptideAttribute attr = it.next();
                if (attr.isMolecularDescriptor()) {
                    pw.write(separator);
                    pw.format("\"%s\"",attr.getDisplayName());
                }
            }
            pw.println();
            // Write data
            for (Peptide pept : attrModel.getPeptides()) {
                pw.format("\"%s\"",pept.getId());
                for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
                    PeptideAttribute attr = it.next();
                    if (attr.isMolecularDescriptor()) {
                        Object val = pept.getAttributeValue(attr);
                        pw.write(separator);
                        pw.format("\"%s\"",val != null ? val.toString() : "");
                    }
                }
                pw.println();
            }
        } finally {
            pw.flush();
            pw.close();
        }
    }

}
