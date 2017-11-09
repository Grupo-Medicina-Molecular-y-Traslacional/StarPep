/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Hashtable;
import java.util.Set;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.services.ProjectManager;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel {

    protected final ProjectManager pc;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected final JXHyperlink select;
    protected final JLabel inputText;
    protected FeatureSelectionModel model;

    public FeatureSelectionPanel() {
        initComponents();
        pc = Lookup.getDefault().lookup(ProjectManager.class);

        select = new JXHyperlink();
        select.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.select.text"));
        select.setToolTipText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.select.toolTipText"));
        select.setClickedColor(new java.awt.Color(0, 51, 255));
        select.setFocusPainted(false);
        select.setFocusable(false);
        select.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        select.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        select.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setMolecularDescriptors();
            }
        });

        inputText = new JLabel();
        topPanel.add(new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.input.label")));
        topPanel.add(inputText);
        topPanel.add(select);

        // Configure sliders        
        uselessSlider.setMinimum(FeatureSelectionModel.ENTROPY_CUTOFF[0]);
        uselessSlider.setMaximum(FeatureSelectionModel.ENTROPY_CUTOFF[2]);
        uselessSlider.setValue(FeatureSelectionModel.ENTROPY_CUTOFF[1]);
        uselessSlider.setMajorTickSpacing(5);
        uselessSlider.setMinorTickSpacing(1);

        ChangeListener uselessListener = new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = (int) source.getValue();
                setEntropyCutoff(val);
            }
        };
        uselessSlider.addChangeListener(uselessListener);

        redundantSlider.setMinimum(FeatureSelectionModel.TANIMOTO_CUTOFF[0]);
        redundantSlider.setMaximum(FeatureSelectionModel.TANIMOTO_CUTOFF[2]);
        redundantSlider.setValue(FeatureSelectionModel.TANIMOTO_CUTOFF[1]);
        redundantSlider.setMajorTickSpacing(5);
        redundantSlider.setMinorTickSpacing(1);
        redundantSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = (int) source.getValue();
                setTanimotoCutoff(val);
            }
        });

        Hashtable<Integer, JLabel> uselessLabelTable = new Hashtable<>();
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.veryWeak")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.weak")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.moderate")));

        uselessSlider.setLabelTable(uselessLabelTable);

        Hashtable<Integer, JLabel> redundantLabelTable = new Hashtable<>();
        redundantLabelTable.put(FeatureSelectionModel.TANIMOTO_CUTOFF[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.veryStrong")));
        redundantLabelTable.put(FeatureSelectionModel.TANIMOTO_CUTOFF[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.strong")));
        redundantLabelTable.put(FeatureSelectionModel.TANIMOTO_CUTOFF[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.moderate")));

        redundantSlider.setLabelTable(redundantLabelTable);
    }

    public void setup(FeatureSelectionModel model) {
        this.model = model;
        setInputText();
        uselessSlider.setValue(model.getEntropyCutoff());
        redundantSlider.setValue(model.getTanimotoCutoff());

        if (model.isRemoveUseless()) {
            if (model.isRemoveRedundant()) {
                option3Button.setSelected(true);
            } else {
                option2Button.setSelected(true);
            }
        } else {
            option1Button.setSelected(true);
        }
    }

    private void setInputText() {
        Set<String> descriptorKeys = model.getDescriptorKeys();
        if (descriptorKeys == null || descriptorKeys.isEmpty()) {
            inputText.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.input.empty"));
        } else {
            inputText.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.input.text", descriptorKeys.size()));
        }
    }

    private void setEntropyCutoff(int val) {
        if (model != null && model.getEntropyCutoff() != val) {
            model.setEntropyCutoff(val);
        }
        uselessLabel.setText(val + "%");
    }

    private void setTanimotoCutoff(int val) {
        if (model != null && model.getTanimotoCutoff() != val) {
            model.setTanimotoCutoff(val);
        }
        redundantLabel.setText(val + "%");
    }

    private void setMolecularDescriptors() {
        AttributesModel attrModel = pc.getAttributesModel();
        if (attrModel != null && model != null) {
            DescriptorSelectionPanel selectionPanel = new DescriptorSelectionPanel(attrModel);
            if (model.getDescriptorKeys() != null) {
                selectionPanel.setSelectedDescriptorKeys(model.getDescriptorKeys());
            }
            DialogDescriptor dd = new DialogDescriptor(selectionPanel, NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.DescriptorSelectionPanel.title"));
            dd.setOptions(new Object[]{DialogDescriptor.OK_OPTION, DialogDescriptor.CANCEL_OPTION});
            if (DialogDisplayer.getDefault().notify(dd) == DialogDescriptor.OK_OPTION) {
                Set<String> selected = selectionPanel.getSelectedDescriptorKeys();
                model.setDescriptorKeys(selected);
                setInputText();
            }
        }
    }

    private void refreshInternalPanels() {
        boolean entropy = option3Button.isSelected() || option2Button.isSelected();
        entropyPanel.setEnabled(entropy);
        uselessSlider.setEnabled(entropy);
        uselessLabel.setEnabled(entropy);
        entropyInfoLabel.setEnabled(entropy);

        boolean tanimoto = option3Button.isSelected();
        tanimotoPanel.setEnabled(tanimoto);
        redundantSlider.setEnabled(tanimoto);
        redundantLabel.setEnabled(tanimoto);
        tanimotoInfoLabel.setEnabled(tanimoto);
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
        topPanel = new javax.swing.JPanel();
        featurePanel = new javax.swing.JPanel();
        option1Button = new javax.swing.JRadioButton();
        option2Button = new javax.swing.JRadioButton();
        option3Button = new javax.swing.JRadioButton();
        entropyPanel = new javax.swing.JPanel();
        uselessSlider = new javax.swing.JSlider();
        uselessLabel = new javax.swing.JLabel();
        entropyInfoLabel = new javax.swing.JLabel();
        tanimotoPanel = new javax.swing.JPanel();
        redundantSlider = new javax.swing.JSlider();
        redundantLabel = new javax.swing.JLabel();
        tanimotoInfoLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.border.title"))); // NOI18N
        setPreferredSize(new java.awt.Dimension(380, 160));
        setLayout(new java.awt.GridBagLayout());

        java.awt.FlowLayout flowLayout1 = new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 5);
        flowLayout1.setAlignOnBaseline(true);
        topPanel.setLayout(flowLayout1);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        add(topPanel, gridBagConstraints);

        featurePanel.setPreferredSize(new java.awt.Dimension(259, 130));
        featurePanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(option1Button);
        org.openide.awt.Mnemonics.setLocalizedText(option1Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.option1Button.text")); // NOI18N
        option1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option1ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        featurePanel.add(option1Button, gridBagConstraints);

        buttonGroup1.add(option2Button);
        org.openide.awt.Mnemonics.setLocalizedText(option2Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removeUselessRButton.text")); // NOI18N
        option2Button.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                option2ButtonItemStateChanged(evt);
            }
        });
        option2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        featurePanel.add(option2Button, gridBagConstraints);

        buttonGroup1.add(option3Button);
        org.openide.awt.Mnemonics.setLocalizedText(option3Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.selectRankedRButton.text")); // NOI18N
        option3Button.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                option3ButtonItemStateChanged(evt);
            }
        });
        option3Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option3ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        featurePanel.add(option3Button, gridBagConstraints);

        entropyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyPanel.border.title"))); // NOI18N
        entropyPanel.setLayout(new java.awt.GridBagLayout());

        uselessSlider.setPaintLabels(true);
        uselessSlider.setPaintTicks(true);
        uselessSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.toolTipText")); // NOI18N
        uselessSlider.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        entropyPanel.add(uselessSlider, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(uselessLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessLabel.text")); // NOI18N
        uselessLabel.setMaximumSize(new java.awt.Dimension(23, 14));
        uselessLabel.setMinimumSize(new java.awt.Dimension(23, 14));
        uselessLabel.setPreferredSize(new java.awt.Dimension(23, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        entropyPanel.add(uselessLabel, gridBagConstraints);

        entropyInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(entropyInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        entropyPanel.add(entropyInfoLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        featurePanel.add(entropyPanel, gridBagConstraints);

        tanimotoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.tanimotoPanel.border.title"))); // NOI18N
        tanimotoPanel.setLayout(new java.awt.GridBagLayout());

        redundantSlider.setPaintLabels(true);
        redundantSlider.setPaintTicks(true);
        redundantSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.toolTipText")); // NOI18N
        redundantSlider.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        tanimotoPanel.add(redundantSlider, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(redundantLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantLabel.text")); // NOI18N
        redundantLabel.setMaximumSize(new java.awt.Dimension(23, 14));
        redundantLabel.setMinimumSize(new java.awt.Dimension(23, 14));
        redundantLabel.setPreferredSize(new java.awt.Dimension(23, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        tanimotoPanel.add(redundantLabel, gridBagConstraints);

        tanimotoInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(tanimotoInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.tanimotoInfoLabel.text")); // NOI18N
        tanimotoInfoLabel.setMaximumSize(new java.awt.Dimension(23, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        tanimotoPanel.add(tanimotoInfoLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        featurePanel.add(tanimotoPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(featurePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void option3ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option3ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(true);
            model.setRemoveRedundant(true);
        }
    }//GEN-LAST:event_option3ButtonActionPerformed

    private void option2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option2ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(true);
            model.setRemoveRedundant(false);
        }
    }//GEN-LAST:event_option2ButtonActionPerformed

    private void option1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(false);
            model.setRemoveRedundant(false);
        }
    }//GEN-LAST:event_option1ButtonActionPerformed

    private void option3ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option3ButtonItemStateChanged
        refreshInternalPanels();
    }//GEN-LAST:event_option3ButtonItemStateChanged

    private void option2ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option2ButtonItemStateChanged
        refreshInternalPanels();
    }//GEN-LAST:event_option2ButtonItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel entropyInfoLabel;
    private javax.swing.JPanel entropyPanel;
    private javax.swing.JPanel featurePanel;
    private javax.swing.JRadioButton option1Button;
    private javax.swing.JRadioButton option2Button;
    private javax.swing.JRadioButton option3Button;
    private javax.swing.JLabel redundantLabel;
    private javax.swing.JSlider redundantSlider;
    private javax.swing.JLabel tanimotoInfoLabel;
    private javax.swing.JPanel tanimotoPanel;
    private javax.swing.JPanel topPanel;
    private javax.swing.JLabel uselessLabel;
    private javax.swing.JSlider uselessSlider;
    // End of variables declaration//GEN-END:variables

}
