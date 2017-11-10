/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.jdesktop.swingx.JXTable;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class DescriptorSelectionPanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected final AttributesModel attrModel;
    protected JXTable table;

    public DescriptorSelectionPanel(final AttributesModel attrModel) {
        this(attrModel, Color.BLUE);
    }

    public DescriptorSelectionPanel(final AttributesModel attrModel, final Color fgColor) {
        initComponents();
        this.attrModel = attrModel;

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                attrModel.addMolecularDescriptorChangeListener(DescriptorSelectionPanel.this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                attrModel.removeMolecularDescriptorChangeListener(DescriptorSelectionPanel.this);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        final TableModel tableModel = createTableModel();
        table = new JXTable(tableModel) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column); //To change body of generated methods, choose Tools | Templates.
                if ((boolean) tableModel.getValueAt(table.convertRowIndexToModel(row), 0)) {
                    c.setForeground(fgColor);
                }
                return c;
            }

        };
        table.setGridColor(Color.LIGHT_GRAY);
        // Column 0: CheckBox Header
        TableColumn tc = table.getColumn(0);
        tc.setHeaderRenderer(new CheckBoxHeader(table.getTableHeader(), 0));
        tc.setPreferredWidth(30);

        // Column 1
        tc = table.getColumn(1);
        tc.setPreferredWidth(210);

        table.setColumnControlVisible(false);
        table.setSortable(true);
        table.setAutoCreateRowSorter(true);
        table.setRowSelectionAllowed(false);

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel()) {
            @Override
            public boolean isSortable(int column) {
                return column > 0;
            }
        };
        table.setRowSorter(sorter);
        scrollPane.setViewportView(table);
    }

    private TableModel createTableModel() {
        String[] columnNames = {"", NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.table.columnName.first"),
            NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.table.columnName.second")};

        HashMap<String, MolecularDescriptor[]> map = attrModel.getAllMolecularDescriptors();
        ArrayList<Object[]> data = new ArrayList(map.size());
        Object[] dataRow;
        for (Map.Entry<String, MolecularDescriptor[]> entry : map.entrySet()) {
            dataRow = new Object[3];
            dataRow[0] = false;
            dataRow[1] = entry.getKey();
            dataRow[2] = entry.getValue().length;
            data.add(dataRow);
        }
        return new MyTableModel(columnNames, data);
    }

    public void addTableModelListener(TableModelListener listener) {
        table.getModel().addTableModelListener(listener);
    }

    public void setSelectedDescriptorKeys(Set<String> keys) {
        TableModel model = table.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            model.setValueAt(keys.contains((String) model.getValueAt(row, 1)), row, 0);
        }
    }

    public Set<String> getSelectedDescriptorKeys() {
        Set<String> keys = new LinkedHashSet<>();
        TableModel model = table.getModel();
        String key;
        for (int row = 0; row < model.getRowCount(); row++) {
            if ((boolean) model.getValueAt(row, 0)) {
                key = (String) model.getValueAt(row, 1);
                keys.add(key);
            }
        }
        return keys;
    }

    public void removeDescriptorRow(String key) {
        MyTableModel model = (MyTableModel) table.getModel();
        for (int row = 0; row < model.getRowCount(); row++) {
            if (model.getValueAt(row, 1).equals(key)) {
                model.removeRow(row);
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

        setPreferredSize(new java.awt.Dimension(440, 380));
        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AttributesModel.MD_ATTR_ADDED)) {
            if (evt.getNewValue() != null) {
                String category = (String) evt.getNewValue();
                ((MyTableModel) table.getModel()).addRow(false, category, attrModel.getMolecularDescriptors(category).length);
            }
        } else if (evt.getPropertyName().equals(AttributesModel.MD_ATTR_REMOVED)) {
            if (evt.getOldValue() != null) {
                String category = (String) evt.getOldValue();
                removeDescriptorRow(category);
            }
        }

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
            switch (c) {
                case 0:
                    return Boolean.class;
                case 1:
                    return String.class;
                case 2:
                    return Integer.class;
            }
            return null;
        }

        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 0;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            data.get(row)[col] = value;
            fireTableCellUpdated(row, col);
        }

        public void addRow(boolean flag, String category, int size) {
            Object[] dataRow = new Object[3];
            dataRow[0] = flag;
            dataRow[1] = category;
            dataRow[2] = size;
            data.add(dataRow);
            int row = data.size() - 1;
            fireTableRowsInserted(row, row);
        }

        public void removeRow(int row) {
            this.data.remove(row);
            fireTableRowsDeleted(row, row);
        }
    }
}
