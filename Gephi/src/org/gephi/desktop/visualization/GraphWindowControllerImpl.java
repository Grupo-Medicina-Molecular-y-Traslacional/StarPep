/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphVizSetting;
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
        if (graphWindow != null) {
            if (!graphWindow.isOpened()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        graphWindow.open();
                        graphWindow.requestActive();
                    }
                });
            } else {
                graphWindow.requestActive();
            }
        }
    }

    @Override
    public synchronized void selectNode(Node node) {
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.selectNode(node);
    }

    @Override
    public synchronized void centerOnNode(Node node) {
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
    public synchronized void selectEdge(Edge edge) {
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.selectEdge(edge);
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            GraphVizSetting oldModel = pc.getGraphVizSetting(oldWs);
            if (oldModel != null) {
                oldModel.removeDisplayedMetadataChangeListener(this);
            }
        }

        GraphVizSetting graphViz = pc.getGraphVizSetting(newWs);
        graphViz.addDisplayedMetadataChangeListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GraphVizSetting.CHANGED_DISPLAYED_METADATA)) {
            if (evt.getNewValue() != null) {
                addMetadataNodes((StarPepAnnotationType) evt.getNewValue());
            } else if (evt.getOldValue() != null) {
                removeMetadataNodes((StarPepAnnotationType) evt.getOldValue());
            }
        }
    }

    private synchronized void addMetadataNodes(StarPepAnnotationType aType) {
        GraphVizSetting graphViz = pc.getGraphVizSetting();
        AttributesModel attrModel = pc.getAttributesModel();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int relType = graphModel.getEdgeType(aType.getRelationType());
        if (relType != -1) {
            Set<Node> toAddNodes = new HashSet<>();
            List<Edge> toAddEdges = new LinkedList<>();
            Node node;
            boolean added;
            for (Peptide peptide : attrModel.getPeptides()) {
                node = peptide.getGraphNode();
                for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                    assert neighbor.getLabel().equals(aType.getLabelName());
                    added = toAddNodes.add(neighbor);
                    toAddEdges.add(graphModel.getGraph().getEdge(node, neighbor, relType));
                    if (added) {
                        addParentNodes(neighbor, toAddNodes, toAddEdges, graphModel);
                    }
                }
            }
            graph.addAllNodes(toAddNodes);
            graph.addAllEdges(toAddEdges);
            if (toAddNodes.size() > 0 || toAddEdges.size() > 0) {
                graphViz.fireChangedGraphView();
            }
        }
    }

    private synchronized void removeMetadataNodes(StarPepAnnotationType aType) {
        GraphVizSetting graphViz = pc.getGraphVizSetting();
        AttributesModel attrModel = pc.getAttributesModel();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        int relType = graphModel.getEdgeType(aType.getRelationType());
        if (relType != -1) {
            Set<Node> toRemoveNodes = new HashSet<>();
            Node node;
            for (Peptide peptide : attrModel.getPeptides()) {
                node = peptide.getGraphNode();
                for (Node neighbor : graph.getNeighbors(node, relType)) {
                    assert neighbor.getLabel().equals(aType.getLabelName());
                    if (toRemoveNodes.add(neighbor)) {
                        addParentNodes(neighbor, toRemoveNodes, null, graphModel);
                    }
                }
            }
            graph.removeAllNodes(toRemoveNodes);
            if (toRemoveNodes.size() > 0) {
                graphViz.fireChangedGraphView();
            }
        }
    }

    @Override
    public synchronized void refreshGraphView(List<Node> toAddNodes, List<Node> toRemoveNodes) {
        GraphVizSetting graphViz = pc.getGraphVizSetting();
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraphVisible();
        boolean modified = false;

        graph.writeLock();
        try {
            if (toRemoveNodes != null && toRemoveNodes.size() > 0) {
                removePeptideNodes(toRemoveNodes, graphViz, graphModel, graph);
                modified = true;
            }

            if (toAddNodes != null && toAddNodes.size() > 0) {
                addPeptideNodes(toAddNodes, graphViz, graphModel, graph);
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
            GraphVizSetting graphViz, GraphModel graphModel, Graph graph) {

        Set<Node> metadataNodes = new HashSet<>();
        List<Edge> toAddEdges = new LinkedList<>();
        int relType;
        boolean added;
        for (Node node : toAddNodes) {
            // Add metada nodes and relationships to list
            for (StarPepAnnotationType aType : StarPepAnnotationType.values()) {
                relType = graphModel.getEdgeType(aType.getRelationType());
                if (relType != -1 && graphViz.isDisplayedMetadata(aType)) {
                    for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                        added = metadataNodes.add(neighbor);
                        toAddEdges.add(graphModel.getGraph().getEdge(node, neighbor, relType));
                        if (added) {                            
                            addParentNodes(neighbor, metadataNodes, toAddEdges, graphModel);
                        }
                    }
                }
            }
        }

        graph.addAllNodes(toAddNodes);
        graph.addAllNodes(metadataNodes);
        graph.addAllEdges(toAddEdges);
    }

    private void removePeptideNodes(List<Node> peptideNodes,
            GraphVizSetting graphViz, GraphModel graphModel, Graph graph) {
        int relType;
        Set<Node> metadataNodes = new LinkedHashSet<>();

        for (Node node : peptideNodes) {
            for (StarPepAnnotationType aType : StarPepAnnotationType.values()) {
                relType = graphModel.getEdgeType(aType.getRelationType());
                if (relType != -1 && graphViz.isDisplayedMetadata(aType)) {
                    for (Node neighbor : graph.getNeighbors(node, relType)) {
                        if (metadataNodes.add(neighbor)) {
                            addParentNodes(neighbor, metadataNodes, null, graphModel);
                        }
                    }
                }
            }
        }

        graph.removeAllNodes(metadataNodes);
        graph.removeAllNodes(peptideNodes);
    }

    private void addParentNodes(Node node, Set<Node> metadataNodes, List<Edge> edgeList, GraphModel graphModel) {
        int relType = graphModel.getEdgeType("is_a");
        if (relType != -1) {
            for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                if (metadataNodes.add(neighbor)) {
                    addParentNodes(neighbor, metadataNodes, edgeList, graphModel);
                    if (edgeList != null) {
                        edgeList.add(graphModel.getGraph().getEdge(node, neighbor, relType));
                    }
                }
            }
        }
    }

}
