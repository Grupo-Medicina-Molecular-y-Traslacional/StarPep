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

    static final int COORDINATE_NONE_OPTION = 0;
    static final int COORDINATE_BASED_OPTION = 1;
    static final int COORDINATE_FREE_OPTION = 2;

    static final String CHANGED_REPRESENTATION = "representation_option";
    static final String CHANGED_CHEM_SPACE = "chemspace_option";
    static final String CHANGED_NETWORK_TYPE = "network_type";

    private Representation representation;
    private ChemSpaceOption csOption;
    private NetworkType networkType;

    public VisualRepresentation() {
        initComponents();
        representation = Representation.NONE;
        csOption = ChemSpaceOption.NONE;
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
                if (jRepComboBox.getSelectedIndex() != COORDINATE_BASED_OPTION) {
                    jRepComboBox.setSelectedIndex(COORDINATE_BASED_OPTION);
                }
                cardLayout.show(settingPanel, "coordinateBased");
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option1.text"));
                break;
            case COORDINATE_FREE:
                if (jRepComboBox.getSelectedIndex() != COORDINATE_FREE_OPTION) {
                    jRepComboBox.setSelectedIndex(COORDINATE_FREE_OPTION);
                }
                cardLayout.show(settingPanel, "coordinateFree");
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.option2.text"));
                break;
            case NONE:
                if (jRepComboBox.getSelectedIndex() != COORDINATE_NONE_OPTION) {
                    jRepComboBox.setSelectedIndex(COORDINATE_NONE_OPTION);
                }
                cardLayout.show(settingPanel, "none");
                extLabel.setText(NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.extLabel.text"));
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
                if (!jOption11.isSelected()) {
                    jOption11.setSelected(true);
                }
                break;
            case ThreeD_SPACE:
                if (!jOption12.isSelected()) {
                    jOption12.setSelected(true);
                }
                break;
            case CHEM_SPACE_NETWORK:
                if (!jOption21.isSelected()) {
                    jOption21.setSelected(true);
                }
                break;
            case SEQ_SIMILARITY_NETWORK:
                if (!jOption22.isSelected()) {
                    jOption22.setSelected(true);
                }
                break;
            case NONE:
                jOption11.setSelected(false);
                jOption12.setSelected(false);
                jOption21.setSelected(false);
                jOption22.setSelected(false);
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
        jRepComboBox = new javax.swing.JComboBox<>();
        settingPanel = new javax.swing.JPanel();
        jNoneLabel = new javax.swing.JLabel();
        coordinateBasedPanel = new javax.swing.JPanel();
        jOption11 = new javax.swing.JRadioButton();
        jOption12 = new javax.swing.JRadioButton();
        coordinateBasedSettingPanel = new javax.swing.JPanel();
        coordinateFreePanel = new javax.swing.JPanel();
        jOption21 = new javax.swing.JRadioButton();
        jOption22 = new javax.swing.JRadioButton();
        coordinateFreeSettingPanel = new javax.swing.JPanel();
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

        jRepComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "-- Select", "Coordinate-based representation", "Coordinate-free representation" }));
        jRepComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRepComboBoxItemStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 0, 0);
        add(jRepComboBox, gridBagConstraints);

        settingPanel.setLayout(new java.awt.CardLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jNoneLabel, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jNoneLabel.text")); // NOI18N
        settingPanel.add(jNoneLabel, "none");

        coordinateBasedPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.coordinateBasedPanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        coordinateBasedPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jOption11);
        org.openide.awt.Mnemonics.setLocalizedText(jOption11, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption11.text")); // NOI18N
        jOption11.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption11ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        coordinateBasedPanel.add(jOption11, gridBagConstraints);

        buttonGroup1.add(jOption12);
        org.openide.awt.Mnemonics.setLocalizedText(jOption12, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption12.text")); // NOI18N
        jOption12.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        coordinateBasedPanel.add(jOption12, gridBagConstraints);

        coordinateBasedSettingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        coordinateBasedSettingPanel.setLayout(new java.awt.CardLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 5);
        coordinateBasedPanel.add(coordinateBasedSettingPanel, gridBagConstraints);

        settingPanel.add(coordinateBasedPanel, "coordinateBased");

        coordinateFreePanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.coordinateFreePanel.border.title"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Tahoma", 1, 11))); // NOI18N
        coordinateFreePanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jOption21);
        org.openide.awt.Mnemonics.setLocalizedText(jOption21, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption21.text")); // NOI18N
        jOption21.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption21ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        coordinateFreePanel.add(jOption21, gridBagConstraints);

        buttonGroup1.add(jOption22);
        org.openide.awt.Mnemonics.setLocalizedText(jOption22, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jOption22.text")); // NOI18N
        jOption22.setVerticalTextPosition(javax.swing.SwingConstants.TOP);
        jOption22.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jOption22ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        coordinateFreePanel.add(jOption22, gridBagConstraints);

        coordinateFreeSettingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        coordinateFreeSettingPanel.setLayout(new java.awt.BorderLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jHSPCheckBox, org.openide.util.NbBundle.getMessage(VisualRepresentation.class, "VisualRepresentation.jHSPCheckBox.text")); // NOI18N
        jHSPCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHSPCheckBoxActionPerformed(evt);
            }
        });
        coordinateFreeSettingPanel.add(jHSPCheckBox, java.awt.BorderLayout.CENTER);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 5);
        coordinateFreePanel.add(coordinateFreeSettingPanel, gridBagConstraints);

        settingPanel.add(coordinateFreePanel, "coordinateFree");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.gridy = 6;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 20, 5, 5);
        add(bottomPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jOption21ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption21ActionPerformed
        setChemSpaceOption(ChemSpaceOption.CHEM_SPACE_NETWORK);
    }//GEN-LAST:event_jOption21ActionPerformed

    private void jOption22ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption22ActionPerformed
        setChemSpaceOption(ChemSpaceOption.SEQ_SIMILARITY_NETWORK);
    }//GEN-LAST:event_jOption22ActionPerformed

    private void jRepComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRepComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            switch (jRepComboBox.getSelectedIndex()) {
                case COORDINATE_NONE_OPTION:
                    setRepresentation(Representation.NONE);
                    break;
                case COORDINATE_FREE_OPTION:
                    setRepresentation(Representation.COORDINATE_FREE);
                    break;
                case COORDINATE_BASED_OPTION:
                    setRepresentation(Representation.COORDINATE_BASED);
                    break;
            }
        }
    }//GEN-LAST:event_jRepComboBoxItemStateChanged

    private void jHSPCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHSPCheckBoxActionPerformed
        setNetworkType(jHSPCheckBox.isSelected() ? NetworkType.HSP : NetworkType.FULL);
    }//GEN-LAST:event_jHSPCheckBoxActionPerformed

    private void jOption11ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jOption11ActionPerformed
        setChemSpaceOption(ChemSpaceOption.TwoD_SPACE);
    }//GEN-LAST:event_jOption11ActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel coordinateBasedPanel;
    private javax.swing.JPanel coordinateBasedSettingPanel;
    private javax.swing.JPanel coordinateFreePanel;
    private javax.swing.JPanel coordinateFreeSettingPanel;
    private javax.swing.JLabel extLabel;
    private javax.swing.JCheckBox jHSPCheckBox;
    private javax.swing.JLabel jNoneLabel;
    private javax.swing.JRadioButton jOption11;
    private javax.swing.JRadioButton jOption12;
    private javax.swing.JRadioButton jOption21;
    private javax.swing.JRadioButton jOption22;
    private javax.swing.JLabel jQuestionLabel;
    private javax.swing.JComboBox<String> jRepComboBox;
    private javax.swing.JPanel settingPanel;
    // End of variables declaration//GEN-END:variables
}
