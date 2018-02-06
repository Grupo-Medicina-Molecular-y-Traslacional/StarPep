/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.util.ArrayList;
import java.util.Arrays;
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
import org.openide.util.Exceptions;
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
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        attrModel = pc.getAttributesModel(workspace);
        ticket = progressTicket;
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
    public void run() {
        if (peptides != null && alignmentModel != null) {
            ticket.switchToDeterminate(peptides.length);

            ArrayList<Peptide> centroids = new ArrayList<>(peptides.length);

            // Sort by decreasing sequence length
            Arrays.parallelSort(peptides, new SeqLengthComparator());

            //Add first centroid
            centroids.add(peptides[0]);

            boolean isRepresentative;
            Peptide query;
            Peptide centroid;
            int rejections;
            float identityScore = alignmentModel.getIndentityScore();
            for (int i = 1; i < peptides.length; i++) {
                isRepresentative = true;
                query = peptides[i];
                rejections = 0;

                // Sort by decreasing common words 
                centroids.sort(new CommonKMersComparator(query.getSequence()));

                // Assign query to cluster
                // Stop if max rejects ocurred
                for (int j = 0; j < centroids.size() && isRepresentative && rejections < MAX_REJECTS; j++) {
                    try {
                        centroid = centroids.get(j);
                        if (PairwiseSequenceAlignment.computeSequenceIdentity(query.getBiojavaSeq(), centroid.getBiojavaSeq(), alignmentModel) >= identityScore) {
                            centroid.addClusterMember(query);
                            isRepresentative = false;
                        } else {
                            rejections++;
                        }
                    } catch (CompoundNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                if (isRepresentative) {
                    centroids.add(query);
                }

                ticket.progress();
            }

        }
    }
}
