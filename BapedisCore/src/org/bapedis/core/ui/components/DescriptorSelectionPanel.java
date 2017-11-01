/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;
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

        MyTableModel tableModel = createTableModel();
        if (tableModel != null) {
            table = new JXTable(tableModel);

            TableColumn tc = table.getColumn(0);
            tc.setCellEditor(table.getDefaultEditor(Boolean.class));
            tc.setCellRenderer(table.getDefaultRenderer(Boolean.class));
            CheckBoxHeader cbHeader = new CheckBoxHeader();
            cbHeader.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    Object source = e.getSource();
                    if (source instanceof AbstractButton == false) {
                        return;
                    }
                    boolean checked = e.getStateChange() == ItemEvent.SELECTED;
                    for (int row = 0; row < table.getRowCount(); row++) {
                        table.setValueAt(checked, row, 0);
                    }
                }
            });
            tc.setHeaderRenderer(cbHeader);

            table.getColumn(1).setPreferredWidth(240);
            table.setHighlighters(HighlighterFactory.createAlternateStriping());
            table.setColumnControlVisible(false);
            table.setSortable(true);
            table.setAutoCreateRowSorter(true);

//                        table.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
//                            @Override
//                            public void valueChanged(ListSelectionEvent e) {
//                                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
//                                deleteButton.setEnabled(!lsm.isSelectionEmpty());
//                            }
//                        });
//                        deleteAllButton.setEnabled(true);
        }
        scrollPane.setViewportView(table);
    }

    private MyTableModel createTableModel() {
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
        return col == 0;
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

class CheckBoxHeader extends JCheckBox
        implements TableCellRenderer, MouseListener {

    protected int column;
    protected boolean mousePressed = false;

    public CheckBoxHeader() {
    }

    @Override
    public Component getTableCellRendererComponent(
            JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        if (table != null) {
            JTableHeader header = table.getTableHeader();
            if (header != null) {
                setForeground(header.getForeground());
                setBackground(header.getBackground());
                setFont(header.getFont());
                header.addMouseListener(this);
            }
        }
        setColumn(column);
        setBorder(UIManager.getBorder("TableHeader.cellBorder"));
        return this;
    }

    protected void setColumn(int column) {
        this.column = column;
    }

    public int getColumn() {
        return column;
    }

    protected void handleClickEvent(MouseEvent e) {
        if (mousePressed) {
            mousePressed = false;
            JTableHeader header = (JTableHeader) (e.getSource());
            JTable tableView = header.getTable();
            TableColumnModel columnModel = tableView.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = tableView.convertColumnIndexToModel(viewColumn);

            if (viewColumn == this.column && e.getClickCount() == 1 && column != -1) {
                doClick();
            }
        }
    }

    public void mouseClicked(MouseEvent e) {
        handleClickEvent(e);
        ((JTableHeader) e.getSource()).repaint();
    }

    public void mousePressed(MouseEvent e) {
        mousePressed = true;
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }
}
