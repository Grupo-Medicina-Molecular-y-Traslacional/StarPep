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
    private final double entropy, maxEntropy;

    public BinsPartition(Bin[] bins) {
        this.bins = bins;
        entropy = calculateEntropy(bins);
        maxEntropy = Math.log(bins.length);
    }

    public Bin[] getBins() {
        return bins;
    }

    public double getEntropy() {
        return entropy;
    }     
    
    public double getMaximumEntropy(){
        return maxEntropy;
    } 
    
    private double calculateEntropy(Bin[] bins) {
        double entropy = 0.;
        double prob;
        for (Bin bin : bins) {
            if (bin.getCount() > 0) {
                prob = (double) bin.getCount() / bins.length;
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy;
    }
    
}
