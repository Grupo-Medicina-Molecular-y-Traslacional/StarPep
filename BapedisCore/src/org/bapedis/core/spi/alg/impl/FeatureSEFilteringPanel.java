/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSEFilteringPanel extends javax.swing.JPanel implements AlgorithmSetupUI, PropertyChangeListener {

    private FeatureSEFiltering algorithm;
    private final NotifyDescriptor errorND;

    public FeatureSEFilteringPanel() {
        initComponents();

        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.errorND"), NotifyDescriptor.ERROR_MESSAGE);

        //Configure correlation methods
        DefaultComboBoxModel model = new DefaultComboBoxModel();
        for (String method : FeatureSEFiltering.CORRELATION_METHODS) {
            model.addElement(method);
        }

        redundantComboBox.setModel(model);
        redundantComboBox.setSelectedIndex(FeatureSEFiltering.CORRELATION_DEFAULT_INDEX);

        //Create document listeners
        DocumentListener topRankDocListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTopRank();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTopRank();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };

        DocumentListener thresholdDocListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateSEThreshold();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateSEThreshold();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };

        DocumentListener corrDocListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCorr();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCorr();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        };

        jTF_top.getDocument().addDocumentListener(topRankDocListener);
        jTF_threshold.getDocument().addDocumentListener(thresholdDocListener);
        jTF_corr.getDocument().addDocumentListener(corrDocListener);
    }

    private void updateTopRank() {
        try {
            if (!jTF_top.getText().isEmpty()) {
                int topRank = Integer.parseInt(jTF_top.getText());
                if (algorithm.getTopRank() != topRank) {
                    algorithm.setTopRank(topRank);
                }
            } else {
                algorithm.setTopRank(-1);
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorND);
            algorithm.setTopRank(-1);
        }
    }

    private void updateSEThreshold() {
        try {
            if (!jTF_threshold.getText().isEmpty()) {
                float threshold = Float.parseFloat(jTF_threshold.getText());
                if (algorithm.getThreshold() != threshold) {
                    algorithm.setThreshold(threshold);
                }
            } else {
                algorithm.setThreshold(-1);
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorND);
            algorithm.setThreshold(-1);
        }
    }

    private void updateCorr() {
        try {
            if (!jTF_corr.getText().isEmpty()) {
                float corr = Float.parseFloat(jTF_corr.getText());
                algorithm.setCorrelationCutoff(corr);
            } else {
                algorithm.setCorrelationCutoff(-1);
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorND);
            algorithm.setCorrelationCutoff(-1);
        }
    }

    private void refreshState() {
        boolean running = algorithm != null && algorithm.isRunning();
        boolean enabled = !running && isEnabled();

        jResetButton.setEnabled(enabled);

        jRB_selectAll.setEnabled(enabled);

        jRB_selectTop.setEnabled(enabled);
        jTF_top.setEnabled(enabled && jRB_selectTop.isSelected());

        jRB_threshold.setEnabled(enabled);
        jTF_threshold.setEnabled(enabled && jRB_threshold.isSelected());
        infoSEThreshold.setEnabled(enabled && jRB_threshold.isSelected());

        redundantComboBox.setEnabled(enabled);
        jLabelThreshodl.setEnabled(enabled && redundantComboBox.getSelectedItem() != FeatureSEFiltering.CORRELATION_NONE);
        jTF_corr.setEnabled(enabled && redundantComboBox.getSelectedItem() != FeatureSEFiltering.CORRELATION_NONE);
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
        jResetButton = new javax.swing.JButton();
        rankingOutputPanel = new javax.swing.JPanel();
        jRB_selectAll = new javax.swing.JRadioButton();
        jRB_selectTop = new javax.swing.JRadioButton();
        jRB_threshold = new javax.swing.JRadioButton();
        jTF_top = new javax.swing.JTextField();
        jTF_threshold = new javax.swing.JTextField();
        infoSEThreshold = new javax.swing.JLabel();
        redundancyPanel = new javax.swing.JPanel();
        redundantComboBox = new javax.swing.JComboBox<>();
        jLabelThreshodl = new javax.swing.JLabel();
        jTF_corr = new javax.swing.JTextField();
        histogramPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(440, 380));
        setPreferredSize(new java.awt.Dimension(440, 280));
        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jResetButton, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jResetButton.text")); // NOI18N
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
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 5, 5);
        add(jResetButton, gridBagConstraints);

        rankingOutputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.rankingOutputPanel.border.title"))); // NOI18N
        rankingOutputPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(jRB_selectAll);
        org.openide.awt.Mnemonics.setLocalizedText(jRB_selectAll, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jRB_selectAll.text")); // NOI18N
        jRB_selectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_selectAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        rankingOutputPanel.add(jRB_selectAll, gridBagConstraints);

        buttonGroup1.add(jRB_selectTop);
        org.openide.awt.Mnemonics.setLocalizedText(jRB_selectTop, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jRB_selectTop.text")); // NOI18N
        jRB_selectTop.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRB_selectTopStateChanged(evt);
            }
        });
        jRB_selectTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_selectTopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        rankingOutputPanel.add(jRB_selectTop, gridBagConstraints);

        buttonGroup1.add(jRB_threshold);
        org.openide.awt.Mnemonics.setLocalizedText(jRB_threshold, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jRB_threshold.text")); // NOI18N
        jRB_threshold.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jRB_threshold.toolTipText")); // NOI18N
        jRB_threshold.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jRB_thresholdStateChanged(evt);
            }
        });
        jRB_threshold.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRB_thresholdActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        rankingOutputPanel.add(jRB_threshold, gridBagConstraints);

        jTF_top.setText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jTF_top.text")); // NOI18N
        jTF_top.setEnabled(false);
        jTF_top.setMinimumSize(new java.awt.Dimension(90, 27));
        jTF_top.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rankingOutputPanel.add(jTF_top, gridBagConstraints);

        jTF_threshold.setText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jTF_threshold.text")); // NOI18N
        jTF_threshold.setEnabled(false);
        jTF_threshold.setMinimumSize(new java.awt.Dimension(90, 27));
        jTF_threshold.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rankingOutputPanel.add(jTF_threshold, gridBagConstraints);

        infoSEThreshold.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoSEThreshold, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.infoSEThreshold.text")); // NOI18N
        infoSEThreshold.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.infoSEThreshold.toolTipText")); // NOI18N
        infoSEThreshold.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        rankingOutputPanel.add(infoSEThreshold, gridBagConstraints);

        redundancyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.redundancyPanel.border.title"))); // NOI18N
        redundancyPanel.setLayout(new java.awt.GridBagLayout());

        redundantComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                redundantComboBoxItemStateChanged(evt);
            }
        });
        redundantComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redundantComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        redundancyPanel.add(redundantComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelThreshodl, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabelThreshodl.text")); // NOI18N
        jLabelThreshodl.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        redundancyPanel.add(jLabelThreshodl, gridBagConstraints);

        jTF_corr.setText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jTF_corr.text")); // NOI18N
        jTF_corr.setEnabled(false);
        jTF_corr.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        redundancyPanel.add(jTF_corr, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        rankingOutputPanel.add(redundancyPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(rankingOutputPanel, gridBagConstraints);

        histogramPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.histogramPanel.border.title"))); // NOI18N
        histogramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(histogramPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jResetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jResetButtonActionPerformed
        algorithm.reset();
        setOption(FeatureSEFiltering.RANKING_DEFAULT_OPTION);

        redundantComboBox.setSelectedIndex(FeatureSEFiltering.CORRELATION_DEFAULT_INDEX);
        float val = algorithm.getCorrelationCutoff();
        if (val > 0) {
            jTF_corr.setText(String.valueOf(val));
        } else {
            jTF_corr.setText("");
        }


    }//GEN-LAST:event_jResetButtonActionPerformed

    private void redundantComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redundantComboBoxActionPerformed
        if (algorithm != null && algorithm.getCorrelationIndex() != redundantComboBox.getSelectedIndex()) {
            algorithm.setCorrelationIndex(redundantComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_redundantComboBoxActionPerformed

    private void jRB_thresholdStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRB_thresholdStateChanged
        jTF_threshold.setEnabled(jRB_threshold.isSelected());
        infoSEThreshold.setEnabled(jRB_threshold.isSelected());
    }//GEN-LAST:event_jRB_thresholdStateChanged

    private void jRB_selectTopStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jRB_selectTopStateChanged
        jTF_top.setEnabled(jRB_selectTop.isSelected());
    }//GEN-LAST:event_jRB_selectTopStateChanged

    private void redundantComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_redundantComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            boolean enable = redundantComboBox.getSelectedItem() != FeatureSEFiltering.CORRELATION_NONE;
            jTF_corr.setEnabled(enable);
            jLabelThreshodl.setEnabled(enable);
        }
    }//GEN-LAST:event_redundantComboBoxItemStateChanged

    private void jRB_selectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_selectAllActionPerformed
        algorithm.setRankingOption(FeatureSEFiltering.RANKING_SELECT_ALL);
    }//GEN-LAST:event_jRB_selectAllActionPerformed

    private void jRB_selectTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_selectTopActionPerformed
        algorithm.setRankingOption(FeatureSEFiltering.RANKING_SELECT_TOP);
    }//GEN-LAST:event_jRB_selectTopActionPerformed

    private void jRB_thresholdActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRB_thresholdActionPerformed
        algorithm.setRankingOption(FeatureSEFiltering.RANKING_ENTROPY_THRESHOLD);
    }//GEN-LAST:event_jRB_thresholdActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel infoSEThreshold;
    private javax.swing.JLabel jLabelThreshodl;
    private javax.swing.JRadioButton jRB_selectAll;
    private javax.swing.JRadioButton jRB_selectTop;
    private javax.swing.JRadioButton jRB_threshold;
    private javax.swing.JButton jResetButton;
    private javax.swing.JTextField jTF_corr;
    private javax.swing.JTextField jTF_threshold;
    private javax.swing.JTextField jTF_top;
    private javax.swing.JPanel rankingOutputPanel;
    private javax.swing.JPanel redundancyPanel;
    private javax.swing.JComboBox<String> redundantComboBox;
    // End of variables declaration//GEN-END:variables

    private void setOption(int option) {
        switch (option) {
            case FeatureSEFiltering.RANKING_SELECT_ALL:
                jRB_selectAll.setSelected(true);
                break;
            case FeatureSEFiltering.RANKING_SELECT_TOP:
                jRB_selectTop.setSelected(true);
                break;
            case FeatureSEFiltering.RANKING_ENTROPY_THRESHOLD:
                jRB_threshold.setSelected(true);
                break;
        }

        if (algorithm.getTopRank() > 0) {
            jTF_top.setText(String.valueOf(algorithm.getTopRank()));
        } else {
            jTF_top.setText("");
        }

        if (algorithm.getThreshold() > 0) {
            jTF_threshold.setText(String.valueOf(algorithm.getThreshold()));
        } else {
            jTF_threshold.setText("");
        }
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.algorithm = (FeatureSEFiltering) algo;

        setOption(algorithm.getRankingOption());

        redundantComboBox.setSelectedIndex(this.algorithm.getCorrelationIndex());

        float val = this.algorithm.getCorrelationCutoff();

        if (val > 0) {
            jTF_corr.setText(String.valueOf(val));
        } else {
            jTF_corr.setText("");
        }

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                algorithm.addPropertyChangeListener(FeatureSEFilteringPanel.this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                algorithm.removePropertyChangeListener(FeatureSEFilteringPanel.this);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        refreshState();

        algorithm.setWidth(histogramPanel.getWidth());
        algorithm.setHeight(histogramPanel.getHeight());

        return this;
    }

    private void setupHistogram(boolean running) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                histogramPanel.removeAll();
                if (!running && algorithm.getHistogramPanel() != null) {
                    histogramPanel.add(algorithm.getHistogramPanel(), BorderLayout.CENTER);
                }
                histogramPanel.revalidate();
                histogramPanel.repaint();
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(algorithm) && evt.getPropertyName().equals(FeatureSEFiltering.RUNNING)) {
            refreshState();
            setupHistogram(algorithm.isRunning());
        }
    }

}
