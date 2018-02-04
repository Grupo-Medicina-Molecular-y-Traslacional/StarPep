/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Hashtable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.biojava.nbio.alignment.Alignments;
import org.biojava.nbio.core.alignment.matrices.SubstitutionMatrixHelper;
import org.biojava.nbio.core.alignment.template.SubstitutionMatrix;
import org.biojava.nbio.core.sequence.compound.AminoAcidCompound;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SequenceAlignmentPanel extends javax.swing.JPanel {

    public static final String GLOBAL_ALIGNMENT_TEXT = "Global (Needleman-Wunsch)";
    public static final String LOCAL_ALIGNMENT_TEXT = "Local (Smith-Waterman)";

    public static final String[] Alignment_Type = new String[]{LOCAL_ALIGNMENT_TEXT, GLOBAL_ALIGNMENT_TEXT};
    public static final String[] Substitution_Matrix = new String[]{
        "Blosum 30 by Henikoff & Henikoff", "Blosum 35 by Henikoff & Henikoff", "Blosum 40 by Henikoff & Henikoff",
        "Blosum 45 by Henikoff & Henikoff", "Blosum 50 by Henikoff & Henikoff", "Blosum 55 by Henikoff & Henikoff",
        "Blosum 60 by Henikoff & Henikoff", "Blosum 62 by Henikoff & Henikoff", "Blosum 65 by Henikoff & Henikoff",
        "Blosum 70 by Henikoff & Henikoff", "Blosum 75 by Henikoff & Henikoff", "Blosum 80 by Henikoff & Henikoff",
        "Blosum 85 by Henikoff & Henikoff", "Blosum 90 by Henikoff & Henikoff", "Blosum 100 by Henikoff & Henikoff",
        "PAM 250 by Gonnet, Cohen & Benner", "PAM 250 by Dayhoff"};

    public static final int DEFAULT_ALIGNMENT_TYPE_INDEX = 0; // Needleman-Wunsch
    public static final int DEFAULT_SUBSTITUTION_MATRIX_INDEX = 7; // Blosum 62 by Henikoff & Henikoff

    private final RichTooltip pidRichTooltip;
    private SequenceAlignmentModel model;

    /**
     * Creates new form SequenceAlignmentPanel
     */
    public SequenceAlignmentPanel() {
        initComponents();

        DefaultComboBoxModel<String> comboBoxModel = new DefaultComboBoxModel();
        for (String s : Alignment_Type) {
            comboBoxModel.addElement(s);
        }
        jATComboBox.setModel(comboBoxModel);
        jATComboBox.setSelectedIndex(DEFAULT_ALIGNMENT_TYPE_INDEX);

        comboBoxModel = new DefaultComboBoxModel<>();
        for (String s : Substitution_Matrix) {
            comboBoxModel.addElement(s);
        }
        jSMComboBox.setModel(comboBoxModel);
        jSMComboBox.setSelectedIndex(DEFAULT_SUBSTITUTION_MATRIX_INDEX);
        
        jPIDSlider.setMinimum(SequenceAlignmentModel.PID_MIN);
        jPIDSlider.setMaximum(SequenceAlignmentModel.PID_MAX);

        //Label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(SequenceAlignmentModel.PID_REFS[0], new JLabel(NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.pidSlider.low")));
        labelTable.put(SequenceAlignmentModel.PID_REFS[1], new JLabel(NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.pidSlider.middle")));
        labelTable.put(SequenceAlignmentModel.PID_REFS[2], new JLabel(NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.pidSlider.high")));
        
        jPIDSlider.setLabelTable(labelTable);
        jPIDLabel.setText(SequenceAlignmentModel.DEFAULT_PID + "%");
        jPIDSlider.setValue(SequenceAlignmentModel.DEFAULT_PID);        

        pidRichTooltip = new RichTooltip();
        pidRichTooltip.setTitle(NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.pidInfo.title"));
        pidRichTooltip.addDescriptionSection(NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.pidInfo1.text"));
        pidRichTooltip.addDescriptionSection(NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.pidInfo2.text"));

    }

    private SubstitutionMatrix<AminoAcidCompound> getSubstitutionMatrix() {
        switch (Substitution_Matrix[jSMComboBox.getSelectedIndex()]) {
            case "Blosum 30 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum30();
            case "Blosum 35 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum35();
            case "Blosum 40 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum40();
            case "Blosum 45 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum45();
            case "Blosum 50 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum50();
            case "Blosum 55 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum55();
            case "Blosum 60 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum60();
            case "Blosum 62 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum62();
            case "Blosum 65 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum65();
            case "Blosum 70 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum70();
            case "Blosum 75 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum75();
            case "Blosum 80 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum80();
            case "Blosum 85 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum85();
            case "Blosum 90 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum90();
            case "Blosum 100 by Henikoff & Henikoff":
                return SubstitutionMatrixHelper.getBlosum100();
            case "PAM 250 by Gonnet, Cohen & Benner":
                return SubstitutionMatrixHelper.getGonnet250();
            case "PAM 250 by Dayhoff":
                return SubstitutionMatrixHelper.getPAM250();
        }
        return null;
    }
    
    private Alignments.PairwiseSequenceAlignerType getAlignerType() {
        switch(Alignment_Type[jATComboBox.getSelectedIndex()]){
            case LOCAL_ALIGNMENT_TEXT:
                return Alignments.PairwiseSequenceAlignerType.LOCAL;
            case GLOBAL_ALIGNMENT_TEXT:
                return Alignments.PairwiseSequenceAlignerType.GLOBAL;                    
        }
        return null;
    }    

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
        
        jATComboBox.setEnabled(enabled);
        jSMComboBox.setEnabled(enabled);
        jPIDLabel.setEnabled(enabled);
        jLessButton.setEnabled(enabled);
        jPIDSlider.setEnabled(enabled);
        jMoreButton.setEnabled(enabled);        
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
        jATComboBox = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jSMComboBox = new javax.swing.JComboBox();
        jPIDInfoLabel = new javax.swing.JLabel();
        jPIDLabel = new javax.swing.JLabel();
        jToolBar1 = new javax.swing.JToolBar();
        jLessButton = new javax.swing.JButton();
        jPIDSlider = new javax.swing.JSlider();
        jMoreButton = new javax.swing.JButton();
        jextLabel = new javax.swing.JLabel();
        jResetButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel1, gridBagConstraints);

        jATComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jATComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jATComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jLabel2, gridBagConstraints);

        jSMComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jSMComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jSMComboBox, gridBagConstraints);

        jPIDInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jPIDInfoLabel, org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jPIDInfoLabel.text")); // NOI18N
        jPIDInfoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                jPIDInfoLabelMouseEntered(evt);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                jPIDInfoLabelMouseExited(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jPIDInfoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jPIDLabel, org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jPIDLabel.text")); // NOI18N
        jPIDLabel.setPreferredSize(new java.awt.Dimension(23, 20));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jPIDLabel, gridBagConstraints);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);
        jToolBar1.setFocusable(false);

        jLessButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/less.png"))); // NOI18N
        jLessButton.setToolTipText(org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jLessButton.toolTipText")); // NOI18N
        jLessButton.setFocusable(false);
        jLessButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLessButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jLessButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLessButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(jLessButton);

        jPIDSlider.setMajorTickSpacing(10);
        jPIDSlider.setMaximum(90);
        jPIDSlider.setMinimum(50);
        jPIDSlider.setMinorTickSpacing(5);
        jPIDSlider.setPaintLabels(true);
        jPIDSlider.setPaintTicks(true);
        jPIDSlider.setToolTipText(org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jPIDSlider.toolTipText")); // NOI18N
        jPIDSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jPIDSliderStateChanged(evt);
            }
        });
        jToolBar1.add(jPIDSlider);

        jMoreButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/more.png"))); // NOI18N
        jMoreButton.setToolTipText(org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jMoreButton.toolTipText")); // NOI18N
        jMoreButton.setFocusable(false);
        jMoreButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMoreButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jMoreButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMoreButtonActionPerformed(evt);
            }
        });
        jToolBar1.add(jMoreButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jToolBar1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jextLabel, org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jextLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        add(jextLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jResetButton, org.openide.util.NbBundle.getMessage(SequenceAlignmentPanel.class, "SequenceAlignmentPanel.jResetButton.text")); // NOI18N
        jResetButton.setMaximumSize(new java.awt.Dimension(145, 23));
        jResetButton.setMinimumSize(new java.awt.Dimension(145, 23));
        jResetButton.setPreferredSize(new java.awt.Dimension(145, 23));
        jResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 5);
        add(jResetButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jATComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jATComboBoxActionPerformed
        if (model != null) {
            model.setAlignerType(getAlignerType());
            switch (model.getAlignerType()) {
                case LOCAL:
                    jPIDLabel.setToolTipText("Identities * 100 / Length of shorter sequence");
                    break;
                case GLOBAL:
                    jPIDLabel.setToolTipText("Identities * 100 / Columns");
                    break;
            }
        }
    }//GEN-LAST:event_jATComboBoxActionPerformed

    private void jSMComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jSMComboBoxActionPerformed
        if (model != null) {
            model.setSubstitutionMatrix(getSubstitutionMatrix());
        }
    }//GEN-LAST:event_jSMComboBoxActionPerformed

    private void jPIDInfoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPIDInfoLabelMouseEntered
        pidRichTooltip.showTooltip(jPIDInfoLabel, evt.getLocationOnScreen());
    }//GEN-LAST:event_jPIDInfoLabelMouseEntered

    private void jPIDInfoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jPIDInfoLabelMouseExited
        pidRichTooltip.hideTooltip();
    }//GEN-LAST:event_jPIDInfoLabelMouseExited

    private void jLessButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLessButtonActionPerformed
        int pid = jPIDSlider.getValue();
        if (pid > jPIDSlider.getMinimum()) {
            jPIDSlider.setValue(pid - 1);
        }
    }//GEN-LAST:event_jLessButtonActionPerformed

    private void jPIDSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jPIDSliderStateChanged
        if (!jPIDSlider.getValueIsAdjusting() && model != null) {
            model.setPercentIdentity(jPIDSlider.getValue());
            jPIDLabel.setText(model.getPercentIdentity() + "%");
        }
    }//GEN-LAST:event_jPIDSliderStateChanged

    private void jMoreButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoreButtonActionPerformed
        int pid = jPIDSlider.getValue();
        if (pid < jPIDSlider.getMaximum()) {
            jPIDSlider.setValue(pid + 1);
        }
    }//GEN-LAST:event_jMoreButtonActionPerformed

    private void jResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetButtonActionPerformed
        jATComboBox.setSelectedIndex(DEFAULT_ALIGNMENT_TYPE_INDEX);
        jSMComboBox.setSelectedIndex(DEFAULT_SUBSTITUTION_MATRIX_INDEX);
        jPIDSlider.setValue(SequenceAlignmentModel.DEFAULT_PID);
    }//GEN-LAST:event_jResetButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox jATComboBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JButton jLessButton;
    private javax.swing.JButton jMoreButton;
    private javax.swing.JLabel jPIDInfoLabel;
    private javax.swing.JLabel jPIDLabel;
    private javax.swing.JSlider jPIDSlider;
    private javax.swing.JButton jResetButton;
    private javax.swing.JComboBox jSMComboBox;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JLabel jextLabel;
    // End of variables declaration//GEN-END:variables
}
