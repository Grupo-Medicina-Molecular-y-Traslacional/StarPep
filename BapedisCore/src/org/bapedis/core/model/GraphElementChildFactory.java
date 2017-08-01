/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

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

    protected final GraphElement type;
    protected final ProjectManager pc;

    public GraphElementChildFactory(GraphElement type) {
        this.type = type;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    protected boolean createKeys(List<Element> list) {
        list.clear();
        GraphModel model = pc.getGraphModel();
        GraphView view = model.getVisibleView();
        Graph graph = model.getGraph(view);
        graph.readLock();
        try {
            switch (type) {
                case Node:
                    for (Node node : graph.getNodes()) {
                        list.add(node);
                    }
                    break;
                case Edge:
                    for (Edge edge : graph.getEdges()) {
                        list.add(edge);
                    }
                    break;
            }
        } finally {
            graph.readUnlock();
        }
        return true;
    }

    @Override
    protected org.openide.nodes.Node createNodeForKey(Element key) {
        return new GraphElementNode(key);
    }

    public void refreshData() {
        refresh(false);
    }

}
