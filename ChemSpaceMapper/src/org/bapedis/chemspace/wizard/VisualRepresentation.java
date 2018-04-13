/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import javax.swing.JPanel;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.openide.util.NbBundle;

public final class VisualRepresentation extends JPanel {

    static final String CHANGED_OPTION = "representation_changed";
    private static final int COMMUNITY_INDEX = 0;
    private ChemSpaceOption csOption;

    public VisualRepresentation() {
        initComponents();

    }

    public ChemSpaceOption getChemSpaceOption() {
        return csOption;
    }

    public void setChemSpaceOption(ChemSpaceOption csOption) {
        ChemSpaceOption oldOption = this.csOption;
        this.csOption = csOption;
        switch (csOption) {
            case N_DIMENSIONAL:
                if (!jOption1.isSelected()) {
                    jOption1.setSelected(true);                    
                }
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option1.text"));
                break;
            case FULL_NETWORK:
                if (!jOption2.isSelected()) {
                    jOption2.setSelected(true);                    
                }
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option2.text"));
                break;
            case COMPRESSED_NETWORK:
                if (!jOption3.isSelected()) {
                    jOption3.setSelected(true);                    
                }
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option3.text"));
                break;
            case NONE:
                jOption1.setSelected(false);
                jOption2.setSelected(false);
                jOption2.setSelected(false);
                jOption3.setSelected(false);
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.text"));
        }
        firePropertyChange(CHANGED_OPTION, oldOption, csOption);
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(VisualRepresentation.class, "ChemSpaceRepresentation.name");
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
        jQuestionLabel = new javax.swing.JLabel();
        jInfo1 = new javax.swing.JLabel();
        jInfo2 = new javax.swing.JLabel();
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        jOption3 = new javax.swing.JRadioButton();
        bottomPanel = new javax.swing.JPanel();
        jOption3_1_Items = new javax.swing.JComboBox<>();
        extLabel = new javax.swing.JLabel();
        jBasedOnLabel = new javax.swing.JLabel();
        jMaxNumberLabel = new javax.swing.JLabel();
        jOption3_2_Items = new javax.swing.JComboBox<>();

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

        org.openide.awt.Mnemonics.setLocalizedText(jInfo1, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jInfo1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 0, 0);
        add(jInfo1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jInfo2, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jInfo2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(7, 5, 0, 0);
        add(jInfo2, gridBagConstraints);

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
        jOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption2, gridBagConstraints);

        buttonGroup1.add(jOption3);
        org.openide.awt.Mnemonics.setLocalizedText(jOption3, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption3.text")); // NOI18N
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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jOption3, gridBagConstraints);

        bottomPanel.setLayout(new java.awt.GridBagLayout());

        jOption3_1_Items.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "communities" }));
        jOption3_1_Items.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        bottomPanel.add(jOption3_1_Items, gridBagConstraints);

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

        org.openide.awt.Mnemonics.setLocalizedText(jBasedOnLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jBasedOnLabel.text")); // NOI18N
        jBasedOnLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 25, 0, 0);
        bottomPanel.add(jBasedOnLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jMaxNumberLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jMaxNumberLabel.text")); // NOI18N
        jMaxNumberLabel.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        bottomPanel.add(jMaxNumberLabel, gridBagConstraints);

        jOption3_2_Items.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1000", "500", "250", "100", "50", "10" }));
        jOption3_2_Items.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        bottomPanel.add(jOption3_2_Items, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption1ActionPerformed
        setChemSpaceOption(ChemSpaceOption.N_DIMENSIONAL);
    }//GEN-LAST:event_jOption1ActionPerformed

    private void jOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption2ActionPerformed
        setChemSpaceOption(ChemSpaceOption.FULL_NETWORK);
    }//GEN-LAST:event_jOption2ActionPerformed

    private void jOption3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption3ActionPerformed
        setChemSpaceOption(ChemSpaceOption.COMPRESSED_NETWORK);
    }//GEN-LAST:event_jOption3ActionPerformed

    private void jOption3ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption3ItemStateChanged
        jBasedOnLabel.setEnabled(jOption3.isSelected());
        jOption3_1_Items.setEnabled(jOption3.isSelected());
        jMaxNumberLabel.setEnabled(jOption3.isSelected());
        jOption3_2_Items.setEnabled(jOption3.isSelected());
    }//GEN-LAST:event_jOption3ItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel extLabel;
    private javax.swing.JLabel jBasedOnLabel;
    private javax.swing.JLabel jInfo1;
    private javax.swing.JLabel jInfo2;
    private javax.swing.JLabel jMaxNumberLabel;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JRadioButton jOption3;
    private javax.swing.JComboBox<String> jOption3_1_Items;
    private javax.swing.JComboBox<String> jOption3_2_Items;
    private javax.swing.JLabel jQuestionLabel;
    // End of variables declaration//GEN-END:variables
}
