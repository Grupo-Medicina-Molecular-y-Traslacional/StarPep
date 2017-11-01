/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.services.ProjectManager;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class DescriptorSelectionPanel extends javax.swing.JPanel {

    protected final AttributesModel attrModel;
    protected JXTable table;
    protected HashMap<String, List<PeptideAttribute>> map;

    public DescriptorSelectionPanel(final AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;

        final JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.loading.text"));
        scrollPane.setViewportView(busyLabel);

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
                        setTableModel(tableModel);
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    busyLabel.setBusy(false);
                    scrollPane.setViewportView(table);
                }
            }

        };
        busyLabel.setBusy(true);
        sw.execute();
    }

    public DescriptorSelectionPanel(TableModel model) {
        attrModel = Lookup.getDefault().lookup(ProjectManager.class).getAttributesModel();
        setTableModel(model);
    }

    private void setTableModel(TableModel tableModel) {
        table = new JXTable(tableModel);
        table.getColumn(0).setPreferredWidth(240);
        table.setHighlighters(HighlighterFactory.createAlternateStriping());
        table.setColumnControlVisible(false);
        table.setSortable(true);
        table.setAutoCreateRowSorter(true);

        add(table, BorderLayout.CENTER);
//                        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//                            @Override
//                            public void valueChanged(ListSelectionEvent e) {
//                                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
//                                deleteButton.setEnabled(!lsm.isSelectionEmpty());
//                            }
//                        });
//                        deleteAllButton.setEnabled(true);

    }

    private MyTableModel createTableModel() {
        String[] columnNames = {NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.table.columnName.first"),
            NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.table.columnName.second")};
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

        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}
