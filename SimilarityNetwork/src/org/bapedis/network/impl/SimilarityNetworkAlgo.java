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
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import static org.bapedis.network.impl.SimilarityGraphEdgeBuilder.graphModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class SimilarityNetworkAlgo implements Algorithm, SimilarityMeasure {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final JQuickHistogram histogram;
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected AttributesModel attrModel;
    protected ProgressTicket progressTicket;
    protected boolean stopRun;
    protected final AlgorithmFactory factory;
    protected final PropertyChangeSupport propertyChangeSupport;
    protected float threshold;

    public SimilarityNetworkAlgo(AlgorithmFactory factory) {
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
        histogram = new JQuickHistogram();
        threshold = 0.7f;
    }

    @Override
    public float getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(float value) {
        float oldValue = this.threshold;
        this.threshold = value;
        propertyChangeSupport.firePropertyChange(CHANGED_THRESHOLD_VALUE, oldValue, threshold);
    }
    
    public SimilarityMeasure getSimilarityMeasure(){
        return this;
    }

    @Override
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
            SimilarityGraphEdgeBuilder.attrModel = attrModel;
            SimilarityGraphEdgeBuilder.peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            SimilarityGraphEdgeBuilder.graphModel = graphModel;
            SimilarityGraphEdgeBuilder.setStopRun(stopRun);
            SimilarityGraphEdgeBuilder.progressTicket = progressTicket;
            SimilarityGraphEdgeBuilder.mainGraph = graphModel.getGraph();
            SimilarityGraphEdgeBuilder.csnGraph = graphModel.getGraph(attrModel.getCsnView());
            SimilarityGraphEdgeBuilder.similarityMeasure = getSimilarityMeasure();
            SimilarityGraphEdgeBuilder.edgeList = new LinkedList<>();
            
            // Remove all edges..
            int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
            for (Node node : graphModel.getGraph().getNodes()){
                for(Edge edge: graphModel.getGraph().getEdges(node, relType)){
                    edge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, -1f);
                }
            }
            SimilarityGraphEdgeBuilder.csnGraph.removeAllEdges(SimilarityGraphEdgeBuilder.csnGraph.getEdges().toCollection());            
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
        SimilarityGraphEdgeBuilder.similarityMeasure = null;
        SimilarityGraphEdgeBuilder.progressTicket = null;
        SimilarityGraphEdgeBuilder.edgeList = null;
        SimilarityGraphEdgeBuilder.progressTicket = null;
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
            attrModel.setSimilarityThreshold(threshold);
            propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY_VALUES, null, null);
            Peptide[] peptides = SimilarityGraphEdgeBuilder.peptides;
            // Workunits for pairwise sim matrix builder
            int workunits = peptides.length * (peptides.length - 1) / 2;
            progressTicket.switchToDeterminate(workunits);
            SimilarityGraphEdgeBuilder task = new SimilarityGraphEdgeBuilder();
            fjPool.invoke(task);
            task.join();
            for (Edge edge : SimilarityGraphEdgeBuilder.edgeList) {
                histogram.addData((float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY));
            }
            propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY_VALUES, null, histogram);
            attrModel.fireChangedGraphView();
        }
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
