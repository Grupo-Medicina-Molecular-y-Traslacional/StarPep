/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.GraphElementAttributeColumn;
import org.bapedis.core.model.GraphElementDataColumn;
import org.bapedis.core.model.GraphEdgeAttributeColumn;
import org.bapedis.core.model.GraphEdgeWrapper;
import org.bapedis.core.model.GraphElementAvailableColumnsModel;
import org.bapedis.core.model.GraphElementNavigatorModel;
import org.bapedis.core.model.GraphElementNode;
import org.bapedis.core.model.GraphElementType;
import org.bapedis.core.model.GraphElementsDataTable;
import org.bapedis.core.model.GraphNodeWrapper;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.actions.CenterNodeOnGraph;
import org.bapedis.core.ui.actions.SelectEdgeOnGraph;
import org.bapedis.core.ui.actions.SelectNodeOnGraph;
import org.bapedis.core.ui.actions.ShowPropertiesAction;
import org.bapedis.core.ui.components.GraphElementAvailableColumnsPanel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.MouseUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "graph/table", displayName = "#GraphElementNavigator.name")
public class GraphElementNavigator extends JComponent implements
        WorkspaceEventListener, PropertyChangeListener, NavigatorPanelWithToolbar {

    protected final JToolBar toolBar, bottomToolbar;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected final InstanceContent content;

    protected final JToggleButton nodesBtn, edgesBtn;
    protected final JButton availableColumnsButton;
    protected final JXBusyLabel busyLabel;
    protected final JXTable table;
    protected final JLabel nodeSizeLabel, edgeSizeLabel;
    protected GraphElementNavigatorModel navigatorModel;
    private final GraphElementDataColumn[] edgeColumns;

    /**
     * Creates new form GraphElementNavigator
     */
    public GraphElementNavigator() {
        initComponents();
        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        table = new JXTable();
        table.setGridColor(Color.LIGHT_GRAY);
        table.setHighlighters(HighlighterFactory.createAlternateStriping());
        table.setColumnControlVisible(false);
        table.setSortable(true);
        table.setAutoCreateRowSorter(true);
        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                tableValueChanged(e);
            }
        });
        table.addMouseListener(new GraphElementPopupAdapter());

        nodesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.node.name"));
        initToogleButton(nodesBtn);

        edgesBtn = new JToggleButton(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.name"));
        initToogleButton(edgesBtn);

        ButtonGroup elementGroup = new ButtonGroup();
        elementGroup.add(nodesBtn);
        elementGroup.add(edgesBtn);

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

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(nodesBtn);
        toolBar.add(edgesBtn);
        toolBar.addSeparator();

        JButton findButton = new JButton(table.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);
        toolBar.add(findButton);

        toolBar.addSeparator();
        availableColumnsButton = new JButton();
        availableColumnsButton.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.availableColumnsButton.text"));
        availableColumnsButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/column.png", false));
        availableColumnsButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.availableColumnsButton.toolTipText"));
        availableColumnsButton.setFocusable(false);
        availableColumnsButton.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                availableColumnsButtonActionPerformed(evt);
            }
        });
        toolBar.add(availableColumnsButton);
        //----------

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.busyLabel.text"));

        pc = Lookup.getDefault().lookup(ProjectManager.class);

        // Botton toolbar
        bottomToolbar = new JToolBar();
        bottomToolbar.setFloatable(false);
        nodeSizeLabel = new JLabel(ImageUtilities.loadImageIcon("/org/bapedis/core/resources/rightArrow.png", false));
        edgeSizeLabel = new JLabel(ImageUtilities.loadImageIcon("/org/bapedis/core/resources/rightArrow.png", false));
        bottomToolbar.add(nodeSizeLabel);
        bottomToolbar.addSeparator();
        bottomToolbar.add(edgeSizeLabel);
        add(bottomToolbar, BorderLayout.SOUTH);

        edgeColumns = new GraphElementDataColumn[]{new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Source),
            null,
            new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Target)};
    }

    private void initToogleButton(JToggleButton btn) {
        btn.setFocusable(false);
        btn.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btn.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
        busyLabel.setBusy(busy);
        for (Component c : toolBar.getComponents()) {
            c.setVisible(!busy);
        }
        bottomToolbar.setVisible(!busy);
    }

    private void nodesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (navigatorModel.getVisualElement() != GraphElementType.Node) {
            navigatorModel.setVisualElement(GraphElementType.Node);
            reload();
            availableColumnsButton.setEnabled(true);
        }

    }

    private void edgesButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (navigatorModel.getVisualElement() != GraphElementType.Edge) {
            navigatorModel.setVisualElement(GraphElementType.Edge);
            reload();
            availableColumnsButton.setEnabled(false);
        }
    }

    private void availableColumnsButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if (navigatorModel.getVisualElement() == GraphElementType.Node) {
            Table columns = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel().getNodeTable();
            GraphElementAvailableColumnsModel nodeAvailableColumnsModel = navigatorModel.getNodeAvailableColumnsModel();
            nodeAvailableColumnsModel.syncronizeTableColumns(columns);
            DialogDescriptor dd = new DialogDescriptor(new GraphElementAvailableColumnsPanel(nodeAvailableColumnsModel), NbBundle.getMessage(GraphElementAvailableColumnsPanel.class, "GraphElementAvailableColumnsPanel.title"));
            dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION});
            DialogDisplayer.getDefault().notify(dd);
            ((GraphElementsDataTable) table.getModel()).resetColumns(nodeAvailableColumnsModel.getAvailableColumns());
        }
    }

    private synchronized void tableValueChanged(ListSelectionEvent e) {
        Collection<? extends GraphElementNode> oldNodes = lookup.lookupAll(GraphElementNode.class);
        for (GraphElementNode node : oldNodes) {
            content.remove(node);
        }
        int[] selectedRows = table.getSelectedRows();
        GraphElementsDataTable dataModel = (GraphElementsDataTable) table.getModel();
        Element element;
        for (int i : selectedRows) {
            element = dataModel.getElementAtRow(table.convertRowIndexToModel(i));
            switch (navigatorModel.getVisualElement()) {
                case Node:
                    content.add(new GraphNodeWrapper((Node) element));
                    break;
                case Edge:
                    content.add(new GraphEdgeWrapper((Edge) element));
                    break;
            }
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
    public synchronized void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            GraphVizSetting oldGraphVizModel = pc.getGraphVizSetting(oldWs);
            oldGraphVizModel.removeGraphViewChangeListener(this);

            QueryModel oldQueryModel = pc.getQueryModel(oldWs);
            oldQueryModel.removePropertyChangeListener(this);

            FilterModel oldFilterModel = pc.getFilterModel(oldWs);
            oldFilterModel.removePropertyChangeListener(this);
        }

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);

        GraphVizSetting graphVizModel = pc.getGraphVizSetting(newWs);
        graphVizModel.addGraphViewChangeListener(this);

        navigatorModel = newWs.getLookup().lookup(GraphElementNavigatorModel.class);
        if (navigatorModel == null) {
            navigatorModel = new GraphElementNavigatorModel();
            newWs.add(navigatorModel);
        }

        switch (navigatorModel.getVisualElement()) {
            case Node:
                nodesBtn.setSelected(true);
                availableColumnsButton.setEnabled(true);
                break;
            case Edge:
                edgesBtn.setSelected(true);
                availableColumnsButton.setEnabled(false);
                break;
        }

        if (queryModel.isRunning() || filterModel.isRunning()) {
            setBusyLabel(true);
        } else {
            setBusyLabel(false);
            reload();
        }
    }

    private void reload() {
        setBusyLabel(true);
        GraphModel graphModel = pc.getGraphModel();
        Table columns = navigatorModel.getVisualElement() == GraphElementType.Node ? graphModel.getNodeTable() : graphModel.getEdgeTable();
        final Graph graph = graphModel.getGraphVisible();
        final GraphElementsDataTable dataModel = navigatorModel.getVisualElement() == GraphElementType.Node ? new GraphElementsDataTable(graph.getNodeCount(), getNodeColumns(columns))
                : new GraphElementsDataTable(graph.getEdgeCount(), getEdgeColumns(columns));
        table.setModel(dataModel);
        nodeSizeLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.nodeSizeLabel.text", graph.getNodeCount()));
        edgeSizeLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edgeSizeLabel.text", graph.getEdgeCount()));

        SwingWorker worker = new SwingWorker<Void, Element>() {
            @Override
            protected Void doInBackground() throws Exception {
                graph.readLock();
                try {
                    switch (navigatorModel.getVisualElement()) {
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
                dataModel.addRow(chunks);
            }

            @Override
            protected void done() {
                try {
                    get();
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
//        if (currentModel != null && currentModel.getMainGView() == AttributesModel.CSN_VIEW) {
//            edgeColumns[1] = new GraphElementAttributeColumn(table.getColumn(ProjectManager.EDGE_TABLE_PRO_SIMILARITY));
//        } else {
        edgeColumns[1] = new GraphElementAttributeColumn(table.getColumn("label"));
//        } 
        return edgeColumns;
    }

    private GraphElementDataColumn[] getNodeColumns(Table table) {
        GraphElementAvailableColumnsModel nodeAvailableColumnsModel = navigatorModel.getNodeAvailableColumnsModel();
        nodeAvailableColumnsModel.syncronizeTableColumns(table);
        return nodeAvailableColumnsModel.getAvailableColumns();
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(GraphVizSetting.CHANGED_GRAPH_VIEW)) {
            reload();
        } else if (evt.getSource() instanceof QueryModel) {
            if (evt.getPropertyName().equals(QueryModel.RUNNING)) {
                setBusyLabel(((QueryModel) evt.getSource()).isRunning());
            }
        } else if (evt.getSource() instanceof FilterModel) {
            if (evt.getPropertyName().equals(FilterModel.RUNNING)) {
                setBusyLabel(((FilterModel) evt.getSource()).isRunning());
            }
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
    public String getDisplayHint() {
        return NbBundle.getMessage(GraphElementNavigator.class,
                "GraphElementNavigator.hint");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void panelActivated(Lookup lkp) {
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void panelDeactivated() {
        pc.removeWorkspaceEventListener(this);
        GraphVizSetting vizModel = pc.getGraphVizSetting();
        vizModel.removeGraphViewChangeListener(this);

        QueryModel queryModel = pc.getQueryModel();
        queryModel.removePropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel();
        filterModel.removePropertyChangeListener(this);

    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    class GraphElementPopupAdapter extends MouseUtils.PopupMouseAdapter {

        public GraphElementPopupAdapter() {
        }

        @Override
        protected void showPopup(MouseEvent evt) {
            int selRow = table.rowAtPoint(evt.getPoint());

            if (selRow != -1) {
                if (!table.getSelectionModel().isSelectedIndex(selRow)) {
                    table.getSelectionModel().clearSelection();
                    table.getSelectionModel().setSelectionInterval(selRow, selRow);
                }
                int rowIndex = table.getSelectedRow();
                GraphElementsDataTable dataModel = (GraphElementsDataTable) table.getModel();
                Element element = dataModel.getElementAtRow(table.convertRowIndexToModel(rowIndex));
                JPopupMenu contextMenu = null;
                if (element instanceof Node) {
                    contextMenu = new JPopupMenu();
                    Node node = (Node) element;
                    contextMenu.add(new SelectNodeOnGraph(node));
                    contextMenu.add(new CenterNodeOnGraph(node));
                    contextMenu.add(new ShowPropertiesAction(new GraphNodeWrapper(node)));
                } else if (element instanceof Edge) {
                    Edge edge = (Edge) element;
                    contextMenu = createContextMenu(edge);
                    contextMenu.add(new ShowPropertiesAction(new GraphEdgeWrapper(edge)));
                }
                if (contextMenu != null) {
                    contextMenu.show(table, evt.getX(), evt.getY());
                }
            } else {
                table.getSelectionModel().clearSelection();
            }
            evt.consume();
        }
    }

    static JPopupMenu createContextMenu(Edge edge) {
        JPopupMenu contextMenu = new JPopupMenu();
        // Add select
        JMenu selectMenu = new JMenu(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.selectOnGraph.name"));

        JMenuItem selectEdge = new JMenuItem(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.name"));
        selectEdge.addActionListener(new SelectEdgeOnGraph(edge));
        selectMenu.add(selectEdge);

        JMenuItem selectSource = new JMenuItem(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.source"));
        selectSource.addActionListener(new SelectNodeOnGraph(edge.getSource()));
        selectMenu.add(selectSource);

        JMenuItem selectTarget = new JMenuItem(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.target"));
        selectTarget.addActionListener(new SelectNodeOnGraph(edge.getTarget()));
        selectMenu.add(selectTarget);
        contextMenu.add(selectMenu);
        // Add center
        JMenu centerMenu = new JMenu(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.centerOnGraph.name"));

        JMenuItem centerSource = new JMenuItem(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.source"));
        centerSource.addActionListener(new CenterNodeOnGraph(edge.getSource()));
        centerMenu.add(centerSource);

        JMenuItem centerTarget = new JMenuItem(NbBundle.getMessage(GraphElementNavigator.class, "GraphElementNavigator.edge.target"));
        centerTarget.addActionListener(new CenterNodeOnGraph(edge.getTarget()));
        centerMenu.add(centerTarget);

        contextMenu.add(centerMenu);

        return contextMenu;
    }
}
