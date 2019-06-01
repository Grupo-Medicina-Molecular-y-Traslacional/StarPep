/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.bapedis.chemspace.model.CoordinateSpace;
import org.bapedis.core.ui.components.JQuickHistogram;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NetworkPanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected MapperAlgorithm csMapper;
    protected NetworkEmbedderAlg netEmbedder;
    private RichTooltip richTooltip;
    private final DecimalFormat formatter;
    protected final JXBusyLabel busyLabel;
    protected final DefaultComboBoxModel<String> modelX, modelY;

    /**
     * Creates new form NetworkPanel
     */
    static {
        UIManager.put("Slider.paintValue", false);
    }

    public NetworkPanel() {
        initComponents();

        Hashtable<Integer, JLabel> thresholdLabelTable = new Hashtable<>();
        thresholdLabelTable.put(0, new JLabel("0"));
        thresholdLabelTable.put(10, new JLabel("0.1"));
        thresholdLabelTable.put(20, new JLabel("0.2"));
        thresholdLabelTable.put(30, new JLabel("0.3"));
        thresholdLabelTable.put(40, new JLabel("0.4"));
        thresholdLabelTable.put(50, new JLabel("0.5"));
        thresholdLabelTable.put(60, new JLabel("0.6"));
        thresholdLabelTable.put(70, new JLabel("0.7"));
        thresholdLabelTable.put(80, new JLabel("0.8"));
        thresholdLabelTable.put(90, new JLabel("0.9"));
        thresholdLabelTable.put(100, new JLabel("1"));
        cutoffSlider.setLabelTable(thresholdLabelTable);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("0.00", symbols);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setVisible(false);

        modelX = new DefaultComboBoxModel<>();
        modelY = new DefaultComboBoxModel<>();

        jXComboBox.setModel(modelX);
        jYComboBox.setModel(modelY);
    }

    public void setUp(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        netEmbedder = csMapper.getNetworkEmbedderAlg();

        double similarityThreshold = netEmbedder.getSimilarityThreshold();
        jCutoffCurrentValue.setText(formatter.format(similarityThreshold));
        cutoffSlider.setValue((int) (similarityThreshold * 100));
        jCutoffNewLabel.setVisible(false);
        jCutoffNewValue.setVisible(false);
        setupAxis();
        setupHistogram();
    }

    @Override
    public void setEnabled(boolean enabled) {
        jCutoffCurrentLabel.setEnabled(enabled);
        jCutoffCurrentValue.setEnabled(enabled);
        jCutoffNewLabel.setEnabled(enabled);
        jCutoffNewValue.setEnabled(enabled);
        jCutoffToolBar.setEnabled(enabled);
        jLessCutoffButton.setEnabled(enabled);
        jMoreCutoffButton.setEnabled(enabled);
        thresholdPanel.setEnabled(enabled);
        histogramPanel.setEnabled(enabled);
        histoInfoLabel.setEnabled(enabled);
        cutoffSlider.setEnabled(enabled);
    }

    public void setupHistogram() {
        histogramPanel.removeAll();
        histogramPanel.setBorder(null);
//        JQuickHistogram histogram = netEmbedder.getHistogram();
//        if (!csMapper.isRunning() && histogram != null) {
//            histogramPanel.add(histogram.createChartPanel(), BorderLayout.CENTER);
//            histogramPanel.setBorder(BorderFactory.createTitledBorder(NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.histogramPanel.borderTitle")));
//        }
        histogramPanel.revalidate();
        histogramPanel.repaint();
//        resetTooltip(histogram);
    }

    private void resetTooltip(JQuickHistogram histogram) {
        richTooltip = new RichTooltip();
        richTooltip.setTitle(NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.histoInfo.title"));
        richTooltip.addDescriptionSection("Number of values: " + ((histogram != null) ? histogram.countValues() : "NaN"));
        richTooltip.addDescriptionSection("Average: " + (histogram != null && histogram.countValues() > 0 ? formatter.format(histogram.getAverage()) : "NaN"));
        richTooltip.addDescriptionSection("Min: " + (histogram != null && histogram.countValues() > 0 ? formatter.format(histogram.getMinValue()) : "NaN"));
        richTooltip.addDescriptionSection("Max: " + (histogram != null && histogram.countValues() > 0 ? formatter.format(histogram.getMaxValue()) : "NaN"));
    }

    private void setRunning(boolean running) {
        jXComboBox.setEnabled(!running);
        jYComboBox.setEnabled(!running);
        
        jCutoffToolBar.setEnabled(!running);
        jLessCutoffButton.setEnabled(!running);
        jMoreCutoffButton.setEnabled(!running);
        cutoffSlider.setEnabled(!running);
        
        if (running) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void thresholdChanged(double value) {
        netEmbedder.setSimilarityThreshold(value);
        jCutoffCurrentValue.setText(formatter.format(value));
        jCutoffNewLabel.setVisible(false);
        jCutoffNewValue.setVisible(false);
        jApplyThresholdButton.setEnabled(false);
    }
    
    public void setupAxis() {
        if (csMapper.isRunning()) {
            modelX.removeAllElements();
            modelY.removeAllElements();
        } else {
            CoordinateSpace xyzSpace = csMapper.getPCATransformer().getXYZSpace();

            if (xyzSpace != null) {
                String[] axisLabels = xyzSpace.getAxisLabels();
                for (String axis : axisLabels) {
                    modelX.addElement(axis);
                    modelY.addElement(axis);
                }
                modelX.setSelectedItem(axisLabels[xyzSpace.getxAxis()]);
                modelY.setSelectedItem(axisLabels[xyzSpace.getyAxis()]);
            }
        }
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

        thresholdPanel = new javax.swing.JPanel();
        jCutoffCurrentLabel = new javax.swing.JLabel();
        jCutoffCurrentValue = new javax.swing.JLabel();
        jCutoffNewLabel = new javax.swing.JLabel();
        jCutoffNewValue = new javax.swing.JLabel();
        jCutoffToolBar = new javax.swing.JToolBar();
        jLessCutoffButton = new javax.swing.JButton();
        cutoffSlider = new javax.swing.JSlider();
        jMoreCutoffButton = new javax.swing.JButton();
        histoInfoLabel = new javax.swing.JLabel();
        histogramPanel = new javax.swing.JPanel();
        jApplyThresholdButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        coordinatesPanel = new javax.swing.JPanel();
        jApplyCoordinateButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jXComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jYComboBox = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        thresholdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.thresholdPanel.border.title"))); // NOI18N
        thresholdPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffCurrentLabel, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jCutoffCurrentLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        thresholdPanel.add(jCutoffCurrentLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffCurrentValue, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jCutoffCurrentValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        thresholdPanel.add(jCutoffCurrentValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffNewLabel, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jCutoffNewLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        thresholdPanel.add(jCutoffNewLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffNewValue, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jCutoffNewValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        thresholdPanel.add(jCutoffNewValue, gridBagConstraints);

        jCutoffToolBar.setFloatable(false);
        jCutoffToolBar.setRollover(true);
        jCutoffToolBar.setPreferredSize(new java.awt.Dimension(420, 90));

        jLessCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/less.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLessCutoffButton, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jLessCutoffButton.text")); // NOI18N
        jLessCutoffButton.setFocusable(false);
        jLessCutoffButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jLessCutoffButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jLessCutoffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jLessCutoffButtonActionPerformed(evt);
            }
        });
        jCutoffToolBar.add(jLessCutoffButton);

        cutoffSlider.setMajorTickSpacing(10);
        cutoffSlider.setMinorTickSpacing(5);
        cutoffSlider.setPaintLabels(true);
        cutoffSlider.setPaintTicks(true);
        cutoffSlider.setToolTipText(org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.cutoffSlider.toolTipText")); // NOI18N
        cutoffSlider.setValue(70);
        cutoffSlider.setMinimumSize(new java.awt.Dimension(360, 80));
        cutoffSlider.setPreferredSize(new java.awt.Dimension(360, 80));
        cutoffSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cutoffSliderStateChanged(evt);
            }
        });
        jCutoffToolBar.add(cutoffSlider);

        jMoreCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/more.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jMoreCutoffButton, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jMoreCutoffButton.text")); // NOI18N
        jMoreCutoffButton.setFocusable(false);
        jMoreCutoffButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jMoreCutoffButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jMoreCutoffButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMoreCutoffButtonActionPerformed(evt);
            }
        });
        jCutoffToolBar.add(jMoreCutoffButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        thresholdPanel.add(jCutoffToolBar, gridBagConstraints);

        histoInfoLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        histoInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        histoInfoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                histoInfoLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                histoInfoLabelMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        thresholdPanel.add(histoInfoLabel, gridBagConstraints);

        histogramPanel.setMinimumSize(new java.awt.Dimension(0, 180));
        histogramPanel.setOpaque(false);
        histogramPanel.setPreferredSize(new java.awt.Dimension(0, 180));
        histogramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        thresholdPanel.add(histogramPanel, gridBagConstraints);

        jApplyThresholdButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyThresholdButton, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jApplyThresholdButton.text")); // NOI18N
        jApplyThresholdButton.setToolTipText(org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jApplyThresholdButton.toolTipText")); // NOI18N
        jApplyThresholdButton.setEnabled(false);
        jApplyThresholdButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyThresholdButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        thresholdPanel.add(jApplyThresholdButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        thresholdPanel.add(jLabel4, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        add(thresholdPanel, gridBagConstraints);

        coordinatesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.coordinatesPanel.border.title"))); // NOI18N
        coordinatesPanel.setLayout(new java.awt.GridBagLayout());

        jApplyCoordinateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyCoordinateButton, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jApplyCoordinateButton.text")); // NOI18N
        jApplyCoordinateButton.setToolTipText(org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jApplyCoordinateButton.toolTipText")); // NOI18N
        jApplyCoordinateButton.setEnabled(false);
        jApplyCoordinateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyCoordinateButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        coordinatesPanel.add(jApplyCoordinateButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        coordinatesPanel.add(jLabel1, gridBagConstraints);

        jXComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        coordinatesPanel.add(jXComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        coordinatesPanel.add(jLabel2, gridBagConstraints);

        jYComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        coordinatesPanel.add(jYComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(NetworkPanel.class, "NetworkPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        coordinatesPanel.add(jLabel3, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        add(coordinatesPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jLessCutoffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLessCutoffButtonActionPerformed
        int cutoff = cutoffSlider.getValue();
        if (cutoff > cutoffSlider.getMinimum()) {
            cutoffSlider.setValue(cutoff - 1);
        }
    }//GEN-LAST:event_jLessCutoffButtonActionPerformed

    private void cutoffSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cutoffSliderStateChanged
        float threshold = cutoffSlider.getValue() / 100.f;
        if (threshold != Float.parseFloat(jCutoffCurrentValue.getText())) {
            jCutoffNewLabel.setVisible(true);
            jCutoffNewValue.setVisible(true);
            jCutoffNewValue.setText(formatter.format(threshold));
            jApplyThresholdButton.setEnabled(netEmbedder != null);
        } else {
            jCutoffNewLabel.setVisible(false);
            jCutoffNewValue.setVisible(false);
            jApplyThresholdButton.setEnabled(false);
            jCutoffNewValue.setText("");
        }
    }//GEN-LAST:event_cutoffSliderStateChanged

    private void jMoreCutoffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoreCutoffButtonActionPerformed
        int cutoff = cutoffSlider.getValue();
        if (cutoff < cutoffSlider.getMaximum()) {
            cutoffSlider.setValue(cutoff + 1);
        }
    }//GEN-LAST:event_jMoreCutoffButtonActionPerformed

    private void histoInfoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_histoInfoLabelMouseExited
        if (richTooltip != null) {
            richTooltip.hideTooltip();
        }
    }//GEN-LAST:event_histoInfoLabelMouseExited

    private void histoInfoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_histoInfoLabelMouseEntered
        if (richTooltip != null) {
            richTooltip.showTooltip(histoInfoLabel, evt.getLocationOnScreen());
        }
    }//GEN-LAST:event_histoInfoLabelMouseEntered

    private void jApplyCoordinateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyCoordinateButtonActionPerformed
        if (csMapper != null) {
            CoordinateSpace xyzSpace = csMapper.getPCATransformer().getXYZSpace();
            xyzSpace.setxAxis(jXComboBox.getSelectedIndex());
            xyzSpace.setyAxis(jYComboBox.getSelectedIndex());
            
            NetworkCoordinateUpdater updater = new NetworkCoordinateUpdater(xyzSpace);
            updater.addPropertyChangeListener(this);
            setRunning(true);
            updater.execute();
        }
    }//GEN-LAST:event_jApplyCoordinateButtonActionPerformed

    private void jXComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXComboBoxActionPerformed
        jApplyCoordinateButton.setEnabled(true);
    }//GEN-LAST:event_jXComboBoxActionPerformed

    private void jYComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYComboBoxActionPerformed
        jApplyCoordinateButton.setEnabled(true);
    }//GEN-LAST:event_jYComboBoxActionPerformed

    private void jApplyThresholdButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyThresholdButtonActionPerformed
        if (netEmbedder != null) {
            double threshold = cutoffSlider.getValue() / 100.0;
            NetworkThresholdUpdater fnUpdater = new NetworkThresholdUpdater(threshold, netEmbedder.getSimilarityThreshold());
            fnUpdater.addPropertyChangeListener(this);
            setRunning(true);
            fnUpdater.execute();
        }
    }//GEN-LAST:event_jApplyThresholdButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel coordinatesPanel;
    private javax.swing.JSlider cutoffSlider;
    private javax.swing.JLabel histoInfoLabel;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JButton jApplyCoordinateButton;
    private javax.swing.JButton jApplyThresholdButton;
    private javax.swing.JLabel jCutoffCurrentLabel;
    private javax.swing.JLabel jCutoffCurrentValue;
    private javax.swing.JLabel jCutoffNewLabel;
    private javax.swing.JLabel jCutoffNewValue;
    private javax.swing.JToolBar jCutoffToolBar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JButton jLessCutoffButton;
    private javax.swing.JButton jMoreCutoffButton;
    private javax.swing.JComboBox<String> jXComboBox;
    private javax.swing.JComboBox<String> jYComboBox;
    private javax.swing.JPanel thresholdPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(NetworkThresholdUpdater.CHANGED_THRESHOLD)) {
            setRunning(false);
            thresholdChanged((double) evt.getNewValue());
        }else if (evt.getPropertyName().equals(NetworkCoordinateUpdater.UPDATED_POSITIONS)){
            setRunning(false);
        }
    }
}
