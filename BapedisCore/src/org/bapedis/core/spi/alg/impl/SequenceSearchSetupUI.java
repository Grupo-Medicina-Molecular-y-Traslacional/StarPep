/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompoundSet;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SequenceSearchSetupUI extends javax.swing.JPanel implements AlgorithmSetupUI {

    protected final JXHyperlink switcherLink;

    private enum Card {
        SEQUENCE, ALIGNMENT
    };
    private Card card;
    protected final PropertyChangeSupport changeSupport;
    protected SequenceSearch searchAlg;
    protected final AminoAcidCompoundSet compoundSet;

    /**
     * Creates new form SeqAlignmentFilterSetupUI
     */
    public SequenceSearchSetupUI() {
        initComponents();

        card = Card.SEQUENCE;
        switcherLink = new JXHyperlink();
        configureSwitcherLink();

        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "sequence");
        changeSupport = new PropertyChangeSupport(this);
        jErrorLabel.setText(" ");
        compoundSet = AminoAcidCompoundSet.getAminoAcidCompoundSet();
        jSeqTextArea.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateValidState();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateValidState();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
            }
        });
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

    private void updateValidState() {
        String seq = jSeqTextArea.getText();
        boolean validState = seq.length() > 0;
        String aa;
        jErrorLabel.setText(" ");
        for (int i = 0; i < seq.length() && validState; i++) {
            aa = seq.substring(i, i + 1);
            if (compoundSet.getCompoundForString(aa) == null) {
                jErrorLabel.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jErrorLabel.text", aa));
                validState = false;
            }
        }
        if (validState && searchAlg != null) {
            try {
                searchAlg.setQuery(new ProteinSequence(jSeqTextArea.getText()));
            } catch (CompoundNotFoundException ex) {
                Exceptions.printStackTrace(ex);
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

        centerPanel = new javax.swing.JPanel();
        seqPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jseqPane = new javax.swing.JScrollPane();
        jSeqTextArea = new javax.swing.JTextArea();
        jErrorLabel = new javax.swing.JLabel();
        jMRLabel = new javax.swing.JLabel();
        jMRComboBox = new javax.swing.JComboBox<>();
        alignmentPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(460, 300));
        setPreferredSize(new java.awt.Dimension(460, 300));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.CardLayout());

        seqPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        seqPanel.add(jLabel1, gridBagConstraints);

        jSeqTextArea.setColumns(20);
        jSeqTextArea.setLineWrap(true);
        jSeqTextArea.setRows(5);
        jSeqTextArea.setWrapStyleWord(true);
        jseqPane.setViewportView(jSeqTextArea);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        seqPanel.add(jseqPane, gridBagConstraints);

        jErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jErrorLabel, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jErrorLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        seqPanel.add(jErrorLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jMRLabel, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jMRLabel.text")); // NOI18N
        jMRLabel.setToolTipText(org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jMRLabel.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 5);
        seqPanel.add(jMRLabel, gridBagConstraints);

        jMRComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "All", "10", "50", "100", "250", "500", "1000" }));
        jMRComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMRComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        seqPanel.add(jMRComboBox, gridBagConstraints);

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

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        searchAlg = (SequenceSearch) algo;
        int maximumResults = searchAlg.getMaximumResults();
        if (maximumResults == -1) {
            jMRComboBox.setSelectedItem("All");
        } else if (maximumResults > 0) {
            jMRComboBox.setSelectedItem(String.valueOf(maximumResults));
        }
        ProteinSequence seq = searchAlg.getQuery();
        jSeqTextArea.setText(seq != null ? seq.getSequenceAsString() : "");
        SequenceAlignmentModel alignmentModel = searchAlg.getAlignmentModel();

        alignmentPanel.removeAll();
        alignmentPanel.add(new SequenceAlignmentPanel(alignmentModel), BorderLayout.CENTER);
        alignmentPanel.revalidate();
        alignmentPanel.repaint();

        return this;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alignmentPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel jErrorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JComboBox<String> jMRComboBox;
    private javax.swing.JLabel jMRLabel;
    private javax.swing.JTextArea jSeqTextArea;
    private javax.swing.JScrollPane jseqPane;
    private javax.swing.JPanel seqPanel;
    // End of variables declaration//GEN-END:variables
}
