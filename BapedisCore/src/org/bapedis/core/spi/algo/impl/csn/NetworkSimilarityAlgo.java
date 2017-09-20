/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
public abstract class NetworkSimilarityAlgo implements Algorithm {

    public static final String GRAPH_EDGE_LABEL = "pairwise_similarity";
    private final float GRAPH_EDGE_WEIGHT = 1f;
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final ProjectManager pc;
    protected final AlgorithmFactory factory;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected ProgressTicket progressTicket;
    protected final PropertyChangeSupport propertyChangeSupport;
    public static final String CHANGED_SIMILARITY = "similarity";
    private boolean stopRun;

    public NetworkSimilarityAlgo(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public void initAlgo() {
        attrModel = pc.getAttributesModel();
        graphModel = pc.getGraphModel();
        stopRun = false;
        PairwiseSimMatrixBuilder.setStopRun(stopRun);
    }

    @Override
    public void endAlgo() {
        attrModel = null;
        graphModel = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        PairwiseSimMatrixBuilder.setStopRun(stopRun);
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
        GraphFactory graphFactory = graphModel.factory();
        Edge graphEdge;
        String id;
        Graph mainGraph = graphModel.getGraph();
        Graph csnGraph = graphModel.getGraph(attrModel.getCsnView());
        mainGraph.writeLock();
        try {
            int relType = graphModel.addEdgeType(GRAPH_EDGE_LABEL);
            for (int i = 0; i < peptides.length - 1 && !stopRun; i++) {
                peptide1 = peptides[i];
                for (int j = i + 1; j < peptides.length && !stopRun; j++) {
                    peptide2 = peptides[j];
                    score = idMatrix.get(peptide1, peptide2);
                    id = String.format("%s-%s", peptide1.getGraphNode().getId(), peptide2.getGraphNode().getId());
                    graphEdge = mainGraph.getEdge(id);
                    if (graphEdge == null) {                        
                        graphEdge = graphFactory.newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, GRAPH_EDGE_WEIGHT, false);
                        graphEdge.setLabel(GRAPH_EDGE_LABEL);
                        mainGraph.addEdge(graphEdge);
                    }
                    graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);
                    
                    if (!csnGraph.hasEdge(id) && score >= 0.7) {
                        csnGraph.addEdge(graphEdge);
                    }
                }
            }            
        } finally {
            mainGraph.writeUnlock();
        }
        propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY, null, idMatrix.getValues());
    }

    protected abstract SimilarityProvider getSimilarityProvider();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
