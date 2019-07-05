/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.model;

/**
 *
 * @author Loge
 */
public class CorrelationPair implements Comparable<CorrelationPair>{
    private final int i, j;
    private final double val;

    public CorrelationPair(int i, int j, double val) {
        this.i = i;
        this.j = j;
        this.val = val;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public double getVal() {
        return val;
    }

    @Override
    public int compareTo(CorrelationPair other) {
        return Double.compare(val, other.val);
    }
    
}
