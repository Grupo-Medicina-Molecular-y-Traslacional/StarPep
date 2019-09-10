/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.chemspace.model.SimilaritySearchingModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ChemSimilaritySearchSetupUI extends javax.swing.JPanel implements AlgorithmSetupUI {

    private final NotifyDescriptor errorND;

    protected ChemBaseSimilaritySearchAlg searchAlg;
    protected SimilaritySearchingModel searchingModel;
    protected JPanel querySetupPanel;

    /**
     * Creates new form ChemSimilaritySearchSetupUI
     */
    public ChemSimilaritySearchSetupUI() {
        initComponents();
        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.errorND"), NotifyDescriptor.ERROR_MESSAGE);

        jTFTopRank.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTopRank();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTopRank();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void updateTopRank() {
        try {
            if (!jTFTopRank.getText().isEmpty()) {
                int topRank = Integer.parseInt(jTFTopRank.getText());
                if (searchingModel.getTopRank() != topRank) {
                    searchingModel.setTopRank(topRank);
                }
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorND);
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        settingPanel.setEnabled(enabled);
        jOption1.setEnabled(enabled);
        jTopPercentComboBox.setEnabled(enabled && jOption1.isSelected());

        jOption2.setEnabled(enabled);
        jTFTopRank.setEnabled(enabled && jOption2.isSelected());

        jOption3.setEnabled(enabled);
        jSpinnerSimThreshold.setEnabled(enabled && jOption3.isSelected());

        jOption4.setEnabled(enabled);
        jSpinnerSimThreshold.setEnabled(enabled && jOption4.isSelected());

        if (querySetupPanel != null) {
            querySetupPanel.setEnabled(enabled);
        }
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        searchAlg = (ChemBaseSimilaritySearchAlg) algo;
        searchingModel = searchAlg.getSearchingModel();
        setupSearchingModel();

        return this;
    }

    private void setupSearchingModel() {
        jTopPercentComboBox.setSelectedIndex(searchingModel.getTopPercentIndex());
        jTFTopRank.setText(String.valueOf(searchingModel.getTopRank()));
        jSpinnerSimThreshold.getModel().setValue(searchingModel.getThreshold());
        jsimTPercentComboBox.setSelectedIndex(searchingModel.getThresholdPercentIndex());

        switch (searchingModel.getOption()) {
            case TOP_RANK_PERCENT_OPTION:
                jOption1.setSelected(true);
                break;
            case TOP_RANK_VALUE_OPTION:
                jOption2.setSelected(true);
                break;
            case SIMILARITY_THRESHOD_VALUE_OPTION:
                jOption3.setSelected(true);
                break;
            case SIMILARITY_THRESHOD_PERCENT_OPTION:
                jOption4.setSelected(true);
                break;
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
        settingPanel = new javax.swing.JPanel();
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        jTFTopRank = new javax.swing.JTextField();
        jTopPercentComboBox = new javax.swing.JComboBox<>();
        jOption3 = new javax.swing.JRadioButton();
        jSpinnerSimThreshold = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        extLabel = new javax.swing.JLabel();
        jOption4 = new javax.swing.JRadioButton();
        jsimTPercentComboBox = new javax.swing.JComboBox<>();

        setLayout(new java.awt.BorderLayout());

        settingPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jOption1);
        org.openide.awt.Mnemonics.setLocalizedText(jOption1, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jOption1.text")); // NOI18N
        jOption1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOption1ItemStateChanged(evt);
            }
        });
        jOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jOption1, gridBagConstraints);

        buttonGroup1.add(jOption2);
        org.openide.awt.Mnemonics.setLocalizedText(jOption2, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jOption2.text")); // NOI18N
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        settingPanel.add(jOption2, gridBagConstraints);

        jTFTopRank.setText(org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jTFTopRank.text")); // NOI18N
        jTFTopRank.setEnabled(false);
        jTFTopRank.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jTFTopRank, gridBagConstraints);

        jTopPercentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1%", "3%", "5%", "10%" }));
        jTopPercentComboBox.setSelectedIndex(-1);
        jTopPercentComboBox.setEnabled(false);
        jTopPercentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTopPercentComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jTopPercentComboBox, gridBagConstraints);

        buttonGroup1.add(jOption3);
        org.openide.awt.Mnemonics.setLocalizedText(jOption3, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jOption3.text")); // NOI18N
        jOption3.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOption3ItemStateChanged(evt);
            }
        });
        jOption3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption3ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jOption3, gridBagConstraints);

        jSpinnerSimThreshold.setModel(new javax.swing.SpinnerNumberModel(0.9d, 0.0d, 1.0d, 0.01d));
        jSpinnerSimThreshold.setEnabled(false);
        jSpinnerSimThreshold.setPreferredSize(new java.awt.Dimension(60, 27));
        jSpinnerSimThreshold.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerSimThresholdStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jSpinnerSimThreshold, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        settingPanel.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.extLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        settingPanel.add(extLabel, gridBagConstraints);

        buttonGroup1.add(jOption4);
        org.openide.awt.Mnemonics.setLocalizedText(jOption4, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jOption4.text")); // NOI18N
        jOption4.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOption4ItemStateChanged(evt);
            }
        });
        jOption4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption4ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jOption4, gridBagConstraints);

        jsimTPercentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "50%", "60%", "70%", "80%", "90%" }));
        jsimTPercentComboBox.setSelectedIndex(-1);
        jsimTPercentComboBox.setEnabled(false);
        jsimTPercentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jsimTPercentComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jsimTPercentComboBox, gridBagConstraints);

        add(settingPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void jSpinnerSimThresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerSimThresholdStateChanged
        double threshold = (double) jSpinnerSimThreshold.getModel().getValue();
        if (searchingModel.getThreshold() != threshold) {
            searchingModel.setThreshold(threshold);
        }
    }//GEN-LAST:event_jSpinnerSimThresholdStateChanged

    private void jTopPercentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTopPercentComboBoxActionPerformed
        int topPercentIndex = jTopPercentComboBox.getSelectedIndex();
        if (searchingModel.getTopPercentIndex() != topPercentIndex) {
            searchingModel.setTopPercentIndex(topPercentIndex);
        }
    }//GEN-LAST:event_jTopPercentComboBoxActionPerformed

    private void jOption1ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption1ItemStateChanged
        jTopPercentComboBox.setEnabled(jOption1.isSelected());
    }//GEN-LAST:event_jOption1ItemStateChanged

    private void jOption2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption2ItemStateChanged
        jTFTopRank.setEnabled(jOption2.isSelected());
    }//GEN-LAST:event_jOption2ItemStateChanged

    private void jOption3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption3ItemStateChanged
        jSpinnerSimThreshold.setEnabled(jOption3.isSelected());
    }//GEN-LAST:event_jOption3ItemStateChanged

    private void jOption4ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption4ItemStateChanged
        jsimTPercentComboBox.setEnabled(jOption4.isSelected());
    }//GEN-LAST:event_jOption4ItemStateChanged

    private void jsimTPercentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jsimTPercentComboBoxActionPerformed
        int thresholdPercentIndex = jsimTPercentComboBox.getSelectedIndex();
        if (searchingModel.getThresholdPercentIndex() != thresholdPercentIndex) {
            searchingModel.setThresholdPercentIndex(thresholdPercentIndex);
        }
    }//GEN-LAST:event_jsimTPercentComboBoxActionPerformed

    private void jOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption1ActionPerformed
        if (searchingModel.getOption() != SimilaritySearchingModel.Options.TOP_RANK_PERCENT_OPTION) {
            searchingModel.setOption(SimilaritySearchingModel.Options.TOP_RANK_PERCENT_OPTION);
        }
    }//GEN-LAST:event_jOption1ActionPerformed

    private void jOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption2ActionPerformed
        if (searchingModel.getOption() != SimilaritySearchingModel.Options.TOP_RANK_VALUE_OPTION) {
            searchingModel.setOption(SimilaritySearchingModel.Options.TOP_RANK_VALUE_OPTION);
        }
    }//GEN-LAST:event_jOption2ActionPerformed

    private void jOption3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption3ActionPerformed
        if (searchingModel.getOption() != SimilaritySearchingModel.Options.SIMILARITY_THRESHOD_VALUE_OPTION) {
            searchingModel.setOption(SimilaritySearchingModel.Options.SIMILARITY_THRESHOD_VALUE_OPTION);
        }
    }//GEN-LAST:event_jOption3ActionPerformed

    private void jOption4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption4ActionPerformed
        if (searchingModel.getOption() != SimilaritySearchingModel.Options.SIMILARITY_THRESHOD_PERCENT_OPTION) {
            searchingModel.setOption(SimilaritySearchingModel.Options.SIMILARITY_THRESHOD_PERCENT_OPTION);
        }
    }//GEN-LAST:event_jOption4ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel extLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JRadioButton jOption3;
    private javax.swing.JRadioButton jOption4;
    private javax.swing.JSpinner jSpinnerSimThreshold;
    private javax.swing.JTextField jTFTopRank;
    private javax.swing.JComboBox<String> jTopPercentComboBox;
    private javax.swing.JComboBox<String> jsimTPercentComboBox;
    private javax.swing.JPanel settingPanel;
    // End of variables declaration//GEN-END:variables
}
