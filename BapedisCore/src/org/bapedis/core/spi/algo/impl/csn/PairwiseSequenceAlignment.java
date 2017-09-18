/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class PairwiseSequenceAlignment implements Algorithm {

    public static final ForkJoinPool fjPool = new ForkJoinPool();
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
    protected final ProjectManager pc;
    protected AttributesModel attrModel;
    protected ProgressTicket progressTicket;
    protected List<ProteinSequence> dataSet;

    public PairwiseSequenceAlignment(AlgorithmFactory factory) {
        this.factory = factory;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
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

    private SubstitutionMatrix getSubMatrix() {
        switch (substitutionMatrix) {
            case "Blosum 30 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum30();
            case "Blosum 35 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum35();
            case "Blosum 40 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum40();
            case "Blosum 45 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum45();
            case "Blosum 50 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum50();
            case "Blosum 55 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum55();
            case "Blosum 60 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum60();
            case "Blosum 62 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum62();
            case "Blosum 65 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum65();
            case "Blosum 70 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum70();
            case "Blosum 75 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum75();
            case "Blosum 80 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum80();
            case "Blosum 85 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum85();
            case "Blosum 90 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum90();
            case "Blosum 100 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum100();
            case "PAM 250 by Gonnet, Cohen & Benner":
                return SubstitutionMatrixHelper.getGonnet250();
            case "PAM 250 by Dayhoff":
                return SubstitutionMatrixHelper.getPAM250();
        }
        return null;
    }

    private Alignments.PairwiseSequenceAlignerType getAlignerType() {
        if (alignmentType.equals("Needleman-Wunsch")) {
            return Alignments.PairwiseSequenceAlignerType.GLOBAL;
        } else if (alignmentType.equals("Smith-Waterman")) {
            return Alignments.PairwiseSequenceAlignerType.LOCAL;
        }
        return null;
    }

    @Override
    public void initAlgo() {
        attrModel = pc.getAttributesModel();
        PairwiseSimMatrixBuilder.setStopRun(false);
    }

    @Override
    public void endAlgo() {
        attrModel = null;
    }

    @Override
    public boolean cancel() {
        PairwiseSimMatrixBuilder.setStopRun(true);
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
        this.progressTicket = progressTicket;
    }

    @Override
    public void run() {
        Peptide[] peptides = attrModel.getPeptides();
        int size = peptides.length + ((dataSet != null) ? dataSet.size() : 0);
        ArrayList<Peptide> peptideList = new ArrayList<>(size);
        for (Peptide pept : peptides) {
            peptideList.add(pept);
        }
        // Workunits for pairwise sim matrix builder
        size = peptideList.size() * (peptideList.size() - 1) / 2;
        progressTicket.switchToDeterminate(size);
        PairwiseSimMatrix idMatrix = new PairwiseSimMatrix(peptideList);

    }

}
