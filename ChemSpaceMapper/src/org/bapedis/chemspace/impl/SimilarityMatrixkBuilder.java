/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.chemspace.spi.SimilarityMeasure;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class SimilarityMatrixkBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private static Peptide[] peptides;
    private static ProgressTicket progressTicket;
    private static SimilarityMeasure similarityMeasure;
    private static float MIN_VALUE = 0.5f;

    private final SimilarityMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    private final static Logger log = Logger.getLogger(SimilarityMatrixkBuilder.class.getName());
    private static AtomicBoolean stopRun = new AtomicBoolean(false);

    SimilarityMatrixkBuilder() {
        this(new SimilarityMatrix(peptides), 0, peptides.length, 0, peptides.length);
    }

    static void setContext(Peptide[] peptides, SimilarityMeasure similarityMeasure, ProgressTicket progressTicket) {
        SimilarityMatrixkBuilder.peptides = peptides;
        SimilarityMatrixkBuilder.similarityMeasure = similarityMeasure;
        SimilarityMatrixkBuilder.progressTicket = progressTicket;        
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
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    try {
                        score = similarityMeasure.computeSimilarity(peptides[y], peptides[x]);
                        if (score >= MIN_VALUE) {
                            matrix.setValue(peptides[y], peptides[x], score);
                        }
                        progressTicket.progress();
                    } catch (MolecularDescriptorNotFoundException ex) {
                        DialogDisplayer.getDefault().notify(ex.getErrorND());
                        stopRun.set(true);
                        throw new RuntimeException(ex);
                    } catch(Exception ex){
                        stopRun.set(true);
                        throw new RuntimeException(ex);
                    }                    
                }
            }
        }
    }

}
