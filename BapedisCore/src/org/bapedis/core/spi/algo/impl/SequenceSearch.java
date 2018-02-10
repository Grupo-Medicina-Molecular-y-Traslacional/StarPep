/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class SequenceSearch implements Algorithm {

    private final ProjectManager pc;
    private ProteinSequence query;
    private Peptide[] targets;
    private final List<Peptide> resultList;
    private boolean stopRun;
    private SequenceAlignmentModel alignmentModel;
    private final SequenceSearchFactory factory;
    protected static final int MAX_REJECTS = 16;
    protected int maximumResults;

    public SequenceSearch(SequenceSearchFactory factory) {
        this.factory = factory;
        alignmentModel = new SequenceAlignmentModel();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        resultList = new LinkedList<>();
        maximumResults = -1;
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }

    public int getMaximumResults() {
        return maximumResults;
    }

    public void setMaximumResults(int maximumResults) {
        this.maximumResults = maximumResults;
    }

    public ProteinSequence getQuery() {
        return query;
    }

    public void setQuery(ProteinSequence query) {
        this.query = query;
    }

    public Peptide[] getTargets() {
        return targets;
    }

    public void setTargets(Peptide[] targets) {
        this.targets = targets;
    }

    public List<Peptide> getResultList() {
        return resultList;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        if (targets == null) {
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            if (attrModel != null) {
                targets = attrModel.getPeptides().toArray(new Peptide[0]);
            }
        }
        stopRun = false;
        resultList.clear();
    }

    @Override
    public void endAlgo() {
        targets = null;
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
    public void run() {
        if (targets != null && query != null) {
            // Sort by decreasing common words
            Arrays.parallelSort(targets, new CommonKMersComparator(query.getSequenceAsString()));

            // Assign peptide from targets to result list
            // Stop if max rejects ocurred
            float identityScore = alignmentModel.getIndentityScore();
            float score;
            int rejections = 0;
            TreeSet<SequenceHit> hits = new TreeSet<>();
            for (int i = 0; i < targets.length && !stopRun && rejections < MAX_REJECTS; i++) {
                try {
                    score = PairwiseSequenceAlignment.computeSequenceIdentity(query, targets[i].getBiojavaSeq(), alignmentModel);
                    if (score >= identityScore) {
                        hits.add(new SequenceHit(targets[i], score));
                    } else {
                        rejections++;
                    }
                } catch (CompoundNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

            SequenceHit hit;
            for (Iterator<SequenceHit> it = hits.descendingIterator(); it.hasNext()
                    && (maximumResults == -1 || resultList.size() < maximumResults);) {
                hit = it.next();
                resultList.add(hit.getPeptide());
            }

        }
    }

    private static class SequenceHit implements Comparable<SequenceHit> {

        private final Peptide peptide;
        private final float score;

        public SequenceHit(Peptide peptide, float score) {
            this.peptide = peptide;
            this.score = score;
        }

        public Peptide getPeptide() {
            return peptide;
        }

        public float getScore() {
            return score;
        }

        @Override
        public int compareTo(SequenceHit other) {
            float diff = getScore() - other.getScore();
            if (diff > 0) {
                return 1;
            }
            return -1;
        }
    }

}

class SeqLengthComparator implements Comparator<Peptide> {

    @Override
    public int compare(Peptide o1, Peptide o2) {
        return o2.getLength() - o1.getLength();
    }
}

class CommonKMersComparator implements Comparator<Peptide> {

    private final Set<String> set;
    private final int k;

    public CommonKMersComparator(String query) {
        k = Math.min(5, query.length());
        set = new HashSet<>();
        for (int i = 0; i <= query.length() - k; i++) {
            set.add(query.substring(i, i + k));
        }
    }

    @Override
    public int compare(Peptide o1, Peptide o2) {
        int c1 = countCommonWords(o1);
        int c2 = countCommonWords(o2);
        return c2 - c1;
    }

    private int countCommonWords(Peptide peptide) {
        String seq = peptide.getSequence();
        int count = 0;
        for (int i = 0; i <= seq.length() - k; i++) {
            if (set.contains(seq.substring(i, i + k))) {
                count++;
            }
        }
        return count;
    }

}
