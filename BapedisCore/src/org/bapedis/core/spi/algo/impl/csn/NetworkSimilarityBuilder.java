/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.util.concurrent.ForkJoinPool;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class NetworkSimilarityBuilder implements Algorithm {

    public static final String GRAPH_EDGE_TYPE="similarity";
    private final float GRAPH_EDGE_WEIGHT = 1f;
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final ProjectManager pc;
    protected final AlgorithmFactory factory;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected ProgressTicket progressTicket;

    public NetworkSimilarityBuilder(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
    }

    @Override
    public void initAlgo() {
        attrModel = pc.getAttributesModel();
        graphModel = pc.getGraphModel();
        PairwiseSimMatrixBuilder.setStopRun(false);
    }

    @Override
    public void endAlgo() {
        attrModel = null;
        graphModel = null;
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
        // Workunits for pairwise sim matrix builder
        int workunits = peptides.length * (peptides.length - 1) / 2;
        progressTicket.switchToDeterminate(workunits);
        PairwiseSimMatrix idMatrix = new PairwiseSimMatrix(peptides);
        PairwiseSimMatrixBuilder task = new PairwiseSimMatrixBuilder(idMatrix, peptides, getSimilarityProvider(), progressTicket);
        fjPool.invoke(task);
        task.join();
        double score;
        Peptide peptide1, peptide2;
        Graph mainGraph = graphModel.getGraph();
        GraphFactory factory = graphModel.factory();
        Edge graphEdge;
        int relType = graphModel.addEdgeType(GRAPH_EDGE_TYPE);
        for (int i = 0; i < peptides.length - 1; i++) {
            peptide1 = peptides[i];
            for (int j = i + 1; j < peptides.length; j++) {
                peptide2 = peptides[j];
                score = idMatrix.get(peptide1, peptide2);
                System.out.println(score);                        
//                graphEdge = factory.newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, GRAPH_EDGE_WEIGHT, false);
            }
        }
    }

    protected abstract SimilarityProvider getSimilarityProvider();

}
