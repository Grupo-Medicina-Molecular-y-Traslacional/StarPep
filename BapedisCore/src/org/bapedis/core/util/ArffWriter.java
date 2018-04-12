/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mguetlein 
 * Modified by loge
 */
public class ArffWriter {

    public static boolean DEBUG = false;

    public static File writeToArffFile(ArffWritable data) throws Exception {
        File f = createTempFile();
        writeToArffFile(f, data);
        return f;
    }

    public static void writeToArffFile(File file, ArffWritable data) throws Exception {
        // if (file.exists())
        // throw new IllegalStateException("arff file exists: '" + file + "'");
        if (DEBUG) {
            System.out.println("Writing arff file: '" + file.getAbsolutePath() + "'"); //(.tmp)");
        }
        writeToArff(new FileWriter(file), data);
    }

    public static void writeToArff(final Writer w, ArffWritable data) throws Exception {
        class LineWriter {

            public void println(String s) throws IOException {
                w.write(s);
                w.write("\n");
            }

            public void println() throws IOException {
                w.write("\n");
            }

            public void println(StringBuffer s) throws IOException {
                w.write(s.toString());
                w.write("\n");
            }
        }
        LineWriter out = new LineWriter();

        out.println(
                "% generated: " + new SimpleDateFormat("yyyy.MM.dd HH:mm:ss").format(new Date()));

        if (data.getAdditionalInfo() != null) {
            for (String info : data.getAdditionalInfo()) {
                out.println("% " + info);
            }
        }

        out.println();
        out.println("@relation \"" + data.getRelationName() + "\"");
        out.println();

        boolean numeric = false;

        for (int i = 0; i < data.getNumAttributes(); i++) {
            String domain[] = data.getAttributeDomain(i);
            if (domain == null) {
                out.println("@attribute \"" + data.getAttributeName(i) + "\" numeric");
                numeric = true;
            } else {
                out.println("@attribute \"" + data.getAttributeName(i) + "\" "
                        + toString(domain, ",", "{", "}", ""));
            }
        }
        out.println();

        out.println("@data");

        boolean sparse = data.isSparse();

        if (sparse) {
            if (numeric) {
                throw new Error(
                        "numeric and sparse is not supported, missing values must explicity represented as ?");
            }

            for (int i = 0; i < data.getNumInstances(); i++) {
                // if (data.isInstanceWithoutAttributeValues(i))
                // continue;

                StringBuffer s = new StringBuffer("{");
                boolean first = true;

                for (int j = 0; j < data.getNumAttributes(); j++) {
                    String value = data.getAttributeValue(i, j);
                    if (value == null || !value.equals("0")) {
                        if (!first) {
                            s.append(", ");
                        } else {
                            first = false;
                        }

                        if (value == null) {
                            s.append(j + " " + data.getMissingValue(j));
                        } else {
                            s.append(j + " " + value);
                        }
                    }
                }
                s.append("}");

                out.println(s);
            }
        } else {
            for (int i = 0; i < data.getNumInstances(); i++) {
                StringBuffer s = new StringBuffer();

                for (int j = 0; j < data.getNumAttributes(); j++) {
                    if (j > 0) {
                        s.append(",");
                    }
                    String value = data.getAttributeValue(i, j);
                    if (value != null) {
                        s.append(value);
                    } else {
                        s.append(data.getMissingValue(j));
                    }
                }
                out.println(s);
            }
        }
        w.close();
    }

    private static String toString(Object array[], String seperator, String openingBracket,
            String closingBracket, String space) {
        String s = openingBracket;
        for (int i = 0; i < array.length; i++) {
            s += array[i] + seperator + space;
        }
        if (array.length > 0) {
            s = s.substring(0, s.length() - (space.length() + seperator.length()));
        }
        s += closingBracket;
        return s;
    }

    private static File createTempFile() throws IOException {
        File tempFile = File.createTempFile("weka-", "-arff");
        tempFile.deleteOnExit();
        return tempFile;
    }
}
