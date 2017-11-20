/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.Set;
import org.bapedis.core.io.Exporter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;

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
            Set<String> keys = attrModel.getMolecularDescriptorKeys();
            //Write header            
            pw.format("\"%s\"", Peptide.ID.getDisplayName());
            for (String key : keys) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    pw.write(separator);
                    pw.format("\"%s\"", attr.getDisplayName());
                }
            }
            pw.println();
            // Write data
            for (Peptide pept : attrModel.getPeptides()) {
                pw.format("\"%s\"", pept.getId());
                for (String key: keys) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        Object val = pept.getAttributeValue(attr);
                        pw.write(separator);
                        pw.format("\"%s\"", val != null ? val.toString() : "");
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
