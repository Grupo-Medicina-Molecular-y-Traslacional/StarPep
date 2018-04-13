/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

import java.util.Arrays;

public class ArrayUtil {

    public static int getMinIndex(float[] array) {
        float min = Float.MAX_VALUE;
        int idx = -1;
        for (int i = 0; i < array.length; i++) {
            if (array[i] < min) {
                min = array[i];
                idx = i;
            }
        }
        return idx;
    }

    /**
     * normalizes to MIN-MAX<br>
     * REPLACES NULL VALUES WITH MEDIAN(MIN,MAX)
     *
     * @param array
     * @return
     */
    public static Double[] normalize(Double array[], double min, double max,
            boolean replaceNullWithMedian) {
        double arrMax = max(array);
        double arrMin = min(array);
        double arrMean = mean(array);
        double deltaVal = arrMax - arrMin;
        double delta = (max - min);
        Double a[] = new Double[array.length];
        if (arrMin == arrMax) {
            Arrays.fill(a, min + delta / 2.0);
            return a;
        } else {
            for (int i = 0; i < a.length; i++) {
                Double v = array[i];
                if (replaceNullWithMedian && (v == null || Double.isNaN(v))) {
                    v = arrMean;
                }

                if (v == null) {
                    a[i] = null;
                } else {
                    a[i] = (v - arrMin) / deltaVal * delta + min;
                }
            }
            return a;
        }
    }

    public static double max(Double[] data) {
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

    public static double min(Double[] data) {
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

    public static double mean(Double[] data) {
        if (data.length == 0) {
            return Double.NaN;
        }
        double sum = 0.0;
        for (int i = 0; i < data.length; i++) {
            sum += data[i];
        }
        return sum / data.length;
    }
    
    public static Double[] logBinning(int numBins, double base) {
        Double[] d = new Double[numBins + 1];
        for (int i = 0; i < numBins + 1; i++) {
            d[i] = Math.pow(base, i);
        }
        return ArrayUtil.normalize(d, 0, 1, false);        
    }    
}
