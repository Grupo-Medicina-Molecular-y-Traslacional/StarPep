/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.util;

import org.bapedis.core.io.OUTPUT_OPTION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class RUtil {

    public static File writeToTable(Peptide[] peptides, MolecularDescriptor[] features, OUTPUT_OPTION output) throws IOException, Exception {
        File f = OSUtil.createTempFile("peptide-", "-features");
        // if (f.exists())
        // throw new IllegalStateException("file " + f.getAbsolutePath() +
        // " already exists");
        try (BufferedWriter bf = new BufferedWriter(new FileWriter(f))) {
            for (MolecularDescriptor md : features) {
                bf.write("\"" + md.getDisplayName() + "\" ");
            }
            bf.write("\n");
            for (int i = 0; i < peptides.length; i++) {
//                bf.write("\"" + (i + 1) + "\" ");
                for (MolecularDescriptor md : features) {
                    bf.write(getAttributeValue(peptides[i], md, output) + " ");
                }
                bf.write("\n");
            }
        }
        return f;
    }

    private static String getAttributeValue(Peptide peptide, MolecularDescriptor attr, OUTPUT_OPTION output) throws Exception {
        switch (output) {
            case Z_SCORE:
                return String.valueOf(attr.getNormalizedZscoreValue(peptide));
            case MIN_MAX:
                return String.valueOf(attr.getNormalizedMinMaxValue(peptide));
        }
        return peptide.getAttributeValue(attr).toString();
    }
}
