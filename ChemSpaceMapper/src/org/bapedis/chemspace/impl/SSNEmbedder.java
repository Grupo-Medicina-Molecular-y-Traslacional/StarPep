/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.List;
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

/**
 *
 * @author loge
 */
public class SSNEmbedder extends AbstractEmbedder implements NetworkEmbedder {

    private AtomicBoolean atomicRun;
    private final AlignmentBasedSimilarity similarityMeasure;
    private SequenceAlignmentModel alignmentModel;
    private SimilarityMatrix similarityMatrix;
    private float similarityThreshold;
    private NetworkType networkType;

    public SSNEmbedder(AlgorithmFactory factory) {
        super(factory);
        alignmentModel = new SequenceAlignmentModel();
        similarityMeasure = (AlignmentBasedSimilarity) new AlignmentBasedSimilarityFactory().createAlgorithm();
        similarityThreshold = 0.7f;
        networkType = NetworkType.FULL;
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
                        
            SimilarityMatrixBuilder task = new SimilarityMatrixBuilder(peptides.toArray(new Peptide[0]));
            task.setContext(similarityMeasure, ticket, atomicRun);
            int workunits = task.getWorkUnits();
            ticket.switchToDeterminate(workunits);

            // Compute new similarity matrix        
            fjPool.invoke(task);
            task.join();
            similarityMatrix = task.getSimilarityMatrix();

            updateNetwork(graphModel, ticket, atomicRun);
        }
    } 

    @Override
    public boolean cancel() {
        super.cancel();
        atomicRun.set(stopRun);
        return stopRun;        
    }        

    @Override
    public void endAlgo() {
        super.endAlgo(); //To change body of generated methods, choose Tools | Templates.

        if (stopRun){ // Cancelled
            similarityMatrix = null;
        }
        atomicRun = null;        
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
    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

}
