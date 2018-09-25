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

            int n = graph.getNodeCount();
            progress.switchToDeterminate(n);

            int diameter = 0;
            double avgDist = 0;
            int totalPaths = 0;
            double[] theta = new double[n];
            int[] d = new int[n];
            LinkedList<Node>[] P = new LinkedList[n];
            
            for (Node s : nodes) {
                if (isCanceled) {
                    return;
                }
                Stack<Node> S = new Stack<>();

                int s_index = (Integer) s.getAttribute(NODE_INDEX);

                //Set init parametetrs for nodes
                for (int j = 0; j < n; j++) {
                    P[j] = new LinkedList<>();
                    theta[j] = 0;
                    d[j] = -1;
                }
                theta[s_index] = 1;
                d[s_index] = 0;

                LinkedList<Node> Q = new LinkedList<>();
                Q.addLast(s);
                while (!Q.isEmpty()) {
                    Node v = Q.removeFirst();
                    S.push(v);
                    int v_index = (Integer) v.getAttribute(NODE_INDEX);

                    EdgeIterable edgeIter = getEdgeIter(graph, v, directed);

                    for (Edge edge : edgeIter) {
                        Node reachable = graph.getOpposite(v, edge);

                        int r_index = (Integer) reachable.getAttribute(NODE_INDEX);
                        if (d[r_index] < 0) {
                            Q.addLast(reachable);
                            d[r_index] = d[v_index] + 1;
                        }
                        if (d[r_index] == (d[v_index] + 1)) {
                            theta[r_index] = theta[r_index] + theta[v_index];
                            P[r_index].addLast(v);
                        }
                    }
                }
                double reachable = 0;
                for (int i = 0; i < n; i++) {
                    if (d[i] > 0) {
                        avgDist += d[i];
                        diameter = Math.max(diameter, d[i]);
                        reachable++;
                    }
                }

                totalPaths += reachable;

                calculateCentrality(s_index, S, P, d, theta);
                progress.progress();
            }
            avgDist /= totalPaths;//mN * (mN - 1.0f);
        } finally {
            graph.readUnlockAll();
        }
    }

    protected abstract void calculateCentrality(int s_index, Stack<Node> stack, LinkedList<Node>[] list, int[] d, double[] theta);

    private EdgeIterable getEdgeIter(Graph graph, Node v, boolean directed) {
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
