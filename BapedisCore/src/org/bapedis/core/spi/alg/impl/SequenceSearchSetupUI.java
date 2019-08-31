/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import javax.swing.JPanel;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SequenceSearchSetupUI extends javax.swing.JPanel implements AlgorithmSetupUI {

    protected enum Card {
        SEQUENCE, ALIGNMENT
    };

    protected final JXHyperlink switcherLink;
    protected Card card;
    protected BaseSequenceSearchAlg searchAlg;
    protected SequenceAlignmentPanel seqAlignmentPanel;
    protected final AlgorithmSetupUI querySetupUI;
    protected JPanel querySetupPanel;

    /**
     * Creates new form SeqAlignmentFilterSetupUI
     *
     * @param querySetupUI
     */
    public SequenceSearchSetupUI(AlgorithmSetupUI querySetupUI) {
        initComponents();

        card = Card.SEQUENCE;
        switcherLink = new JXHyperlink();
        configureSwitcherLink();

        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "sequence");
        this.querySetupUI = querySetupUI;
    }

    private void configureSwitcherLink() {
        switcherLink.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.text"));
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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(switcherLink, gridBagConstraints);
    }

    private void switchPanel() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        switch (card) {
            case ALIGNMENT:
                cl.show(centerPanel, "sequence");
                switcherLink.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.text"));
                card = Card.SEQUENCE;
                break;
            case SEQUENCE:
                switcherLink.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.alternativeText"));
                cl.show(centerPanel, "alignment");
                card = Card.ALIGNMENT;
                break;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        switcherLink.setEnabled(enabled);
        jMRLabel.setEnabled(enabled);
        jMRComboBox.setEnabled(enabled);
        if (seqAlignmentPanel != null) {
            seqAlignmentPanel.setEnabled(enabled);
        }
        if (querySetupPanel != null){
            querySetupPanel.setEnabled(enabled);
        }
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        searchAlg = (BaseSequenceSearchAlg) algo;
        
        int maximumResults = searchAlg.getMaximumResults();
        if (maximumResults == -1) {
            jMRComboBox.setSelectedItem("All");
        } else if (maximumResults > 0) {
            jMRComboBox.setSelectedItem(String.valueOf(maximumResults));
        }

        SequenceAlignmentModel alignmentModel = searchAlg.getAlignmentModel();        
        seqAlignmentPanel = new SequenceAlignmentPanel(alignmentModel);
        
        alignmentPanel.removeAll();
        alignmentPanel.add(seqAlignmentPanel, BorderLayout.CENTER);
        alignmentPanel.revalidate();
        alignmentPanel.repaint();

        querySetupPanel = querySetupUI.getSettingPanel(algo);
        queryPanel.add(querySetupPanel, BorderLayout.CENTER);
        
        return this;
//        ProteinSequence seq = searchAlg.getQuery();
//        jSeqTextArea.setText(seq != null ? seq.getSequenceAsString() : "");
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

        centerPanel = new javax.swing.JPanel();
        seqPanel = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        jMRLabel = new javax.swing.JLabel();
        jMRComboBox = new javax.swing.JComboBox<>();
        queryPanel = new javax.swing.JPanel();
        alignmentPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(460, 300));
        setPreferredSize(new java.awt.Dimension(460, 300));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.CardLayout());

        seqPanel.setLayout(new java.awt.GridBagLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        org.openide.awt.Mnemonics.setLocalizedText(jMRLabel, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jMRLabel.text")); // NOI18N
        jMRLabel.setToolTipText(org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jMRLabel.toolTipText")); // NOI18N
        toolBar.add(jMRLabel);

        jMRComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "10", "50", "100", "250", "500", "1000" }));
        jMRComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMRComboBoxActionPerformed(evt);
            }
        });
        toolBar.add(jMRComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        seqPanel.add(toolBar, gridBagConstraints);

        queryPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        seqPanel.add(queryPanel, gridBagConstraints);

        centerPanel.add(seqPanel, "sequence");

        alignmentPanel.setLayout(new java.awt.BorderLayout());
        centerPanel.add(alignmentPanel, "alignment");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jMRComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMRComboBoxActionPerformed
        if (searchAlg != null) {
            String selectedItem = (String) jMRComboBox.getSelectedItem();
            if (selectedItem.equals("All")) {
                searchAlg.setMaximumResults(-1);
            } else {
                searchAlg.setMaximumResults(Integer.parseInt(selectedItem));
            }
        }
    }//GEN-LAST:event_jMRComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alignmentPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JComboBox<String> jMRComboBox;
    private javax.swing.JLabel jMRLabel;
    private javax.swing.JPanel queryPanel;
    private javax.swing.JPanel seqPanel;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables
}
