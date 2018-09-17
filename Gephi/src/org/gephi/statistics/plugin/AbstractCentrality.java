/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class AbstractCentrality implements Algorithm {

    protected GraphModel graphModel;
    protected Graph graph;
    protected boolean isCanceled;
    protected ProgressTicket progress;
    private final boolean directed = false;
    protected final String NODE_INDEX = "indexAttr";

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.progress = progressTicket;
        isCanceled = false;
        graphModel = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel(workspace);
        addCentralityColumn(graphModel);
        graph = graphModel.getGraphVisible();

        //Add index column
        Table table = graphModel.getNodeTable();
        table.addColumn(NODE_INDEX, Integer.class);

        int index = 0;
        for (Node s : graph.getNodes()) {
            s.setAttribute(NODE_INDEX, index);
            index++;
        }
    }

    protected abstract void addCentralityColumn(GraphModel graphModel);

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
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    protected void calculateDistanceMetrics() {
        int n = graph.getNodeCount();
        progress.switchToDeterminate(graph.getNodeCount());

        int totalPaths = 0;
        NodeIterable nodesIterable = graph.getNodes();
        for (Node s : nodesIterable) {
            Stack<Node> S = new Stack<>();

            LinkedList<Node>[] P = new LinkedList[n];
            double[] theta = new double[n];
            int[] d = new int[n];

            int s_index = (Integer) s.getAttribute(NODE_INDEX);

            //setInitParametetrsForNode(s, P, theta, d, s_index, n)
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

                    int r_index = (Integer)reachable.getAttribute(NODE_INDEX);
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
                    nodeEccentricity[s_index] = (int) Math.max(nodeEccentricity[s_index], d[i]);
                    nodeCloseness[s_index] += d[i];
                    nodeHarmonicCloseness[s_index] += Double.isInfinite(d[i]) ? 0.0 : 1.0 / d[i];
                    diameter = Math.max(diameter, d[i]);
                    reachable++;
                }
            }

            radius = (int) Math.min(nodeEccentricity[s_index], radius);

            if (reachable != 0) {
                nodeCloseness[s_index] = (nodeCloseness[s_index] == 0) ? 0 : reachable / nodeCloseness[s_index];
                nodeHarmonicCloseness[s_index] = nodeHarmonicCloseness[s_index] / reachable;
            }

            totalPaths += reachable;

            double[] delta = new double[n];
            while (!S.empty()) {
                Node w = S.pop();
                int w_index = indicies.get(w);
                ListIterator<Node> iter1 = P[w_index].listIterator();
                while (iter1.hasNext()) {
                    Node u = iter1.next();
                    int u_index = indicies.get(u);
                    delta[u_index] += (theta[u_index] / theta[w_index]) * (1 + delta[w_index]);
                }
                if (w != s) {
                    nodeBetweenness[w_index] += delta[w_index];
                }
            }
            if (isCanceled) {
                nodesIterable.doBreak();
                return metrics;
            }
            progress.progress();
        }

        avgDist /= totalPaths;//mN * (mN - 1.0f);

        calculateCorrection(graph, indicies, nodeBetweenness, directed, normalized);

        return metrics;
    }

    private EdgeIterable getEdgeIter(Graph graph, Node v, boolean directed) {
        EdgeIterable edgeIter;
        if (directed) {
            edgeIter = ((DirectedGraph) graph).getOutEdges(v);
        } else {
            edgeIter = graph.getEdges(v);
        }
        return edgeIter;
    }

}
