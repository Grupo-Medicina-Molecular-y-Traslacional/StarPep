/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.CardLayout;
import javax.swing.JPanel;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.chemspace.model.CompressedModel;
import org.bapedis.chemspace.model.NetworkType;
import org.openide.util.NbBundle;

public final class VisualRepresentation extends JPanel {

    static final String CHANGED_CHEM_SPACE = "chemspace_option";
    static final String CHANGED_NETWORK_TYPE = "network_type";
    private ChemSpaceOption csOption;
    private NetworkType networkType;
    private int compressedStrategyIndex, compressedMaxSuperNodes;

    public VisualRepresentation() {
        initComponents();
        networkType = NetworkType.NONE;
        compressedStrategyIndex = CompressedModel.DEFAULT_STRATEGY_INDEX;
        compressedMaxSuperNodes = CompressedModel.DEFAULT_MAX_SUPER_NODES;
    }

    public ChemSpaceOption getChemSpaceOption() {
        return csOption;
    }

    public void setChemSpaceOption(ChemSpaceOption csOption) {
        ChemSpaceOption oldOption = this.csOption;
        this.csOption = csOption;
        switch (csOption) {
            case N_DIMENSIONAL_SPACE:
                if (!jOption1.isSelected()) {
                    jOption1.setSelected(true);
                }
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option1.text"));
                break;
            case CHEM_SPACE_NETWORK:
                if (!jOption2.isSelected()) {
                    jOption2.setSelected(true);
                }
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option2.text"));
                break;
            case SEQ_SIMILARITY_NETWORK:
                if (!jOption3.isSelected()){
                    jOption3.setSelected(true);
                }
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option3.text"));
                break;
            case NONE:
                jOption1.setSelected(false);
                jOption2.setSelected(false);
                jOption3.setSelected(false);
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.text"));
        }
        refreshNetworkOptionPanel(jOption2.isSelected() || jOption3.isSelected());
        firePropertyChange(CHANGED_CHEM_SPACE, oldOption, csOption);
    }

    public NetworkType getNetworkType() {
        return networkType;
    }

    public void setNetworkType(NetworkType networkType) {
        NetworkType oldNetworkType = this.networkType;
        this.networkType = networkType;
        refeshNetworkType();
        firePropertyChange(CHANGED_NETWORK_TYPE, oldNetworkType, networkType);
    }

    private void refeshNetworkType() {
        CardLayout optionSettingCL = (CardLayout) optionSettingPanel.getLayout();
        switch (networkType) {
            case FULL:
                if (!jOptionFN.isSelected()) {
                    jOptionFN.setSelected(true);
                }
                optionSettingCL.show(optionSettingPanel, "full");
                break;
            case COMPRESSED:
                if (!jOptionCN.isSelected()) {
                    jOptionCN.setSelected(true);
                }
                optionSettingCL.show(optionSettingPanel, "compressed");
                break;
        }
    }

    public int getCompressedStrategyIndex() {
        return compressedStrategyIndex;
    }

    public void setCompressedStrategyIndex(int compressedStrategyIndex) {
        this.compressedStrategyIndex = compressedStrategyIndex;
        if (jOptionCN_1_Items.getSelectedIndex() != compressedStrategyIndex) {
            jOptionCN_1_Items.setSelectedIndex(compressedStrategyIndex);
        }
    }

    public int getCompressedMaxSuperNodes() {
        return compressedMaxSuperNodes;
    }

    public void setCompressedMaxSuperNodes(int maxSuperNodes) {
        this.compressedMaxSuperNodes = maxSuperNodes;
        String val = String.valueOf(maxSuperNodes);
        if (!jOptionCN_2_Items.getSelectedItem().equals(val)) {
            jOptionCN_2_Items.setSelectedItem(val);
        }
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(VisualRepresentation.class, "ChemSpaceRepresentation.name");
    }

