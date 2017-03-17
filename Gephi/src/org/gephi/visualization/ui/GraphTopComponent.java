/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.gephi.visualization.ui;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.db.model.NeoPeptide;
import org.bapedis.db.model.NeoPeptideModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Subgraph;
import org.gephi.preview.api.PreviewController;
import org.gephi.tools.api.ToolController;
import org.gephi.ui.utils.UIUtils;
import org.gephi.visualization.VizController;
import org.gephi.visualization.apiimpl.GraphDrawable;
import org.gephi.visualization.opengl.AbstractEngine;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

@ConvertAsProperties(dtd = "-//org.gephi.visualization.component//Graph//EN",
        autostore = false)
@TopComponent.Description(preferredID = "GraphTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "editor", openAtStartup = true, roles = {"overview"})
@ActionID(category = "Window", id = "org.gephi.visualization.component.GraphTopComponent")
@ActionReference(path = "Menu/Window", position = 500)
@TopComponent.OpenActionRegistration(displayName = "#CTL_GraphTopComponent",
        preferredID = "GraphTopComponent")
public class GraphTopComponent extends TopComponent implements WorkspaceEventListener, LookupListener, PropertyChangeListener {

    protected final ProjectManager pm;
    protected Lookup.Result<NeoPeptideModel> peptideLkpResult;
    protected Lookup.Result<FilterModel> filterLkpResult;
    private transient AbstractEngine engine;
    private transient VizBarController vizBarController;
    private transient GraphDrawable drawable;
    private CollapsePanel collapsePanel;

    public GraphTopComponent() {
        initComponents();

        setName(NbBundle.getMessage(GraphTopComponent.class, "CTL_GraphTopComponent"));
//        setToolTipText(NbBundle.getMessage(GraphTopComponent.class, "HINT_GraphTopComponent"));

        //Request component activation and therefore initialize JOGL2 component
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                open();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        //Init
                        initCollapsePanel();
                        add(collapsePanel, java.awt.BorderLayout.PAGE_END);
                        initToolPanels();
                        drawable = VizController.getInstance().getDrawable();
                        engine = VizController.getInstance().getEngine();

                        requestActive();
//                        scrollPane.setViewportView(drawable.getGraphComponent());
                        add(drawable.getGraphComponent(), BorderLayout.CENTER);
                        remove(waitingLabel);
                    }
                });
            }
        });
        pm = Lookup.getDefault().lookup(ProjectManager.class);

