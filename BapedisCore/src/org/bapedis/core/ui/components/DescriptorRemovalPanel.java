/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class DescriptorRemovalPanel extends javax.swing.JPanel {

    protected final AttributesModel attrModel;
    protected JXTable table;
    HashMap<String, List<PeptideAttribute>> map;

    public DescriptorRemovalPanel(final AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;

        SwingWorker sw = new SwingWorker<MyTableModel, Void>() {
            @Override
            protected MyTableModel doInBackground() throws Exception {
                map = new LinkedHashMap<>();
                String key;
                List<PeptideAttribute> list;
                for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
                    PeptideAttribute attr = it.next();
                    if (attr.isMolecularDescriptor() && attr.getOriginAlgorithm() != null) {
                        key = attr.getOriginAlgorithm().getFactory().getName();
                        if (!map.containsKey(key)) {
                            list = new LinkedList<>();
                            map.put(key, list);
                        }
                        list = map.get(key);
                        list.add(attr);
                    }
                }
                return createTableModel();
            }

            @Override
            protected void done() {
                try {
                    MyTableModel tableModel = get();
                    if (tableModel != null) {
                        table = new JXTable(tableModel);
                        table.setHighlighters(HighlighterFactory.createAlternateStriping());
                        table.setColumnControlVisible(false);
                        table.setSortable(true);
                        table.setAutoCreateRowSorter(true);
                        scrollPane.setViewportView(table);
                        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
                            @Override
                            public void valueChanged(ListSelectionEvent e) {
                                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                                deleteButton.setEnabled(!lsm.isSelectionEmpty());
                            }
                        });
                        deleteAllButton.setEnabled(true);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }

        };
        sw.execute();
        deleteButton.setEnabled(false);
        deleteAllButton.setEnabled(false);
    }

    private MyTableModel createTableModel() {
        String[] columnNames = {NbBundle.getMessage(DescriptorRemovalPanel.class, "DescriptorRemovalPanel.columnName.first"),
            NbBundle.getMessage(DescriptorRemovalPanel.class, "DescriptorRemovalPanel.columnName.second")};
        ArrayList<Object[]> data = new ArrayList(map.size());
        Object[] dataRow;
        int row = 0;
        for (Map.Entry<String, List<PeptideAttribute>> entry : map.entrySet()) {
            dataRow = new Object[2];
            dataRow[0] = entry.getKey();
            dataRow[1] = entry.getValue().size();
            data.add(row++, dataRow);
        }
        return map.isEmpty() ? null : new MyTableModel(columnNames, data);
    }

    private void delete() {
        int[] selectedRows = table.getSelectedRows();
        for (int i = 0; i < selectedRows.length; i++) {
            selectedRows[i] = table.convertRowIndexToModel(selectedRows[i]);
        }
        String key;
        List<PeptideAttribute> list;
        for (int i = 0; i < selectedRows.length; i++) {
            key = (String) table.getModel().getValueAt(selectedRows[i], 0);
            list = map.get(key);
            for (PeptideAttribute attribute : list) {
                attrModel.deleteAttribute(attribute);
            }
            map.remove(key);
        }
        ((MyTableModel) table.getModel()).removeRows(selectedRows);
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
        rightPanel = new javax.swing.JPanel();
        deleteButton = new javax.swing.JButton();
        deleteAllButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(440, 380));
        setPreferredSize(new java.awt.Dimension(440, 380));
        setLayout(new java.awt.GridBagLayout());

        scrollPane.setMinimumSize(new java.awt.Dimension(275, 23));
        scrollPane.setPreferredSize(new java.awt.Dimension(275, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 0);
        add(scrollPane, gridBagConstraints);

        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.Y_AXIS));

        org.openide.awt.Mnemonics.setLocalizedText(deleteButton, org.openide.util.NbBundle.getMessage(DescriptorRemovalPanel.class, "DescriptorRemovalPanel.deleteButton.text")); // NOI18N
        deleteButton.setMaximumSize(new java.awt.Dimension(79, 29));
        deleteButton.setMinimumSize(new java.awt.Dimension(79, 29));
        deleteButton.setPreferredSize(new java.awt.Dimension(79, 29));
        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteButtonActionPerformed(evt);
            }
        });
        rightPanel.add(deleteButton);

        org.openide.awt.Mnemonics.setLocalizedText(deleteAllButton, org.openide.util.NbBundle.getMessage(DescriptorRemovalPanel.class, "DescriptorRemovalPanel.deleteAllButton.text")); // NOI18N
        deleteAllButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteAllButtonActionPerformed(evt);
            }
        });
        rightPanel.add(deleteAllButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(25, 5, 0, 5);
        add(rightPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void deleteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteButtonActionPerformed
        delete();
    }//GEN-LAST:event_deleteButtonActionPerformed

    private void deleteAllButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_deleteAllButtonActionPerformed
        if (table.getRowCount() > 0) {
            table.setRowSelectionInterval(0, table.getRowCount() - 1);
            delete();
        }
    }//GEN-LAST:event_deleteAllButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton deleteAllButton;
    private javax.swing.JButton deleteButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}

class MyTableModel extends AbstractTableModel {

    private final String[] columnNames;
    private final ArrayList<Object[]> data;

    public MyTableModel(String[] columnNames, ArrayList<Object[]> data) {
        this.columnNames = columnNames;
        this.data = data;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return data.size();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return data.get(row)[col];
    }

    @Override
    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
        throw new UnsupportedOperationException("The table is not editable");
    }

    public void removeRows(int[] rows) {
        Arrays.sort(rows);
        for (int i = rows.length - 1; i >= 0; i--) {
            this.data.remove(rows[i]);
            fireTableRowsDeleted(rows[i], rows[i]);
        }
    }
}