    private void refreshNetworkOptionPanel(boolean enable) {
        networkTypePanel.setEnabled(enable);

        jOptionFN.setEnabled(enable);
        optionFNPanel.setEnabled(enable);

        jOptionCN.setEnabled(enable);
        optionCNPanel.setEnabled(enable);
        jBasedOnLabel.setEnabled(enable);
        jOptionCN_1_Items.setEnabled(enable);
        jMaxNumberLabel.setEnabled(enable);
        jOptionCN_2_Items.setEnabled(enable);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        buttonGroup1 = new javax.swing.ButtonGroup();
        buttonGroup2 = new javax.swing.ButtonGroup();
        jQuestionLabel = new javax.swing.JLabel();
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        jOption3 = new javax.swing.JRadioButton();
        networkTypePanel = new javax.swing.JPanel();
        jOptionFN = new javax.swing.JRadioButton();
        jOptionCN = new javax.swing.JRadioButton();
        optionSettingPanel = new javax.swing.JPanel();
        jNoneLabel = new javax.swing.JLabel();
        optionFNPanel = new javax.swing.JPanel();
        optionCNPanel = new javax.swing.JPanel();
        jBasedOnLabel = new javax.swing.JLabel();
        jOptionCN_1_Items = new javax.swing.JComboBox<>();
        jMaxNumberLabel = new javax.swing.JLabel();
        jOptionCN_2_Items = new javax.swing.JComboBox<>();
        bottomPanel = new javax.swing.JPanel();
        extLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(460, 400));
        setPreferredSize(new java.awt.Dimension(500, 460));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jQuestionLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jQuestionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jQuestionLabel, gridBagConstraints);

        buttonGroup1.add(jOption1);
        org.openide.awt.Mnemonics.setLocalizedText(jOption1, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption1.text")); // NOI18N
        jOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption1, gridBagConstraints);

        buttonGroup1.add(jOption2);
        org.openide.awt.Mnemonics.setLocalizedText(jOption2, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption2.text")); // NOI18N
        jOption2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOption2ItemStateChanged(evt);
            }
        });
        jOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption2, gridBagConstraints);

        buttonGroup1.add(jOption3);
        org.openide.awt.Mnemonics.setLocalizedText(jOption3, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption3.text")); // NOI18N
        jOption3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption3, gridBagConstraints);

        networkTypePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.networkTypePanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        networkTypePanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup2.add(jOptionFN);
        org.openide.awt.Mnemonics.setLocalizedText(jOptionFN, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOptionFN.text")); // NOI18N
        jOptionFN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOptionFNActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        networkTypePanel.add(jOptionFN, gridBagConstraints);

        buttonGroup2.add(jOptionCN);
        org.openide.awt.Mnemonics.setLocalizedText(jOptionCN, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOptionCN.text")); // NOI18N
        jOptionCN.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jOptionCN.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOptionCNActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        networkTypePanel.add(jOptionCN, gridBagConstraints);

        optionSettingPanel.setLayout(new java.awt.CardLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jNoneLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jNoneLabel.text")); // NOI18N
        optionSettingPanel.add(jNoneLabel, "none");

        optionFNPanel.setLayout(new java.awt.GridBagLayout());
        optionSettingPanel.add(optionFNPanel, "full");

        optionCNPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jBasedOnLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jBasedOnLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        optionCNPanel.add(jBasedOnLabel, gridBagConstraints);

        jOptionCN_1_Items.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "communities" }));
        jOptionCN_1_Items.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOptionCN_1_ItemsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        optionCNPanel.add(jOptionCN_1_Items, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jMaxNumberLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jMaxNumberLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        optionCNPanel.add(jMaxNumberLabel, gridBagConstraints);

        jOptionCN_2_Items.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1000", "500", "250", "100", "50", "10" }));
        jOptionCN_2_Items.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOptionCN_2_ItemsActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        optionCNPanel.add(jOptionCN_2_Items, gridBagConstraints);

        optionSettingPanel.add(optionCNPanel, "compressed");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        networkTypePanel.add(optionSettingPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 25, 0, 5);
        add(networkTypePanel, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        extLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(extLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.text")); // NOI18N
        extLabel.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bottomPanel.add(extLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption1ActionPerformed
        setChemSpaceOption(ChemSpaceOption.N_DIMENSIONAL_SPACE);
    }//GEN-LAST:event_jOption1ActionPerformed

    private void jOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption2ActionPerformed
        setChemSpaceOption(ChemSpaceOption.CHEM_SPACE_NETWORK);
    }//GEN-LAST:event_jOption2ActionPerformed

    private void jOptionFNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOptionFNActionPerformed
        setNetworkType(NetworkType.FULL);
    }//GEN-LAST:event_jOptionFNActionPerformed

    private void jOptionCNActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOptionCNActionPerformed
        setNetworkType(NetworkType.COMPRESSED);
    }//GEN-LAST:event_jOptionCNActionPerformed

    private void jOptionCN_1_ItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOptionCN_1_ItemsActionPerformed
        if (compressedStrategyIndex != jOptionCN_1_Items.getSelectedIndex()) {
            compressedStrategyIndex = jOptionCN_1_Items.getSelectedIndex();
        }
    }//GEN-LAST:event_jOptionCN_1_ItemsActionPerformed

    private void jOptionCN_2_ItemsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOptionCN_2_ItemsActionPerformed
        String val = String.valueOf(compressedMaxSuperNodes);
        if (!val.equals(jOptionCN_2_Items.getSelectedItem())) {
            compressedMaxSuperNodes = Integer.parseInt((String) jOptionCN_2_Items.getSelectedItem());
        }
    }//GEN-LAST:event_jOptionCN_2_ItemsActionPerformed

    private void jOption2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption2ItemStateChanged
        refreshNetworkOptionPanel(jOption2.isSelected());
    }//GEN-LAST:event_jOption2ItemStateChanged

    private void jOption3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption3ActionPerformed
        setChemSpaceOption(ChemSpaceOption.SEQ_SIMILARITY_NETWORK);
    }//GEN-LAST:event_jOption3ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.ButtonGroup buttonGroup2;
    private javax.swing.JLabel extLabel;
    private javax.swing.JLabel jBasedOnLabel;
    private javax.swing.JLabel jMaxNumberLabel;
    private javax.swing.JLabel jNoneLabel;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JRadioButton jOption3;
    private javax.swing.JRadioButton jOptionCN;
    private javax.swing.JComboBox<String> jOptionCN_1_Items;
    private javax.swing.JComboBox<String> jOptionCN_2_Items;
    private javax.swing.JRadioButton jOptionFN;
    private javax.swing.JLabel jQuestionLabel;
    private javax.swing.JPanel networkTypePanel;
    private javax.swing.JPanel optionCNPanel;
    private javax.swing.JPanel optionFNPanel;
    private javax.swing.JPanel optionSettingPanel;
    // End of variables declaration//GEN-END:variables
}
