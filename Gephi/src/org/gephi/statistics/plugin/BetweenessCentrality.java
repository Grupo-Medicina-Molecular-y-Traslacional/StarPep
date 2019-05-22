/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Stack;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 * Ref: Ulrik Brandes, A Faster Algorithm for Betweenness Centrality, in Journal
 * of Mathematical Sociology 25(2):163-177, (2001)
 *
 * @author pjmcswee
 * @author Jonny Wray
 */
public class BetweenessCentrality extends AbstractCentrality {

    public static final String BETWEENNESS = "betweenesscentrality";
    private AlgorithmProperty[] property;
    private boolean normalized;
    private double[] nodeBetweenness;
    private GraphVizSetting graphViz;

    public BetweenessCentrality(BetweenessCentralityFactory factory) {
        super(factory);

        try {
            String CATEGORY = "Properties";
            normalized = false;
            property = new AlgorithmProperty[]{AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(BetweenessCentrality.class, "Property.normalize.name"), CATEGORY, NbBundle.getMessage(BetweenessCentrality.class, "Property.normalize.desc"), "isNormalized", "setNormalized")};
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
            property = null;
        }
    }

    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(Boolean normalized) {
        this.normalized = normalized;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);

        graphViz = pc.getGraphVizSetting(workspace);
        nodeBetweenness = new double[graph.getNodeCount()];
    }
     

    @Override
    protected void calculateCentrality(){
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
    }
    
    private void calculateCentrality(int s_index, Stack<Node> stack, LinkedList<Node>[] list, int[] d, double[] theta) {
        double[] delta = new double[graph.getNodeCount()];
        while (!stack.empty()) {
            Node w = stack.pop();
            int w_index = (Integer) w.getAttribute(NODE_INDEX);
            ListIterator<Node> iter1 = list[w_index].listIterator();
            while (iter1.hasNext()) {
                Node u = iter1.next();
                int u_index = (Integer) u.getAttribute(NODE_INDEX);
                delta[u_index] += (theta[u_index] / theta[w_index]) * (1 + delta[w_index]);
            }
            if (w_index != s_index) {
                nodeBetweenness[w_index] += delta[w_index];
            }
        }
    }

    @Override
    public void endAlgo() {
        int n = nodes.length;

        for (Node s : nodes) {
            int s_index = (Integer) s.getAttribute(NODE_INDEX);

            if (!directed) {
                nodeBetweenness[s_index] /= 2;
            }
            if (normalized) {
                nodeBetweenness[s_index] /= directed ? (n - 1) * (n - 2) : (n - 1) * (n - 2) / 2;
            }
        }

        //Add betweenness centrality column
        boolean fireEvent = false;
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(BETWEENNESS)) {
            nodeTable.addColumn(BETWEENNESS, "Betweenness Centrality", Double.class, new Double(0));
            fireEvent = true;
        }

        //Set default values
        Double defaultValue = new Double(0);
        for(Node node: graphModel.getGraph().getNodes()){
            node.setAttribute(BETWEENNESS, defaultValue);
        }
        
        //Save values
        for (Node s : nodes) {
            int s_index = (Integer) s.getAttribute(NODE_INDEX);
            s.setAttribute(BETWEENNESS, nodeBetweenness[s_index]);
        }

        super.endAlgo();
        nodeBetweenness = null;

        if (fireEvent) {
            graphViz.fireChangedGraphView();
        }
        graphViz = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return property;
    }

}
