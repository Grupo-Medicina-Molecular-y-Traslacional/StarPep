/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.util;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.featureSelection.model.IntDoublePair;

/**
 *
 * @author Loge
 */
public class CorrelationUtility {
    
    public static double[] rank(double[] data) {
        // Array recording initial positions of data to be ranked
        IntDoublePair[] ranks = new IntDoublePair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntDoublePair(data[i], i);
        }

        // Sort the IntDoublePairs
        Arrays.parallelSort(ranks);

        // Walk the sorted array, filling output array using sorted positions,
        // resolving ties as we go
        double[] out = new double[ranks.length];
        int pos = 1;  // position in sorted array
        out[ranks[0].getPosition()] = pos;
        List<Integer> tiesTrace = new LinkedList<>();
        tiesTrace.add(ranks[0].getPosition());
        for (int i = 1; i < ranks.length; i++) {
            if (Double.compare(ranks[i].getValue(), ranks[i - 1].getValue()) > 0) {
                // tie sequence has ended (or had length 1)
                pos = i + 1;
                if (tiesTrace.size() > 1) {  // if seq is nontrivial, resolve
                    resolveTie(out, tiesTrace);
                }
                tiesTrace = new LinkedList<>();
                tiesTrace.add(ranks[i].getPosition());
            } else {
                // tie sequence continues
                tiesTrace.add(ranks[i].getPosition());
            }
            out[ranks[i].getPosition()] = pos;
        }
        if (tiesTrace.size() > 1) {  // handle tie sequence at end
            resolveTie(out, tiesTrace);
        }
        return out;
    }
    
    public static double calculatePearsonCorrelation(double[] xArray, double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new IllegalArgumentException("Array dimensions mismatch");
        } else if (xArray.length < 2) {
            throw new IllegalArgumentException("Insufficient array dimension");
        }

        double diff1, diff2, num = 0.0, sx = 0.0, sy = 0.0;
        double xMean = MolecularDescriptor.mean(xArray), yMean = MolecularDescriptor.mean(yArray);

        for (int i = 0; i < xArray.length; i++) {
            diff1 = xArray[i] - xMean;
            diff2 = yArray[i] - yMean;
            num += (diff1 * diff2);
            sx += (diff1 * diff1);
            sy += (diff2 * diff2);
        }
        return num / Math.sqrt(sx * sy);
    }    
    
    /**
     * Resolve a sequence of ties, using the configured {@link TiesStrategy}.
     * The input <code>ranks</code> array is expected to take the same value for
     * all indices in <code>tiesTrace</code>. The common value is recoded
     * according to the tiesStrategy. For example, if ranks = <5,8,2,6,2,7,1,2>,
     * tiesTrace = <2,4,7> and tiesStrategy is MINIMUM, ranks will be unchanged.
     * The same array and trace with tiesStrategy AVERAGE will come out
     * <5,8,3,6,3,7,1,3>.
     *
     * @param ranks array of ranks
     * @param tiesTrace list of indices where <code>ranks</code> is constant --
     * that is, for any i and j in TiesTrace, <code> ranks[i] == ranks[j]
     * </code>
     */
    private static void resolveTie(double[] ranks, List<Integer> tiesTrace) {
//        // constant value of ranks over tiesTrace
        final double c = ranks[tiesTrace.get(0)];

        // length of sequence of tied ranks
        final int length = tiesTrace.size();

        // Replace ranks with average 
        double value = (2 * c + length - 1) / 2d;
//        double sum = 0;
//        for (int pos : tiesTrace) {
//            sum += pos;
//        }
//        double avg = sum / length;

        Iterator<Integer> iterator = tiesTrace.iterator();
        while (iterator.hasNext()) {
            ranks[iterator.next()] = value;
        }
    }
    
}
