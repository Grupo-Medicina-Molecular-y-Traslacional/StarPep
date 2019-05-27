/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class SimilarityMatrixBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private static float MIN_VALUE = 0.7f;
    private Peptide[] peptides;
    private ProgressTicket progressTicket;
    private AbstractDistance distFunc;
    private AtomicBoolean stopRun;    
    
    private final SimilarityMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    private final static Logger log = Logger.getLogger(SimilarityMatrixBuilder.class.getName());

    SimilarityMatrixBuilder(Peptide[] peptides) {
        this(peptides, new SimilarityMatrix(peptides), 0, peptides.length, 0, peptides.length);   
    }

    void setContext(AbstractDistance distFunc, ProgressTicket progressTicket, AtomicBoolean stopRun) {        
        this.distFunc = distFunc;
        this.progressTicket = progressTicket;        
        this.stopRun = stopRun;
    }

    private SimilarityMatrixBuilder(Peptide[] peptides, SimilarityMatrix matrix, int xlow, int xhigh, int ylow, int yhigh) {
        this.peptides = peptides;
        this.matrix = matrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
    }

    void setStopRun(boolean stop) {
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
                SimilarityMatrixBuilder up = new SimilarityMatrixBuilder(peptides, matrix, xlow, xhigh, ylow, middle);
                up.setContext(distFunc, progressTicket, stopRun);
                SimilarityMatrixBuilder down = new SimilarityMatrixBuilder(peptides, matrix, xlow, xhigh, middle, yhigh);
                down.setContext(distFunc, progressTicket, stopRun);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            SimilarityMatrixBuilder left = new SimilarityMatrixBuilder(peptides, matrix, xlow, middle, ylow, yhigh);
            left.setContext(distFunc, progressTicket, stopRun);
            SimilarityMatrixBuilder right = new SimilarityMatrixBuilder(peptides, matrix, middle, xhigh, ylow, yhigh);        
            right.setContext(distFunc, progressTicket, stopRun);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() {
        double distance, similarity;
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    try {
//                        similarityMeasure.setPeptidesToCompare(peptides[y], peptides[x]);
//                        similarityMeasure.run();
//                        score = similarityMeasure.getSimilarityValue();
//                        if (score >= MIN_VALUE) {
//                            matrix.setValue(peptides[y], peptides[x], score);
//                        }
                        progressTicket.progress();
                    } catch(Exception ex){
                        stopRun.set(true);
                        throw new RuntimeException(ex);
                    }                    
                }
            }
        }
    }

}
