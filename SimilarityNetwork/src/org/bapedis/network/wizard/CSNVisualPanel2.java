/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.wizard;

import java.awt.Color;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Set;
import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import org.bapedis.core.spi.algo.impl.AllDescriptors;
import org.bapedis.core.ui.components.AllDescriptorTable;
import org.bapedis.network.impl.CSNAlgorithm;
import org.jdesktop.swingx.JXTable;
import org.openide.util.NbBundle;

public final class CSNVisualPanel2 extends JPanel {

    private final CSNAlgorithm csnAlgo;
    private final AllDescriptorTable table;

    /**
     * Creates new form CSNVisualPanel2
     */
    public CSNVisualPanel2(CSNAlgorithm csnAlgo) {
        this.csnAlgo = csnAlgo;
        initComponents();

        // Descriptor Table        
        table = new AllDescriptorTable();
        jScrollPane.setViewportView(table);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CSNVisualPanel2.class, "CSNVisualPanel2.name");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jScrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables

}
