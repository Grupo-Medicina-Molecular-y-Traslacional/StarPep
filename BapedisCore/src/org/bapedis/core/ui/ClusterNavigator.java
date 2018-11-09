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
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.JButton;
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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.ClusterNavigatorModel;
import org.bapedis.core.model.ClusterNode;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.awt.MouseUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "peptide/clustering", displayName = "#ClusterNavigator.name")
public class ClusterNavigator extends JComponent implements
        WorkspaceEventListener, NavigatorPanelWithToolbar, LookupListener, PropertyChangeListener {

    protected final InstanceContent content;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected final JToolBar toolBar;
    protected final JXTable table;
    protected final JButton findButton, refreshButton;
    protected Lookup.Result<PeptideNode> peptideLkpResult;
    protected Lookup.Result<AttributesModel> attrModelLkpResult;
    protected final JXBusyLabel busyLabel;
    protected final JPanel bottomPanel;
    protected final JLabel clusterSizeLabel, filteredSizeLabel;
    protected AttributesModel currentAttrModel;
    protected ClusterNavigatorModel currentNavModel;
    private final RowSorterListener sorterListener;
    private static final String[] columnNames = {NbBundle.getMessage(ClusterNavigator.class, "ClusterNavigator.table.columnName.first"),
        NbBundle.getMessage(ClusterNavigator.class, "ClusterNavigator.table.columnName.second"),
        NbBundle.getMessage(ClusterNavigator.class, "ClusterNavigator.table.columnName.three")};

    public ClusterNavigator() {
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
        table.addMouseListener(new ClusterPopupAdapter());

        scrollPane.setViewportView(table);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "ClusterNavigator.busyLabel.text"));

        findButton = new JButton(table.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(MetadataNavigator.class, "ClusterNavigator.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        refreshButton = new JButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/refresh.png", false));
        refreshButton.setToolTipText(NbBundle.getMessage(MetadataNavigator.class, "ClusterNavigator.refreshButton.toolTipText"));
        refreshButton.setFocusable(false);
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
                if (sorter != null) {
                    sorter.setRowFilter(null);
                }
            }
        });

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(refreshButton);

        toolBar.addSeparator();
        toolBar.add(findButton);

        // Botton toolbar
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        clusterSizeLabel = new JLabel();
        clusterSizeLabel.setIcon(ImageUtilities.loadImageIcon("/org/bapedis/core/resources/rightArrow.png", false));

        filteredSizeLabel = new JLabel();
        filteredSizeLabel.setIcon(ImageUtilities.loadImageIcon("/org/bapedis/core/resources/rightArrow.png", false));

        bottomPanel.add(clusterSizeLabel);
        bottomPanel.add(filteredSizeLabel);

        add(bottomPanel, BorderLayout.SOUTH);

        sorterListener = new RowSorterListener() {
            @Override
            public void sorterChanged(RowSorterEvent e) {
                clusterSizeLabel.setText(NbBundle.getMessage(MetadataNavigator.class, "ClusterNavigator.filteredSizeLabel.text", table.getRowCount()));
                clusterSizeLabel.setVisible(true);
            }
        };

    }

    private synchronized void tableValueChanged(ListSelectionEvent e) {
        Collection<? extends ClusterNode> oldNodes = lookup.lookupAll(ClusterNode.class);
        for (ClusterNode node : oldNodes) {
            content.remove(node);
        }
        int rowIndex = table.getSelectedRow();
        if (rowIndex != -1) {
            MyTableModel dataModel = (MyTableModel) table.getModel();
            Cluster cluster = dataModel.getClusterAtRow(table.convertRowIndexToModel(rowIndex));
            content.add(new ClusterNode(cluster));
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

    private void removeAttrLookupListener() {
        if (attrModelLkpResult != null) {
            attrModelLkpResult.removeLookupListener(this);
            attrModelLkpResult = null;
        }
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
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

            currentNavModel.removePropertyChangeListener(this);
        }

        attrModelLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        attrModelLkpResult.addLookupListener(this);

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);

        currentAttrModel = pc.getAttributesModel(newWs);
        if (currentAttrModel != null) {
            currentAttrModel.addQuickFilterChangeListener(this);
        }

        currentNavModel = pc.getClusterNavModel(newWs);
        currentNavModel.addPropertyChangeListener(this);

        reload();
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "ClusterNavigator.name");
    }

    @Override
    public String getDisplayHint() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "ClusterNavigator.hint");
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

        if (currentAttrModel != null) {
            currentAttrModel.removeQuickFilterChangeListener(this);
        }

        QueryModel queryModel = pc.getQueryModel();
        queryModel.removePropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel();
        filterModel.removePropertyChangeListener(this);

        currentNavModel.removePropertyChangeListener(this);
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    private void reload() {
        Cluster[] clusters = currentNavModel.getClusters();
        if (clusters != null) {
            List<Peptide> peptides = currentAttrModel.getPeptides();
            setBusyLabel(true);
            SwingWorker worker = new SwingWorker<TableModel, Void>() {
                @Override
                protected TableModel doInBackground() throws Exception {
                    TreeSet<Integer> set = new TreeSet<>();
                    for (Peptide p : peptides) {
                        set.add(p.getId());
                    }

                    List<Cluster> clusterList = new LinkedList<>();
                    boolean flag;
                    for (Cluster c : clusters) {
                        flag = false;
                        for (Peptide p : c.getMembers()) {
                            if (set.contains(p.getId())) {
                                flag = true;
                                break;
                            }
                        }
                        if (flag) {
                            clusterList.add(c);
                        }
                    }

                    TableModel dataModel = new MyTableModel(columnNames, clusterList.toArray(new Cluster[0]));
                    return dataModel;
                }

                @Override
                protected void done() {
                    try {
                        TableModel dataModel = get();
                        setDataModel(dataModel);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                        setDataModel(new MyTableModel(columnNames, new Cluster[0]));
                    } finally {
                        setBusyLabel(false);
                    }
                }

            };
            worker.execute();
        } else {
            setDataModel(new MyTableModel(columnNames, new Cluster[0]));
        }
    }

    private void setDataModel(TableModel dataModel) {
        table.setModel(dataModel);

        TableRowSorter sorter = new TableRowSorter(dataModel);
        sorter.addRowSorterListener(sorterListener);
        table.setRowSorter(sorter);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(attrModelLkpResult)) {
            Collection<? extends AttributesModel> attrModels = attrModelLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                if (currentAttrModel != null) {
                    currentAttrModel.removeQuickFilterChangeListener(this);
                }
                currentAttrModel = attrModels.iterator().next();
                currentAttrModel.addQuickFilterChangeListener(this);
                reload();
            }
        } else if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends PeptideNode> peptideNodes = peptideLkpResult.allInstances();
            if (table.getRowSorter() instanceof TableRowSorter) {
                TableRowSorter sorter = (TableRowSorter) table.getRowSorter();
                if (!peptideNodes.isEmpty() && sorter != null) {
                    Peptide peptide = peptideNodes.iterator().next().getPeptide();
                    if (peptide.hasAttribute(AbstractCluster.CLUSTER_ATTR)) {
                        Integer clusterID = (Integer) peptide.getAttributeValue(AbstractCluster.CLUSTER_ATTR);
                        sorter.setRowFilter(RowFilter.regexFilter("^" + clusterID + "$", 0));
                    }
                }
            }
        }
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
        busyLabel.setBusy(busy);
        for (Component c : toolBar.getComponents()) {
            c.setEnabled(!busy);
        }
        bottomPanel.setVisible(!busy);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentAttrModel)) {
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
        } else if (evt.getSource() instanceof ClusterNavigatorModel) {
            if (evt.getPropertyName().equals(ClusterNavigatorModel.RUNNING)) {
                setBusyLabel(((ClusterNavigatorModel) evt.getSource()).isRunning());
            } else if (evt.getPropertyName().equals(ClusterNavigatorModel.CHANGED_CLUSTER)) {
                reload();
            }
        }
    }

    class ClusterPopupAdapter extends MouseUtils.PopupMouseAdapter {

        public ClusterPopupAdapter() {
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
                MyTableModel dataModel = (MyTableModel) table.getModel();
                Cluster cluster = (Cluster) dataModel.getClusterAtRow(table.convertRowIndexToModel(rowIndex));
                JPopupMenu contextMenu = new JPopupMenu();
//                contextMenu.add(new ShowPropertiesAction(new ClusterNode(cluster)));
//                contextMenu.show(table, evt.getX(), evt.getY());
            } else {
                table.getSelectionModel().clearSelection();
            }
            evt.consume();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    private static class MyTableModel extends AbstractTableModel {

        private final String[] columnNames;
        private final Cluster[] data;

        public MyTableModel(String[] columnNames, Cluster[] data) {
            this.columnNames = columnNames;
            this.data = data;
        }

        @Override
        public int getColumnCount() {
            return columnNames.length;
        }

        @Override
        public int getRowCount() {
            return data.length;
        }

        @Override
        public String getColumnName(int col) {
            return columnNames[col];
        }

        @Override
        public Object getValueAt(int row, int col) {
            switch (col) {
                case 0:
                    return data[row].getId();
                case 1:
                    return data[row].getSize();
                case 2:
                    return data[row].getPercentageComp();
            }
            return null;
        }

        @Override
        public Class getColumnClass(int c) {
            switch (c) {
                case 0:
                    return Integer.class;
                case 1:
                    return Integer.class;
                case 2:
                    return Double.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return false;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
            throw new UnsupportedOperationException("Not supported");
        }

        public Cluster getClusterAtRow(int row) {
            return data[row];
        }
    }
}
