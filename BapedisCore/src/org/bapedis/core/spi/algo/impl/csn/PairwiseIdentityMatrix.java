/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import org.bapedis.core.model.Peptide;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class PairwiseIdentityMatrix implements Serializable {

    protected ArrayList<Peptide> peptide;
    protected HashMap<Peptide, Integer> map;
    protected double[] data;
    protected int size;

    public PairwiseIdentityMatrix(ArrayList<Peptide> peptide) {
        this.peptide = peptide;
        map = new HashMap<>();
        for (int i = 0; i < peptide.size(); i++) {
            map.put(peptide.get(i), i);
        }
        size = peptide.size();
        data = new double[size * (size - 1) / 2];
    }
    
    public boolean contains(Peptide peptide){
        return map.containsKey(peptide);
    }

    public void set(Peptide peptide1, Peptide peptide2, double value) {
        int x = map.get(peptide1);
        int y = map.get(peptide2);
        assert x != y || (x == y && value == 1);
        if (x != y) {
            data[ pos(x, y)] = value;
        }
    }

    public double get(Peptide peptide1, Peptide peptide2) {
        int x = map.get(peptide1);
        int y = map.get(peptide2);
        if (x == y) {
            return 1;
        }
        return data[ pos(x, y)];
    }

    public double[] getData() {
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
        peptide = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            peptide.set(i, ((Peptide) o.readObject()));
        }
        map = new HashMap<>();
        for (int i = 0; i < peptide.size(); i++) {
            map.put(peptide.get(i), i);
        }
        data = new double[size * (size - 1) / 2];
        for (int i = 0; i < data.length; i++) {
            data[i] = o.readDouble();
        }
    }
}
