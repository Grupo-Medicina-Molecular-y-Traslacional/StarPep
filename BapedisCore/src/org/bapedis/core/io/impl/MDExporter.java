/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.io.impl;

import java.io.File;
import java.io.PrintWriter;
import java.util.Map;
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
            Map<String, MolecularDescriptor[]> map = attrModel.getAllMolecularDescriptors();
            //Write header            
            pw.format("\"%s\"", Peptide.ID.getDisplayName());
            for (Map.Entry<String, MolecularDescriptor[]> entry : map.entrySet()) {
                for (MolecularDescriptor attr : entry.getValue()) {
                    pw.write(separator);
                    pw.format("\"%s\"", attr.getDisplayName());
                }
            }
            pw.println();
            // Write data
            for (Peptide pept : attrModel.getPeptides()) {
                pw.format("\"%s\"", pept.getId());
                for (Map.Entry<String, MolecularDescriptor[]> entry : map.entrySet()) {
                    for (MolecularDescriptor attr : entry.getValue()) {
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
