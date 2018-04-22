/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import javax.swing.JLabel;
import javax.swing.UIManager;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.chemspace.model.JQuickHistogram;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ChemSpaceNetworkPanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected MapperAlgorithm csMapper;
    protected NetworkEmbedder netEmbedder;
    private RichTooltip richTooltip;
    private final DecimalFormat formatter;

    /**
     * Creates new form NetworkPanel
     */
    static {
        UIManager.put("Slider.paintValue", false);
    }

    public ChemSpaceNetworkPanel() {
        initComponents();

        Hashtable<Integer, JLabel> thresholdLabelTable = new Hashtable<>();
        thresholdLabelTable.put(50, new JLabel("0.5"));
        thresholdLabelTable.put(60, new JLabel("0.6"));
        thresholdLabelTable.put(70, new JLabel("0.7"));
        thresholdLabelTable.put(80, new JLabel("0.8"));
        thresholdLabelTable.put(90, new JLabel("0.9"));
        thresholdLabelTable.put(100, new JLabel("1"));
        cutoffSlider.setLabelTable(thresholdLabelTable);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.addRunningListener(ChemSpaceNetworkPanel.this);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.removeRunningListener(ChemSpaceNetworkPanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("0.00", symbols);
    }

    public void setUp(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        switch (csMapper.getChemSpaceOption()) {
            case CHEM_SPACE_NETWORK:
                netEmbedder = csMapper.getCSNEmbedderAlg();
                break;
            case SEQ_SIMILARITY_NETWORK:
                netEmbedder = csMapper.getSSNEmbedderAlg();
                break;
        }
        float similarityThreshold = netEmbedder.getSimilarityThreshold();
        jCutoffCurrentValue.setText(formatter.format(similarityThreshold));
        cutoffSlider.setValue((int) (similarityThreshold * 100));
        jCutoffNewLabel.setVisible(false);
        jCutoffNewValue.setVisible(false);
        setupHistogram(csMapper.isRunning());
    }

    private void setupHistogram(boolean running) {
        histogramPanel.removeAll();
        SimilarityMatrix matrix = netEmbedder.getSimilarityMatrix();
        JQuickHistogram histogram = null;
        if (!running && matrix != null) {
            histogram = matrix.getHistogram();
            histogramPanel.add(histogram.createChartPanel(), BorderLayout.CENTER);
        }
        histogramPanel.revalidate();
        histogramPanel.repaint();
        resetTooltip(histogram);
    }

    private void resetTooltip(JQuickHistogram histogram) {
        richTooltip = new RichTooltip();
        richTooltip.setTitle(NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.histoInfo.title"));
        richTooltip.addDescriptionSection("Number of values: " + ((histogram != null) ? histogram.countValues() : "NaN"));
        richTooltip.addDescriptionSection("Average: " + (histogram != null && histogram.countValues() > 0 ? formatter.format(histogram.getAverage()) : "NaN"));
        richTooltip.addDescriptionSection("Min: " + (histogram != null && histogram.countValues() > 0 ? formatter.format(histogram.getMinValue()) : "NaN"));
        richTooltip.addDescriptionSection("Max: " + (histogram != null && histogram.countValues() > 0 ? formatter.format(histogram.getMaxValue()) : "NaN"));
    }

    private void setRunning(boolean running) {
        jApplyButton.setEnabled(!running);
        jCutoffToolBar.setEnabled(!running);
        jLessCutoffButton.setEnabled(!running);
        jMoreCutoffButton.setEnabled(!running);
        cutoffSlider.setEnabled(!running);
    }

    private void changeThreshold(float value) {
        jCutoffCurrentValue.setText(formatter.format(value));
        jCutoffNewLabel.setVisible(false);
        jCutoffNewValue.setVisible(false);
        jApplyButton.setEnabled(false);
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

        jApplyButton = new javax.swing.JButton();
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

        setLayout(new java.awt.GridBagLayout());

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyButton, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jApplyButton.text")); // NOI18N
        jApplyButton.setToolTipText(org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jApplyButton.toolTipText")); // NOI18N
        jApplyButton.setEnabled(false);
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(jApplyButton, gridBagConstraints);

        thresholdPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.thresholdPanel.border.title"))); // NOI18N
        thresholdPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffCurrentLabel, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jCutoffCurrentLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        thresholdPanel.add(jCutoffCurrentLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffCurrentValue, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jCutoffCurrentValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        thresholdPanel.add(jCutoffCurrentValue, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffNewLabel, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jCutoffNewLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        thresholdPanel.add(jCutoffNewLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffNewValue, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jCutoffNewValue.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        thresholdPanel.add(jCutoffNewValue, gridBagConstraints);

        jCutoffToolBar.setFloatable(false);
        jCutoffToolBar.setRollover(true);
        jCutoffToolBar.setPreferredSize(new java.awt.Dimension(420, 90));

        jLessCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/less.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLessCutoffButton, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jLessCutoffButton.text")); // NOI18N
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
        cutoffSlider.setMinimum(50);
        cutoffSlider.setMinorTickSpacing(5);
        cutoffSlider.setPaintLabels(true);
        cutoffSlider.setPaintTicks(true);
        cutoffSlider.setToolTipText(org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.cutoffSlider.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(jMoreCutoffButton, org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.jMoreCutoffButton.text")); // NOI18N
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
        gridBagConstraints.gridy = 1;
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
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        thresholdPanel.add(histoInfoLabel, gridBagConstraints);

        histogramPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ChemSpaceNetworkPanel.class, "ChemSpaceNetworkPanel.histogramPanel.border.title"))); // NOI18N
        histogramPanel.setMinimumSize(new java.awt.Dimension(0, 180));
        histogramPanel.setOpaque(false);
        histogramPanel.setPreferredSize(new java.awt.Dimension(0, 180));
        histogramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        thresholdPanel.add(histogramPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        add(thresholdPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed
        if (netEmbedder != null && netEmbedder.getSimilarityMatrix() != null) {
            NetworkThresholdUpdater fnUpdater = new NetworkThresholdUpdater(netEmbedder);
            fnUpdater.addPropertyChangeListener(this);
            setRunning(true);
            fnUpdater.execute();
        }
    }//GEN-LAST:event_jApplyButtonActionPerformed

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
            jApplyButton.setEnabled(netEmbedder != null && netEmbedder.getSimilarityMatrix() != null);
        } else {
            jCutoffNewLabel.setVisible(false);
            jCutoffNewValue.setVisible(false);
            jApplyButton.setEnabled(false);
            jCutoffNewValue.setText("");
        }
        if (netEmbedder != null) {
            netEmbedder.setSimilarityThreshold(threshold);
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


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSlider cutoffSlider;
    private javax.swing.JLabel histoInfoLabel;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JLabel jCutoffCurrentLabel;
    private javax.swing.JLabel jCutoffCurrentValue;
    private javax.swing.JLabel jCutoffNewLabel;
    private javax.swing.JLabel jCutoffNewValue;
    private javax.swing.JToolBar jCutoffToolBar;
    private javax.swing.JButton jLessCutoffButton;
    private javax.swing.JButton jMoreCutoffButton;
    private javax.swing.JPanel thresholdPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csMapper != null) {
            if (evt.getSource().equals(csMapper)) {
                if (evt.getPropertyName().equals(MapperAlgorithm.RUNNING)) {
                    setupHistogram(csMapper.isRunning());
                }
            } else if (evt.getPropertyName().equals(NetworkThresholdUpdater.CHANGED_THRESHOLD)) {
                setRunning(false);
                changeThreshold((float) evt.getNewValue());
            }
        }
    }
}
