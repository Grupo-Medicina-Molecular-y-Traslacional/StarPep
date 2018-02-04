/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class SequenceClustering implements Algorithm {

    private final ProjectManager pc;
    private final SequenceClusteringFactory factory;
    private boolean stopRun;
    private ProgressTicket ticket;
    private AttributesModel attrModel;
    private Peptide[] peptides;
    private SequenceAlignmentModel alignmentModel;    
    
    protected static final int MAX_REJECTS = 8;

    public SequenceClustering(SequenceClusteringFactory factory) {
        this.factory = factory;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }        

    @Override
    public void initAlgo(Workspace workspace) {
        attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
        }

    }

    @Override
    public void endAlgo() {
        attrModel = null;
        peptides = null;
        ticket = null;
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
        if (peptides != null && alignmentModel != null) {
            ticket.switchToDeterminate(peptides.length);

            List<Peptide> centroidList = new LinkedList<>();

            // Sort by decreasing sequence length
            Arrays.parallelSort(peptides, new SeqLengthComparator());

            //Add first centroid
            centroidList.add(peptides[0]);

            boolean isRepresentative;
            Peptide query;
            Peptide centroid;
            int rejections;
            Peptide[] centroids = new Peptide[]{peptides[0]};
            float identityScore = alignmentModel.getIndentityScore();
            for (int i = 1; i < peptides.length; i++) {
                isRepresentative = true;
                query = peptides[i];
                rejections = 0;

                // Sort by decreasing common words            
                Arrays.parallelSort(centroids, new CommonKMersComparator(query));

                // Assign query to cluster
                // Stop if max rejects ocurred
                int pos = 0;
                do {
                    centroid = centroids[pos++];
                    if (SequenceSearch.computeSequenceIdentity(query, centroid, alignmentModel) >= identityScore) {
                        centroid.addClusterMember(query);
                        isRepresentative = false;
                    } else {
                        rejections++;
                    }
                } while (isRepresentative && pos < centroids.length && rejections < MAX_REJECTS);

                if (isRepresentative) {
                    centroidList.add(query);
                    centroids = centroidList.toArray(new Peptide[0]);
                }

                ticket.progress();
            }

        }
    }

    private static class SeqLengthComparator implements Comparator<Peptide> {

        @Override
        public int compare(Peptide o1, Peptide o2) {
            return o2.getLength() - o1.getLength();
        }
    }

    private static class CommonKMersComparator implements Comparator<Peptide> {

        private final Set<String> set;
        private final int k;

        public CommonKMersComparator(Peptide query) {
            k = 6;
            set = new HashSet<>();
            String seq = query.getSequence();
            for (int i = 0; i <= seq.length() - k; i++) {
                set.add(seq.substring(i, i + k - 1));
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
                if (set.contains(seq.substring(i, i + k - 1))) {
                    count++;
                }
            }
            return count;
        }

    }
}
