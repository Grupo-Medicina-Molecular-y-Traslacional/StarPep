/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.concurrent.ForkJoinPool;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
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
    protected Workspace workspace;

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
        this.workspace = workspace;
        attrModel = pc.getAttributesModel(workspace);
        stopRun = false;
        if (attrModel != null) {
            // Setup Similarity Matrix Builder
            SimilarityMatrixBuilder.setStopRun(stopRun);
            SimilarityMatrixBuilder.peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            SimilarityMatrixBuilder.progressTicket = progressTicket;
            SimilarityMatrixBuilder.similarityMeasure = getSimilarityMeasure();
            SimilarityMatrixBuilder.histogram = histogram;

            // Remove all similarity edges..
            GraphModel graphModel = pc.getGraphModel(workspace);
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
        workspace = null;
        attrModel = null;
        progressTicket = null;
        SimilarityMatrixBuilder.peptides = null;
        SimilarityMatrixBuilder.similarityMeasure = null;
        SimilarityMatrixBuilder.progressTicket = null;
        SimilarityMatrixBuilder.histogram = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        SimilarityMatrixBuilder.setStopRun(stopRun);
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

            // Delete old similarity matrix
            SimilarityMatrix matrix = workspace.getLookup().lookup(SimilarityMatrix.class);
            if (matrix != null) {
                workspace.remove(matrix);
            }

            // Build new similarity matrix
            SimilarityMatrixBuilder task = new SimilarityMatrixBuilder();
            int workunits = task.getSize();
            progressTicket.switchToDeterminate(workunits);
            fjPool.invoke(task);
            task.join();

            // Add the new similarity matrix to workspace
            workspace.add(task.getSimilarityMatrix());
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
