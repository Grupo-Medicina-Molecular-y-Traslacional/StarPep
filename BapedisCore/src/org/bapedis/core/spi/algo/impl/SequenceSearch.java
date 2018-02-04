/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.alignment.SimpleGapPenalty;
import org.biojava.nbio.core.alignment.template.SequencePair;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class SequenceSearch implements Algorithm{

    private ProteinSequence query;
    private Peptide[] targets;    
    private ProgressTicket ticket;
    private boolean stopRun;
    private SequenceAlignmentModel alignmentModel;
    private final SequenceSearchFactory factory;

    public SequenceSearch(SequenceSearchFactory factory) {
        this.factory = factory;
    }        
    
    @Override
    public void initAlgo(Workspace workspace) {
        
    }

    @Override
    public void endAlgo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        ticket = progressTicket;
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    

    public static float computeSequenceIdentity(Peptide peptide1, Peptide peptide2, SequenceAlignmentModel model) {
        Alignments.PairwiseSequenceAlignerType alignerType = model.getAlignerType();
        SubstitutionMatrix<AminoAcidCompound> substitutionMatrix = model.getSubstitutionMatrix();        
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        float score;
        if (peptide1.getSequence().equals(peptide2.getSequence())) {
            score = 1;
        } else {
            try {
                pair = Alignments.getPairwiseAlignment(peptide1.getBiojavaSeq(), peptide2.getBiojavaSeq(),
                        alignerType, gapPenalty, substitutionMatrix);
                score = ((float) pair.getNumIdenticals()) / getDenominatorValue(pair, peptide1, peptide2, alignerType);
            } catch (CompoundNotFoundException ex) {
//                log.log(Level.SEVERE, "Compound Not Found Exception: {0}", ex.getMessage());
                Exceptions.printStackTrace(ex);
                score = -1;
            }
        }
        return score;
    }    
    
    private static int getDenominatorValue(SequencePair<ProteinSequence, AminoAcidCompound> pair, Peptide peptide1, Peptide peptide2, Alignments.PairwiseSequenceAlignerType alignerType) {
        switch (alignerType) {
            case LOCAL:
                return Math.min(peptide1.getSequence().length(), peptide2.getSequence().length());
            case GLOBAL:
                return pair.getLength();
        }
        return 0;
    }    
}
