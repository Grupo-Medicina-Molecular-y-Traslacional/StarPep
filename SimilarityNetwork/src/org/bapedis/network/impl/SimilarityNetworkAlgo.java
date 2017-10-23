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
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
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
    protected double threshold;

    public SimilarityNetworkAlgo(AlgorithmFactory factory) {
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
        histogram = new JQuickHistogram();
        threshold = 0.7;
    }

    @Override
    public double getThreshold() {
        return threshold;
    }

    @Override
    public void setThreshold(double value) {
        double oldValue = this.threshold;
        this.threshold = value;
        propertyChangeSupport.firePropertyChange(CHANGED_THRESHOLD_VALUE, oldValue, threshold);
    }

    @Override
    public JQuickHistogram getHistogram() {
        return histogram;
    }

    @Override
    public void initAlgo() {
        histogram.clear();
        attrModel = pc.getAttributesModel();
        stopRun = false;
        if (attrModel != null) {
            GraphModel graphModel = pc.getGraphModel();
            SimilarityGraphEdgeBuilder.attrModel = attrModel;
            SimilarityGraphEdgeBuilder.peptides = attrModel.getPeptides();
            SimilarityGraphEdgeBuilder.graphModel = graphModel;
            SimilarityGraphEdgeBuilder.setStopRun(stopRun);
            SimilarityGraphEdgeBuilder.progressTicket = progressTicket;
            SimilarityGraphEdgeBuilder.mainGraph = graphModel.getGraph();
            SimilarityGraphEdgeBuilder.csnGraph = graphModel.getGraph(attrModel.getCsnView());
            SimilarityGraphEdgeBuilder.similarityMeasure = this;
            SimilarityGraphEdgeBuilder.edgeList = new LinkedList<>();
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
                histogram.addData((Double) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY));
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
