/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.services.ProjectManager;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.openide.nodes.ChildFactory;
import org.openide.util.Lookup;

/**
 *
 * @author cicese
 */
public class GraphElementChildFactory extends ChildFactory<Element> {

    protected final GraphElementType type;
    protected final ProjectManager pc;

    public GraphElementChildFactory(GraphElementType type) {
        this.type = type;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    protected boolean createKeys(List<Element> list) {
        GraphModel model = pc.getGraphModel();
        GraphView view = model.getVisibleView();
        Graph graph = model.getGraph(view);
        graph.readLock();
        try {
            LinkedList<Element> list2 = new LinkedList();
            switch (type) {
                case Node:
                    for (Node node : graph.getNodes()) {
                        list2.add(node);
                    }
                    break;
                case Edge:
                    for (Edge edge : graph.getEdges()) {
                        list2.add(edge);
                    }
                    break;
            }
            list.addAll(list2);
        } finally {
            graph.readUnlock();
        }
        return true;
    }

    @Override
    protected org.openide.nodes.Node createNodeForKey(Element key) {
        return new GraphElementNode(key);
    }

}
