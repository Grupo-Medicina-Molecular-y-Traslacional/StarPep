/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class SequenceClustering extends AbstractCluster {

    private final HashMap<Integer, Cluster> clusterMap;
    private SequenceAlignmentModel alignmentModel;

    protected static final int MAX_REJECTS = 8;

    public SequenceClustering(SequenceClusteringFactory factory) {
        super(factory);
        alignmentModel = new SequenceAlignmentModel();
        clusterMap = new HashMap<>();
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        clusterMap.clear();
    }

    @Override
    public void endAlgo() {
        super.endAlgo();
        clusterMap.clear();
    }

    @Override
    protected List<Cluster> cluterize() {
        List<Cluster> clusterList = new LinkedList<>();
        if (alignmentModel != null) {
            ticket.switchToDeterminate(peptides.length);

            // Sort by decreasing sequence length
            Arrays.parallelSort(peptides, new SeqLengthComparator());

            int id=1;
            //Add first cluster            
            Cluster cluster = new Cluster(id++, peptides.length);            
            cluster.addMember(peptides[0]);
            cluster.setCentroid(peptides[0]);
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
                    cluster = new Cluster(id++, peptides.length);
                    cluster.addMember(query);
                    cluster.setCentroid(query);
                    clusterList.add(cluster);
                    clusterMap.put(query.getId(), cluster);
                    // Increase centroids array
                    centroids = new Peptide[centroids.length + 1];
                    int pos = 0;
                    for (Cluster c : clusterList) {
                        centroids[pos++] = c.getCentroid();
                    }
                }

                ticket.progress();
            }

            pc.reportMsg("Number of clusters: " + clusterList.size(), workspace);
        }
        return clusterList;
    }

}
