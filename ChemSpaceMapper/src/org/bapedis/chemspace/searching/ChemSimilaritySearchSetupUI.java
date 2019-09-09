/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.chemspace.model.SimilaritySearchingModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.MultiQuery;
import org.bapedis.core.spi.alg.SingleQuery;
import org.bapedis.core.ui.components.MultiQueryPanel;
import org.bapedis.core.ui.components.SingleQueryPanel;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ChemSimilaritySearchSetupUI extends javax.swing.JPanel implements AlgorithmSetupUI {

    protected enum Card {
        SEQUENCE, SETTINGS
    };

    private final NotifyDescriptor errorND;
    protected final JXHyperlink switcherLink;
    protected Card card;
    protected ChemBaseSimilaritySearchAlg searchAlg;
    protected SimilaritySearchingModel searchingModel;
    protected JPanel querySetupPanel;

    /**
     * Creates new form ChemSimilaritySearchSetupUI
     */
    public ChemSimilaritySearchSetupUI() {
        initComponents();

        card = Card.SEQUENCE;
        switcherLink = new JXHyperlink();
        configureSwitcherLink();

        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "sequence");

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
                searchingModel.setOption(SimilaritySearchingModel.Options.TOP_RANK_VALUE_OPTION);
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorND);
        }
    }

    private void configureSwitcherLink() {
        switcherLink.setText(NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.switcherLink.text"));
        switcherLink.setClickedColor(new java.awt.Color(0, 51, 255));
        switcherLink.setFocusPainted(false);
        switcherLink.setFocusable(false);
        switcherLink.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        switcherLink.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        switcherLink.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switchPanel();
            }
        });

        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(switcherLink, gridBagConstraints);
    }

    private void switchPanel() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        switch (card) {
            case SETTINGS:
                cl.show(centerPanel, "sequence");
                switcherLink.setText(NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.switcherLink.text"));
                card = Card.SEQUENCE;
                break;
            case SEQUENCE:
                switcherLink.setText(NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.switcherLink.alternativeText"));
                cl.show(centerPanel, "settings");
                card = Card.SETTINGS;
                break;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        switcherLink.setEnabled(enabled);
        
        settingPanel.setEnabled(enabled);
        jOption1.setEnabled(enabled);
        jTopPercentComboBox.setEnabled(enabled);
        jOption2.setEnabled(enabled);
        jTFTopRank.setEnabled(enabled);
        jOption3.setEnabled(enabled);
        jSpinnerSimThreshold.setEnabled(enabled);
        
        if (querySetupPanel != null){
            querySetupPanel.setEnabled(enabled);
        }
    }
    

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        searchAlg = (ChemBaseSimilaritySearchAlg) algo;
        searchingModel = searchAlg.getSearchingModel();
        setupSearchingModel();

        if (searchAlg instanceof SingleQuery) {
            querySetupPanel = new SingleQueryPanel((SingleQuery) searchAlg);
        } else if (searchAlg instanceof MultiQuery) {
            querySetupPanel = new MultiQueryPanel((MultiQuery) searchAlg);
        } else {
            querySetupPanel = null;
        }

        seqPanel.removeAll();
        seqPanel.add(querySetupPanel, BorderLayout.CENTER);
        seqPanel.revalidate();
        seqPanel.repaint();

        return this;
    }

    private void setupSearchingModel() {
        jTopPercentComboBox.setSelectedIndex(searchingModel.getTopPercentIndex());
        jTFTopRank.setText(String.valueOf(searchingModel.getTopRank()));
        jSpinnerSimThreshold.getModel().setValue(searchingModel.getThreshold());

        switch (searchingModel.getOption()) {
            case TOP_RANK_PERCENT_OPTION:
                jOption1.setSelected(true);
                break;
            case TOP_RANK_VALUE_OPTION:
                jOption2.setSelected(true);
                break;
            case SIMILARITY_THRESHOD_OPTION:
                jOption3.setSelected(true);
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
        centerPanel = new javax.swing.JPanel();
        seqPanel = new javax.swing.JPanel();
        settingPanel = new javax.swing.JPanel();
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        jTFTopRank = new javax.swing.JTextField();
        jTopPercentComboBox = new javax.swing.JComboBox<>();
        jOption3 = new javax.swing.JRadioButton();
        jSpinnerSimThreshold = new javax.swing.JSpinner();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.CardLayout());

        seqPanel.setLayout(new java.awt.BorderLayout());
        centerPanel.add(seqPanel, "sequence");

        settingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.settingPanel.border.title"))); // NOI18N
        settingPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jOption1);
        org.openide.awt.Mnemonics.setLocalizedText(jOption1, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jOption1.text")); // NOI18N
        jOption1.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOption1ItemStateChanged(evt);
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jOption2, gridBagConstraints);

        jTFTopRank.setText(org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jTFTopRank.text")); // NOI18N
        jTFTopRank.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jTFTopRank, gridBagConstraints);

        jTopPercentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1%", "3%", "5%", "10%" }));
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
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        settingPanel.add(jOption3, gridBagConstraints);

        jSpinnerSimThreshold.setModel(new javax.swing.SpinnerNumberModel(0.9d, 0.0d, 1.0d, 0.01d));
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(ChemSimilaritySearchSetupUI.class, "ChemSimilaritySearchSetupUI.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        settingPanel.add(jLabel2, gridBagConstraints);

        centerPanel.add(settingPanel, "settings");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jSpinnerSimThresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerSimThresholdStateChanged
        double threshold = (double) jSpinnerSimThreshold.getModel().getValue();
        if (searchingModel.getThreshold() != threshold) {
            searchingModel.setThreshold(threshold);
        }
        searchingModel.setOption(SimilaritySearchingModel.Options.SIMILARITY_THRESHOD_OPTION);
    }//GEN-LAST:event_jSpinnerSimThresholdStateChanged

    private void jTopPercentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTopPercentComboBoxActionPerformed
        int topPercentIndex = jTopPercentComboBox.getSelectedIndex();
        if (searchingModel.getTopPercentIndex() != topPercentIndex) {
            searchingModel.setTopPercentIndex(topPercentIndex);
        }
        searchingModel.setOption(SimilaritySearchingModel.Options.TOP_RANK_PERCENT_OPTION);

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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JRadioButton jOption3;
    private javax.swing.JSpinner jSpinnerSimThreshold;
    private javax.swing.JTextField jTFTopRank;
    private javax.swing.JComboBox<String> jTopPercentComboBox;
    private javax.swing.JPanel seqPanel;
    private javax.swing.JPanel settingPanel;
    // End of variables declaration//GEN-END:variables
}
