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
import org.bapedis.core.spi.alg.MultiQuery;
import org.bapedis.core.spi.alg.SingleQuery;
import org.bapedis.core.ui.components.MultiQueryPanel;
import org.bapedis.core.ui.components.SequenceAlignmentPanel;
import org.bapedis.core.ui.components.SingleQueryPanel;
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
    protected JPanel queryPanel;

    /**
     * Creates new form SeqAlignmentFilterSetupUI
     *
     */
    public SequenceSearchSetupUI() {
        initComponents();

        card = Card.SEQUENCE;
        switcherLink = new JXHyperlink();
        configureSwitcherLink();

        CardLayout cl = (CardLayout) centerPanel.getLayout();
        cl.show(centerPanel, "sequence");
    }

    private void configureSwitcherLink() {
        switcherLink.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.text"));
        switcherLink.setToolTipText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.toolTipText"));
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
            case ALIGNMENT:
                cl.show(centerPanel, "sequence");
                switcherLink.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.text"));
                switcherLink.setToolTipText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.toolTipText"));
                card = Card.SEQUENCE;
                break;
            case SEQUENCE:
                switcherLink.setText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.alternativeText"));
                switcherLink.setToolTipText(NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.switcherLink.alternaToolTipText"));
                cl.show(centerPanel, "alignment");
                card = Card.ALIGNMENT;
                break;
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        switcherLink.setEnabled(enabled);
        if (seqAlignmentPanel != null) {
            seqAlignmentPanel.setEnabled(enabled);
        }
        if (queryPanel != null){
            queryPanel.setEnabled(enabled);
        }
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        searchAlg = (BaseSequenceSearchAlg) algo;       

        if (searchAlg.isWorkspaceInput()){
            jOption2.setSelected(true);
        }else{
            jOption1.setSelected(true);
        }
        
        SequenceAlignmentModel alignmentModel = searchAlg.getAlignmentModel();        
        seqAlignmentPanel = new SequenceAlignmentPanel(alignmentModel);
        
        alignmentPanel.removeAll();
        alignmentPanel.add(seqAlignmentPanel, BorderLayout.CENTER);
        alignmentPanel.revalidate();
        alignmentPanel.repaint();

        if (searchAlg instanceof SingleQuery){
            queryPanel = new SingleQueryPanel((SingleQuery)searchAlg);
        }else if (searchAlg instanceof MultiQuery){ 
            queryPanel = new MultiQueryPanel((MultiQuery)searchAlg);
        }else{
            queryPanel = null;
        }
        
        querySetupPanel.removeAll();
        querySetupPanel.add(queryPanel, BorderLayout.CENTER);
        querySetupPanel.revalidate();
        querySetupPanel.repaint();        
        
        return this;
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
        inputPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        querySetupPanel = new javax.swing.JPanel();
        alignmentPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(460, 300));
        setPreferredSize(new java.awt.Dimension(460, 300));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setLayout(new java.awt.CardLayout());

        seqPanel.setLayout(new java.awt.GridBagLayout());

        inputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.inputPanel.border.title"))); // NOI18N
        inputPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        inputPanel.add(jLabel1, gridBagConstraints);

        buttonGroup1.add(jOption1);
        jOption1.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jOption1, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jOption1.text")); // NOI18N
        jOption1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        inputPanel.add(jOption1, gridBagConstraints);

        buttonGroup1.add(jOption2);
        org.openide.awt.Mnemonics.setLocalizedText(jOption2, org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.jOption2.text")); // NOI18N
        jOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        inputPanel.add(jOption2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        seqPanel.add(inputPanel, gridBagConstraints);

        querySetupPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        seqPanel.add(querySetupPanel, gridBagConstraints);

        centerPanel.add(seqPanel, "sequence");

        alignmentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(SequenceSearchSetupUI.class, "SequenceSearchSetupUI.alignmentPanel.border.title"))); // NOI18N
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

    private void jOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption1ActionPerformed
        if (searchAlg != null && searchAlg.isWorkspaceInput()){
            searchAlg.setWorkspaceInput(false);
        }
    }//GEN-LAST:event_jOption1ActionPerformed

    private void jOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption2ActionPerformed
        if (searchAlg != null && !searchAlg.isWorkspaceInput()){
            searchAlg.setWorkspaceInput(true);
        }
    }//GEN-LAST:event_jOption2ActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel alignmentPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel inputPanel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JPanel querySetupPanel;
    private javax.swing.JPanel seqPanel;
    // End of variables declaration//GEN-END:variables
}
