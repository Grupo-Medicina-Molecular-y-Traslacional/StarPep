/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.awt.BorderLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SequenceSimilarityNetworkPanel extends javax.swing.JPanel implements AlgorithmSetupUI {

    /**
     * Creates new form PairwiseSequenceAlignmentPanel
     */
    protected SequenceSimilarityNetwork seqAlignmentAlgo;
    protected final ThresholdRangePanel thresholdPanel;
    private final RichTooltip richTooltip;

    public SequenceSimilarityNetworkPanel() {
        initComponents();
        thresholdPanel = new ThresholdRangePanel();
        southPanel.add(thresholdPanel, BorderLayout.CENTER);
        richTooltip = new RichTooltip(NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "PairwiseSequenceAlignmentPanel.info.title"), NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "PairwiseSequenceAlignmentPanel.info.text"));
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

        alignmentPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jATComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jSMComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        infoLabel = new javax.swing.JLabel();
        jPercentComboBox = new javax.swing.JComboBox<>();
        jScoreComboBox = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        southPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        alignmentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "SequenceSimilarityNetworkPanel.alignmentPanel.border.title"))); // NOI18N
        alignmentPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "SequenceSimilarityNetworkPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        alignmentPanel.add(jLabel1, gridBagConstraints);

        jATComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Needleman-Wunsch", "Smith-Waterman" }));
        jATComboBox.setSelectedIndex(-1);
        jATComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jATComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        alignmentPanel.add(jATComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "SequenceSimilarityNetworkPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        alignmentPanel.add(jLabel2, gridBagConstraints);

        jSMComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Blosum 30 by Henikoff & Henikoff", "Blosum 35 by Henikoff & Henikoff", "Blosum 40 by Henikoff & Henikoff", "Blosum 45 by Henikoff & Henikoff", "Blosum 50 by Henikoff & Henikoff", "Blosum 55 by Henikoff & Henikoff", "Blosum 60 by Henikoff & Henikoff", "Blosum 62 by Henikoff & Henikoff", "Blosum 65 by Henikoff & Henikoff", "Blosum 70 by Henikoff & Henikoff", "Blosum 75 by Henikoff & Henikoff", "Blosum 80 by Henikoff & Henikoff", "Blosum 85 by Henikoff & Henikoff", "Blosum 90 by Henikoff & Henikoff", "Blosum 100 by Henikoff & Henikoff", "PAM 250 by Gonnet, Cohen & Benner", "PAM 250 by Dayhoff" }));
        jSMComboBox.setSelectedIndex(-1);
        jSMComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSMComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        alignmentPanel.add(jSMComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "SequenceSimilarityNetworkPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        alignmentPanel.add(jLabel3, gridBagConstraints);

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/info.png"))); // NOI18N
        infoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                infoLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                infoLabelMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        alignmentPanel.add(infoLabel, gridBagConstraints);

        jPercentComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Percent sequence identity", "Percent positive substitutions" }));
        jPercentComboBox.setSelectedIndex(-1);
        jPercentComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jPercentComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        alignmentPanel.add(jPercentComboBox, gridBagConstraints);

        jScoreComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jScoreComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        alignmentPanel.add(jScoreComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(SequenceSimilarityNetworkPanel.class, "SequenceSimilarityNetworkPanel.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        alignmentPanel.add(jLabel5, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(alignmentPanel, gridBagConstraints);

        southPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(southPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jPercentComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jPercentComboBoxActionPerformed
        seqAlignmentAlgo.setSimilarityTypeIndex(jPercentComboBox.getSelectedIndex());
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (String item : SequenceSimilarityNetwork.Similarity_Score[seqAlignmentAlgo.getSimilarityTypeIndex()]) {
            model.addElement(item);
        }
        model.setSelectedItem(SequenceSimilarityNetwork.Similarity_Score[seqAlignmentAlgo.getSimilarityTypeIndex()][seqAlignmentAlgo.getSimilarityScoreIndex()]);
        jScoreComboBox.setModel(model);        
    }//GEN-LAST:event_jPercentComboBoxActionPerformed

    private void jATComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jATComboBoxActionPerformed
        seqAlignmentAlgo.setAlignmentTypeIndex(jATComboBox.getSelectedIndex());
    }//GEN-LAST:event_jATComboBoxActionPerformed

    private void jSMComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSMComboBoxActionPerformed
        seqAlignmentAlgo.setSubstitutionMatrixIndex(jSMComboBox.getSelectedIndex());
    }//GEN-LAST:event_jSMComboBoxActionPerformed

    private void jScoreComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jScoreComboBoxActionPerformed
        if (jScoreComboBox.getSelectedIndex() != -1) {
            seqAlignmentAlgo.setSimilarityScoreIndex(jScoreComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_jScoreComboBoxActionPerformed

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        richTooltip.hideTooltip();
    }//GEN-LAST:event_infoLabelMouseExited

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
       richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
    }//GEN-LAST:event_infoLabelMouseEntered

    @Override
    public JPanel getEditPanel(Algorithm algo) {
        seqAlignmentAlgo = (SequenceSimilarityNetwork) algo;
        jATComboBox.setSelectedIndex(seqAlignmentAlgo.getAlignmentTypeIndex());
        jSMComboBox.setSelectedIndex(seqAlignmentAlgo.getSubstitutionMatrixIndex());
        jPercentComboBox.setSelectedIndex(seqAlignmentAlgo.getSimilarityTypeIndex());                
        thresholdPanel.setup(seqAlignmentAlgo);
        return this;
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alignmentPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JComboBox jATComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JComboBox<String> jPercentComboBox;
    private javax.swing.JComboBox jSMComboBox;
    private javax.swing.JComboBox<String> jScoreComboBox;
    private javax.swing.JPanel southPanel;
    // End of variables declaration//GEN-END:variables


}