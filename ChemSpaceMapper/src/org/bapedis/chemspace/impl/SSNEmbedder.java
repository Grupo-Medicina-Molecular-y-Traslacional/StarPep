/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.chemspace.spi.impl.AlignmentBasedSimilarity;
import org.bapedis.chemspace.spi.impl.AlignmentBasedSimilarityFactory;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;

/**
 *
 * @author loge
 */
public class SSNEmbedder extends AbstractEmbedder implements NetworkEmbedder {

    private static final ForkJoinPool fjPool = new ForkJoinPool();
    private SimilarityMatrixBuilder task;
    private AtomicBoolean atomicRun;
    private final AlignmentBasedSimilarity similarityMeasure;
    private SequenceAlignmentModel alignmentModel;
    private SimilarityMatrix similarityMatrix;
    private float similarityThreshold;
    private NetworkType networkType;
    private CompressedModel compressedModel;

    public SSNEmbedder(AlgorithmFactory factory) {
        super(factory);
        alignmentModel = new SequenceAlignmentModel();
        similarityMeasure = (AlignmentBasedSimilarity) new AlignmentBasedSimilarityFactory().createAlgorithm();
        similarityThreshold = 0.7f;
        networkType = NetworkType.FULL;
        compressedModel = new CompressedModel();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); //To change body of generated methods, choose Tools | Templates.
        similarityMatrix = null;
        atomicRun = new AtomicBoolean(stopRun);
    }

    @Override
    public void run() {
        if (attrModel != null) {
            List<Peptide> peptides = attrModel.getPeptides();

            // Setup Similarity Matrix Builder
            similarityMeasure.setAlignmentModel(alignmentModel);
            task = new SimilarityMatrixBuilder(peptides.toArray(new Peptide[0]));
            task.setContext(similarityMeasure, ticket, atomicRun);
            int workunits = task.getWorkUnits();
            ticket.switchToDeterminate(workunits);

            // Compute new similarity matrix        
            fjPool.invoke(task);
            task.join();
            similarityMatrix = task.getSimilarityMatrix();

            switch (networkType) {
                case FULL:
                    createFullNetwork();
                    break;
                case COMPRESSED:
                    break;
            }
        }
    }
    
    private  void createFullNetwork(){
        Peptide[] peptides = similarityMatrix.getPeptides();
        NetworkEmbedder.clearGraph(graphModel);   
        Edge graphEdge;
        Float score;
        String id;
        for (int i = 0; i < peptides.length - 1 && !stopRun; i++) {
            for (int j = i + 1; j < peptides.length && !stopRun; j++) {
                score = similarityMatrix.getValue(peptides[i], peptides[j]);
                if (score != null && score >= similarityThreshold) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        id = String.format("%s-%s", peptides[i].getId(), peptides[j].getId());
                        graphEdge = NetworkEmbedder.createGraphEdge(graphModel, id, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
                        graph.writeLock();
                        try {
                            graph.addEdge(graphEdge);
                        } finally {
                            graph.writeUnlock();
                        }
                    }
                }
            }
        }    
    }      

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public void setAlignmentModel(SequenceAlignmentModel alignmentModel) {
        this.alignmentModel = alignmentModel;
    }

    @Override
    public float getSimilarityThreshold() {
        return similarityThreshold;
    }

    @Override
    public void setSimilarityThreshold(float similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    @Override
    public void setNetworkType(NetworkType networkType) {
        this.networkType = networkType;
    }

    @Override
    public NetworkType getNetworkType() {
        return networkType;
    }

    @Override
    public CompressedModel getCompressedModel() {
        return compressedModel;
    }

    @Override
    public void setCompressedModel(CompressedModel compressedModel) {
        this.compressedModel = compressedModel;
    }

    @Override
    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

}
