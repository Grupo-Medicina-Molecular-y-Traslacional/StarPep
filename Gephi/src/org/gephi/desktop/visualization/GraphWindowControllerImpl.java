/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.SwingUtilities;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ServiceProvider(service = GraphWindowController.class)
public class GraphWindowControllerImpl implements GraphWindowController, WorkspaceEventListener, LookupListener, PropertyChangeListener {

    protected final ProjectManager pc;
    private TopComponent graphWindow;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected AttributesModel currentModel;
    protected final String GRAPHDB_NAME = "graphDB";
    protected final String CHEMSPACE_NAME = "chemSpace";

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

    private void setDisplayName(final String name) {
        if (graphWindow != null) {
            graphWindow.setDisplayName(NbBundle.getMessage(GraphWindowControllerImpl.class, "CTL_GraphTC_" + name));
        }
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeGraphViewChangeListener(this);
            }
        }

        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        peptideLkpResult.addLookupListener(this);

        AttributesModel peptidesModel = pc.getAttributesModel(newWs);
        if (peptidesModel != null) {
            currentModel = peptidesModel;
            if (currentModel.getMainGView() == AttributesModel.GRAPH_DB_VIEW) {
                setDisplayName(GRAPHDB_NAME);
            } else if (currentModel.getMainGView() == AttributesModel.CSN_VIEW) {
                setDisplayName(CHEMSPACE_NAME);
            }
            currentModel.addGraphViewChangeListener(this);
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (ev.getSource().equals(peptideLkpResult)) {
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                if (currentModel != null) {
                    currentModel.removeGraphViewChangeListener(this);
                }
                this.currentModel = attrModels.iterator().next();
                currentModel.addGraphViewChangeListener(this);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel) && evt.getPropertyName().equals(AttributesModel.CHANGED_GVIEW)) {
            if (currentModel.getMainGView() == AttributesModel.GRAPH_DB_VIEW) {
                setDisplayName(GRAPHDB_NAME);
            } else if (currentModel.getMainGView() == AttributesModel.CSN_VIEW) {
                setDisplayName(CHEMSPACE_NAME);
            }
        }
    }
}