//Preview configuration
//        PreviewController previewController = Lookup.getDefault().lookup(PreviewController.class);
//        PreviewModel previewModel = previewController.getModel();
//        previewModel.getProperties().putValue(PreviewProperty.SHOW_NODE_LABELS, Boolean.TRUE);
//        previewModel.getProperties().putValue(PreviewProperty.NODE_LABEL_COLOR, new DependantOriginalColor(Color.WHITE));
//        previewModel.getProperties().putValue(PreviewProperty.EDGE_CURVED, Boolean.FALSE);
//        previewModel.getProperties().putValue(PreviewProperty.EDGE_OPACITY, 50);
//        previewModel.getProperties().putValue(PreviewProperty.BACKGROUND_COLOR, Color.BLACK);
//
//        //New Processing target, get the PApplet
//        G2DTarget target = (G2DTarget) previewController.getRenderTarget(RenderTarget.G2D_TARGET);
//        final PreviewSketch previewSketch = new PreviewSketch(target);
//        previewController.refreshPreview();
//
//        add(previewSketch, BorderLayout.CENTER);
    }

    private void initCollapsePanel() {
        vizBarController = new VizBarController();
        collapsePanel = new CollapsePanel();
        if (VizController.getInstance().getVizConfig().isShowVizVar()) {
            collapsePanel.init(vizBarController.getToolbar(), vizBarController.getExtendedBar(), false);
        } else {
            collapsePanel.setVisible(false);
        }
    }
    private SelectionToolbar selectionToolbar;
    private ActionsToolbar actionsToolbar;
    private JComponent toolbar;
    private JComponent propertiesBar;

    private void initToolPanels() {
        final ToolController tc = Lookup.getDefault().lookup(ToolController.class);
        if (tc != null) {
            if (VizController.getInstance().getVizConfig().isToolbar()) {
                JPanel westPanel = new JPanel(new BorderLayout(0, 0));
                if (UIUtils.isAquaLookAndFeel()) {
                    westPanel.setBackground(UIManager.getColor("NbExplorerView.background"));
                }

                toolbar = tc.getToolbar();
                if (toolbar != null) {
                    westPanel.add(toolbar, BorderLayout.CENTER);
                }
                selectionToolbar = new SelectionToolbar();
                actionsToolbar = new ActionsToolbar();

                westPanel.add(selectionToolbar, BorderLayout.NORTH);
                westPanel.add(actionsToolbar, BorderLayout.SOUTH);
                add(westPanel, BorderLayout.WEST);
            }

            if (VizController.getInstance().getVizConfig().isPropertiesbar()) {
                propertiesBar = tc.getPropertiesBar();
                if (propertiesBar != null) {
                    add(propertiesBar, BorderLayout.NORTH);
                }
            }
        }

        //Workspace events
//        ProjectController projectController = Lookup.getDefault().lookup(ProjectController.class);
//        projectController.addWorkspaceListener(new WorkspaceListener() {
//            @Override
//            public void initialize(Workspace workspace) {
//            }
//
//            @Override
//            public void select(Workspace workspace) {
//                if (toolbar != null) {
//                    toolbar.setEnabled(true);
//                }
//                if (propertiesBar != null) {
//                    propertiesBar.setEnabled(true);
//                }
//                if (actionsToolbar != null) {
//                    actionsToolbar.setEnabled(true);
//                }
//                if (selectionToolbar != null) {
//                    selectionToolbar.setEnabled(true);
//                }
//            }
//
//            @Override
//            public void unselect(Workspace workspace) {
//            }
//
//            @Override
//            public void close(Workspace workspace) {
//            }
//
//            @Override
//            public void disable() {
//                if (toolbar != null) {
//                    toolbar.setEnabled(false);
//                }
//                if (tc != null) {
//                    tc.select(null);//Unselect any selected tool
//                }
//                if (propertiesBar != null) {
//                    propertiesBar.setEnabled(false);
//                }
//                if (actionsToolbar != null) {
//                    actionsToolbar.setEnabled(false);
//                }
//                if (selectionToolbar != null) {
//                    selectionToolbar.setEnabled(false);
//                }
//            }
//        });
        boolean hasWorkspace = true;
        if (toolbar != null) {
            toolbar.setEnabled(hasWorkspace);
        }
        if (propertiesBar != null) {
            propertiesBar.setEnabled(hasWorkspace);
        }
        if (actionsToolbar != null) {
            actionsToolbar.setEnabled(hasWorkspace);
        }
        if (selectionToolbar != null) {
            selectionToolbar.setEnabled(hasWorkspace);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        waitingLabel = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        waitingLabel.setBackground(new java.awt.Color(255, 255, 255));
        org.openide.awt.Mnemonics.setLocalizedText(waitingLabel, org.openide.util.NbBundle.getMessage(GraphTopComponent.class, "GraphTopComponent.waitingLabel.text")); // NOI18N
        waitingLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        add(waitingLabel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel waitingLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void componentOpened() {
        pm.addWorkspaceEventListener(this);
        workspaceChanged(null, pm.getCurrentWorkspace());
    }

    @Override
    public void componentClosed() {
        removeLookupListener();
        pm.removeWorkspaceEventListener(this);
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
        if (filterLkpResult != null) {
            filterLkpResult.removeLookupListener(this);
            filterLkpResult = null;
        }
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            FilterModel oldFilterModel = oldWs.getLookup().lookup(FilterModel.class);
            if (oldFilterModel != null) {
                oldFilterModel.removePropertyChangeListener(this);
            }
        }
        updateView();
        peptideLkpResult = newWs.getLookup().lookupResult(NeoPeptideModel.class);
        peptideLkpResult.addLookupListener(this);
        filterLkpResult = newWs.getLookup().lookupResult(FilterModel.class);
        filterLkpResult.addLookupListener(this);
        FilterModel filterModel = newWs.getLookup().lookup(FilterModel.class);
        if (filterModel != null) {
            filterModel.addPropertyChangeListener(this);
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            updateView();
        } else if (le.getSource().equals(filterLkpResult)) {
            Collection<? extends FilterModel> filterModels = filterLkpResult.allInstances();
            if (!filterModels.isEmpty()) {
                FilterModel filterModel = filterModels.iterator().next();
                filterModel.addPropertyChangeListener(this);
                updateView();
            }
        }
    }

    private void updateView() {
        Workspace workspace = Lookup.getDefault().lookup(ProjectManager.class).getCurrentWorkspace();
        FilterModel filterModel = workspace.getLookup().lookup(FilterModel.class);
        NeoPeptideModel peptideModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        if (peptideModel != null) {
            Peptide[] peptides = peptideModel.getPeptides();
            Graph mainGraph = peptideModel.getGraph();
            GraphModel model = mainGraph.getModel();
            GraphView mainView = mainGraph.getView();
            GraphView oldView = model.getVisibleView();

            if (!oldView.isMainView()) {
                model.destroyView(oldView);
            }

            if (filterModel == null || filterModel.isEmpty()) {
                model.setVisibleView(mainView);
            } else {
                GraphView newView = model.createView();
                Subgraph subGraph = model.getGraph(newView);
                NeoPeptide neoPeptide;
                List<Node> neighbors;
                List<Edge> edges;
                for (Peptide p : peptides) {
                    neoPeptide = (NeoPeptide) p;
                    subGraph.addNode(neoPeptide.getGraphNode());
                    neighbors = new LinkedList<>();
                    edges = new LinkedList<>();
                    for (Node neighbor : neoPeptide.getNeighbors()) {
                        if (!subGraph.hasNode(neighbor.getId())) {
                            neighbors.add(neighbor);
                        }
                    }
                    subGraph.addAllNodes(neighbors);
                    for (Edge edge : mainGraph.getEdges(neoPeptide.getGraphNode())) {
                        edges.add(edge);
                    }
                    subGraph.addAllEdges(edges);
                }
                model.setVisibleView(newView);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof FilterModel) {
            updateView();
        }
    }
}
