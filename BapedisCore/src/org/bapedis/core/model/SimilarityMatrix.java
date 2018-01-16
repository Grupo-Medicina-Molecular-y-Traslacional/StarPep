/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class SimilarityMatrix implements Serializable {

    protected static PeptideAttribute INDEX_ATTR = new PeptideAttribute("indexAttr", "indexAttr", Integer.class);
    protected Float[] data;

    public SimilarityMatrix(Peptide[] peptides) { 
        for(int index = 0; index < peptides.length; index++){
            peptides[index].setAttributeValue(INDEX_ATTR, index);
        }
        int size = peptides.length;
        data = new Float[size * (size - 1) / 2];
    }
    

    public void setValue(Peptide peptide1, Peptide peptide2, Float value) {
        int x = (int)peptide1.getAttributeValue(INDEX_ATTR);
        int y = (int)peptide2.getAttributeValue(INDEX_ATTR);
        data[ pos(x, y)] = value;
    }

    public Float getValue(Peptide peptide1, Peptide peptide2) {
        int x = (int)peptide1.getAttributeValue(INDEX_ATTR);
        int y = (int)peptide2.getAttributeValue(INDEX_ATTR);
        return data[ pos(x, y)];
    }

    public Float[] getValues() {
        return data;
    }
    
    public int getSize(){
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
        for (Float value : data) {
            o.writeDouble(value);
        }
    }

    private void readObject(ObjectInputStream o)
            throws IOException, ClassNotFoundException {
        int size = o.readInt();
        data = new Float[size];
        for (int i = 0; i < data.length; i++) {
            data[i] = o.readFloat();
        }
    }
}
