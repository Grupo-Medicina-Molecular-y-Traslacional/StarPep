/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.Set;
import javax.swing.JPanel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.core.ui.components.DescriptorSelectionPanel;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetworkPanel extends javax.swing.JPanel implements AlgorithmSetupUI {

    protected final ProjectManager pc;
    protected ChemicalSpaceNetwork csnAlgo;
    protected final JXHyperlink select;
    protected final ThresholdRangePanel thresholdPanel;

    public ChemicalSpaceNetworkPanel() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);

        select = new JXHyperlink();
        select.setText(NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.select.text"));
        select.setToolTipText(NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.select.toolTipText"));
//        select.setClickedColor(new java.awt.Color(0, 51, 255));
        select.setFocusPainted(false);
        select.setFocusable(false);
        select.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        select.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        select.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setMolecularDescriptors();
            }
        });

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = GridBagConstraints.WEST;
        gridBagConstraints.insets = new Insets(0, 2, 0, 0);
        optionPanel.add(select, gridBagConstraints);      

        thresholdPanel = new ThresholdRangePanel();
        southPanel.add(thresholdPanel, BorderLayout.CENTER);
    }

    private void setMolecularDescriptors() {
        AttributesModel attrModel = pc.getAttributesModel();
        if (attrModel != null && csnAlgo != null) {
            DescriptorSelectionPanel selectionPanel = new DescriptorSelectionPanel(attrModel);
            Set<String> descriptorKeys = csnAlgo.getDescriptorKeys();
            selectionPanel.setSelectedDescriptorKeys(descriptorKeys);

            DialogDescriptor dd = new DialogDescriptor(selectionPanel, NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.DescriptorSelectionPanel.title"));
            dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION});
            if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
                Set<String> selected = selectionPanel.getSelectedDescriptorKeys();
                descriptorKeys.clear();
                descriptorKeys.addAll(selected);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        descriptorsPanel = new javax.swing.JPanel();
        optionPanel = new javax.swing.JPanel();
        option1RadioButton = new javax.swing.JRadioButton();
        option2RadioButton = new javax.swing.JRadioButton();
        jLabel1 = new javax.swing.JLabel();
        metricComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        normComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        southPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        descriptorsPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.descriptorsPanel.border.title"))); // NOI18N
        descriptorsPanel.setMinimumSize(new java.awt.Dimension(442, 137));
        descriptorsPanel.setPreferredSize(new java.awt.Dimension(442, 137));
        descriptorsPanel.setLayout(new java.awt.GridBagLayout());

        optionPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(option1RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(option1RadioButton, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.option1RadioButton.text")); // NOI18N
        option1RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option1RadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        optionPanel.add(option1RadioButton, gridBagConstraints);

        buttonGroup1.add(option2RadioButton);
        org.openide.awt.Mnemonics.setLocalizedText(option2RadioButton, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.option2RadioButton.text")); // NOI18N
        option2RadioButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                option2RadioButtonItemStateChanged(evt);
            }
        });
        option2RadioButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option2RadioButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        optionPanel.add(option2RadioButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 2, 0);
        descriptorsPanel.add(optionPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        descriptorsPanel.add(jLabel1, gridBagConstraints);

        metricComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tanimoto Coefficient" }));
        metricComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                metricComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        descriptorsPanel.add(metricComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        descriptorsPanel.add(jLabel3, gridBagConstraints);

        normComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Z-score", "Min-max" }));
        normComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                normComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        descriptorsPanel.add(normComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(ChemicalSpaceNetworkPanel.class, "ChemicalSpaceNetworkPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.VERTICAL;
        gridBagConstraints.weighty = 1.0;
        descriptorsPanel.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(descriptorsPanel, gridBagConstraints);

        southPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(southPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void option2RadioButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option2RadioButtonItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            select.setVisible(true);
        } else if (evt.getStateChange() == ItemEvent.DESELECTED) {
            select.setVisible(false);
        }
    }//GEN-LAST:event_option2RadioButtonItemStateChanged

    private void metricComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_metricComboBoxActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setMetricIndex(metricComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_metricComboBoxActionPerformed

    private void normComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_normComboBoxActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setNormalizationIndex(normComboBox.getSelectedIndex());
        }

    }//GEN-LAST:event_normComboBoxActionPerformed

    private void option2RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option2RadioButtonActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setOptionIndex(1);
        }
    }//GEN-LAST:event_option2RadioButtonActionPerformed

    private void option1RadioButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1RadioButtonActionPerformed
        if (csnAlgo != null) {
            csnAlgo.setOptionIndex(0);
        }
    }//GEN-LAST:event_option1RadioButtonActionPerformed

    @Override
    public JPanel getEditPanel(Algorithm algo) {
        this.csnAlgo = (ChemicalSpaceNetwork) algo;
        switch (csnAlgo.getOptionIndex()) {
            case 0:
                option1RadioButton.setSelected(true);
                select.setVisible(false);
                break;
            case 1:
                option2RadioButton.setSelected(true);
                select.setVisible(true);
                break;
        }
        metricComboBox.setSelectedIndex(csnAlgo.getMetricIndex());
        normComboBox.setSelectedIndex(csnAlgo.getNormalizationIndex());

        thresholdPanel.setup(csnAlgo);
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel descriptorsPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JComboBox<String> metricComboBox;
    private javax.swing.JComboBox<String> normComboBox;
    private javax.swing.JRadioButton option1RadioButton;
    private javax.swing.JRadioButton option2RadioButton;
    private javax.swing.JPanel optionPanel;
    private javax.swing.JPanel southPanel;
    // End of variables declaration//GEN-END:variables

}
