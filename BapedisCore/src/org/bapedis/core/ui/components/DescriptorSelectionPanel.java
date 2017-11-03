/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.jdesktop.swingx.JXTable;
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
        this(attrModel, Color.BLUE);
    }

    public void addTableModelListener(TableModelListener listener) {
        table.getModel().addTableModelListener(listener);
    }

    public DescriptorSelectionPanel(final AttributesModel attrModel, final Color fgColor) {
        initComponents();
        this.attrModel = attrModel;

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
        // Column 0
        TableColumn tc = table.getColumn(0);
        tc.setHeaderRenderer(new CheckBoxHeader2(table.getTableHeader(), 0));
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
        ArrayList<Object[]> data = new ArrayList(map.size());
        Object[] dataRow;
        int row = 0;
        for (Map.Entry<String, List<PeptideAttribute>> entry : map.entrySet()) {
            dataRow = new Object[3];
            dataRow[0] = false;
            dataRow[1] = entry.getKey();
            dataRow[2] = entry.getValue().size();
            data.add(row++, dataRow);
        }
        return new MyTableModel(columnNames, data);
    }

    public HashMap<String, List<PeptideAttribute>> getSelectedDescriptor() {
        HashMap<String, List<PeptideAttribute>> newMap = new LinkedHashMap<>();
        TableModel model = table.getModel();
        String key;
        for (int row = 0; row < model.getRowCount(); row++) {
            if ((boolean) model.getValueAt(row, 0)) {
                key = (String) model.getValueAt(row, 1);
                newMap.put(key, map.get(key));
            }
        }
        return newMap;
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
        java.awt.GridBagConstraints gridBagConstraints;

        scrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
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
        fireTableChanged(new TableModelEvent(this, row));
    }

    public void removeRow(int row) {
        this.data.remove(row);
        fireTableRowsDeleted(row, row);
    }
}

class CheckBoxHeader implements TableCellRenderer {

    private final JCheckBox check = new JCheckBox();

    public CheckBoxHeader(JTableHeader header, final int index) {
        // index is the column to be modified
        check.setOpaque(false);
        check.setFont(header.getFont());
        header.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = ((JTableHeader) e.getSource()).getTable();
                TableColumnModel columnModel = table.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int modelColumn = table.convertColumnIndexToModel(viewColumn);
                if (modelColumn == index) {
                    check.setSelected(!check.isSelected());
                    TableModel m = table.getModel();
                    Boolean f = check.isSelected();
                    for (int i = 0; i < m.getRowCount(); i++) {
                        m.setValueAt(f, i, index);
                    }
                    ((JTableHeader) e.getSource()).repaint();
                }
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable tbl, Object val, boolean isS, boolean hasF, int row, int col) {
        TableCellRenderer r = tbl.getTableHeader().getDefaultRenderer();
        JLabel l = (JLabel) r.getTableCellRendererComponent(tbl, val, isS, hasF, row, col);
        l.setIcon(new CheckBoxIcon(check));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    private static class CheckBoxIcon implements Icon {

        private final JCheckBox check;

        public CheckBoxIcon(JCheckBox check) {
            this.check = check;
        }

        @Override
        public int getIconWidth() {
            return check.getPreferredSize().width;
        }

        @Override
        public int getIconHeight() {
            return check.getPreferredSize().height;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            SwingUtilities.paintComponent(
                    g, check, (Container) c, x, y, getIconWidth(), getIconHeight());
        }
    }
}

class CheckBoxHeader2 extends JCheckBox implements TableCellRenderer {

    CheckBoxHeader2(JTableHeader header, final int index) {
        setOpaque(false);
        setFont(header.getFont());
        setHorizontalAlignment(SwingConstants.CENTER);
        setToolTipText("Check all");
        header.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = ((JTableHeader) e.getSource()).getTable();
                TableColumnModel columnModel = table.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int modelColumn = table.convertColumnIndexToModel(viewColumn);
                if (modelColumn == index) {
                    doClick();
//                    setSelected(!isSelected());
                    TableModel m = table.getModel();
                    boolean flag = isSelected();
                    for (int i = 0; i < m.getRowCount(); i++) {
                        m.setValueAt(flag, i, index);
                    }
                    ((JTableHeader) e.getSource()).repaint();
                }
            }
        });
    }

    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JTableHeader header = table.getTableHeader();
        Color bg = header.getBackground();        
        setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));        
        return this;
    }
}
