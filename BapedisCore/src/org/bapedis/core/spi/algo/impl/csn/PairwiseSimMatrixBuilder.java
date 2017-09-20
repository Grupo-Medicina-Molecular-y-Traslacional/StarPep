/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class PairwiseSimMatrixBuilder extends RecursiveAction {

    static final int SEQUENTIAL_THRESHOLD = 10;
    protected Peptide[] peptides;
    protected PairwiseSimMatrix idMatrix;
    protected int xlow, xhigh, ylow, yhigh;
    protected final SimilarityProvider similarityProvider;
    protected final ProgressTicket progressTicket;
    final static Logger log = Logger.getLogger(PairwiseSimMatrixBuilder.class.getName());
    protected static AtomicBoolean stopRun = new AtomicBoolean(false);

    public PairwiseSimMatrixBuilder(PairwiseSimMatrix idMatrix, Peptide[] peptides, SimilarityProvider similarityProvider, ProgressTicket progressTicket) {
        this(idMatrix, peptides, 0, idMatrix.getSize(), 0, idMatrix.getSize(), similarityProvider, progressTicket);
    }

    public PairwiseSimMatrixBuilder(PairwiseSimMatrix idMatrix, Peptide[] peptides, int xlow, int xhigh, int ylow, int yhigh, SimilarityProvider similarityProvider, ProgressTicket progressTicket) {
        this.peptides = peptides;
        this.idMatrix = idMatrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
        this.similarityProvider = similarityProvider;
        this.progressTicket = progressTicket;
    }

    public static void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    public PairwiseSimMatrix getIdentityMatrix() {
        return idMatrix;
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
                PairwiseSimMatrixBuilder up;
                PairwiseSimMatrixBuilder down;
                up = new PairwiseSimMatrixBuilder(idMatrix, peptides, xlow, xhigh, ylow, middle, similarityProvider, progressTicket);
                down = new PairwiseSimMatrixBuilder(idMatrix, peptides, xlow, xhigh, middle, yhigh, similarityProvider, progressTicket);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            PairwiseSimMatrixBuilder left;
            PairwiseSimMatrixBuilder right;
            int middle = xlow + (xhigh - xlow) / 2;
            left = new PairwiseSimMatrixBuilder(idMatrix, peptides, xlow, middle, ylow, yhigh, similarityProvider, progressTicket);
            right = new PairwiseSimMatrixBuilder(idMatrix, peptides, middle, xhigh, ylow, yhigh, similarityProvider, progressTicket);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() {
        Peptide peptide1, peptide2;
        double score;
        for (int y = ylow; y < yhigh; y++) {
            peptide1 = peptides[y];
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                peptide2 = peptides[x];
                if (!stopRun.get()) {
                    score = similarityProvider.computeSimilarity(peptide1, peptide2);
                    idMatrix.set(peptide1, peptide2, score);
                }
                progressTicket.progress();
            }
        }
    }

}
