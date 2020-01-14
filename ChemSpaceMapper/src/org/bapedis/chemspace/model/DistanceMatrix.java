/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import org.bapedis.core.model.Peptide;
import static org.bapedis.chemspace.impl.MapperAlgorithm.INDEX_ATTR;
/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class DistanceMatrix {

    protected double[] data;

    public DistanceMatrix(Peptide[] peptides) {
        int size = peptides.length;
        data = new double[size * (size - 1) / 2];
    }    

    public void setValue(Peptide peptide1, Peptide peptide2, double value) {
        if (peptide1.hasAttribute(INDEX_ATTR) && peptide2.hasAttribute(INDEX_ATTR)) {
            int x = (int) peptide1.getAttributeValue(INDEX_ATTR);
            int y = (int) peptide2.getAttributeValue(INDEX_ATTR);
            assert x != y;
            data[pos(x, y)] = value;
        }
    }

    public double getValue(Peptide peptide1, Peptide peptide2) {
        if (peptide1.hasAttribute(INDEX_ATTR) && peptide2.hasAttribute(INDEX_ATTR)) {
            int x = (int) peptide1.getAttributeValue(INDEX_ATTR);
            int y = (int) peptide2.getAttributeValue(INDEX_ATTR);
            assert x != y;
            return data[pos(x, y)];
        }
        return Double.NaN;
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

}
