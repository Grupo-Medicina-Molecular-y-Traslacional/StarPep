/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.wizard;

import javax.swing.JPanel;
import org.bapedis.core.ui.components.AllDescriptorTable;
import org.bapedis.network.impl.CSNAlgorithm;
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
        jInfoLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(640, 480));
        setLayout(new java.awt.GridBagLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(jScrollPane, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jInfoLabel, org.openide.util.NbBundle.getMessage(CSNVisualPanel2.class, "CSNVisualPanel2.jInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jInfoLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jInfoLabel;
    private javax.swing.JScrollPane jScrollPane;
    // End of variables declaration//GEN-END:variables

}