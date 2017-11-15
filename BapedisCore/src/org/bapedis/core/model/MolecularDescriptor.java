/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

/**
 *
 * @author loge
 */
public class MolecularDescriptor extends PeptideAttribute {

    public static final String DEFAULT_CATEGORY = "Default";
    protected final String category;
    protected double max, min, mean, var, std;

    public MolecularDescriptor(String id, String displayName, Class<?> type) {
        this(id, displayName, type, DEFAULT_CATEGORY);
    }

    public MolecularDescriptor(String id, String displayName, Class<?> type, String category) {
        super(id, displayName, type);
        this.category = category;
        min = Double.NaN;
        max = Double.NaN;
        mean = Double.NaN;
        std = Double.NaN;
    }

    public String getCategory() {
        return category;
    }

    public void resetSummaryStats(Peptide[] peptides) throws MolecularDescriptorNotFoundException {
        double[] data = new double[peptides.length];
        int pos = 0;
        for (Peptide pept : peptides) {
            data[pos++] = getDoubleValue(pept, this);
        }
        min = min(data);
        max = max(data);
        mean = mean(data);
        var = varp(data, mean);
        std = Math.sqrt(var);
    }

    public double getMax() {
        return max;
    }

    public double getMin() {
        return min;
    }

    public double getMean() {
        return mean;
    }

    public double getVar() {
        return var;
    }

    public double getStd() {
        return std;
    }
    
    public double getNormalizedMinMaxValue(Peptide peptide) throws MolecularDescriptorNotFoundException{
       if (Double.isNaN(min) || Double.isNaN(max)){
         throw new UnsupportedOperationException("The min and max values have not been calculated for molecular feature: " + displayName);
       }
       double val = getDoubleValue(peptide, this);
       return (val - min) / (max - min);
    }
    
    public double getNormalizedZscoreValue(Peptide peptide) throws MolecularDescriptorNotFoundException{
       if (Double.isNaN(mean) || Double.isNaN(std)){
         throw new UnsupportedOperationException("The mean and std values have not been calculated for molecular feature: " + displayName);
       } 
       double val = getDoubleValue(peptide, this);
       return (val - mean) / std;
    }

    public static double getDoubleValue(Peptide pept, MolecularDescriptor attribute) throws MolecularDescriptorNotFoundException {
        Object val = pept.getAttributeValue(attribute);
        if (val == null) {
            throw new MolecularDescriptorNotFoundException(pept, attribute);
        }
        return convertToDouble(val);
    }

    private static double convertToDouble(Object value) {
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        throw new IllegalArgumentException("Unknown double value : " + value);
    }

    private static double max(double[] data) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                return Double.NaN;
            }
            if (data[i] > max) {
                max = data[i];
            }
        }
        return max;
    }

    private static double min(double[] data) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < data.length; i++) {
            if (Double.isNaN(data[i])) {
                return Double.NaN;
            }
            if (data[i] < min) {
                min = data[i];
            }
        }
        return min;
    }

    private static double mean(double[] data) {
        if (data.length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum / data.length;
    }

    private static double varp(double[] data, double avg) {
        if (data.length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            sum += (data[i] - avg) * (data[i] - avg);
        }
        return sum / data.length;
    }
}
