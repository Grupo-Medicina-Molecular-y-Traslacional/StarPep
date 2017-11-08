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
import javax.swing.JPanel;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTable;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AllDescriptorsPanel extends javax.swing.JPanel implements AlgorithmSetupUI {

    private AllDescriptors algo;
    private final JXHyperlink checkAll, uncheckAll;
    protected JXTable table;

    public AllDescriptorsPanel() {
        initComponents();

        table = new JXTable() {
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                TableModel tableModel = table.getModel();
                if ((boolean) tableModel.getValueAt(table.convertRowIndexToModel(row), 1)) {
                    c.setForeground(Color.BLUE);
                }
                return c;
            }

        };
        table.setGridColor(Color.LIGHT_GRAY);
        table.setRowSelectionAllowed(false);
        table.setTableHeader(null);                

        scrollPane.setViewportView(table);

        checkAll = new JXHyperlink();
        checkAll.setText(NbBundle.getMessage(AllDescriptorsPanel.class, "AllDescriptorsPanel.checkAll.text"));
        checkAll.setToolTipText(org.openide.util.NbBundle.getMessage(AllDescriptorsPanel.class, "AllDescriptorsPanel.checkAll.toolTipText"));
        checkAll.setClickedColor(new java.awt.Color(0, 51, 255));
        checkAll.setFocusPainted(false);
        checkAll.setFocusable(false);
        checkAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        checkAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        checkAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAll(true);
            }
        });
        topPanel.add(checkAll);

        uncheckAll = new JXHyperlink();
        uncheckAll.setText(NbBundle.getMessage(AllDescriptorsPanel.class, "AllDescriptorsPanel.unCheckAll.text"));
        uncheckAll.setToolTipText(org.openide.util.NbBundle.getMessage(AllDescriptorsPanel.class, "AllDescriptorsPanel.unCheckAll.toolTipText"));
        uncheckAll.setClickedColor(new java.awt.Color(0, 51, 255));
        uncheckAll.setFocusPainted(false);
        uncheckAll.setFocusable(false);
        uncheckAll.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        uncheckAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        uncheckAll.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                checkAll(false);
            }
        });
        topPanel.add(uncheckAll);
    }

    private void checkAll(boolean selected) {
        if (algo != null) {
            TableModel model = table.getModel();
            for (int row = 0; row < model.getRowCount(); row++){
                model.setValueAt(selected, row, 1);
            }
        }
    }

    private void setSubset() {
        if (algo != null) {

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

        buttonGroup = new javax.swing.ButtonGroup();
        topPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.GridBagLayout());

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 20, 2);
        flowLayout1.setAlignOnBaseline(true);
        topPanel.setLayout(flowLayout1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 5, 0);
        add(topPanel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(scrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup;
    private javax.swing.JScrollPane scrollPane;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getEditPanel(Algorithm algo) {
        this.algo = (AllDescriptors) algo;
        table.setModel(new MyTableModel());        
//        setSelectedGroupIndex(this.algo.getButtonGroupIndex());

        return this;
    }

//    private void setSelectedGroupIndex(int index) {
//        switch (index) {
//            case 0:
//                selectAllRButton.setSelected(true);
//                break;
//            case 1:
//                removeUselessRButton.setSelected(true);
//                break;
//            case 2:
//                selectRankedRButton.setSelected(true);
//                break;
//        }
//    }

    class MyTableModel extends AbstractTableModel {

        private final ArrayList<Object[]> data;

        public MyTableModel() {
            data = new ArrayList<>();
            if (algo != null) {
                ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
                for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
                    final AlgorithmFactory f = it.next();
                    if (!f.equals(algo.getFactory()) && f.getCategory() == AlgorithmCategory.MolecularDescriptor) {
                        data.add(new Object[]{f.getName(), algo.isIncluded(f.getName())});
                    }
                }
            }
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public Class getColumnClass(int c) {
            switch (c) {
                case 0:
                    return String.class;
                case 1:
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
            return col == 1;
        }

        @Override
        public void setValueAt(Object value, int row, int col) {
            if (algo != null) {
                data.get(row)[col] = value;
                if ((boolean)value){
                    algo.includeAlgorithm((String)data.get(row)[0]);
                }else{
                    algo.excludeAlgorithm((String)data.get(row)[0]);
                }
                fireTableChanged(new TableModelEvent(this, row));
            }
        }

    }
}
