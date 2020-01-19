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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.RowSorterEvent;
import javax.swing.event.RowSorterListener;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.GraphEdgeAttributeColumn;
import org.bapedis.core.model.GraphElementAttributeColumn;
import org.bapedis.core.model.GraphElementDataColumn;
import org.bapedis.core.model.GraphElementsDataTable;
import org.bapedis.core.model.MetadataNavigatorModel;
import org.bapedis.core.model.MetadataNode;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.actions.ShowPropertiesAction;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.awt.MouseUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "peptide/metadata", displayName = "#MetadataNavigator.name")
public class MetadataNavigator extends JComponent implements
        WorkspaceEventListener, NavigatorPanelWithToolbar, LookupListener, PropertyChangeListener {

    protected static final String ALL = NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.choose.all");
    protected final InstanceContent content;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected final JToolBar toolBar;
    protected final JPanel bottomPanel;
    protected final JButton findButton, refreshButton;
    protected final JXTable table;
    protected final JXBusyLabel busyLabel;
    protected final JLabel fetchedSizeLabel, filteredSizeLabel;
    protected Lookup.Result<PeptideNode> peptideLkpResult;
    protected Lookup.Result<AttributesModel> attrModelLkpResult;
    protected AttributesModel currentModel;
    private final RowSorterListener sorterListener;
    private final DefaultComboBoxModel comboBoxModel;
    protected final JComboBox comboBox;
    private MetadataNavigatorModel navigatorModel;
    private final GraphElementDataColumn[] edgeColumns;

    /**
     * Creates new form LibraryPanel
     */
    public MetadataNavigator() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        initComponents();
        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        table = new JXTable();
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
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
        table.addMouseListener(new MetadataPopupAdapter());

        scrollPane.setViewportView(table);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.busyLabel.text"));

        findButton = new JButton(table.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        comboBoxModel = new DefaultComboBoxModel();

        comboBoxModel.addElement(ALL);
        for (StarPepAnnotationType aType : StarPepAnnotationType.values()) {
            comboBoxModel.addElement(aType.getLabelName());
        }

        comboBox = new JComboBox(comboBoxModel);
        comboBox.setSelectedIndex(-1);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });

        refreshButton = new JButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/refresh.png", false));
        refreshButton.setToolTipText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.refreshButton.toolTipText"));
        refreshButton.setFocusable(false);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
                if (sorter != null) {
                    sorter.setRowFilter(null);
                    filteredSizeLabel.setVisible(false);
                }
            }
        });

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(comboBox);
        toolBar.add(refreshButton);

        toolBar.addSeparator();
        toolBar.add(findButton);

        // Botton toolbar
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        fetchedSizeLabel = new JLabel();
        fetchedSizeLabel.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/rightArrow.png", false));

        filteredSizeLabel = new JLabel();
        filteredSizeLabel.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/rightArrow.png", false));

        bottomPanel.add(fetchedSizeLabel);
        bottomPanel.add(filteredSizeLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        sorterListener = new RowSorterListener() {
            @Override
            public void sorterChanged(RowSorterEvent e) {
                filteredSizeLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.filteredSizeLabel.text", table.getRowCount()));
                filteredSizeLabel.setVisible(true);
            }
        };

        edgeColumns = new GraphElementDataColumn[]{new GraphEdgeAttributeColumn(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.labelColumn.source"),
            GraphEdgeAttributeColumn.Direction.Source),
            null,
            new GraphEdgeAttributeColumn(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.labelColumn.target"),
            GraphEdgeAttributeColumn.Direction.Target)};
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
        busyLabel.setBusy(busy);
        for (Component c : toolBar.getComponents()) {
            c.setEnabled(!busy);
        }
        bottomPanel.setVisible(!busy);
    }

    private synchronized void tableValueChanged(ListSelectionEvent e) {
        Collection<? extends MetadataNode> oldNodes = lookup.lookupAll(MetadataNode.class);
        for (MetadataNode node : oldNodes) {
            content.remove(node);
        }
        int rowIndex = table.getSelectedRow();
        if (rowIndex != -1) {
            GraphElementsDataTable dataModel = (GraphElementsDataTable) table.getModel();
            Edge edge = (Edge) dataModel.getElementAtRow(table.convertRowIndexToModel(rowIndex));
            content.add(new MetadataNode(edge));
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
        removeAttrLookupListener();
        if (oldWs != null) {
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeQuickFilterChangeListener(this);
            }

            QueryModel oldQueryModel = pc.getQueryModel(oldWs);
            oldQueryModel.removePropertyChangeListener(this);

            FilterModel oldFilterModel = pc.getFilterModel(oldWs);
            oldFilterModel.removePropertyChangeListener(this);

            comboBox.setSelectedIndex(-1);
        }

        attrModelLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        attrModelLkpResult.addLookupListener(this);

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);

        currentModel = pc.getAttributesModel(newWs);
        if (currentModel != null) {
            currentModel.addQuickFilterChangeListener(this);
        }

        navigatorModel = pc.getMetadataNavModel(newWs);

        comboBox.setSelectedIndex(navigatorModel.getSelectedIndex());
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "MetadataNavigator.name");
    }

    @Override
    public String getDisplayHint() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "MetadataNavigator.hint");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void panelActivated(Lookup lkp) {
        peptideLkpResult = Utilities.actionsGlobalContext().lookupResult(PeptideNode.class);
        peptideLkpResult.addLookupListener(this);

        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void panelDeactivated() {
        peptideLkpResult.removeLookupListener(this);
        removeAttrLookupListener();
        pc.removeWorkspaceEventListener(this);

        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }

        QueryModel queryModel = pc.getQueryModel();
        queryModel.removePropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel();
        filterModel.removePropertyChangeListener(this);

        comboBox.setSelectedIndex(-1);
    }

    private void removeAttrLookupListener() {
        if (attrModelLkpResult != null) {
            attrModelLkpResult.removeLookupListener(this);
            attrModelLkpResult = null;
        }
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;

    }

    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (comboBox.getSelectedIndex() != -1) {
                QueryModel queryModel = pc.getQueryModel();
                FilterModel filterModel = pc.getFilterModel();
                if (queryModel.isRunning() || filterModel.isRunning()) {
                    setBusyLabel(true);
                } else {
                    reload();
                }
                if (navigatorModel.getSelectedIndex() != comboBox.getSelectedIndex()) {
                    navigatorModel.setSelectedIndex(comboBox.getSelectedIndex());
                }
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(attrModelLkpResult)) {
            Collection<? extends AttributesModel> attrModels = attrModelLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                if (currentModel != null) {
                    currentModel.removeQuickFilterChangeListener(this);
                }
                currentModel = attrModels.iterator().next();
                currentModel.addQuickFilterChangeListener(this);
                reload();
            }
        } else if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends PeptideNode> peptideNodes = peptideLkpResult.allInstances();
            if (table.getRowSorter() instanceof TableRowSorter) {
                TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
                if (!peptideNodes.isEmpty() && sorter != null) {
                    Peptide peptide = peptideNodes.iterator().next().getPeptide();
                    sorter.setRowFilter(RowFilter.regexFilter("^" + peptide.getID() + "$", 0));
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)) {
            if (evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
                reload();
            }
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

    private void reload() {
        setBusyLabel(true);

        GraphModel graphModel = pc.getGraphModel();
        Graph graph = graphModel.getGraph();

        StarPepAnnotationType itemAnnotationType = null;
        String item = (String) comboBox.getSelectedItem();
        for (StarPepAnnotationType aType : StarPepAnnotationType.values()) {
            if (aType.getLabelName().equals(item)) {
                itemAnnotationType = aType;
            }
        }

        final StarPepAnnotationType annotationType = itemAnnotationType;
        int relType = annotationType == null ? -1 : graphModel.getEdgeType(annotationType.getRelationType());
        int count = relType == -1 ? graph.getEdgeCount() : graph.getEdgeCount(relType);

        final AttributesModel peptidesModel = currentModel;

        Table edgeTable = graphModel.getEdgeTable();
        edgeColumns[1] = new GraphElementAttributeColumn(edgeTable.getColumn("label"));

        final GraphElementsDataTable dataModel = new GraphElementsDataTable(count, edgeColumns);
        table.setModel(dataModel);

        TableRowSorter sorter = new TableRowSorter(dataModel);
        sorter.addRowSorterListener(sorterListener);
        table.setRowSorter(sorter);

        SwingWorker worker = new SwingWorker<Void, Element>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (peptidesModel != null) {
                    graph.readLock();
                    try {
                        EdgeIterable edgeIterable;
                        Node graphNode;
                        for (Peptide peptide : peptidesModel.getPeptides()) {
                            graphNode = peptide.getGraphNode();
                            edgeIterable = (annotationType == null) || (relType == -1) ? graph.getEdges(graphNode) : graph.getEdges(graphNode, relType);
                            for (Edge edge : edgeIterable) {
                                if (annotationType != null) {
                                    if (edge.getTarget().getLabel().equals(annotationType.getLabelName())) {
                                        publish(edge);
                                    }
                                } else {
                                    publish(edge);
                                }
                            }
                        }

                    } finally {
                        graph.readUnlock();
                    }
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
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    fetchedSizeLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.fetchedSizeLabel.text", table.getRowCount()));
                    filteredSizeLabel.setVisible(false);
                    setBusyLabel(false);
                }
            }
        };
        worker.execute();
    }

    class MetadataPopupAdapter extends MouseUtils.PopupMouseAdapter {

        public MetadataPopupAdapter() {
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
                Edge edge = (Edge) dataModel.getElementAtRow(table.convertRowIndexToModel(rowIndex));
                JPopupMenu contextMenu = GraphElementNavigator.createContextMenu(edge);
                contextMenu.add(new ShowPropertiesAction(new MetadataNode(edge)));
                contextMenu.show(table, evt.getX(), evt.getY());
            } else {
                table.getSelectionModel().clearSelection();
            }
            evt.consume();

//            if (treePath != null) {
//                DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) treePath.getLastPathComponent();
//                DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
//                if (selectedNode != null && !selectedNode.equals(treeNode)) {
//                    tree.getSelectionModel().clearSelection();
//                    tree.getSelectionModel().setSelectionPath(treePath);
//                }
//                if (treeNode.getUserObject() != null) {
//                    QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
//                    Metadata metadata = (Metadata) treeNode.getUserObject();
//                    boolean isAdded = queryModel.contains(metadata);
//                    Action[] actions = new Action[]{new AddToQueryModel(metadata), new RemoveFromQueryModel(metadata)};
//                    JPopupMenu contextMenu = new JPopupMenu();
//                    contextMenu.add(actions[0]);
//                    contextMenu.add(actions[1]);
//                    actions[0].setEnabled(!isAdded);
//                    actions[1].setEnabled(isAdded);
//
//                    if (metadata.getGraphNode() != null) {
//                        contextMenu.addSeparator();
//                        contextMenu.add(new SelectNodeOnGraph(metadata.getGraphNode()));
//                        contextMenu.add(new CenterNodeOnGraph(metadata.getGraphNode()));
//
//                    }
//
//                    contextMenu.show(table, evt.getX(), evt.getY());
//                }
//            }
        }
    }
}
