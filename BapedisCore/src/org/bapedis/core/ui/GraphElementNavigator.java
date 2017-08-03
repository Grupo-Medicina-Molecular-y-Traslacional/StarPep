/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphElementAttributeColumn;
import org.bapedis.core.model.GraphElementDataColumn;
import org.bapedis.core.model.GraphEdgeAttributeColumn;
import org.bapedis.core.model.GraphElementAvailableColumnsModel;
import org.bapedis.core.model.GraphElementType;
import org.bapedis.core.model.GraphElementsDataTable;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.ui.components.AvailableColumnsPanel;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "graph/table", displayName = "#GraphElementNavigator.name")
public class GraphElementNavigator extends JComponent implements ExplorerManager.Provider,
        WorkspaceEventListener, PropertyChangeListener, LookupListener, NavigatorPanelWithToolbar {

    protected final ExplorerManager explorerMgr;
    protected final JToolBar toolBar;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected AttributesModel currentModel;

    protected final JToggleButton nodesBtn, edgesBtn;
    protected GraphElementType type;
    protected final ElementItem[] rootContext;
    protected final JXBusyLabel busyLabel;
    protected final JXTable table;
    private boolean activated;

    private final GraphElementDataColumn sourceColumn = new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Source);
    private final GraphElementDataColumn targetColumn = new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Targe);
    private final GraphElementDataColumn[] edgeColumns = new GraphElementDataColumn[3];

    private final GraphElementAvailableColumnsModel nodeAvailableColumnsModel = new GraphElementAvailableColumnsModel();

    /**
     * Creates new form GraphElementNavigator
     */
    public GraphElementNavigator() {
        initComponents();

        explorerMgr = new ExplorerManager();

        nodesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.node.name"));
        initToogleButton(nodesBtn);

        edgesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.name"));
        initToogleButton(edgesBtn);

        ButtonGroup elementGroup = new ButtonGroup();
        elementGroup.add(nodesBtn);
        elementGroup.add(edgesBtn);

        type = GraphElementType.Node;
        nodesBtn.setSelected(true);
        nodesBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nodesButtonActionPerformed(evt);
            }
        });
        edgesBtn.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                edgesButtonActionPerformed(evt);
            }
        });

        toolBar = new JToolBar();
        toolBar.add(nodesBtn);
        toolBar.add(edgesBtn);
        toolBar.addSeparator();

        JButton availableColumnsButton = new JButton();
        availableColumnsButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/config.png", false));
        availableColumnsButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.availableColumnsButton.toolTipText"));
        availableColumnsButton.setFocusable(false);
        availableColumnsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                availableColumnsButtonActionPerformed(evt);
            }
        });
        toolBar.add(availableColumnsButton);

        lookup = ExplorerUtils.createLookup(explorerMgr, getActionMap());

        rootContext = new ElementItem[]{new ElementItem(GraphElementType.Node), new ElementItem(GraphElementType.Edge)};

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.busyLabel.text"));

        table = new JXTable();
//        table.setHighlighters(HighlighterFactory.createAlternateStriping());
//        table.setColumnControlVisible(false);
//        table.setSortable(true);
//        table.setAutoCreateRowSorter(true);

        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);

        activated = false;
        setBusyLabel(false);
    }

    private void initToogleButton(JToggleButton btn) {
        btn.setFocusable(false);
        btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
    }

    private void nodesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (type != GraphElementType.Node) {
            type = GraphElementType.Node;
            rootContext[type.ordinal()].reload();
        }

    }

    private void edgesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (type != GraphElementType.Edge) {
            type = GraphElementType.Edge;
            rootContext[type.ordinal()].reload();
        }
    }

    private void availableColumnsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (type == GraphElementType.Node) {
            Table columns = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getNodeTable();
            nodeAvailableColumnsModel.syncronizeTableColumns(columns);
            DialogDescriptor dd = new DialogDescriptor(new AvailableColumnsPanel(nodeAvailableColumnsModel), NbBundle.getMessage(AvailableColumnsPanel.class, "AvailableColumnsPanel.title"));
            dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
            DialogDisplayer.getDefault().notify(dd);
            ((GraphElementsDataTable)table.getModel()).resetColumns(nodeAvailableColumnsModel.getAvailableColumns());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeQuickFilterChangeListener(this);

            }
        }
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class
        );
        peptideLkpResult.addLookupListener(this);

        AttributesModel peptidesModel = pc.getAttributesModel(newWs);
        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }
        this.currentModel = peptidesModel;
        if (currentModel != null) {
            currentModel.addQuickFilterChangeListener(this);
        }
        setDirtyData();
    }

    private void setDirtyData() {
        rootContext[0].setDirty(true);
        rootContext[1].setDirty(true);
        if (activated) {
            rootContext[type.ordinal()].reload();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)
                && evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
            setDirtyData();
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            setDirtyData();
        }
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;
    }

    @Override
    public String
            getDisplayName() {
        return NbBundle.getMessage(GraphElementNavigator.class,
                "GraphElementNavigator.name");
    }

    @Override
    public String
            getDisplayHint() {
        return NbBundle.getMessage(GraphElementNavigator.class,
                "GraphElementNavigator.hint");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    @Override
    public void panelActivated(Lookup lkp) {
        activated = true;
        if (rootContext[type.ordinal()].isDirty()) {
            rootContext[type.ordinal()].reload();
        }
//        explorerMgr.setRootContext(rootContext[type.ordinal()].rootContext);
    }

    @Override
    public void panelDeactivated() {
        activated = false;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    private class ElementItem {

        private final GraphElementType type;
        private boolean dirty;

        public ElementItem(GraphElementType type) {
            this.type = type;
            dirty = false;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public void reload() {
            setBusyLabel(true);
            GraphModel graphModel = pc.getGraphModel();
            Table columns = type == GraphElementType.Node ? graphModel.getNodeTable() : graphModel.getEdgeTable();
            GraphView view = graphModel.getVisibleView();
            final Graph graph = graphModel.getGraph(view);
            final GraphElementsDataTable dataModel = type == GraphElementType.Node ? new GraphElementsDataTable(graph.getNodeCount(), getNodeColumns(columns))
                    : new GraphElementsDataTable(graph.getEdgeCount(), getEdgeColumns(columns));
            table.setModel(dataModel);

            SwingWorker worker = new SwingWorker<Void, Element>() {
                @Override
                protected Void doInBackground() throws Exception {
                    graph.readLock();
                    try {
                        switch (type) {
                            case Node:
                                for (Node node : graph.getNodes()) {
                                    publish(node);
                                }
                                break;
                            case Edge:
                                for (Edge edge : graph.getEdges()) {
                                    publish(edge);
                                }
                                break;
                        }
                    } finally {
                        graph.readUnlock();
                    }
                    return null;
                }

                @Override
                protected void process(List<Element> chunks) {
                    for (Element element : chunks) {
                        dataModel.addRow(element);
                    }
                }

                @Override
                protected void done() {
                    try {
                        get();
                        dirty = false;
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        setBusyLabel(false);
                    }
                }
            };
            worker.execute();
        }

        private GraphElementDataColumn[] getEdgeColumns(Table table) {
            edgeColumns[0] = sourceColumn;
            edgeColumns[1] = new GraphElementAttributeColumn(table.getColumn("label"));
            edgeColumns[2] = targetColumn;
            return edgeColumns;
        }

        private GraphElementDataColumn[] getNodeColumns(Table table) {
            nodeAvailableColumnsModel.syncronizeTableColumns(table);
            return nodeAvailableColumnsModel.getAvailableColumns();
        }

    }
}
