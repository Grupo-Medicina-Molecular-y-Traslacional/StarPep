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
  public class BinsPartition {
    private final Bin[] bins;
    private final int numberOfInstances;
    private final double entropy;

    public BinsPartition(Bin[] bins, int numberOfInstances) {
        this.bins = bins;
        this.numberOfInstances = numberOfInstances;
        entropy = calcEntropy(bins);
    }

    public Bin[] getBins() {
        return bins;
    }

    public double getEntropy() {
        return entropy;
    }         

    public int getNumberOfInstances() {
        return numberOfInstances;
    }        
    
    private double calcEntropy(Bin[] bins) {
        double sum = 0.;
        double prob;
        for (Bin bin : bins) {
            if (bin.getCount() > 0) {
                prob = (double) bin.getCount() / numberOfInstances;
                sum -= prob * Math.log(prob);
            }
        }
        return sum;
    }
    
}
