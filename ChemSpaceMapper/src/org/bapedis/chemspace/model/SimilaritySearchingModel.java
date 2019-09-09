/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

/**
 *
 * @author Loge
 */
public class SimilaritySearchingModel {

    public enum Options {
        TOP_RANK_VALUE_OPTION, TOP_RANK_PERCENT_OPTION, SIMILARITY_THRESHOD_OPTION, SIMILARITY_PERCENT_OPTION
    }

    public static final int[] TOP_RANK_PERCENT = new int[]{1, 3, 5, 10};
    
    public static final int[] SIMILARITY_THRESHOLD_PERCENT = new int[]{50, 60, 70, 80, 90};

    private Options option;

    private int topRank;
    private int topPercentIndex;
    
    private double threshold;
    private int thresholdPercentIndex;
    

    public SimilaritySearchingModel() {
        topRank = 50;
        topPercentIndex = 3;
        threshold = 0.7;
        thresholdPercentIndex = 2;
    }

    public Options getOption() {
        return option;
    }

    public void setOption(Options option) {
        this.option = option;
    }

    public int getTopRank() {
        return topRank;
    }

    public void setTopRank(int topRank) {
        this.topRank = topRank;
    }

    public int getTopPercentIndex() {
        return topPercentIndex;
    }

    public int getTopPercentValue() {
        return TOP_RANK_PERCENT[topPercentIndex];
    }

    public void setTopPercentIndex(int topPercentIndex) {
        if (topPercentIndex < 0 || topPercentIndex > TOP_RANK_PERCENT.length) {
            throw new IllegalStateException("Unknown value for  top percent index: " + topPercentIndex);
        }
        this.topPercentIndex = topPercentIndex;
    }

    public double getThreshold() {
        return threshold;
    }

    public void setThreshold(double threshold) {
        this.threshold = threshold;
    }

    public int getThresholdPercentIndex() {
        return thresholdPercentIndex;
    }

    public void setThresholdPercentIndex(int thresholdPercentIndex) {
        if (thresholdPercentIndex < 0 || thresholdPercentIndex > SIMILARITY_THRESHOLD_PERCENT.length) {
            throw new IllegalStateException("Unknown value for  threshold percent index: " + thresholdPercentIndex);
        }        
        this.thresholdPercentIndex = thresholdPercentIndex;
    }
    
    public int getThresholdPercentValue(){
        return SIMILARITY_THRESHOLD_PERCENT[thresholdPercentIndex];
    }

}
