/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JToolBar;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.chemspace.actions.CopyClusterToWorkspace;
import org.bapedis.chemspace.actions.RemoveCluster;
import org.bapedis.chemspace.actions.RemoveOtherClusters;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.awt.MouseUtils;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class ClusterPanel extends JComponent implements PropertyChangeListener {

    protected final ProjectManager pc;

    protected final JToolBar toolBar;
    protected final JXTable table;
    protected final JButton findButton, refreshButton, scatter3DButton;
    protected final JXBusyLabel busyLabel;
    private static final String[] columnNames = {NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.table.columnName.first"),
        NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.table.columnName.second"),
        NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.table.columnName.three")};

    protected MapperAlgorithm csMapper;
    protected AbstractCluster clusteringAlg;

    public ClusterPanel() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        initComponents();

        table = new JXTable();
//        table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
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
        busyLabel.setText(NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.busyLabel.text"));

        findButton = new JButton(table.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/search.png", false));
        findButton.setFocusable(false);

        refreshButton = new JButton(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/refresh.png", false));
        refreshButton.setToolTipText(NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.refreshButton.toolTipText"));
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

        scatter3DButton = new JButton(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/coordinates.png", false));
        scatter3DButton.setToolTipText(NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.scatter3DButton.toolTipText"));
        scatter3DButton.setFocusable(false);
        scatter3DButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        toolBar.add(refreshButton);

        toolBar.add(scatter3DButton);

        toolBar.addSeparator();
        List<? extends Action> actions = Utilities.actionsForPath("Actions/EditCluster");
        for (Action action : actions) {
            toolBar.add(action);
        }

        toolBar.addSeparator();
        toolBar.add(findButton);

        topPanel.add(toolBar);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.addRunningListener(ClusterPanel.this);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.removeRunningListener(ClusterPanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    public void setUp(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        this.clusteringAlg = csMapper.getClusteringAlg();
        setClusters();
    }

    private synchronized void tableValueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            ListSelectionModel lsm = (ListSelectionModel) e.getSource();

            if (!lsm.isSelectionEmpty()) {
                // Find out which indexes are selected.
                int minIndex = lsm.getMinSelectionIndex();
                int maxIndex = lsm.getMaxSelectionIndex();
                for (int i = minIndex; i <= maxIndex; i++) {
                    if (lsm.isSelectedIndex(i)) {
                        MyTableModel dataModel = (MyTableModel) table.getModel();
                        Cluster cluster = dataModel.getClusterAtRow(table.convertRowIndexToModel(i));
                    }
                }
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
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = new javax.swing.JScrollPane();
        topPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(scrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(topPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void setClusters() {
        if (clusteringAlg != null && clusteringAlg.getClusters() != null) {
            AttributesModel attrModel = pc.getAttributesModel();
            List<Peptide> peptides = attrModel.getPeptides();
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
                    for (Cluster c : clusteringAlg.getClusters()) {
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
                        table.setModel(dataModel);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                        table.setModel(new MyTableModel(columnNames, new Cluster[0]));
                    } finally {
                        setBusyLabel(false);
                    }
                }

            };
            worker.execute();
        } else {
            table.setModel(new MyTableModel(columnNames, new Cluster[0]));
        }
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : table);
        busyLabel.setBusy(busy);
        for (Component c : toolBar.getComponents()) {
            c.setEnabled(!busy);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
//        if (evt.getSource().equals(currentAttrModel)) {
//            if (evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
//                reload();
//            }
//        } else if (evt.getSource() instanceof QueryModel) {
//            if (evt.getPropertyName().equals(QueryModel.RUNNING)) {
//                setBusyLabel(((QueryModel) evt.getSource()).isRunning());
//            }
//        } else if (evt.getSource() instanceof FilterModel) {
//            if (evt.getPropertyName().equals(FilterModel.RUNNING)) {
//                setBusyLabel(((FilterModel) evt.getSource()).isRunning());
//            }
//        } else if (evt.getSource() instanceof ClusterNavigatorModel) {
//            if (evt.getPropertyName().equals(ClusterNavigatorModel.RUNNING)) {
//                setBusyLabel(((ClusterNavigatorModel) evt.getSource()).isRunning());
//            } else if (evt.getPropertyName().equals(ClusterNavigatorModel.CHANGED_CLUSTER)) {
//                reload();
//            }
//        }
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
                Cluster cluster = dataModel.getClusterAtRow(table.convertRowIndexToModel(rowIndex));

                JPopupMenu contextMenu = new JPopupMenu();
                contextMenu.add(new RemoveCluster(cluster));
                contextMenu.add(new RemoveOtherClusters());

                JMenuItem subMenu = new JMenu(NbBundle.getMessage(ClusterPanel.class, "ClusterPanel.copyCluster.name"));
                subMenu.add(new JMenuItem(new CopyClusterToWorkspace(null, cluster)));
                Workspace currWs = pc.getCurrentWorkspace();
                Workspace otherWs;
                for (Iterator<? extends Workspace> it = pc.getWorkspaceIterator(); it.hasNext();) {
                    otherWs = it.next();
                    if (currWs != otherWs) {
                        subMenu.add(new JMenuItem(new CopyClusterToWorkspace(otherWs, cluster)));
                    }
                }
                contextMenu.add(subMenu);
                contextMenu.show(table, evt.getX(), evt.getY());
            } else {
                table.getSelectionModel().clearSelection();
            }
            evt.consume();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel topPanel;
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
                    return data[row].getPercentage();
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
