/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author Loge
 */
public class NMIMatrix {
    protected double[] data;

    public NMIMatrix(int size) {
        data = new double[size * (size - 1) / 2];
    }

    public void setValue(int j, int k, double value) {
        assert j != k;
        data[pos(j, k)] = value;
    }

    public double getValue(int j, int k) {
        assert j != k;
        return data[pos(j, k)];
    }

    public double[] getValues() {
        return data;
    }

    public int getSize() {
        return data.length;
    }

    private int pos(int j, int k) {
        int a = j > k ? j : k;
        int b = j < k ? j : k;
        return a * (a - 1) / 2 + b;
    }
    
}
