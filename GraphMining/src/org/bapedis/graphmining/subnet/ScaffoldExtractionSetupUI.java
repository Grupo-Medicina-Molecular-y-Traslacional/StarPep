/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.subnet;

import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.bapedis.graphmining.centrality.BetweenessCentrality;
import org.bapedis.graphmining.centrality.HubBridgeCentrality;
import org.bapedis.graphmining.centrality.WeightedDegree;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Table;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ScaffoldExtractionSetupUI extends javax.swing.JPanel implements AlgorithmSetupUI {

    private static String[] centralityColumn = new String[]{
        NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.centrality.text"),
        BetweenessCentrality.BETWEENNESS,
        HubBridgeCentrality.HUB_BRIDGE,
        WeightedDegree.WINDEGREE,
        WeightedDegree.WOUTDEGREE,
        WeightedDegree.WDEGREE,};
    private ScaffoldExtraction scaffoldAlg;
    private final DefaultComboBoxModel comboboxModel;
    private JPanel settingPanel;

    /**
     * Creates new form ScaffoldNetworkConstructionSetupUI
     */
    public ScaffoldExtractionSetupUI() {
        initComponents();
        comboboxModel = (DefaultComboBoxModel) jCentralityComboBox.getModel();
        comboboxModel.addElement(centralityColumn[0]);
    }

    @Override
    public void setEnabled(boolean enabled) {
        jLabel1.setEnabled(enabled);
        jCentralityComboBox.setEnabled(enabled);
        settingPanel.setEnabled(enabled && comboboxModel.getSelectedItem() != centralityColumn[0]);
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

        jLabel1 = new javax.swing.JLabel();
        jCentralityComboBox = new javax.swing.JComboBox<>();
        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ScaffoldExtractionSetupUI.class, "ScaffoldExtractionSetupUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel1, gridBagConstraints);

        jCentralityComboBox.setFocusable(false);
        jCentralityComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCentralityComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jCentralityComboBox, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jCentralityComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCentralityComboBoxActionPerformed
        if (comboboxModel.getSelectedItem() instanceof Column) {
            if (scaffoldAlg != null) {
                scaffoldAlg.setColumn((Column) comboboxModel.getSelectedItem());
                settingPanel.setEnabled(true);
            }
        } else if (scaffoldAlg != null) {
            scaffoldAlg.setColumn(null);
            settingPanel.setEnabled(false);
        }
    }//GEN-LAST:event_jCentralityComboBoxActionPerformed

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        scaffoldAlg = (ScaffoldExtraction) algo;

        //Stup center panel
        settingPanel = new SequenceAlignmentPanel(scaffoldAlg.getAlignmentModel());
        centerPanel.removeAll();
        centerPanel.add(settingPanel, BorderLayout.CENTER);
        centerPanel.revalidate();
        centerPanel.repaint();

        //Setup centrality measures
        Table nodeTable = ScaffoldExtraction.pc.getGraphModel().getNodeTable();
        Column column;
        boolean added;
        for (int i = 1; i < centralityColumn.length; i++) {
            if (nodeTable.hasColumn(centralityColumn[i])) {
                column = nodeTable.getColumn(centralityColumn[i]);
                added = false;
                for (int j = 0; j < comboboxModel.getSize(); j++) {
                    if (comboboxModel.getElementAt(j).equals(column)) {
                        added = true;
                    }
                }
                if (!added) {
                    comboboxModel.addElement(column);
                }
            }
        }
        column = scaffoldAlg.getColumn();
        if (column == null) {
            comboboxModel.setSelectedItem(centralityColumn[0]);
            settingPanel.setEnabled(false);
        } else {
            comboboxModel.setSelectedItem(column);
            settingPanel.setEnabled(true);
        }
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox<String> jCentralityComboBox;
    private javax.swing.JLabel jLabel1;
    // End of variables declaration//GEN-END:variables
}
