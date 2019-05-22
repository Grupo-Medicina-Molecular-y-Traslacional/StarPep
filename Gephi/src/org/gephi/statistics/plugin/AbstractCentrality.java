/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import java.util.LinkedList;
import java.util.Stack;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class AbstractCentrality implements Algorithm {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final AlgorithmFactory factory;
    protected GraphModel graphModel;
    protected Graph graph;
    protected Node[] nodes;
    protected boolean isCanceled;
    protected Workspace workspace;
    protected ProgressTicket progress;
    protected final boolean directed = false;
    protected final String NODE_INDEX = "nodeIndex";

    public AbstractCentrality(AlgorithmFactory factory) {
        this.factory = factory;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progress = progressTicket;
        isCanceled = false;
        graphModel = pc.getGraphModel(workspace);
        graph = graphModel.getGraphVisible();
        nodes = graph.getNodes().toArray();
        
        //Add index column
        Table table = graphModel.getNodeTable();
        table.addColumn(NODE_INDEX, Integer.class);
    }

    @Override
    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    @Override
    public void endAlgo() {
        //Remove index column
        Table table = graphModel.getNodeTable();
        table.removeColumn(NODE_INDEX);

        graph = null;
        graphModel = null;
        workspace = null;
        progress = null;
        nodes = null;
    }

    @Override
    public void run() {
        int index = 0;
        graph.readLock();
        try {            
            for (Node s : nodes) {
                s.setAttribute(NODE_INDEX, index);
                index++;
            }

            calculateCentrality();
        } finally {
            graph.readUnlockAll();
        }
    }

    protected abstract void calculateCentrality();

    protected EdgeIterable getEdgeIter(Graph graph, Node v, boolean directed) {
        EdgeIterable edgeIter;
        if (directed) {
            edgeIter = ((DirectedGraph) graph).getOutEdges(v);
        } else {
            edgeIter = graph.getEdges(v);
        }
        return edgeIter;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

}
