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
import org.bapedis.core.model.GraphViz;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SimilarityMatrix;
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
public abstract class SimilarityNetworkBaseAlgo implements Algorithm, SimilarityMeasure {

    public static final String CHANGED_SIMILARITY_VALUES = "similarity_values";
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final JQuickHistogram histogram;
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected AttributesModel attrModel;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket progressTicket;
    protected boolean stopRun;
    protected final AlgorithmFactory factory;
    protected final PropertyChangeSupport propertyChangeSupport;
    protected Workspace workspace;

    public SimilarityNetworkBaseAlgo(AlgorithmFactory factory) {
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
        stopRun = false;
        this.workspace = workspace;
        attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            graphModel = pc.getGraphModel(workspace);
            graph = graphModel.getGraphVisible();
            GraphViz graphViz = pc.getGraphViz(workspace);

            // Setup Similarity Matrix Builder
            SimilarityNetworkBuilder.setStopRun(stopRun);
            SimilarityNetworkBuilder.peptides = peptides;
            SimilarityNetworkBuilder.progressTicket = progressTicket;
            SimilarityNetworkBuilder.similarityMeasure = getSimilarityMeasure();
            SimilarityNetworkBuilder.histogram = histogram;

            // Remove all similarity edges..
            int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
            Graph mainGraph = graphModel.getGraph();
            mainGraph.writeLock();
            try {
                for (Node node : mainGraph.getNodes()) {
                    mainGraph.clearEdges(node, relType);
                }
            } finally {
                mainGraph.writeUnlock();
            }
        }
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        attrModel = null;
        graphModel = null;
        graph = null;
        progressTicket = null;
        SimilarityNetworkBuilder.peptides = null;
        SimilarityNetworkBuilder.similarityMeasure = null;
        SimilarityNetworkBuilder.progressTicket = null;
        SimilarityNetworkBuilder.histogram = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        SimilarityNetworkBuilder.setStopRun(stopRun);
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
        if (peptides != null) {
            propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY_VALUES, null, null);

            // Delete old similarity matrix
            SimilarityMatrix matrix = workspace.getLookup().lookup(SimilarityMatrix.class);
            if (matrix != null) {
                workspace.remove(matrix);
            }

            // Build new similarity matrix
            SimilarityNetworkBuilder task = new SimilarityNetworkBuilder();
            int workunits = task.getSize();
            progressTicket.switchToDeterminate(workunits);
            fjPool.invoke(task);
            task.join();

            // Add the new similarity matrix to workspace
            matrix = task.getSimilarityMatrix();
            workspace.add(matrix);

            // Add similarity edge to graph
            Edge graphEdge;
            Float score;
            graph.writeLock();
            try {
                for (int i=0; i < peptides.length -1; i++) {
                    for (int j = i + 1; j < peptides.length; j++) {
                        score = matrix.getValue(peptides[i], peptides[j]);
                        if (score != null && score >= 0.3) {
                            graphEdge = createGraphEdge(peptides[i], peptides[j], score);
                            graph.addEdge(graphEdge);
                        }
                    }
                }
            } finally {
                graph.writeUnlock();
            }

            propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY_VALUES, null, histogram);
        }
    }

    private Edge createGraphEdge(Peptide peptide1, Peptide peptide2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        String id = String.format("%s-%s", peptide1.getId(), peptide2.getId());

        // Create Edge
        Edge graphEdge = graphModel.factory().newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        graphModel.getGraph().addEdge(graphEdge);
        graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);

        return graphEdge;
    }

    public void addSimilarityChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeSimilarityChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
