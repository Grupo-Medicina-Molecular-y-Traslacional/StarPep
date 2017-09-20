/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class PairwiseSimMatrix implements Serializable {

    protected Peptide[] peptide;
    protected double[] data;
    protected int size;
    public static final PeptideAttribute MAPPING_INDEX = new PeptideAttribute("mapIndex", "Internal mapping index", Integer.class);

    public PairwiseSimMatrix(Peptide[] peptide) {
        this.peptide = peptide;        
        for (int i = 0; i < peptide.length; i++) {
            peptide[i].setAttributeValue(MAPPING_INDEX, i);
        }
        size = peptide.length;
        data = new double[size * (size - 1) / 2];
    }    

    public void set(Peptide peptide1, Peptide peptide2, double value) {
        int x = (int)peptide1.getAttributeValue(MAPPING_INDEX);
        int y = (int)peptide2.getAttributeValue(MAPPING_INDEX);;
        assert x != y || (x == y && value == 1);
        if (x != y) {
            data[ pos(x, y)] = value;
        }
    }

    public double get(Peptide peptide1, Peptide peptide2) {
        int x = (int)peptide1.getAttributeValue(MAPPING_INDEX);
        int y = (int)peptide2.getAttributeValue(MAPPING_INDEX);
        if (x == y) {
            return 1;
        }
        return data[ pos(x, y)];
    }

    public double[] getValues() {
        return data;
    }

    private int pos(int x, int y) {
        int a = x > y ? x : y;
        int b = x < y ? x : y;
        return a * (a - 1) / 2 + b;
    }

    public int getSize() {
        return size;
    }

    private void writeObject(ObjectOutputStream o)
            throws IOException {
        o.writeInt(size);
        for (Peptide seq : peptide) {
            o.writeObject(seq);
        }
        for (double d : data) {
            o.writeDouble(d);
        }
    }

    private void readObject(ObjectInputStream o)
            throws IOException, ClassNotFoundException {
        size = o.readInt();
        peptide = new Peptide[size];
        for (int i = 0; i < size; i++) {
            peptide[i] = ((Peptide) o.readObject());
        }
        for (int i = 0; i < peptide.length; i++) {
            peptide[i].setAttributeValue(MAPPING_INDEX, i);
        }
        data = new double[size * (size - 1) / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = o.readDouble();
        }
    }
}
