/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.logging.Logger;
import org.bapedis.core.model.Peptide;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.Alignments.PairwiseSequenceAlignerType;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
public class PairwiseIdentityMatrixBuilder extends RecursiveAction {

    static final int SEQUENTIAL_THRESHOLD = 10;
    protected ArrayList<Peptide> peptides;
    protected PairwiseIdentityMatrix idMatrix;
    protected int xlow, xhigh, ylow, yhigh;
    protected SubstitutionMatrix<AminoAcidCompound> substitutionMatrix;
    protected Alignments.PairwiseSequenceAlignerType alignerType;
    protected int identityType;
    protected PropertyChangeSupport monitorSupport;
    final static Logger log = Logger.getLogger(PairwiseIdentityMatrixBuilder.class.getName());

    public PairwiseIdentityMatrixBuilder(PairwiseIdentityMatrix idMatrix, ArrayList<Peptide> peptides, SubstitutionMatrix<AminoAcidCompound> substitutionMatrix, PairwiseSequenceAlignerType alignerType, int identityType, PropertyChangeSupport monitorSupport) {
        this(idMatrix, peptides, 0, idMatrix.getSize(), 0, idMatrix.getSize(), substitutionMatrix, alignerType, identityType, monitorSupport);
    }

    public PairwiseIdentityMatrixBuilder(PairwiseIdentityMatrix idMatrix, ArrayList<Peptide> peptides, int xlow, int xhigh, int ylow, int yhigh, SubstitutionMatrix<AminoAcidCompound> substitutionMatrix, PairwiseSequenceAlignerType alignerType, int identityType, PropertyChangeSupport monitorSupport) {
        this.peptides = peptides;
        this.idMatrix = idMatrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
        this.substitutionMatrix = substitutionMatrix;
        this.alignerType = alignerType;
        this.identityType = identityType;
        this.monitorSupport = monitorSupport;
    }

    public PairwiseIdentityMatrix getIdentityMatrix() {
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

    public void addPropertyChangeSupport(PropertyChangeSupport monitorSupport) {
        this.monitorSupport = monitorSupport;
    }

    @Override
    protected void compute() {
        if (xlow >= yhigh) {
            return; // Discard the elements above the diagonal
        }
        if (xhigh - xlow <= SEQUENTIAL_THRESHOLD) {
            if (yhigh - ylow <= SEQUENTIAL_THRESHOLD) {
                computeDirectly();
            } else {
                int middle = ylow + (yhigh - ylow) / 2;
                PairwiseIdentityMatrixBuilder up;
                PairwiseIdentityMatrixBuilder down;
                up = new PairwiseIdentityMatrixBuilder(idMatrix, peptides, xlow, xhigh, ylow, middle, substitutionMatrix, alignerType, identityType, monitorSupport);
                down = new PairwiseIdentityMatrixBuilder(idMatrix, peptides, xlow, xhigh, middle, yhigh, substitutionMatrix, alignerType, identityType, monitorSupport);
                invokeAll(up, down);
            }
        } else {
            PairwiseIdentityMatrixBuilder left;
            PairwiseIdentityMatrixBuilder right;
            int middle = xlow + (xhigh - xlow) / 2;
            left = new PairwiseIdentityMatrixBuilder(idMatrix, peptides, xlow, middle, ylow, yhigh, substitutionMatrix, alignerType, identityType, monitorSupport);
            right = new PairwiseIdentityMatrixBuilder(idMatrix, peptides, middle, xhigh, ylow, yhigh, substitutionMatrix, alignerType, identityType, monitorSupport);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() {
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        Peptide peptide1, peptide2;
        double score = 0;
        for (int y = ylow; y < yhigh; y++) {
//            seq1 = uniqueSeqs.get(y);
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
//                seq2 = uniqueSeqs.get(x);
//                if (seq1.getSequence().equals(seq2.getSequence())) {
//                    score = 1;
//                    log.warning("There have been found identical sequences in the unique sequence list.");
//                } else {
//                    pair = Alignments.getPairwiseAlignment(seq1.getProteinSequence(), seq2.getProteinSequence(),
//                            alignerType, gapPenalty, substitutionMatrix);
//                    if (identityType == 1) {
//                        score = ((double) pair.getNumIdenticals()) / Math.min(seq1.getSequence().length(), seq2.getSequence().length());
//                    } else if (identityType == 2) {
//                        score = ((double) pair.getNumIdenticals()) / pair.getLength();
//                    }
//                }
//                idMatrix.set(seq1.getSequence(), seq2.getSequence(), score);
//                if (monitorSupport != null)
//                    monitorSupport.firePropertyChange("progress", 0, 1);
            }
        }
    }

}
