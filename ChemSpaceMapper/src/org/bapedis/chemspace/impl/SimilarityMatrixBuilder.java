/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.bapedis.chemspace.spi.SimilarityCoefficient;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class SimilarityMatrixBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private static float MIN_VALUE = 0.7f;
    private Peptide[] peptides;
    private ProgressTicket progressTicket;
    private SimilarityCoefficient similarityMeasure;
    private AtomicBoolean stopRun;    
    
    private final SimilarityMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    private final static Logger log = Logger.getLogger(SimilarityMatrixBuilder.class.getName());

    SimilarityMatrixBuilder(Peptide[] peptides) {
        this(peptides, new SimilarityMatrix(peptides), 0, peptides.length, 0, peptides.length);   
    }

    void setContext(SimilarityCoefficient similarityMeasure, ProgressTicket progressTicket, AtomicBoolean stopRun) {        
        this.similarityMeasure = similarityMeasure;
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
                up.setContext(similarityMeasure, progressTicket, stopRun);
                SimilarityMatrixBuilder down = new SimilarityMatrixBuilder(peptides, matrix, xlow, xhigh, middle, yhigh);
                down.setContext(similarityMeasure, progressTicket, stopRun);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            SimilarityMatrixBuilder left = new SimilarityMatrixBuilder(peptides, matrix, xlow, middle, ylow, yhigh);
            left.setContext(similarityMeasure, progressTicket, stopRun);
            SimilarityMatrixBuilder right = new SimilarityMatrixBuilder(peptides, matrix, middle, xhigh, ylow, yhigh);        
            right.setContext(similarityMeasure, progressTicket, stopRun);
            invokeAll(left, right);
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
