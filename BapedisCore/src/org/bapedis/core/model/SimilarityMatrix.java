/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.bapedis.core.ui.components.JQuickHistogram;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class SimilarityMatrix implements Serializable {

    protected final Peptide[] peptides;
    protected final JQuickHistogram histogram;
    protected static PeptideAttribute INDEX_ATTR = new PeptideAttribute("indexAttr", "indexAttr", Integer.class, false);
    protected double[] data;

    public SimilarityMatrix(Peptide[] peptides) {
        this.peptides = peptides;
        for (int index = 0; index < peptides.length; index++) {
            peptides[index].setAttributeValue(INDEX_ATTR, index);
        }
        int size = peptides.length;
        data = new double[size * (size - 1) / 2];
        histogram = new JQuickHistogram();
    }

    public Peptide[] getPeptides() {
        return peptides;
    }        

    public void setValue(Peptide peptide1, Peptide peptide2, double value) {
        if (peptide1.hasAttribute(INDEX_ATTR) && peptide2.hasAttribute(INDEX_ATTR)) {
            int x = (int) peptide1.getAttributeValue(INDEX_ATTR);
            int y = (int) peptide2.getAttributeValue(INDEX_ATTR);
            assert x != y;
            data[pos(x, y)] = value;
//            histogram.addData(value);
        }
    }

    public JQuickHistogram getHistogram() {
        return histogram;
    }

    public double getValue(Peptide peptide1, Peptide peptide2) {
        if (peptide1.hasAttribute(INDEX_ATTR) && peptide2.hasAttribute(INDEX_ATTR)) {
            int x = (int) peptide1.getAttributeValue(INDEX_ATTR);
            int y = (int) peptide2.getAttributeValue(INDEX_ATTR);
            assert x != y;
            return data[pos(x, y)];
        }
        return -1.f;
    }

    public double[] getValues() {
        return data;
    }

    public int getSize() {
        return data.length;
    }

    private int pos(int x, int y) {
        int a = x > y ? x : y;
        int b = x < y ? x : y;
        return a * (a - 1) / 2 + b;
    }

    private void writeObject(ObjectOutputStream o)
            throws IOException {
        o.writeInt(data.length);
        for (double value : data) {
            o.writeDouble(value);
        }
    }

    private void readObject(ObjectInputStream o)
            throws IOException, ClassNotFoundException {
        int size = o.readInt();
        data = new double[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = o.readFloat();
        }
    }
}
