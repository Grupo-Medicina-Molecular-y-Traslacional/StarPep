/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class PairwiseSimMatrixBuilder extends RecursiveAction {

    static final int SEQUENTIAL_THRESHOLD = 10;
    protected ArrayList<Peptide> peptides;
    protected PairwiseSimMatrix idMatrix;
    protected int xlow, xhigh, ylow, yhigh;
    protected SubstitutionMatrix<AminoAcidCompound> substitutionMatrix;
    protected Alignments.PairwiseSequenceAlignerType alignerType;
    protected int identityType;
    protected ProgressTicket progressTicket;
    final static Logger log = Logger.getLogger(PairwiseSimMatrixBuilder.class.getName());
    protected static AtomicBoolean stopRun = new AtomicBoolean(false);

    public PairwiseSimMatrixBuilder(PairwiseSimMatrix idMatrix, ArrayList<Peptide> peptides, SubstitutionMatrix<AminoAcidCompound> substitutionMatrix, PairwiseSequenceAlignerType alignerType, int identityType, ProgressTicket progressTicket) {
        this(idMatrix, peptides, 0, idMatrix.getSize(), 0, idMatrix.getSize(), substitutionMatrix, alignerType, identityType, progressTicket);
    }

    public PairwiseSimMatrixBuilder(PairwiseSimMatrix idMatrix, ArrayList<Peptide> peptides, int xlow, int xhigh, int ylow, int yhigh, SubstitutionMatrix<AminoAcidCompound> substitutionMatrix, PairwiseSequenceAlignerType alignerType, int identityType, ProgressTicket progressTicket) {
        this.peptides = peptides;
        this.idMatrix = idMatrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
        this.substitutionMatrix = substitutionMatrix;
        this.alignerType = alignerType;
        this.identityType = identityType;
        this.progressTicket = progressTicket;
    }

    public static void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    public PairwiseSimMatrix getIdentityMatrix() {
        return idMatrix;
    }

    public PairwiseSequenceAlignerType getAlignerType() {
        return alignerType;
    }

    public int getIdentityType() {
        return identityType;
    }

    public SubstitutionMatrix<AminoAcidCompound> getSubstitutionMatrix() {
        return substitutionMatrix;
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
                up = new PairwiseSimMatrixBuilder(idMatrix, peptides, xlow, xhigh, ylow, middle, substitutionMatrix, alignerType, identityType, progressTicket);
                down = new PairwiseSimMatrixBuilder(idMatrix, peptides, xlow, xhigh, middle, yhigh, substitutionMatrix, alignerType, identityType, progressTicket);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            PairwiseSimMatrixBuilder left;
            PairwiseSimMatrixBuilder right;
            int middle = xlow + (xhigh - xlow) / 2;
            left = new PairwiseSimMatrixBuilder(idMatrix, peptides, xlow, middle, ylow, yhigh, substitutionMatrix, alignerType, identityType, progressTicket);
            right = new PairwiseSimMatrixBuilder(idMatrix, peptides, middle, xhigh, ylow, yhigh, substitutionMatrix, alignerType, identityType, progressTicket);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() {
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        Peptide peptide1, peptide2;
        double score = 0;
        for (int y = ylow; y < yhigh; y++) {
            peptide1 = peptides.get(y);
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                peptide2 = peptides.get(x);
                if (peptide1.getSequence().equals(peptide2.getSequence())) {
                    score = 1;
                    log.warning("There have been found identical sequences in the unique sequence list.");
                } else {
                    try {
                        pair = Alignments.getPairwiseAlignment(new ProteinSequence(peptide1.getSequence()), new ProteinSequence(peptide2.getSequence()),
                                alignerType, gapPenalty, substitutionMatrix);
                        if (identityType == 1) {
                            score = ((double) pair.getNumIdenticals()) / Math.min(peptide1.getSequence().length(), peptide2.getSequence().length());
                        } else if (identityType == 2) {
                            score = ((double) pair.getNumIdenticals()) / pair.getLength();
                        }
                    } catch (CompoundNotFoundException ex) {
                        log.log(Level.SEVERE, "Compound Not Found Exception: {0}", ex.getMessage());
                        Exceptions.printStackTrace(ex);
                        score = -1;
                    }
                }
                idMatrix.set(peptide1, peptide2, score);
                progressTicket.progress();
            }
        }
    }

}
