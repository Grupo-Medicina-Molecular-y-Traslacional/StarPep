/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.model.FeatureSelectionModel;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel {

    protected final FeatureSelectionModel model;

    public FeatureSelectionPanel(FeatureSelectionModel model) {
        initComponents();
        this.model = model;

        // Configure sliders        
        uselessSlider.setMinimum(FeatureSelectionModel.ENTROPY_CUTOFF_MIN);
        uselessSlider.setMaximum(FeatureSelectionModel.ENTROPY_CUTOFF_MAX);
        uselessSlider.setMajorTickSpacing(FeatureSelectionModel.ENTROPY_MAJORTICKSPACING);
        uselessSlider.setMinorTickSpacing(FeatureSelectionModel.ENTROPY_MINORTICKSPACING);
        int val = model.getEntropyCutoff();
        uselessSlider.setValue(val);
        uselessLabel.setText(val + "%");
        uselessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!uselessSlider.getValueIsAdjusting()) {
                    int val = uselessSlider.getValue();
                    uselessLabel.setText(val + "%");
                    model.setEntropyCutoff(val);
                }
            }
        });

        Hashtable<Integer, JLabel> uselessLabelTable = new Hashtable<>();
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.low")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.middle")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.high")));

        uselessSlider.setLabelTable(uselessLabelTable);

        redundantSlider.setMinimum(FeatureSelectionModel.CORRELATION_CUTOFF_MIN);
        redundantSlider.setMaximum(FeatureSelectionModel.CORRELATION_CUTOFF_MAX);
        redundantSlider.setMajorTickSpacing(FeatureSelectionModel.CORRELATION_MAJORTICKSPACING);
        redundantSlider.setMinorTickSpacing(FeatureSelectionModel.CORRELATION_MINORTICKSPACING);
        val = model.getCorrelationCutoff();
        redundantSlider.setValue(val);
        redundantLabel.setText(val + "%");
        redundantSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!redundantSlider.getValueIsAdjusting()) {
                    int val = redundantSlider.getValue();
                    redundantLabel.setText(val + "%");
                    model.setCorrelationCutoff(val);
                }
            }
        });

        Hashtable<Integer, JLabel> redundantLabelTable = new Hashtable<>();
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.low")));
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.middle")));
        redundantLabelTable.put(FeatureSelectionModel.CORRELATION_CUTOFF_REFS[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.high")));

        redundantSlider.setLabelTable(redundantLabelTable);

        if (model.isRemoveUseless()) {
            if (model.isRemoveRedundant()) {
                option2Button.setSelected(true);
            } else {
                option1Button.setSelected(true);
            }
        }

    }

//    static {
//        UIManager.put("Slider.paintValue", false);
//    }
    
    public void refreshState() {
        boolean enabled = (option2Button.isSelected() || option1Button.isSelected()) && !model.isRunning();
        entropyPanel.setEnabled(enabled);
        uselessSlider.setEnabled(enabled);
        uselessLabel.setEnabled(enabled);
        entropyInfoLabel.setEnabled(enabled);
        jLessUselessButton.setEnabled(enabled);
        jMoreUselessButton.setEnabled(enabled);

        enabled = option2Button.isSelected() && !model.isRunning();
        correlationPanel.setEnabled(enabled);
        redundantSlider.setEnabled(enabled);
        redundantLabel.setEnabled(enabled);
        redundantInfoLabel.setEnabled(enabled);
        jLessRedundantButton.setEnabled(enabled);
        jMoreRedundantButton.setEnabled(enabled);
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
        option1Button = new javax.swing.JRadioButton();
        option2Button = new javax.swing.JRadioButton();
        jSettingPanel = new javax.swing.JPanel();
        entropyPanel = new javax.swing.JPanel();
        uselessLabel = new javax.swing.JLabel();
        entropyInfoLabel = new javax.swing.JLabel();
        jEntropyToolBar = new javax.swing.JToolBar();
        jLessUselessButton = new javax.swing.JButton();
        uselessSlider = new javax.swing.JSlider();
        jMoreUselessButton = new javax.swing.JButton();
        correlationPanel = new javax.swing.JPanel();
        redundantLabel = new javax.swing.JLabel();
        redundantInfoLabel = new javax.swing.JLabel();
        jRedundantToolBar = new javax.swing.JToolBar();
        jLessRedundantButton = new javax.swing.JButton();
        redundantSlider = new javax.swing.JSlider();
        jMoreRedundantButton = new javax.swing.JButton();
        jResetButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(440, 380));
        setPreferredSize(new java.awt.Dimension(440, 380));
        setLayout(new java.awt.GridBagLayout());

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
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(option1Button, gridBagConstraints);

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
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(option2Button, gridBagConstraints);

        jSettingPanel.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        jSettingPanel.setLayout(new java.awt.GridBagLayout());

        entropyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyPanel.border.title"))); // NOI18N
        entropyPanel.setPreferredSize(new java.awt.Dimension(493, 140));
        entropyPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(uselessLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessLabel.text")); // NOI18N
        uselessLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        uselessLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        uselessLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        entropyPanel.add(uselessLabel, gridBagConstraints);

        entropyInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(entropyInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        entropyPanel.add(entropyInfoLabel, gridBagConstraints);

        jEntropyToolBar.setFloatable(false);
        jEntropyToolBar.setRollover(true);
        jEntropyToolBar.setPreferredSize(new java.awt.Dimension(338, 90));

        jLessUselessButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/less.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLessUselessButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.jLessUselessButton.text")); // NOI18N
        jLessUselessButton.setFocusable(false);
        jLessUselessButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLessUselessButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jEntropyToolBar.add(jLessUselessButton);

        uselessSlider.setMajorTickSpacing(10);
        uselessSlider.setMaximum(30);
        uselessSlider.setMinimum(10);
        uselessSlider.setMinorTickSpacing(5);
        uselessSlider.setPaintLabels(true);
        uselessSlider.setPaintTicks(true);
        uselessSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.toolTipText")); // NOI18N
        uselessSlider.setEnabled(false);
        uselessSlider.setMinimumSize(new java.awt.Dimension(280, 80));
        uselessSlider.setPreferredSize(new java.awt.Dimension(280, 80));
        jEntropyToolBar.add(uselessSlider);

        jMoreUselessButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/more.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jMoreUselessButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.jMoreUselessButton.text")); // NOI18N
        jMoreUselessButton.setFocusable(false);
        jMoreUselessButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMoreUselessButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jEntropyToolBar.add(jMoreUselessButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        entropyPanel.add(jEntropyToolBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        jSettingPanel.add(entropyPanel, gridBagConstraints);

        correlationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.correlationPanel.border.title"))); // NOI18N
        correlationPanel.setPreferredSize(new java.awt.Dimension(493, 140));
        correlationPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(redundantLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantLabel.text")); // NOI18N
        redundantLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        redundantLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        redundantLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        correlationPanel.add(redundantLabel, gridBagConstraints);

        redundantInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(redundantInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantInfoLabel.text")); // NOI18N
        redundantInfoLabel.setMaximumSize(new java.awt.Dimension(23, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        correlationPanel.add(redundantInfoLabel, gridBagConstraints);

        jRedundantToolBar.setFloatable(false);
        jRedundantToolBar.setRollover(true);
        jRedundantToolBar.setPreferredSize(new java.awt.Dimension(338, 90));

        jLessRedundantButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/less.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLessRedundantButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.jLessRedundantButton.text")); // NOI18N
        jLessRedundantButton.setFocusable(false);
        jLessRedundantButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLessRedundantButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jRedundantToolBar.add(jLessRedundantButton);

        redundantSlider.setMajorTickSpacing(20);
        redundantSlider.setMaximum(80);
        redundantSlider.setMinimum(40);
        redundantSlider.setMinorTickSpacing(10);
        redundantSlider.setPaintLabels(true);
        redundantSlider.setPaintTicks(true);
        redundantSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.toolTipText")); // NOI18N
        redundantSlider.setEnabled(false);
        redundantSlider.setMinimumSize(new java.awt.Dimension(280, 80));
        redundantSlider.setPreferredSize(new java.awt.Dimension(280, 80));
        jRedundantToolBar.add(redundantSlider);

        jMoreRedundantButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/more.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jMoreRedundantButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.jMoreRedundantButton.text")); // NOI18N
        jMoreRedundantButton.setFocusable(false);
        jMoreRedundantButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMoreRedundantButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jRedundantToolBar.add(jMoreRedundantButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        correlationPanel.add(jRedundantToolBar, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jSettingPanel.add(correlationPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jResetButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.jResetButton.text")); // NOI18N
        jResetButton.setMaximumSize(new java.awt.Dimension(145, 23));
        jResetButton.setMinimumSize(new java.awt.Dimension(145, 23));
        jResetButton.setPreferredSize(new java.awt.Dimension(145, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 5);
        jSettingPanel.add(jResetButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 2, 5);
        add(jSettingPanel, gridBagConstraints);
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
        refreshState();
    }//GEN-LAST:event_option2ButtonItemStateChanged

    private void option1ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option1ButtonItemStateChanged
        refreshState();
    }//GEN-LAST:event_option1ButtonItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel correlationPanel;
    private javax.swing.JLabel entropyInfoLabel;
    private javax.swing.JPanel entropyPanel;
    private javax.swing.JToolBar jEntropyToolBar;
    private javax.swing.JButton jLessRedundantButton;
    private javax.swing.JButton jLessUselessButton;
    private javax.swing.JButton jMoreRedundantButton;
    private javax.swing.JButton jMoreUselessButton;
    private javax.swing.JToolBar jRedundantToolBar;
    private javax.swing.JButton jResetButton;
    private javax.swing.JPanel jSettingPanel;
    private javax.swing.JRadioButton option1Button;
    private javax.swing.JRadioButton option2Button;
    private javax.swing.JLabel redundantInfoLabel;
    private javax.swing.JLabel redundantLabel;
    private javax.swing.JSlider redundantSlider;
    private javax.swing.JLabel uselessLabel;
    private javax.swing.JSlider uselessSlider;
    // End of variables declaration//GEN-END:variables

}
