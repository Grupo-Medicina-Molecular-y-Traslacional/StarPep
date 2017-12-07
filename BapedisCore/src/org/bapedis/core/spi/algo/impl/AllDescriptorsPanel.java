/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.core.ui.components.CheckBoxHeader;
import org.jdesktop.swingx.JXTable;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AllDescriptorsPanel extends javax.swing.JPanel implements AlgorithmSetupUI {

    private AllDescriptors algo;
    protected JXTable table;
    private ProjectManager pc;

    public AllDescriptorsPanel() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);

        table = new JXTable(new MyTableModel()) {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                TableModel tableModel = table.getModel();
                if ((boolean) tableModel.getValueAt(table.convertRowIndexToModel(row), 2)) {
                    c.setForeground(Color.BLUE);
                }
                return c;
            }

        };
        //Column 0
        TableColumn tc = table.getColumn(0);
        tc.setCellRenderer(new DefaultTableCellRenderer(){
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel renderedLabel = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); //To change body of generated methods, choose Tools | Templates.
                renderedLabel.setHorizontalAlignment(SwingConstants.LEFT);
                return renderedLabel;
            }
            
        });
        tc.setPreferredWidth(20);

        
        // Column 2: CheckBox Header
        tc = table.getColumn(2);
        tc.setHeaderRenderer(new CheckBoxHeader(table.getTableHeader(), 2));
        tc.setPreferredWidth(30);

        table.setGridColor(Color.LIGHT_GRAY);
        table.setColumnControlVisible(false);
        table.setSortable(true);
        table.setAutoCreateRowSorter(true);
        table.setRowSelectionAllowed(false);

        TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(table.getModel()) {
            @Override
            public boolean isSortable(int column) {
                return column < 2;
            }
        };
        table.setRowSorter(sorter);

        scrollPane.setViewportView(table);
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

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getEditPanel(Algorithm algo) {
        this.algo = (AllDescriptors) algo;
        ((MyTableModel) table.getModel()).refresh();
        return this;
    }

    class MyTableModel extends AbstractTableModel {

        private final ArrayList<Object[]> data;

        public MyTableModel() {
            data = new ArrayList<>();
            String fName = NbBundle.getMessage(AllDescriptorsFactory.class, "AllDescriptors.name");
            int no = 1;
            for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
                final AlgorithmFactory f = it.next();
                if (!f.getName().equals(fName) && f.getCategory() == AlgorithmCategory.MolecularDescriptor) {
                    data.add(new Object[]{no++, f.getName(), false});
                }
            }
        }

        public void refresh() {
            if (algo != null) {
                boolean flag;
                for (int row = 0; row < data.size(); row++) {
                    flag = algo.isIncluded((String) data.get(row)[1]);
                    if (((boolean) data.get(row)[2]) != flag) {
                        data.get(row)[2] = flag;
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
                    return NbBundle.getMessage(AllDescriptorsPanel.class, "AllDescriptorsPanel.table.firstColumn");
                case 1:
                    return NbBundle.getMessage(AllDescriptorsPanel.class, "AllDescriptorsPanel.table.secondColumn");
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
            if (algo != null) {
                data.get(row)[col] = value;
                if ((boolean) value) {
                    algo.includeAlgorithm((String) data.get(row)[1]);
                } else {
                    algo.excludeAlgorithm((String) data.get(row)[1]);
                }
                fireTableCellUpdated(row, col);
            }
        }

    }
}