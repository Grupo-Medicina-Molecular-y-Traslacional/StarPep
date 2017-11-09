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
    protected double max, min, mean, std;

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

    public void resetSummaryStats(Peptide[] peptides) {
        double[] data = new double[peptides.length];
        int pos = 0;
        for (Peptide pept : peptides) {
            data[pos++] = getDoubleValue(pept, this);
        }
        min = min(data);
        max = max(data);
        mean = mean(data);
        std = stddevp(data);
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

    public double getStd() {
        return std;
    }

    public static double normalizationMinMax(Object value, double min, double max) {
        double val = convertToDouble(value);
        return (val - min) / (max - min);
    }

    public static double getDoubleValue(Peptide pept, MolecularDescriptor attribute) {
        Object val = pept.getAttributeValue(attribute);
        return val == null ? Double.NaN : convertToDouble(val);
    }

    private static double convertToDouble(Object value) {
        if (value instanceof Double) {
            return (double) value;
        } else if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        throw new IllegalArgumentException("Unknown double value : " + value);
    }

    private static double max(double[] a) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if (Double.isNaN(a[i])) {
                return Double.NaN;
            }
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }

    private static double min(double[] a) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if (Double.isNaN(a[i])) {
                return Double.NaN;
            }
            if (a[i] < min) {
                min = a[i];
            }
        }
        return min;
    }

    private static double mean(double[] a) {
        if (a.length == 0) {
            return Double.NaN;
        }
        double sum = sum(a);
        return sum / a.length;
    }

    private static double varp(double[] a) {
        if (a.length == 0) {
            return Double.NaN;
        }
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / a.length;
    }

    private static double stddevp(double[] a) {
        return Math.sqrt(varp(a));
    }

    private static double sum(double[] a) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }
}
