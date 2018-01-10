/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.GraphViz;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ServiceProvider(service = GraphWindowController.class)
public class GraphWindowControllerImpl implements GraphWindowController, WorkspaceEventListener, PropertyChangeListener {

    protected final ProjectManager pc;
    private TopComponent graphWindow;

    public GraphWindowControllerImpl() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                MultiViewDescription[] multiviews = new MultiViewDescription[2];
                multiviews[0] = new NeoGraphSceneDescription();
                multiviews[1] = new NeoGraphPreViewDescription();
                graphWindow = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
                graphWindow.setDisplayName(NbBundle.getMessage(GraphWindowControllerImpl.class, "CTL_GraphTC_title"));
//                graphWindow.setIcon(ImageUtilities.loadImage("org/gephi/desktop/visualization/resources/gephilogo.png", false));
                workspaceChanged(null, pc.getCurrentWorkspace());
            }
        });
    }

    @Override
    public TopComponent getGraphWindow() {
        return graphWindow;
    }

    @Override
    public void openGraphWindow() {
        if (graphWindow != null && !graphWindow.isOpened()) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    graphWindow.open();
                    graphWindow.requestActive();
                }
            });
        }
    }

    @Override
    public void selectNode(Node node) {
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.selectNode(node);
    }

    @Override
    public void centerOnNode(Node node) {
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.centerOnNode(node);
    }

    @Override
    public void closeGraphWindow() {
        if (graphWindow != null) {
            graphWindow.close();
        }
    }

    @Override
    public void selectEdge(Edge edge) {
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.selectEdge(edge);
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            GraphViz oldModel = pc.getGraphViz(oldWs);
            if (oldModel != null) {
                oldModel.removeGraphViewChangeListener(this);
            }
        }

        GraphViz graphViz = pc.getGraphViz(newWs);
        graphViz.addGraphViewChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GraphViz.CHANGED_DISPLAYED_METADATA)) {
            if (evt.getNewValue() != null) {
                addMetadataNodes((AnnotationType) evt.getNewValue());
            } else if (evt.getOldValue() != null) {
                removeMetadataNodes((AnnotationType) evt.getOldValue());
            }
        } else if (evt.getPropertyName().equals(GraphViz.CHANGED_DISPLAYED_CSN)) {

        } else if (evt.getPropertyName().equals(GraphViz.CHANGED_THRESHOLD)) {
            changedThreshold((float) evt.getOldValue(), (float) evt.getNewValue());
        }
    }

    private void addMetadataNodes(AnnotationType aType) {
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int relType = graphModel.getEdgeType(aType.getRelationType());
        if (relType != -1) {
            graph.writeLock();
            try {
                for (Node node : graph.getNodes()) {
                    for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                        if (!graph.hasNode(neighbor.getId())) {
                            graph.addNode(neighbor);
                        }
                        Edge edge = graphModel.getGraph().getEdge(node, neighbor, relType);
                        if (!graph.hasEdge(edge.getId())) {
                            graph.addEdge(edge);
                        }
                    }
                }
            } finally {
                graph.writeUnlock();
            }
        }
    }

    private void removeMetadataNodes(AnnotationType aType) {
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int relType = graphModel.getEdgeType(aType.getRelationType());
        if (relType != -1) {
            graph.writeLock();
            try {
                for (Node node : graph.getNodes()) {
                    for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                        if (graph.hasNode(neighbor.getId())) {
                            graph.removeNode(neighbor);
                        }
                    }
                }
            } finally {
                graph.writeUnlock();
            }
        }
    }

    private void changedThreshold(float oldValue, float newValue) {
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        float score;
        int relType = graphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        if (relType != -1) {
            if (newValue < oldValue) { // to add edges      
                graph.writeLock();
                try {
                    for (Node node : graph.getNodes()) {
                        for (Edge edge : graphModel.getGraph().getEdges(node, relType)) {
                            if (graph.hasNode(edge.getSource().getId()) && graph.hasNode(edge.getTarget().getId())) {
                                score = (float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                                if (score >= newValue) {
                                    graph.addEdge(edge);
                                }
                            }
                        }
                    }
                } finally {
                    graph.writeUnlock();
                }
            } else if (newValue > oldValue) { // to remove edges
                graph.writeLock();
                try {
                    for (Edge edge : graph.getEdges()) {
                        score = (float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                        if (score < newValue) {
                            graph.removeEdge(edge);
                        }
                    }
                } finally {
                    graph.writeUnlock();
                }
            }
        }
    }

    private void refreshCSNView() {
        GraphViz graphViz = pc.getGraphViz();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int simRelType = graphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        if (simRelType != -1) {
            Edge edge;
            float score;
            Node[] nodes = graph.getNodes().toArray();
            graph.writeLock();
            try {
                if (graphViz.isCsnVisible()) {
                    for (int i = 0; i < nodes.length - 1; i++) {
                        for (int j = i + 1; j < nodes.length; j++) {
                            edge = graphModel.getGraph().getEdge(nodes[i], nodes[j], simRelType);
                            if (edge != null) {
                                score = (float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                                if (score >= graphViz.getSimilarityThreshold()) {
                                    graph.addEdge(edge);
                                }
                            }
                        }
                    }
                } else {
                    for (Node node : nodes) {
                        graph.clearEdges(node, simRelType);
                    }
                }
            } finally {
                graph.writeUnlock();
            }
        }
    }

    @Override
    public void refreshGraphView(List<Node> toAddNodes, List<Node> toRemoveNodes) {
        GraphViz graphViz = pc.getGraphViz();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        boolean modified = false;

        graph.writeLock();
        try {
            if (toAddNodes != null && toAddNodes.size() > 0) {
                addPeptideNodes(toAddNodes, graphViz, graphModel, graph);
                modified = true;
            }

            if (toRemoveNodes != null && toRemoveNodes.size() > 0) {
                removePeptideNodes(toRemoveNodes, graphViz, graphModel, graph);
                modified = true;
            }
        } finally {
            graph.writeUnlock();
        }

        if (modified) {
            graphViz.fireChangedGraphView();
        }
    }

    private void addPeptideNodes(List<Node> toAddNodes,
            GraphViz graphViz, GraphModel graphModel, Graph graph) {

        Set<Node> toAddMetadataNodes = new HashSet<>();
        List<Edge> toAddEdges = new LinkedList<>();
        int simRelType = graphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        float score;
        int relType;

        for (Node node : toAddNodes) {
            // Add similarity edges to list
            if (simRelType != -1 && graphViz.isCsnVisible()) {
                for (Node node2 : graph.getNodes()) {
                    Edge edge = graphModel.getGraph().getEdge(node, node2, simRelType);
                    if (edge != null) {
                        score = (float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                        if (score >= graphViz.getSimilarityThreshold()) {
                            toAddEdges.add(edge);
                        }
                    }
                }
            }

            // Add metada nodes and relationships to list
            for (AnnotationType aType : AnnotationType.values()) {
                relType = graphModel.getEdgeType(aType.getRelationType());
                if (relType != -1 && graphViz.isDisplayedMetadata(aType)) {
                    for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                        toAddMetadataNodes.add(neighbor);
                        toAddEdges.add(graphModel.getGraph().getEdge(node, neighbor, relType));
                    }
                }
            }

            // Add node
            graph.addNode(node);
        }

        graph.addAllNodes(toAddMetadataNodes);
        graph.addAllEdges(toAddEdges);
    }

    private void removePeptideNodes(List<Node> toRemoveNodes,
            GraphViz graphViz, GraphModel graphModel, Graph graph) {
        int relType;
        List<Node> singleMetadataNodes = new LinkedList<>();

        for (Node node : toRemoveNodes) {
            for (AnnotationType aType : AnnotationType.values()) {
                relType = graphModel.getEdgeType(aType.getRelationType());
                if (relType != -1 && graphViz.isDisplayedMetadata(aType)) {
                    for (Node neighbor : graph.getNeighbors(node, relType)) {
                        if (graph.getDegree(neighbor) == 1) {
                            singleMetadataNodes.add(neighbor);
                        }
                    }
                }
            }

            //Remove node
            graph.removeNode(node);
        }

        graph.removeAllNodes(singleMetadataNodes);
    }

}
