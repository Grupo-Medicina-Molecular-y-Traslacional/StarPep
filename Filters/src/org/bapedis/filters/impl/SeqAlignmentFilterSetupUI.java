/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterSetupUI;
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
public class SeqAlignmentFilterSetupUI extends javax.swing.JPanel implements FilterSetupUI {

    protected final JXHyperlink switcherLink;

    private enum Card {
        SEQUENCE, ALIGNMENT
    };
    private Card card;
    protected final PropertyChangeSupport changeSupport;
    protected SeqAlignmentFilter filter;
    protected boolean validState;
    protected SequenceAlignmentModel alignmentModel;
    protected final AminoAcidCompoundSet compoundSet;

    /**
     * Creates new form SeqAlignmentFilterSetupUI
     */
    public SeqAlignmentFilterSetupUI() {
        initComponents();

        card = Card.SEQUENCE;
        switcherLink = new JXHyperlink();
        configureSwitcherLink();

        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "sequence");
        changeSupport = new PropertyChangeSupport(this);
        validState = false;
        jErrorLabel.setVisible(false);
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
        switcherLink.setText(NbBundle.getMessage(SeqAlignmentFilter.class, "SeqAlignmentFilterSetupUI.switcherLink.text"));
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
                switcherLink.setText(NbBundle.getMessage(SeqAlignmentFilterSetupUI.class, "SeqAlignmentFilterSetupUI.switcherLink.text"));
                card = Card.SEQUENCE;
                break;
            case SEQUENCE:
                switcherLink.setText(NbBundle.getMessage(SeqAlignmentFilterSetupUI.class, "SeqAlignmentFilterSetupUI.switcherLink.alternativeText"));
                cl.show(centerPanel, "alignment");
                card = Card.ALIGNMENT;
                break;
        }
    }

    private void updateValidState() {
        boolean oldValue = validState;
        String seq = jSeqTextArea.getText();
        validState = seq.length() > 0;
        String aa;
        for (int i = 0; i < seq.length() && validState; i++) {
            aa = seq.substring(i, i + 1);
            if (compoundSet.getCompoundForString(aa) == null) {
                jErrorLabel.setText(NbBundle.getMessage(SeqAlignmentFilterSetupUI.class, "SeqAlignmentFilterSetupUI.jErrorLabel.text", aa));
                validState = false;
            }
        }
        jErrorLabel.setVisible(seq.length() > 0 && !validState);
        changeSupport.firePropertyChange(VALID_STATE, oldValue, validState);
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
        alignmentPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(460, 300));
        setPreferredSize(new java.awt.Dimension(460, 300));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.CardLayout());

        seqPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SeqAlignmentFilterSetupUI.class, "SeqAlignmentFilterSetupUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        seqPanel.add(jseqPane, gridBagConstraints);

        jErrorLabel.setForeground(new java.awt.Color(255, 0, 0));
        org.openide.awt.Mnemonics.setLocalizedText(jErrorLabel, org.openide.util.NbBundle.getMessage(SeqAlignmentFilterSetupUI.class, "SeqAlignmentFilterSetupUI.jErrorLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        seqPanel.add(jErrorLabel, gridBagConstraints);

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

    @Override
    public JPanel getEditPanel(Filter filter) {
        this.filter = (SeqAlignmentFilter) filter;
        ProteinSequence seq = this.filter.getQuery();
        jSeqTextArea.setText(seq != null? seq.getSequenceAsString(): "");
        
        alignmentModel = copyAlignmentModel(this.filter.getAlignmentModel());
        alignmentPanel.removeAll();
        alignmentPanel.add(new SequenceAlignmentPanel(alignmentModel), BorderLayout.CENTER);
        alignmentPanel.revalidate();
        alignmentPanel.repaint();
        return this;
    }

    private SequenceAlignmentModel copyAlignmentModel(SequenceAlignmentModel model) {
        SequenceAlignmentModel copyModel = new SequenceAlignmentModel();
        copyModel.setAlignmentTypeIndex(model.getAlignmentTypeIndex());
        copyModel.setSubstitutionMatrixIndex(model.getSubstitutionMatrixIndex());
        copyModel.setPercentIdentity(model.getPercentIdentity());
        return copyModel;
    }

    @Override
    public boolean isValidState() {
        return validState;
    }

    @Override
    public void saveSettings() {
        try {
            this.filter.setAlignmentModel(alignmentModel);
            this.filter.setQuery(new ProteinSequence(jSeqTextArea.getText()));
            this.filter.resetSearch();
        } catch (CompoundNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void cancelSettings() {
        filter = null;
        alignmentModel = null;        
    }

    @Override
    public void addValidStateListener(PropertyChangeListener listener) {
        changeSupport.addPropertyChangeListener(listener);
    }

    @Override
    public void removeValidStateListener(PropertyChangeListener listener) {
        changeSupport.removePropertyChangeListener(listener);
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alignmentPanel;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel jErrorLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextArea jSeqTextArea;
    private javax.swing.JScrollPane jseqPane;
    private javax.swing.JPanel seqPanel;
    // End of variables declaration//GEN-END:variables
}
