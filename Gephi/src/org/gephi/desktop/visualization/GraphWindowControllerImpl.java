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
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphViz;
import org.bapedis.core.model.Peptide;
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
                oldModel.removeDisplayedMetadataChangeListener(this);
            }
        }

        GraphViz graphViz = pc.getGraphViz(newWs);
        graphViz.addDisplayedMetadataChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GraphViz.CHANGED_DISPLAYED_METADATA)) {
            if (evt.getNewValue() != null) {
                addMetadataNodes((AnnotationType) evt.getNewValue());
            } else if (evt.getOldValue() != null) {
                removeMetadataNodes((AnnotationType) evt.getOldValue());
            }
        }
    }

    private void addMetadataNodes(AnnotationType aType) {
        GraphViz graphViz = pc.getGraphViz();
        AttributesModel attrModel = pc.getAttributesModel();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int relType = graphModel.getEdgeType(aType.getRelationType());
        if (relType != -1) {
            Set<Node> nodes = new HashSet<>();
            List<Edge> edges = new LinkedList<>();
            Node node;
            Edge edge;
            for (Peptide peptide : attrModel.getPeptides()) {
                node = peptide.getGraphNode();
                for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                    nodes.add(neighbor);
                    edge = graphModel.getGraph().getEdge(node, neighbor, relType);
                    edges.add(edge);
                }
            }
            graph.addAllNodes(nodes);
            graph.addAllEdges(edges);
            if (nodes.size() > 0 || edges.size() > 0) {
                graphViz.fireChangedGraphView();
            }
        }
    }

    private void removeMetadataNodes(AnnotationType aType) {
        GraphViz graphViz = pc.getGraphViz();
        AttributesModel attrModel = pc.getAttributesModel();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int relType = graphModel.getEdgeType(aType.getRelationType());
        if (relType != -1) {
            Set<Node> nodes = new HashSet<>();
            Node node;
            for (Peptide peptide : attrModel.getPeptides()) {
                node = peptide.getGraphNode();
                for (Node neighbor : graph.getNeighbors(node, relType)) {
                    nodes.add(neighbor);
                }
            }
            graph.removeAllNodes(nodes);
            if (nodes.size() > 0) {
                graphViz.fireChangedGraphView();
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
        int relType;

        for (Node node : toAddNodes) {
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
