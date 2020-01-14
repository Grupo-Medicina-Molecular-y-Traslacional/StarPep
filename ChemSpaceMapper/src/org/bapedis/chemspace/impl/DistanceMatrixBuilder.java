/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.model.DistanceMatrix;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class DistanceMatrixBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private Peptide[] peptides;
    private ProgressTicket progressTicket;
    protected AlgorithmFactory distFactory;
    protected double[][] descriptorMatrix;
    private AtomicBoolean stopRun;    
    
    private final DistanceMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    private final static Logger log = Logger.getLogger(DistanceMatrixBuilder.class.getName());

    DistanceMatrixBuilder(Peptide[] peptides, AlgorithmFactory distFactory, double[][] descriptorMatrix, ProgressTicket progressTicket, AtomicBoolean stopRun) {
        this(peptides, new DistanceMatrix(peptides), 0, peptides.length, 0, peptides.length); 
        this.distFactory = distFactory;
        this.descriptorMatrix = descriptorMatrix;
        this.progressTicket = progressTicket;        
        this.stopRun = stopRun;        
    }
    
    private DistanceMatrixBuilder(Peptide[] peptides, DistanceMatrix matrix, int xlow, int xhigh, int ylow, int yhigh, DistanceMatrixBuilder parent){
        this(peptides, matrix, xlow, xhigh, ylow, yhigh);
        this.distFactory = parent.distFactory;
        this.descriptorMatrix = parent.descriptorMatrix;
        this.progressTicket = parent.progressTicket;        
        this.stopRun = parent.stopRun;                
    }

    private DistanceMatrixBuilder(Peptide[] peptides, DistanceMatrix matrix, int xlow, int xhigh, int ylow, int yhigh) {
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

    public DistanceMatrix getDistanceMatrix() {
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
                DistanceMatrixBuilder up = new DistanceMatrixBuilder(peptides, matrix, xlow, xhigh, ylow, middle, this);
                DistanceMatrixBuilder down = new DistanceMatrixBuilder(peptides, matrix, xlow, xhigh, middle, yhigh, this);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            DistanceMatrixBuilder left = new DistanceMatrixBuilder(peptides, matrix, xlow, middle, ylow, yhigh, this);
            DistanceMatrixBuilder right = new DistanceMatrixBuilder(peptides, matrix, middle, xhigh, ylow, yhigh, this);        
            invokeAll(left, right);
        }
    }      

    private void computeDirectly() {
        AbstractDistance distAlg = (AbstractDistance)distFactory.createAlgorithm();        
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    try {
                        distAlg.setContext(peptides[y], peptides[x], descriptorMatrix);
                        distAlg.run();
                        matrix.setValue(peptides[y], peptides[x], distAlg.getDistance());
                        progressTicket.progress();
                    } catch(Exception ex){
                        stopRun.set(true);
                        throw new RuntimeException(ex);
                    }                    
                }
            }
        }
        distAlg.endAlgo();
    }

}
