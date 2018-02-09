/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
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
    private Peptide[] peptides;
    private final List<Cluster> clusterList;
    private final HashMap<String, Cluster> clusterMap;
    private SequenceAlignmentModel alignmentModel;

    protected static final int MAX_REJECTS = 8;

    public SequenceClustering(SequenceClusteringFactory factory) {
        this.factory = factory;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        alignmentModel = new SequenceAlignmentModel();
        clusterList = new LinkedList<>();
        clusterMap = new HashMap<>();
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }

    public Peptide[] getPeptides() {
        return peptides;
    }

    public void setPeptides(Peptide[] peptides) {
        this.peptides = peptides;
    }

    public List<Cluster> getClusterList() {
        return clusterList;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        if (peptides == null) {
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            if (attrModel != null) {
                peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            }
        }
        stopRun = false;
        ticket = progressTicket;
        clusterList.clear();
        clusterMap.clear();
    }

    @Override
    public void endAlgo() {
        peptides = null;
        ticket = null;
        clusterMap.clear();
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

            // Sort by decreasing sequence length
            Arrays.parallelSort(peptides, new SeqLengthComparator());

            //Add first cluster
            Cluster cluster = new Cluster(peptides[0]);
            clusterList.add(cluster);
            clusterMap.put(peptides[0].getId(), cluster);
            Peptide[] centroids = new Peptide[]{peptides[0]};

            boolean isRepresentative;
            Peptide query;
            Peptide centroid;
            int rejections;
            float identityScore = alignmentModel.getIndentityScore();
            for (int i = 1; i < peptides.length && !stopRun; i++) {
                isRepresentative = true;
                query = peptides[i];
                rejections = 0;

                // Sort by decreasing common words 
                Arrays.parallelSort(centroids, new CommonKMersComparator(query.getSequence()));

                // Assign query to cluster
                // Stop if max rejects ocurred
                for (int j = 0; j < centroids.length && isRepresentative && rejections < MAX_REJECTS; j++) {
                    try {
                        centroid = centroids[j];
                        if (PairwiseSequenceAlignment.computeSequenceIdentity(query.getBiojavaSeq(), centroid.getBiojavaSeq(), alignmentModel) >= identityScore) {
                            cluster = clusterMap.get(centroid.getId());
                            cluster.addMember(query);
                            isRepresentative = false;
                        } else {
                            rejections++;
                        }
                    } catch (CompoundNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                if (isRepresentative) {
                    cluster = new Cluster(query);
                    clusterList.add(cluster);
                    clusterMap.put(query.getId(), cluster);
                    // Increase centroids array
                    centroids = new Peptide[centroids.length + 1];
                    int pos=0;
                    for(Cluster c: clusterList){
                        centroids[pos++] = c.getCentroid();
                    }
                }

                ticket.progress();
            }

        }
    }
}
