/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.network.model.Cluster;
import org.bapedis.network.model.SeqClusteringModel;
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
public class SeqClusterBuilder {

    protected final Alignments.PairwiseSequenceAlignerType alignerType;
    protected final SubstitutionMatrix<AminoAcidCompound> substitutionMatrix;
    protected final float identityScore;

    protected static final int MAX_REJECTS = 32;

    public SeqClusterBuilder(SeqClusteringModel model) {
        alignerType = model.getAlignerType();
        substitutionMatrix = model.getSubstitutionMatrix();
        identityScore = model.getIndentityScore();
    }

    public List<Cluster> clusterize(Peptide[] peptides) {
        List<Cluster> clusters = new LinkedList<>();

        // Sort by decreasing sequence length
        Arrays.sort(peptides, new SeqLengthComparator());

        // Create clusters
        clusters.add(new Cluster(peptides[0]));

        boolean isRepresentative;
        Peptide query;
        Peptide centroid;
        int rejections;
        Iterator<Cluster> iterator;
        Cluster c;
        for (int i = 1; i < peptides.length; i++) {
            isRepresentative = true;
            query = peptides[i];
            rejections = 0;
            // Sort by decreasing common words
            Collections.sort(clusters, new ClusterComparator(query));

            // Assign query to cluster
            // Stop if max rejects ocurred
            iterator = clusters.iterator();
            do {
                c = iterator.next();
                centroid = c.getCentroid();
                if (computeSequenceIdentity(query, centroid) >= identityScore) {
                    c.addMember(query);
                    isRepresentative = false;
                } else {
                    rejections++;
                }
            } while (isRepresentative && iterator.hasNext() && rejections < MAX_REJECTS);
            
            if (isRepresentative){
                clusters.add(new Cluster(query));
            }
        }

        return clusters;
    }

    private float computeSequenceIdentity(Peptide peptide1, Peptide peptide2) {
        SimpleGapPenalty gapPenalty = new SimpleGapPenalty();
        SequencePair<ProteinSequence, AminoAcidCompound> pair;
        float score;
        if (peptide1.getSequence().equals(peptide2.getSequence())) {
            score = 1;
        } else {
            try {
                pair = Alignments.getPairwiseAlignment(peptide1.getBiojavaSeq(), peptide2.getBiojavaSeq(),
                        alignerType, gapPenalty, substitutionMatrix);
                score = ((float) pair.getNumIdenticals()) / getDenominatorValue(pair, peptide1, peptide2);
            } catch (CompoundNotFoundException ex) {
//                log.log(Level.SEVERE, "Compound Not Found Exception: {0}", ex.getMessage());
                Exceptions.printStackTrace(ex);
                score = -1;
            }
        }
        return score;
    }

    private int getDenominatorValue(SequencePair<ProteinSequence, AminoAcidCompound> pair, Peptide peptide1, Peptide peptide2) {
        switch (alignerType) {
            case LOCAL:
                return Math.min(peptide1.getSequence().length(), peptide2.getSequence().length());
            case GLOBAL:
                return pair.getLength();
        }
        return 0;
    }

}

class SeqLengthComparator implements Comparator<Peptide> {

    @Override
    public int compare(Peptide o1, Peptide o2) {
        return o2.getLength() - o1.getLength();
    }
}

class ClusterComparator implements Comparator<Cluster> {

    private final Set<String> set;
    private final int k;

    public ClusterComparator(Peptide query) {
        k = 3;
        set = new HashSet<>();
        String seq = query.getSequence();
        for (int i = 0; i <= seq.length() - k; i++) {
            set.add(seq.substring(i, i + k - 1));
        }
    }

    @Override
    public int compare(Cluster o1, Cluster o2) {
        int c1 = countCommonWords(o1.getCentroid());
        int c2 = countCommonWords(o2.getCentroid());
        return c2 - c1;
    }

    private int countCommonWords(Peptide peptide) {
        String seq = peptide.getSequence();
        int count = 0;
        for (int i = 0; i <= seq.length() - k; i++) {
            if (set.contains(seq.substring(i, i + k - 1))) {
                count++;
            }
        }
        return count;
    }

}
