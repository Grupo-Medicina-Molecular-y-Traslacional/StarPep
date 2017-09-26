/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

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
import org.gephi.graph.api.GraphModel;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class NetworkSimilarityAlgo implements Algorithm {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final AlgorithmFactory factory;
    protected final PropertyChangeSupport propertyChangeSupport;
    public static final String CHANGED_SIMILARITY = "similarity";

    public NetworkSimilarityAlgo(AlgorithmFactory factory) {
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    @Override
    public void initAlgo() {
        AttributesModel attrModel = pc.getAttributesModel();
        GraphModel graphModel = pc.getGraphModel();
        SimilarityGraphEdgeBuilder.attrModel = attrModel;
        SimilarityGraphEdgeBuilder.peptides = attrModel.getPeptides();
        SimilarityGraphEdgeBuilder.graphModel = graphModel;
        SimilarityGraphEdgeBuilder.setStopRun(false);
        SimilarityGraphEdgeBuilder.mainGraph = graphModel.getGraph();
        SimilarityGraphEdgeBuilder.csnGraph = graphModel.getGraph(attrModel.getCsnView());
        SimilarityGraphEdgeBuilder.similarityMeasure = getSimilarityProvider();
        SimilarityGraphEdgeBuilder.edgeList = new LinkedList<>();        
    }

    @Override
    public void endAlgo() {
        SimilarityGraphEdgeBuilder.attrModel = null;
        SimilarityGraphEdgeBuilder.peptides = null;
        SimilarityGraphEdgeBuilder.graphModel = null;
        SimilarityGraphEdgeBuilder.mainGraph = null;
        SimilarityGraphEdgeBuilder.csnGraph = null;
        SimilarityGraphEdgeBuilder.similarityMeasure = null;
        SimilarityGraphEdgeBuilder.progressTicket = null;
        SimilarityGraphEdgeBuilder.edgeList = null;
    }

    @Override
    public boolean cancel() {
        SimilarityGraphEdgeBuilder.setStopRun(true);
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
        SimilarityGraphEdgeBuilder.progressTicket=progressTicket;
    }

    @Override
    public void run() {
        propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY, null, null);
        Peptide[] peptides = SimilarityGraphEdgeBuilder.peptides;
        // Workunits for pairwise sim matrix builder
        int workunits = peptides.length * (peptides.length - 1) / 2;
        SimilarityGraphEdgeBuilder.progressTicket.switchToDeterminate(workunits);
        SimilarityGraphEdgeBuilder task = new SimilarityGraphEdgeBuilder();
        fjPool.invoke(task);
        task.join();
        propertyChangeSupport.firePropertyChange(CHANGED_SIMILARITY, null, SimilarityGraphEdgeBuilder.edgeList);
    }

    protected abstract SimilarityMeasure getSimilarityProvider();

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
