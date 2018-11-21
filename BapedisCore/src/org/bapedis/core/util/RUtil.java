/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.util;

import java.io.BufferedReader;
import org.bapedis.core.io.OUTPUT_OPTION;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.openide.modules.Places;

/**
 *
 * @author loge
 */
public class RUtil {

    public static String R_LIB_DIR = "r_libs";
    private static String REPOSITORY = "http://cran.r-project.org";
    public static Binary RSCRIPT_BINARY = new Binary("Rscript", "CM_RSCRIPT_PATH");

    // private static String REPOSITORY = "http://ftp5.gwdg.de/pub/misc/cran"; // göttingen
    public static String installAndLoadPackage(String pack) {
        return ".libPaths(\"" + OSUtil.getAbsolutePathEscaped(new File(Places.getCacheDirectory(), R_LIB_DIR))
                + "\")\n"
                + //
                "packages <- installed.packages()[,1]\n" //
                + "if (!(is.element(\"" + pack + "\", packages))) install.packages(\"" + pack + "\",repos=\""
                + REPOSITORY + "\",lib=\"" + OSUtil.getAbsolutePathEscaped(new File(Places.getCacheDirectory(), R_LIB_DIR)) + "\")\n"
                + //
                "library(\"" + pack + "\")\n";
    }

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

    public static List<Integer[]> readCluster(String matrixFile) {
        File f = new File(matrixFile);
        if (!f.exists()) {
            throw new IllegalStateException("matrix file not found: " + f.getAbsolutePath());
        }
        List<Integer[]> l = new ArrayList<Integer[]>();
        try {
            BufferedReader bf = new BufferedReader(new FileReader(f));
            String line;
            boolean firstline = true;
            while ((line = bf.readLine()) != null) {
                // System.out.println(line);
                String s[] = line.split(" ");
                // System.out.println(ArrayUtil.toString(s));

                if (firstline) {
                    firstline = false;
                } else {
                    Integer c[] = null;
                    for (int i = 0; i < s.length; i++) {
                        if (i == 1) {
                            String ss = s[i].replaceAll("^\"|\"$", "");
                            if (ss.length() == 0) {
                                c = new Integer[0];
                            } else if (ss.contains("#")) {
                                String vals[] = ss.split("#");
                                c = new Integer[vals.length];
                                for (int j = 0; j < vals.length; j++) {
                                    c[j] = new Integer(vals[j]);
                                }
                            } else {
                                c = new Integer[]{new Integer(ss)};
                            }
                        }
                    }
                    l.add(c);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return l;

    }
}
