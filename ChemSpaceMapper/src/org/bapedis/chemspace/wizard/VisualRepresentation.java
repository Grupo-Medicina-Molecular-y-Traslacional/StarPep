/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.wizard;

import java.awt.CardLayout;
import java.awt.event.ItemEvent;
import javax.swing.JPanel;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.Representation;
import org.openide.util.NbBundle;

public final class VisualRepresentation extends JPanel {

    static final int THREED_OPTION = -1; // Not supported
    static final int TWOD_OPTION = 0;

    static final int CHEMSPACE_NETWORK_OPTION = 0;
    static final int SEQUENCE_NETWORK_OPTION = 1;

    static final String CHANGED_REPRESENTATION = "representation_option";
    static final String CHANGED_CHEM_SPACE = "chemspace_option";
    static final String CHANGED_NETWORK_TYPE = "network_type";

    private Representation representation;
    private ChemSpaceOption csOption;
    private NetworkType networkType;

    public VisualRepresentation() {
        initComponents();
        representation = Representation.COORDINATE_BASED;
        csOption = ChemSpaceOption.TwoD_SPACE;
        networkType = NetworkType.FULL;

        setRepresentation(representation);
        setChemSpaceOption(csOption);
        setNetworkType(networkType);
    }

    public Representation getRepresentation() {
        return representation;
    }

    public void setRepresentation(Representation rep) {
        Representation oldRepresentation = this.representation;
        this.representation = rep;
        CardLayout cardLayout = (CardLayout) settingPanel.getLayout();
        switch (rep) {
            case COORDINATE_BASED:
                if (!jOption1.isSelected()) {
                    jOption1.setSelected(true);
                }
                cardLayout.show(settingPanel, "coordinateBased");
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option1.text"));
                break;
            case COORDINATE_FREE:
                if (!jOption2.isSelected()) {
                    jOption2.setSelected(true);
                }
                cardLayout.show(settingPanel, "coordinateFree");
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option2.text"));
                break;
        }
        firePropertyChange(CHANGED_REPRESENTATION, oldRepresentation, rep);
    }

    public ChemSpaceOption getChemSpaceOption() {
        return csOption;
    }

    public void setChemSpaceOption(ChemSpaceOption csOption) {
        ChemSpaceOption oldOption = this.csOption;
        this.csOption = csOption;
        switch (csOption) {
            case TwoD_SPACE:
                if (jOption1CB.getSelectedIndex() != TWOD_OPTION) {
                    jOption1CB.setSelectedIndex(TWOD_OPTION);
                }
                break;
            case ThreeD_SPACE:
                if (jOption1CB.getSelectedIndex() != THREED_OPTION) {
                    jOption1CB.setSelectedIndex(THREED_OPTION);
                }
                break;
            case CHEM_SPACE_NETWORK:
                if (jOption2CB.getSelectedIndex() != CHEMSPACE_NETWORK_OPTION) {
                    jOption2CB.setSelectedIndex(CHEMSPACE_NETWORK_OPTION);
                }
                break;
            case SEQ_SIMILARITY_NETWORK:
                if (jOption2CB.getSelectedIndex() != SEQUENCE_NETWORK_OPTION){
                    jOption2CB.setSelectedIndex(SEQUENCE_NETWORK_OPTION);
                }
                break;
        }
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
        switch (networkType) {
            case FULL:
                if (jHSPCheckBox.isSelected()) {
                    jHSPCheckBox.setSelected(false);
                }
                break;
            case HSP:
                if (!jHSPCheckBox.isSelected()) {
                    jHSPCheckBox.setSelected(true);
                }
                break;
        }
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
        jOption1 = new javax.swing.JRadioButton();
        jOption2 = new javax.swing.JRadioButton();
        settingPanel = new javax.swing.JPanel();
        jNoneLabel = new javax.swing.JLabel();
        coordinateBasedPanel = new javax.swing.JPanel();
        jOption1CB = new javax.swing.JComboBox<>();
        extLabel1 = new javax.swing.JLabel();
        coordinateFreePanel = new javax.swing.JPanel();
        jOption2CB = new javax.swing.JComboBox<>();
        jHSPCheckBox = new javax.swing.JCheckBox();
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
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jOption1, gridBagConstraints);

        buttonGroup1.add(jOption2);
        org.openide.awt.Mnemonics.setLocalizedText(jOption2, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption2.text")); // NOI18N
        jOption2.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jOption2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(jOption2, gridBagConstraints);

        settingPanel.setLayout(new java.awt.CardLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jNoneLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jNoneLabel.text")); // NOI18N
        settingPanel.add(jNoneLabel, "none");

        coordinateBasedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.coordinateBasedPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        coordinateBasedPanel.setLayout(new java.awt.GridBagLayout());

        jOption1CB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2D" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        coordinateBasedPanel.add(jOption1CB, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel1, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        coordinateBasedPanel.add(extLabel1, gridBagConstraints);

        settingPanel.add(coordinateBasedPanel, "coordinateBased");

        coordinateFreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.coordinateFreePanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        coordinateFreePanel.setLayout(new java.awt.GridBagLayout());

        jOption2CB.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chemical Space Network", "Sequence Similarity Network" }));
        jOption2CB.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jOption2CBItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        coordinateFreePanel.add(jOption2CB, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jHSPCheckBox, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jHSPCheckBox.text")); // NOI18N
        jHSPCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHSPCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        coordinateFreePanel.add(jHSPCheckBox, gridBagConstraints);

        settingPanel.add(coordinateFreePanel, "coordinateFree");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 5);
        add(settingPanel, gridBagConstraints);

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
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 5);
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jOption2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption2ActionPerformed
        setRepresentation(Representation.COORDINATE_FREE);
    }//GEN-LAST:event_jOption2ActionPerformed

    private void jHSPCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHSPCheckBoxActionPerformed
        setNetworkType(jHSPCheckBox.isSelected() ? NetworkType.HSP : NetworkType.FULL);
    }//GEN-LAST:event_jHSPCheckBoxActionPerformed

    private void jOption2CBItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jOption2CBItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            switch (jOption2CB.getSelectedIndex()) {
                case CHEMSPACE_NETWORK_OPTION:
                    setChemSpaceOption(ChemSpaceOption.CHEM_SPACE_NETWORK);
                    break;
                case SEQUENCE_NETWORK_OPTION:
                    setChemSpaceOption(ChemSpaceOption.SEQ_SIMILARITY_NETWORK);
                    break;
            }
        }      
    }//GEN-LAST:event_jOption2CBItemStateChanged

    private void jOption1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption1ActionPerformed
        setRepresentation(Representation.COORDINATE_BASED);
    }//GEN-LAST:event_jOption1ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel coordinateBasedPanel;
    private javax.swing.JPanel coordinateFreePanel;
    private javax.swing.JLabel extLabel;
    private javax.swing.JLabel extLabel1;
    private javax.swing.JCheckBox jHSPCheckBox;
    private javax.swing.JLabel jNoneLabel;
    private javax.swing.JRadioButton jOption1;
    private javax.swing.JComboBox<String> jOption1CB;
    private javax.swing.JRadioButton jOption2;
    private javax.swing.JComboBox<String> jOption2CB;
    private javax.swing.JLabel jQuestionLabel;
    private javax.swing.JPanel settingPanel;
    // End of variables declaration//GEN-END:variables
}
