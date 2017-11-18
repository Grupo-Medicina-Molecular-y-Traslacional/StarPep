/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.task.FeatureSelector;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected final AttributesModel attrModel;
    protected final FeatureSelectionModel model;
    protected final JXBusyLabel busyLabel;

    public FeatureSelectionPanel(FeatureSelectionModel model, AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;
        this.model = model;

        // Configure sliders        
        uselessSlider.setMinimum(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[0]);
        uselessSlider.setMaximum(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[1]);
        uselessSlider.setMajorTickSpacing(10);
        uselessSlider.setMinorTickSpacing(1);
        int val = model.getEntropyCutoff();
        uselessSlider.setValue(val);
        uselessLabel.setText(val + "%");
        uselessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = (int) source.getValue();
                uselessLabel.setText(val + "%");
            }
        });

        Hashtable<Integer, JLabel> uselessLabelTable = new Hashtable<>();
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.weak")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.moderate")));

        uselessSlider.setLabelTable(uselessLabelTable);

        redundantSlider.setMinimum(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[0]);
        redundantSlider.setMaximum(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[1]);
        redundantSlider.setMajorTickSpacing(8);
        redundantSlider.setMinorTickSpacing(1);
        val = model.getCorrelationCutoff();
        redundantSlider.setValue(val);
        redundantLabel.setText(val + "%");
        redundantSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = (int) source.getValue();
                redundantLabel.setText(val + "%");
            }
        });

        Hashtable<Integer, JLabel> redundantLabelTable = new Hashtable<>();
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.strong")));
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.moderate")));

        redundantSlider.setLabelTable(redundantLabelTable);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removing.text"));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boolean running = model.isRunning();
        busyLabel.setBusy(running);
        busyLabel.setVisible(running);
        rightPanel.add(busyLabel);
        
        
        if (model.isRemoveUseless()) {
            if (model.isRemoveRedundant()) {
                option2Button.setSelected(true);
            } else {
                option1Button.setSelected(true);
            }
        }
        
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                model.addPropertyChangeListener(FeatureSelectionPanel.this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                model.removePropertyChangeListener(FeatureSelectionPanel.this);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });         

    }

    static {
        UIManager.put("Slider.paintValue", false);
    }

    private void refreshInternalPanels() {
        boolean enabled = (option2Button.isSelected() || option1Button.isSelected()) && !model.isRunning();
        entropyPanel.setEnabled(enabled);
        uselessSlider.setEnabled(enabled);
        uselessLabel.setEnabled(enabled);
        entropyInfoLabel.setEnabled(enabled);

        enabled = option2Button.isSelected() && !model.isRunning();
        tanimotoPanel.setEnabled(enabled);
        redundantSlider.setEnabled(enabled);
        redundantLabel.setEnabled(enabled);
        tanimotoInfoLabel.setEnabled(enabled);
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
        option1Button = new javax.swing.JRadioButton();
        option2Button = new javax.swing.JRadioButton();
        entropyPanel = new javax.swing.JPanel();
        uselessSlider = new javax.swing.JSlider();
        uselessLabel = new javax.swing.JLabel();
        entropyInfoLabel = new javax.swing.JLabel();
        extLabel1 = new javax.swing.JLabel();
        tanimotoPanel = new javax.swing.JPanel();
        redundantSlider = new javax.swing.JSlider();
        redundantLabel = new javax.swing.JLabel();
        tanimotoInfoLabel = new javax.swing.JLabel();
        extLabel2 = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(440, 250));
        setPreferredSize(new java.awt.Dimension(440, 250));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setPreferredSize(new java.awt.Dimension(259, 130));
        centerPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(option1Button);
        org.openide.awt.Mnemonics.setLocalizedText(option1Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removeUselessRButton.text")); // NOI18N
        option1Button.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                option1ButtonItemStateChanged(evt);
            }
        });
        option1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option1ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        centerPanel.add(option1Button, gridBagConstraints);

        buttonGroup1.add(option2Button);
        org.openide.awt.Mnemonics.setLocalizedText(option2Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.selectRankedRButton.text")); // NOI18N
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
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        centerPanel.add(option2Button, gridBagConstraints);

        entropyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyPanel.border.title"))); // NOI18N
        entropyPanel.setLayout(new java.awt.GridBagLayout());

        uselessSlider.setPaintLabels(true);
        uselessSlider.setPaintTicks(true);
        uselessSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.toolTipText")); // NOI18N
        uselessSlider.setValue(0);
        uselessSlider.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        entropyPanel.add(uselessSlider, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(uselessLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessLabel.text")); // NOI18N
        uselessLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        uselessLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        uselessLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        entropyPanel.add(uselessLabel, gridBagConstraints);

        entropyInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(entropyInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        entropyPanel.add(entropyInfoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel1, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.extLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        entropyPanel.add(extLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        centerPanel.add(entropyPanel, gridBagConstraints);

        tanimotoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.tanimotoPanel.border.title"))); // NOI18N
        tanimotoPanel.setLayout(new java.awt.GridBagLayout());

        redundantSlider.setPaintLabels(true);
        redundantSlider.setPaintTicks(true);
        redundantSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.toolTipText")); // NOI18N
        redundantSlider.setValue(0);
        redundantSlider.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        tanimotoPanel.add(redundantSlider, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(redundantLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantLabel.text")); // NOI18N
        redundantLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        redundantLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        redundantLabel.setPreferredSize(new java.awt.Dimension(31, 14));
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

        org.openide.awt.Mnemonics.setLocalizedText(extLabel2, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.extLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tanimotoPanel.add(extLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        centerPanel.add(tanimotoPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(centerPanel, gridBagConstraints);

        rightPanel.setMaximumSize(new java.awt.Dimension(110, 58));
        rightPanel.setMinimumSize(new java.awt.Dimension(110, 58));
        rightPanel.setPreferredSize(new java.awt.Dimension(110, 58));
        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.Y_AXIS));

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/delete.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removeButton.text")); // NOI18N
        removeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        removeButton.setMaximumSize(new java.awt.Dimension(99, 29));
        removeButton.setMinimumSize(new java.awt.Dimension(99, 29));
        removeButton.setPreferredSize(new java.awt.Dimension(99, 29));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        rightPanel.add(removeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        add(rightPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void option2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option2ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(true);
            model.setRemoveRedundant(true);
        }
    }//GEN-LAST:event_option2ButtonActionPerformed

    private void option1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(true);
            model.setRemoveRedundant(false);
        }
    }//GEN-LAST:event_option1ButtonActionPerformed

    private void option2ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option2ButtonItemStateChanged
        refreshInternalPanels();
    }//GEN-LAST:event_option2ButtonItemStateChanged

    private void option1ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option1ButtonItemStateChanged
        refreshInternalPanels();
    }//GEN-LAST:event_option1ButtonItemStateChanged

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (model.getOwnerWS().isBusy()) {
            DialogDisplayer.getDefault().notify(model.getOwnerWS().getBusyNotifyDescriptor());
        } else {
            model.setEntropyCutoff(uselessSlider.getValue());
            model.setCorrelationCutoff(redundantSlider.getValue());
            FeatureSelector selector = new FeatureSelector(model, attrModel);
            selector.execute();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel entropyInfoLabel;
    private javax.swing.JPanel entropyPanel;
    private javax.swing.JLabel extLabel1;
    private javax.swing.JLabel extLabel2;
    private javax.swing.JRadioButton option1Button;
    private javax.swing.JRadioButton option2Button;
    private javax.swing.JLabel redundantLabel;
    private javax.swing.JSlider redundantSlider;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JLabel tanimotoInfoLabel;
    private javax.swing.JPanel tanimotoPanel;
    private javax.swing.JLabel uselessLabel;
    private javax.swing.JSlider uselessSlider;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(model) && evt.getPropertyName().equals(FeatureSelectionModel.RUNNING)){
            boolean running = model.isRunning();
            busyLabel.setBusy(running);
            busyLabel.setVisible(running);  
            refreshInternalPanels();
        }        
    }

}
