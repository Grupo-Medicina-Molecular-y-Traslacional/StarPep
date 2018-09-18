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
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
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
    protected void calculateCentrality(int s_index, Stack<Node> stack, LinkedList<Node>[] list, int[] d, double[] theta) {
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
        int n = graph.getNodeCount();

        for (Node s : graph.getNodes()) {
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

        //Save values
        for (Node s : graph.getNodes()) {
            int s_index = (Integer) s.getAttribute(NODE_INDEX);
            s.setAttribute(BETWEENNESS, nodeBetweenness[s_index]);
        }

        super.endAlgo();
        nodeBetweenness = null;

        if (fireEvent) {
            graphViz.fireChangedGraphTable();
        }
        graphViz = null;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return property;
    }

}