/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.network.spi.SimilarityMeasure;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.network.model.SimilarityMatrix;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class SimilarityMatrixkBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private static Peptide[] peptides;
    private static Cluster[] cluster;
    private static ProgressTicket progressTicket;
    private static SimilarityMeasure similarityMeasure;
    private static float MIN_VALUE = CSNAlgorithm.SIMILARITY_CUTOFF_MIN / 100.f;

    private final SimilarityMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    private final static Logger log = Logger.getLogger(SimilarityMatrixkBuilder.class.getName());
    private static AtomicBoolean stopRun = new AtomicBoolean(false);

    SimilarityMatrixkBuilder() {
        this(new SimilarityMatrix(peptides), 0, peptides.length, 0, peptides.length);
    }

    static void setContext(Cluster[] cluster, Peptide[] peptides, ProgressTicket progressTicket, SimilarityMeasure similarityMeasure) {
        SimilarityMatrixkBuilder.cluster = cluster;
        SimilarityMatrixkBuilder.peptides = peptides;
        SimilarityMatrixkBuilder.progressTicket = progressTicket;
        SimilarityMatrixkBuilder.similarityMeasure = similarityMeasure;
    }

    private SimilarityMatrixkBuilder(SimilarityMatrix matrix, int xlow, int xhigh, int ylow, int yhigh) {
        this.matrix = matrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
    }

    static void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    public SimilarityMatrix getSimilarityMatrix() {
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
                invokeAll(new SimilarityMatrixkBuilder(matrix, xlow, xhigh, ylow, middle),
                        new SimilarityMatrixkBuilder(matrix, xlow, xhigh, middle, yhigh));
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            invokeAll(new SimilarityMatrixkBuilder(matrix, xlow, middle, ylow, yhigh),
                    new SimilarityMatrixkBuilder(matrix, middle, xhigh, ylow, yhigh));
        }
    }

    private void computeDirectly() {
        float score;
        Cluster clusterX, clusterY;
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    try {
                        score = similarityMeasure.computeSimilarity(peptides[y], peptides[x]);

                        if (cluster != null) {
                            clusterY = cluster[y];
                            clusterX = cluster[x];
                            int count = 1;
                            for (Peptide p1 : clusterY.getMembers()) {
                                for (Peptide p2 : clusterX.getMembers()) {
                                    score += similarityMeasure.computeSimilarity(p1, p2);
                                    count++;
                                }
                            }
                            score = score / count;
                        }
                        
                    } catch (MolecularDescriptorNotFoundException ex) {
                        DialogDisplayer.getDefault().notify(ex.getErrorND());
                        stopRun.set(true);
                        score = -1;
                    }
                    if (score >= MIN_VALUE) {
                        matrix.setValue(peptides[y], peptides[x], score);
                    }
                    progressTicket.progress();
                }
            }
        }
    }

}
