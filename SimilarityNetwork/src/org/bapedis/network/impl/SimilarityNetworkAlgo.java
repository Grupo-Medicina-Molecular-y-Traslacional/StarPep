/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.concurrent.ForkJoinPool;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphViz;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class SimilarityNetworkAlgo implements Algorithm, SimilarityMeasure {
    
    public static final String CHANGED_SIMILARITY_VALUES = "similarity_values";
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final JQuickHistogram histogram;
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected AttributesModel attrModel;
    protected ProgressTicket progressTicket;
    protected boolean stopRun;
    protected final AlgorithmFactory factory;
    protected final PropertyChangeSupport propertyChangeSupport;

    public SimilarityNetworkAlgo(AlgorithmFactory factory) {
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
        histogram = new JQuickHistogram();
    }

    public SimilarityMeasure getSimilarityMeasure() {
        return this;
    }

    public JQuickHistogram getHistogram() {
        return histogram;
    }

    @Override
    public void initAlgo(Workspace workspace) {
        histogram.clear();
        attrModel = pc.getAttributesModel(workspace);
        stopRun = false;
        if (attrModel != null) {
            GraphModel graphModel = pc.getGraphModel(workspace);
            GraphViz graphViz = pc.getGraphViz(workspace);
            SimilarityGraphEdgeBuilder.attrModel = attrModel;
            SimilarityGraphEdgeBuilder.peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            SimilarityGraphEdgeBuilder.graphModel = graphModel;
            SimilarityGraphEdgeBuilder.setStopRun(stopRun);
            SimilarityGraphEdgeBuilder.progressTicket = progressTicket;
            SimilarityGraphEdgeBuilder.mainGraph = graphModel.getGraph();
            SimilarityGraphEdgeBuilder.csnGraph = graphModel.getGraphVisible();
            SimilarityGraphEdgeBuilder.threshold = graphViz.getSimilarityThreshold();
            SimilarityGraphEdgeBuilder.similarityMeasure = getSimilarityMeasure();
            SimilarityGraphEdgeBuilder.histogram = histogram;                    

            // Remove all similarity edges..
            int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
            removeAllSimilarityEdges(graphModel.getGraph(), relType);
            if (!graphModel.getVisibleView().isMainView()) {
                removeAllSimilarityEdges(graphModel.getGraphVisible(), relType);
            }
        }
    }

    private void removeAllSimilarityEdges(Graph graph, int relType) {
        graph.writeLock();
        try {
            for (Node node : graph.getNodes()) {
                graph.clearEdges(node, relType);
            }
        } finally {
            graph.writeUnlock();
        }
    }

    @Override
    public void endAlgo() {
        attrModel = null;
        progressTicket = null;
        SimilarityGraphEdgeBuilder.attrModel = null;
        SimilarityGraphEdgeBuilder.peptides = null;
        SimilarityGraphEdgeBuilder.graphModel = null;
        SimilarityGraphEdgeBuilder.mainGraph = null;
        SimilarityGraphEdgeBuilder.csnGraph = null;
        SimilarityGraphEdgeBuilder.threshold = null;
        SimilarityGraphEdgeBuilder.similarityMeasure = null;
        SimilarityGraphEdgeBuilder.progressTicket = null;
        SimilarityGraphEdgeBuilder.progressTicket = null;
        SimilarityGraphEdgeBuilder.histogram = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        SimilarityGraphEdgeBuilder.setStopRun(stopRun);
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
        if (attrModel != null) {
            propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY_VALUES, null, null);
            Peptide[] peptides = SimilarityGraphEdgeBuilder.peptides;
            // Workunits for pairwise sim matrix builder
            int workunits = peptides.length * (peptides.length - 1) / 2;
            progressTicket.switchToDeterminate(workunits);
            SimilarityGraphEdgeBuilder task = new SimilarityGraphEdgeBuilder();
            fjPool.invoke(task);
            task.join();
            
            propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY_VALUES, null, histogram);
        }
    }

    public void addSimilarityChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeSimilarityChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
