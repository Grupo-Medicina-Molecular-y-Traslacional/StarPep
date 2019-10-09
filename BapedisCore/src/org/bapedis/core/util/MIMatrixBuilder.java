/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.util;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.Bin;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.MIMatrix;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.impl.FeatureDiscretization;
import org.bapedis.core.task.ProgressTicket;
import static java.util.concurrent.ForkJoinTask.invokeAll;

/**
 *
 * @author Loge
 */
public class MIMatrixBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private int[][] binIndex;
    private MolecularDescriptor[] features;
    private ProgressTicket progressTicket;
    private AtomicBoolean stopRun;

    private final MIMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    private MIMatrixBuilder(MolecularDescriptor[] features, int[][] binIndex, ProgressTicket progressTicket, AtomicBoolean stopRun) {
        this(features, binIndex, new MIMatrix(features.length),
                0, features.length, 0, features.length,
                progressTicket, stopRun);
    }

    private MIMatrixBuilder(MolecularDescriptor[] features, int[][] binIndex, MIMatrix matrix,
            int xlow, int xhigh, int ylow, int yhigh,
            ProgressTicket progressTicket, AtomicBoolean stopRun) {
        this.binIndex = binIndex;
        this.features = features;
        this.matrix = matrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
        this.progressTicket = progressTicket;
        this.stopRun = stopRun;
    }
    
    public static MIMatrixBuilder createMatrixBuilder(Peptide[] peptides, MolecularDescriptor[] features, ProgressTicket ticket, AtomicBoolean stopRun) throws MolecularDescriptorNotFoundException {
        int[][] binIndex = new int[peptides.length][features.length];
        for (int i = 0; i < peptides.length; i++) {
            for (int j = 0; j < features.length; j++) {
                binIndex[i][j] = FeatureDiscretization.getBinIndex(peptides[i], features[j], features[j].getBinsPartition().getBins().length);
            }
        }
        return new MIMatrixBuilder(features, binIndex, ticket, stopRun);
    }    

    public void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    public MIMatrix getMIMatrix() {
        return matrix;
    }

    public int getWorkUnits() {
        return matrix.getSize();
    }

    @Override
    protected void compute() {
        if (xlow >= yhigh || stopRun.get()) {
            return; // Discard the elements above the diagonal
        }
        if (xhigh - xlow <= SEQUENTIAL_THRESHOLD) {
            if (yhigh - ylow <= SEQUENTIAL_THRESHOLD) {
                if (!stopRun.get()) {
                    computeDirectly();
                }
            } else if (!stopRun.get()) {
                int middle = ylow + (yhigh - ylow) / 2;
                // up and down
                MIMatrixBuilder up = new MIMatrixBuilder(features, binIndex, matrix, xlow, xhigh, ylow, middle, progressTicket, stopRun);
                MIMatrixBuilder down = new MIMatrixBuilder(features, binIndex, matrix, xlow, xhigh, middle, yhigh, progressTicket, stopRun);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            MIMatrixBuilder left = new MIMatrixBuilder(features, binIndex, matrix, xlow, middle, ylow, yhigh, progressTicket, stopRun);
            MIMatrixBuilder right = new MIMatrixBuilder(features, binIndex, matrix, middle, xhigh, ylow, yhigh, progressTicket, stopRun);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() {
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    try {
                        matrix.setValue(y, x, mutualInformation(y, x));
                        progressTicket.progress();
                    } catch (Exception ex) {
                        stopRun.set(true);
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    private double mutualInformation(int j, int k) throws MolecularDescriptorNotFoundException {
        double sum = 0.;

        Bin[] bj = features[j].getBinsPartition().getBins();
        Bin[] bk = features[k].getBinsPartition().getBins();

        //Calculating mutual information
        int numberOfInstances = binIndex.length;
        double pj, pk, pjk;
        for (int i = 0; i < bj.length; i++) {
            pj = (double) bj[i].getCount() / numberOfInstances;
            for (int l = 0; l < bk.length; l++) {
                pk = (double) bk[l].getCount() / numberOfInstances;
                pjk = (double) countInstances(binIndex, j, i, k, l) / numberOfInstances;
                if (pj > 0 && pk > 0 && pjk > 0) {
                    sum += pjk * Math.log(pjk / (pj * pk));
                }
            }
        }

        return sum;
    }

    private int countInstances(int[][] binIndex, int j, int i, int k, int l) throws MolecularDescriptorNotFoundException {
        int count = 0;
        for (int p = 0; p < binIndex.length; p++) {
            if (binIndex[p][j] == i && binIndex[p][k] == l) {
                count++;
            }
        }
        return count;
    }
}
