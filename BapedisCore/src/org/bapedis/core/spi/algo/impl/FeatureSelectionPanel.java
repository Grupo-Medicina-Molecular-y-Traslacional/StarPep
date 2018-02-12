/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Hashtable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel implements AlgorithmSetupUI, PropertyChangeListener {

    private FeatureSelectionAlgo algorithm;

    public FeatureSelectionPanel() {
        initComponents();
        
        //Configure correlation methods
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for(String method: FeatureSelectionAlgo.CORRELATION_METHODS){
            model.addElement(method);
        }
        redundantComboBox.setModel(model);

        // Configure sliders        
        uselessSlider.setMinimum(FeatureSelectionAlgo.ENTROPY_CUTOFF_MIN);
        uselessSlider.setMaximum(FeatureSelectionAlgo.ENTROPY_CUTOFF_MAX);
        uselessSlider.setMajorTickSpacing(FeatureSelectionAlgo.ENTROPY_MAJORTICKSPACING);
        uselessSlider.setMinorTickSpacing(FeatureSelectionAlgo.ENTROPY_MINORTICKSPACING);
        uselessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!uselessSlider.getValueIsAdjusting()) {
                    int val = uselessSlider.getValue();
                    entropyLabel.setText(val + "%");
                    if (algorithm != null) {
                        algorithm.setEntropyCutoff(val);
                    }
                }
            }
        });

        Hashtable<Integer, JLabel> uselessLabelTable = new Hashtable<>();
        uselessLabelTable.put(FeatureSelectionAlgo.ENTROPY_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.low")));
        uselessLabelTable.put(FeatureSelectionAlgo.ENTROPY_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.middle")));
        uselessLabelTable.put(FeatureSelectionAlgo.ENTROPY_CUTOFF_REFS[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.high")));
        uselessSlider.setLabelTable(uselessLabelTable);

        redundantSlider.setMinimum(FeatureSelectionAlgo.CORRELATION_CUTOFF_MIN);
        redundantSlider.setMaximum(FeatureSelectionAlgo.CORRELATION_CUTOFF_MAX);
        redundantSlider.setMajorTickSpacing(FeatureSelectionAlgo.CORRELATION_MAJORTICKSPACING);
        redundantSlider.setMinorTickSpacing(FeatureSelectionAlgo.CORRELATION_MINORTICKSPACING);
        redundantSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                if (!redundantSlider.getValueIsAdjusting()) {
                    int val = redundantSlider.getValue();
                    correlationLabel.setText(val + "%");
                    if (algorithm != null) {
                        algorithm.setCorrelationCutoff(val);
                    }
                }
            }
        });

        Hashtable<Integer, JLabel> redundantLabelTable = new Hashtable<>();
        redundantLabelTable.put(FeatureSelectionAlgo.CORRELATION_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.low")));
        redundantLabelTable.put(FeatureSelectionAlgo.CORRELATION_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.middle")));
        redundantLabelTable.put(FeatureSelectionAlgo.CORRELATION_CUTOFF_REFS[2], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.high")));
        redundantSlider.setLabelTable(redundantLabelTable);                

    }

//    static {
//        UIManager.put("Slider.paintValue", false);
//    }
    
    private void refreshState() {
        boolean running = algorithm != null && algorithm.isRunning();
        boolean enabled = !running && isEnabled();
        option1Button.setEnabled(enabled);
        option2Button.setEnabled(enabled);
        jResetButton.setEnabled(enabled);

        enabled = (option2Button.isSelected() || option1Button.isSelected()) && !running && isEnabled();
        uselessSlider.setEnabled(enabled);
        entropyLabel.setEnabled(enabled);
        entropyInfoLabel.setEnabled(enabled);
        jLessUselessButton.setEnabled(enabled);
        jMoreUselessButton.setEnabled(enabled);

        enabled = option2Button.isSelected() && !running && isEnabled();
        redundantLabel.setEnabled(enabled);
        redundantComboBox.setEnabled(enabled);
        redundantSlider.setEnabled(enabled);
        correlationLabel.setEnabled(enabled);
        correlationInfoLabel.setEnabled(enabled);
        jLessRedundantButton.setEnabled(enabled);
        jMoreRedundantButton.setEnabled(enabled);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
        refreshState();
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
        jResetButton = new javax.swing.JButton();
        entropyLabel = new javax.swing.JLabel();
        entropyInfoLabel = new javax.swing.JLabel();
        jEntropyToolBar = new javax.swing.JToolBar();
        jLessUselessButton = new javax.swing.JButton();
        uselessSlider = new javax.swing.JSlider();
        jMoreUselessButton = new javax.swing.JButton();
        correlationLabel = new javax.swing.JLabel();
        correlationInfoLabel = new javax.swing.JLabel();
        jRedundantToolBar = new javax.swing.JToolBar();
        jLessRedundantButton = new javax.swing.JButton();
        redundantSlider = new javax.swing.JSlider();
        jMoreRedundantButton = new javax.swing.JButton();
        redundantComboBox = new javax.swing.JComboBox<>();
        redundantLabel = new javax.swing.JLabel();

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

        org.openide.awt.Mnemonics.setLocalizedText(jResetButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.jResetButton.text")); // NOI18N
        jResetButton.setMaximumSize(new java.awt.Dimension(145, 23));
        jResetButton.setMinimumSize(new java.awt.Dimension(145, 23));
        jResetButton.setPreferredSize(new java.awt.Dimension(145, 23));
        jResetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jResetButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        jSettingPanel.add(jResetButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(entropyLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyLabel.text")); // NOI18N
        entropyLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        entropyLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        entropyLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jSettingPanel.add(entropyLabel, gridBagConstraints);

        entropyInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(entropyInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jSettingPanel.add(entropyInfoLabel, gridBagConstraints);

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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSettingPanel.add(jEntropyToolBar, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(correlationLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.correlationLabel.text")); // NOI18N
        correlationLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        correlationLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        correlationLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jSettingPanel.add(correlationLabel, gridBagConstraints);

        correlationInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(correlationInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.correlationInfoLabel.text")); // NOI18N
        correlationInfoLabel.setMaximumSize(new java.awt.Dimension(23, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jSettingPanel.add(correlationInfoLabel, gridBagConstraints);

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
        gridBagConstraints.gridy = 5;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        jSettingPanel.add(jRedundantToolBar, gridBagConstraints);

        redundantComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redundantComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        jSettingPanel.add(redundantComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(redundantLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantLabel.text")); // NOI18N
        redundantLabel.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantLabel.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        jSettingPanel.add(redundantLabel, gridBagConstraints);

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
        if (algorithm != null) {
            algorithm.setRemoveUseless(true);
            algorithm.setRemoveRedundant(true);
        }
    }//GEN-LAST:event_option2ButtonActionPerformed

    private void option1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1ButtonActionPerformed
        if (algorithm != null) {
            algorithm.setRemoveUseless(true);
            algorithm.setRemoveRedundant(false);
        }
    }//GEN-LAST:event_option1ButtonActionPerformed

    private void option2ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option2ButtonItemStateChanged
        refreshState();
    }//GEN-LAST:event_option2ButtonItemStateChanged

    private void option1ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option1ButtonItemStateChanged
        refreshState();
    }//GEN-LAST:event_option1ButtonItemStateChanged

    private void jResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetButtonActionPerformed
        uselessSlider.setValue(FeatureSelectionAlgo.ENTROPY_DEFAULT_VALUE);
        redundantComboBox.setSelectedIndex(FeatureSelectionAlgo.CORRELATION_DEFAULT_INDEX);
        redundantSlider.setValue(FeatureSelectionAlgo.CORRELATION_DEFAULT_VALUE);
    }//GEN-LAST:event_jResetButtonActionPerformed

    private void redundantComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redundantComboBoxActionPerformed
        if (algorithm != null && algorithm.getCorrelationIndex() != redundantComboBox.getSelectedIndex()){
            algorithm.setCorrelationIndex(redundantComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_redundantComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel correlationInfoLabel;
    private javax.swing.JLabel correlationLabel;
    private javax.swing.JLabel entropyInfoLabel;
    private javax.swing.JLabel entropyLabel;
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
    private javax.swing.JComboBox<String> redundantComboBox;
    private javax.swing.JLabel redundantLabel;
    private javax.swing.JSlider redundantSlider;
    private javax.swing.JSlider uselessSlider;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.algorithm = (FeatureSelectionAlgo) algo;
        int val = this.algorithm.getEntropyCutoff();
        uselessSlider.setValue(val);
        entropyLabel.setText(val + "%");

        redundantComboBox.setSelectedIndex(this.algorithm.getCorrelationIndex());
        
        val = this.algorithm.getCorrelationCutoff();
        redundantSlider.setValue(val);
        correlationLabel.setText(val + "%");

        if (this.algorithm.isRemoveUseless()) {
            if (this.algorithm.isRemoveRedundant()) {
                option2Button.setSelected(true);
            } else {
                option1Button.setSelected(true);
            }
        }
        
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                algorithm.addPropertyChangeListener(FeatureSelectionPanel.this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                algorithm.removePropertyChangeListener(FeatureSelectionPanel.this);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });      
        
        refreshState();

        return this;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(algorithm) && evt.getPropertyName().equals(FeatureSelectionAlgo.RUNNING)) {
            refreshState();
        }
    }

}
