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
import java.awt.event.ItemEvent;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.GraphEdgeAttributeColumn;
import org.bapedis.core.model.GraphElementAttributeColumn;
import org.bapedis.core.model.GraphElementDataColumn;
import org.bapedis.core.model.GraphElementsDataTable;
import org.bapedis.core.model.MetadataNavigatorModel;
import org.bapedis.core.model.MetadataNode;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
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
        WorkspaceEventListener, NavigatorPanelWithToolbar, LookupListener {

    protected final InstanceContent content;
    private final DefaultComboBoxModel comboBoxModel;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected final JToolBar toolBar, bottomToolbar;
    protected final JButton findButton;
    protected final JComboBox comboBox;
    protected final JXTable table;
    protected final JXBusyLabel busyLabel;
    protected final JLabel metadataSizeLabel;
    private MetadataNavigatorModel navigatorModel;
    protected Lookup.Result<PeptideNode> lkpResult;

    /**
     * Creates new form LibraryPanel
     */
    public MetadataNavigator() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        initComponents();
        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        lkpResult = Utilities.actionsGlobalContext().lookupResult(PeptideNode.class);
        lkpResult.addLookupListener(this);

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

        scrollPane.setViewportView(table);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(GraphElementNavigator.class, "MetadataNavigator.busyLabel.text"));

        findButton = new JButton(table.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(GraphElementNavigator.class, "MetadataNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        comboBoxModel = new DefaultComboBoxModel();

        comboBoxModel.addElement(new AnnotationItem(null));
        for (AnnotationType aType : AnnotationType.values()) {
            comboBoxModel.addElement(new AnnotationItem(aType));
        }

        comboBox = new JComboBox(comboBoxModel);
        comboBox.setSelectedIndex(-1);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });

        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(comboBox);
        toolBar.addSeparator();
        toolBar.add(findButton);

        // Botton toolbar
        bottomToolbar = new JToolBar();
        bottomToolbar.setFloatable(false);
        metadataSizeLabel = new JLabel();
        bottomToolbar.add(metadataSizeLabel);
        add(bottomToolbar, BorderLayout.SOUTH);
    }

    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (comboBox.getSelectedItem() instanceof AnnotationItem) {
                AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
                findButton.setEnabled(true);
                item.reload();
                if (navigatorModel.getSelectedIndex() != comboBox.getSelectedIndex()) {
                    navigatorModel.setSelectedIndex(comboBox.getSelectedIndex());
                }
            } else {
                table.setModel(null);
                findButton.setEnabled(false);
                metadataSizeLabel.setText("");
            }
        }
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
        for (Component c : toolBar.getComponents()) {
            c.setEnabled(!busy);
        }
        bottomToolbar.setVisible(!busy);
    }

    private void tableValueChanged(ListSelectionEvent e) {
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
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        navigatorModel = newWs.getLookup().lookup(MetadataNavigatorModel.class);
        if (navigatorModel == null) {
            navigatorModel = new MetadataNavigatorModel();
            newWs.add(navigatorModel);
        }

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
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void panelDeactivated() {
        pc.removeWorkspaceEventListener(this);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;

    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(lkpResult)) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            TableRowSorter sorter = item.getRowSorter();
            if (sorter != null) {
                Collection<? extends PeptideNode> peptideNodes = lkpResult.allInstances();
                if (!peptideNodes.isEmpty()) {
                    Peptide peptide = peptideNodes.iterator().next().getPeptide();
                    sorter.setRowFilter(RowFilter.regexFilter("^" + peptide.getId() +"$", 0));
                }
            }
        }
    }

    private class AnnotationItem {

        private final GraphElementDataColumn[] columns;
        private final AnnotationType annotationType;
        private TableRowSorter sorter;

        public AnnotationItem(AnnotationType annotationType) {
            this.annotationType = annotationType;
            GraphModel graphModel = pc.getGraphModel();
            columns = new GraphElementDataColumn[]{new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Source),
                new GraphElementAttributeColumn(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.labelColumn.name"), 
                                                graphModel.getEdgeTable().getColumn("label")),
                new GraphEdgeAttributeColumn(GraphEdgeAttributeColumn.Direction.Targe)};

        }

        public void reload() {
            setBusyLabel(true);
            GraphModel graphModel = pc.getGraphModel();
            Graph graph = graphModel.getGraph();

            int relType = annotationType == null ? -1 : graphModel.getEdgeType(annotationType.getRelationType());
            int count = relType == -1 ? graph.getEdgeCount() : graph.getEdgeCount(relType);

            final AttributesModel peptidesModel = pc.getAttributesModel();
            final GraphElementsDataTable dataModel = new GraphElementsDataTable(count, columns);
            table.setModel(dataModel);
            sorter = new TableRowSorter(dataModel);
            table.setRowSorter(sorter);

            SwingWorker worker = new SwingWorker<Void, Element>() {
                @Override
                protected Void doInBackground() throws Exception {
                    graph.readLock();
                    try {
                        EdgeIterable edgeIterable;
                        Node graphNode;
                        for (Peptide peptide : peptidesModel.getPeptides()) {
                            graphNode = peptide.getGraphNode();
                            edgeIterable = (annotationType == null) ? graph.getEdges(graphNode) : graph.getEdges(graphNode, relType);;
                            for (Edge edge : edgeIterable) {
                                publish(edge);
                            }
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
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        metadataSizeLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.metadataSizeLabel.text", dataModel.getRowCount()));
                        setBusyLabel(false);
                    }
                }
            };
            worker.execute();

        }

        public AnnotationType getAnnotationType() {
            return annotationType;
        }

        public TableRowSorter getRowSorter() {
            return sorter;
        }

        @Override
        public String toString() {
            if (annotationType != null) {
                return annotationType.getDisplayName();
            }
            return NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.choose.text");
        }

    }

//    class MetadataPopupAdapter extends MouseUtils.PopupMouseAdapter {
//        
//        public MetadataPopupAdapter() {
//        }
//
//        @Override
//        protected void showPopup(MouseEvent evt) {
//            int selRow = table.rowAtPoint(evt.getPoint());
//            
//            if (selRow != -1){
//                if (!table.getSelectionModel().isSelectedIndex(selRow)) {
//                    table.getSelectionModel().clearSelection();
//                    table.getSelectionModel().setSelectionInterval(selRow, selRow);
//                }
//                int rowIndex = table.getSelectedRow();
//                GraphElementsDataTable dataModel = (GraphElementsDataTable) table.getModel();
//                String metadataName = (String)dataModel.getValueAt(table.convertRowIndexToModel(rowIndex), 2); 
//                
//            }
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
//            evt.consume();
//        }
//    }
}
