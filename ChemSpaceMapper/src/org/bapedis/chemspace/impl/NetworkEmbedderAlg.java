/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.ui.components.JQuickHistogram;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class NetworkEmbedderAlg implements Algorithm, Cloneable {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected ProgressTicket ticket;
    private AtomicBoolean stopRun;
    private AbstractDistance distFunc;
    private double maxDistance;
    private double currentThreshold;

    public NetworkEmbedderAlg(AlgorithmFactory factory) {
        this.factory = factory;
        currentThreshold = 0.7;
    }

    public AbstractDistance getDistanceFunction() {
        return distFunc;
    }

    public void setDistanceFunction(AbstractDistance distFunc) {
        this.distFunc = distFunc;
    }

    public double getSimilarityThreshold() {
        return currentThreshold;
    }

    public void setSimilarityThreshold(double threshold) {
        this.currentThreshold = threshold;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {        
        this.workspace = workspace;
        this.ticket = progressTicket;
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            graphModel = pc.getGraphModel(workspace);

            //Load features
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }
            MolecularDescriptor[] features = allFeatures.toArray(new MolecularDescriptor[0]);

            distFunc.setFeatures(allFeatures);
            double distance;
            for (int i = 0; i < peptides.length - 1; i++) {
                for (int j = i + 1; j < peptides.length; j++) {
                    try {
                        distance = distFunc.distance(peptides[i], peptides[j]);
                        if (distance > maxDistance) {
                            maxDistance = distance;
                        }
                    } catch (MolecularDescriptorNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
        stopRun = new AtomicBoolean(false);
    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        graphModel = null;
        stopRun = null;
    }

    @Override
    public boolean cancel() {
        stopRun.set(true);
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
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public void run() {
        // Remove all edges..
        Graph mainGraph = graphModel.getGraph();
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        mainGraph.writeLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                mainGraph.clearEdges(node, relType);
            }
        } finally {
            mainGraph.writeUnlock();
            pc.getGraphVizSetting().fireChangedGraphView();
        }

        // Create new edges...
        if (peptides != null && !stopRun.get()) {
            try {
                ticket.switchToDeterminate(peptides.length);
                HSPNetworkBuilder task = new HSPNetworkBuilder(peptides, graphModel, distFunc, maxDistance, currentThreshold, ticket, stopRun);               
                fjPool.invoke(task);
                task.join();
            } finally {
                pc.getGraphVizSetting().fireChangedGraphView();
            }
        }
    }

}
