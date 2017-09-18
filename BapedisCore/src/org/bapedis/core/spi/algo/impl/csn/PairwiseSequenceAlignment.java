/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class PairwiseSequenceAlignment implements Algorithm {

    protected final String NeedlemanWunsch = "Needleman-Wunsch";
    protected final String SmithWaterman = "Smith-Waterman";
    protected final String[] Substitution_Matrix = new String[]{
        "Blosum 30 by Henikoff & Henikoff", "Blosum 35 by Henikoff & Henikoff", "Blosum 40 by Henikoff & Henikoff",
        "Blosum 45 by Henikoff & Henikoff", "Blosum 50 by Henikoff & Henikoff", "Blosum 55 by Henikoff & Henikoff",
        "Blosum 60 by Henikoff & Henikoff", "Blosum 62 by Henikoff & Henikoff", "Blosum 65 by Henikoff & Henikoff",
        "Blosum 70 by Henikoff & Henikoff", "Blosum 75 by Henikoff & Henikoff", "Blosum 80 by Henikoff & Henikoff",
        "Blosum 85 by Henikoff & Henikoff", "Blosum 90 by Henikoff & Henikoff", "Blosum 100 by Henikoff & Henikoff",
        "PAM 250 by Gonnet, Cohen & Benner", "PAM 250 by Dayhoff"};
    protected String alignmentType, substitutionMatrix, sequenceIdentity;
    protected final AlgorithmFactory factory;

    public PairwiseSequenceAlignment(AlgorithmFactory factory) {
        this.factory = factory;
    }

    public String getAlignmentType() {
        return alignmentType;
    }

    public void setAlignmentType(String alignmentType) {
        if (!alignmentType.equals(NeedlemanWunsch) && !alignmentType.equals(SmithWaterman)) {
            throw new IllegalArgumentException("Unknown value for alignment type");
        }
        this.alignmentType = alignmentType;
    }

    public String getSubstitutionMatrix() {
        return substitutionMatrix;
    }

    public void setSubstitutionMatrix(String substitutionMatrix) {
        boolean acepted = false;
        for (String sm : Substitution_Matrix) {
            if (substitutionMatrix.equals(sm)) {
                acepted = true;
                break;
            }
        }
        if (!acepted) {
            throw new IllegalArgumentException("Unknown value for substitution matrix");
        }
        this.substitutionMatrix = substitutionMatrix;
    }

    @Override
    public void initAlgo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void endAlgo() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean cancel() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
