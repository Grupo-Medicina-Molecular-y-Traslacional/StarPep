/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.AllDescriptorsFactory;
import org.jdesktop.swingx.JXTable;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AllDescriptorTable extends JXTable {
    
    public AllDescriptorTable() {
        //Set Model
        setModel(new MyTableModel());

        //Column 0
        TableColumn tc = getColumn(0);
        tc.setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel renderedLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
                renderedLabel.setHorizontalAlignment(SwingConstants.LEFT);
                return renderedLabel;
            }
            
        });
        tc.setPreferredWidth(20);

        // Column 2: CheckBox Header
        tc = getColumn(2);
        tc.setHeaderRenderer(new CheckBoxHeader(getTableHeader(), 2));
        tc.setPreferredWidth(30);
        
        setGridColor(Color.LIGHT_GRAY);
        setColumnControlVisible(false);
        setSortable(true);
        setAutoCreateRowSorter(true);
        setRowSelectionAllowed(false);
        
        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(getModel()) {
            @Override
            public boolean isSortable(int column) {
                return column < 2;
            }
        };
        setRowSorter(sorter);
    }
    
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
        Component c = super.prepareRenderer(renderer, row, column);
        TableModel tableModel = getModel();
        if ((boolean) tableModel.getValueAt(convertRowIndexToModel(row), 2)) {
            c.setForeground(Color.BLUE);
        }
        return c;
    }
    
    public void setup(AllDescriptors algo) {
        ((MyTableModel) getModel()).setup(algo);
    }
    
    private class MyTableModel extends AbstractTableModel {
        
        private AllDescriptors algo;
        private final ArrayList<Object[]> data;
        
        public MyTableModel() {
            AllDescriptors allDescriptors = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
            Set<String> allDescriptorKeys = allDescriptors.getDescriptorKeys();
            this.data = new ArrayList<>(allDescriptorKeys.size());
            int no = 1;
            for (String key : allDescriptorKeys) {
                data.add(new Object[]{no++, key, true});
            }
        }
        
        public void setup(AllDescriptors algo) {
            this.algo = algo;
            if (algo != null) {
                boolean flag;
                for (int row = 0; row < data.size(); row++) {
                    flag = algo.isIncluded((String) data.get(row)[1]);
                    if (((boolean) data.get(row)[2]) != flag) {
                        data.get(row)[2] = flag;
                        fireTableCellUpdated(row, 2);
                    }
                }
            } else {
                for (int row = 0; row < data.size(); row++) {
                    if ((boolean) data.get(row)[2]) {
                        data.get(row)[2] = false;
                        fireTableCellUpdated(row, 2);
                    }
                }
            }
        }
        
        @Override
        public int getColumnCount() {
            return 3;
        }
        
        @Override
        public String getColumnName(int col) {
            switch (col) {
                case 0:
                    return NbBundle.getMessage(AllDescriptorTable.class, "AllDescriptorTable.firstColumn");
                case 1:
                    return NbBundle.getMessage(AllDescriptorTable.class, "AllDescriptorTable.secondColumn");
            }
            return "";
        }
        
        @Override
        public Class getColumnClass(int c) {
            switch (c) {
                case 0:
                    return Integer.class;
                case 1:
                    return String.class;
                case 2:
                    return Boolean.class;
            }
            return null;
        }
        
        @Override
        public int getRowCount() {
            return data.size();
        }
        
        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            return data.get(rowIndex)[columnIndex];
        }
        
        @Override
        public boolean isCellEditable(int row, int col) {
            return col == 2;
        }
        
        @Override
        public void setValueAt(Object value, int row, int col) {
            data.get(row)[col] = value;
            if (algo != null) {
                if ((boolean) value) {
                    algo.includeAlgorithm((String) data.get(row)[1]);
                } else {
                    algo.excludeAlgorithm((String) data.get(row)[1]);
                }
            }
            fireTableCellUpdated(row, col);
        }
        
    }
    
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);        
        if (getColumnCount() > 2 && getColumn(2).getHeaderRenderer() instanceof CheckBoxHeader) {
            CheckBoxHeader header = (CheckBoxHeader) getColumn(2).getHeaderRenderer();
            header.setEnabled(enabled);
        }        
    }
    
}
