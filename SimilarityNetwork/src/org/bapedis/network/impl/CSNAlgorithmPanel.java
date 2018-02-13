/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Hashtable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.bapedis.network.model.SimilarityMatrix;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXHyperlink;
import org.jfree.chart.ChartPanel;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Home
 */
public class CSNAlgorithmPanel extends javax.swing.JPanel implements AlgorithmSetupUI, PropertyChangeListener {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final JXHyperlink openWizardLink;
    private CSNAlgorithm csnAlgo;
    private RichTooltip richTooltip;
    private ChartPanel chartPanel;
    protected final JXBusyLabel busyLabel;
    private final DecimalFormat formatter;

    /**
     * Creates new form CSNAlgorithmPanel
     */
    public CSNAlgorithmPanel() {
        initComponents();
        openWizardLink = new JXHyperlink();
        configureOpenWizardLink();

        cutoffSlider.setMinimum(CSNAlgorithm.SIMILARITY_CUTOFF_MIN);
        cutoffSlider.setMaximum(CSNAlgorithm.SIMILARITY_CUTOFF_MAX);
        cutoffSlider.setMajorTickSpacing(CSNAlgorithm.SIMILARITY_MAJORTICKSPACING);
        cutoffSlider.setMinorTickSpacing(CSNAlgorithm.SIMILARITY_MINORTICKSPACING);

        //Label table
        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();
        labelTable.put(CSNAlgorithm.SIMILARITY_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.cutoffSlider.low")));
        labelTable.put(CSNAlgorithm.SIMILARITY_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.cutoffSlider.middle")));
        labelTable.put(CSNAlgorithm.SIMILARITY_CUTOFF_REFS[2], new JLabel(NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.cutoffSlider.high")));

        cutoffSlider.setLabelTable(labelTable);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("0.00", symbols);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (csnAlgo != null) {
                    csnAlgo.addRunningListener(CSNAlgorithmPanel.this);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (csnAlgo != null) {
                    csnAlgo.removeRunningListener(CSNAlgorithmPanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void configureOpenWizardLink() {
        openWizardLink.setIcon(ImageUtilities.loadImageIcon("org/bapedis/network/resources/wizard.png", false));
        openWizardLink.setText(NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.openWizardLink.text"));
        openWizardLink.setClickedColor(new java.awt.Color(0, 51, 255));
        openWizardLink.setFocusPainted(false);
        openWizardLink.setFocusable(false);
        openWizardLink.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (csnAlgo != null) {
                    WizardDescriptor wiz = CSNAlgorithmFactory.createWizardDescriptor(csnAlgo);
                    if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {

                    }
                }
            }
        });

        topPanel.add(openWizardLink);
    }

    private void setBusy(boolean busy) {
        busyLabel.setBusy(busy);
        if (busy) {
            if (chartPanel != null) {
                histogramPanel.remove(chartPanel);
            }
            histogramPanel.add(busyLabel, BorderLayout.CENTER);
            infoLabel.setEnabled(false);
        } else {
            histogramPanel.remove(busyLabel);
            setupHistogram();
            if (chartPanel != null) {
                histogramPanel.add(chartPanel, BorderLayout.CENTER);
                infoLabel.setEnabled(true);
            }
        }
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    private void setupHistogram() {
        if (chartPanel != null) {
            histogramPanel.remove(chartPanel);
        }
        SimilarityMatrix matrix = csnAlgo.getSimilarityMatrix();
        if (matrix != null) {
            JQuickHistogram histogram = matrix.getHistogram();
            chartPanel = histogram.createChartPanel();
            resetTooltip(histogram);
        } else {
            chartPanel = null;
        }
    }

    private void resetTooltip(JQuickHistogram histogram) {
        richTooltip = new RichTooltip();
        richTooltip.setTitle(NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.info.title"));
        richTooltip.addDescriptionSection("Number of values: " + histogram.countValues());
        richTooltip.addDescriptionSection("Average: " + (histogram.countValues() > 0 ? formatter.format(histogram.getAverage()) : "NaN"));
        richTooltip.addDescriptionSection("Min: " + (histogram.countValues() > 0 ? formatter.format(histogram.getMinValue()) : "NaN"));
        richTooltip.addDescriptionSection("Max: " + (histogram.countValues() > 0 ? formatter.format(histogram.getMaxValue()) : "NaN"));
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        topPanel.setEnabled(enabled);
        openWizardLink.setEnabled(enabled);
        jCutoffInfoLabel.setEnabled(enabled);
        jCutoffValueLabel.setEnabled(enabled);
        jApplyButton.setEnabled(enabled);
        jCutoffToolBar.setEnabled(enabled);
        for (Component c : jCutoffToolBar.getComponents()) {
            c.setEnabled(enabled);
        }
        infoLabel.setEnabled(enabled);
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

        topPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        jCutoffInfoLabel = new javax.swing.JLabel();
        jCutoffValueLabel = new javax.swing.JLabel();
        jCutoffToolBar = new javax.swing.JToolBar();
        jLessCutoffButton = new javax.swing.JButton();
        cutoffSlider = new javax.swing.JSlider();
        jMoreCutoffButton = new javax.swing.JButton();
        histogramPanel = new javax.swing.JPanel();
        jApplyButton = new javax.swing.JButton();
        infoLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(topPanel, gridBagConstraints);

        centerPanel.setBorder(null);
        centerPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffInfoLabel, org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.jCutoffInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        centerPanel.add(jCutoffInfoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jCutoffValueLabel, org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.jCutoffValueLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        centerPanel.add(jCutoffValueLabel, gridBagConstraints);

        jCutoffToolBar.setFloatable(false);
        jCutoffToolBar.setRollover(true);
        jCutoffToolBar.setPreferredSize(new java.awt.Dimension(338, 90));

        jLessCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/less.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jLessCutoffButton, org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.jLessCutoffButton.text")); // NOI18N
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
        cutoffSlider.setToolTipText(org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.cutoffSlider.toolTipText")); // NOI18N
        cutoffSlider.setMinimumSize(new java.awt.Dimension(360, 80));
        cutoffSlider.setPreferredSize(new java.awt.Dimension(360, 80));
        cutoffSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                cutoffSliderStateChanged(evt);
            }
        });
        jCutoffToolBar.add(cutoffSlider);

        jMoreCutoffButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/more.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jMoreCutoffButton, org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.jMoreCutoffButton.text")); // NOI18N
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        centerPanel.add(jCutoffToolBar, gridBagConstraints);

        histogramPanel.setMinimumSize(new java.awt.Dimension(0, 180));
        histogramPanel.setOpaque(false);
        histogramPanel.setPreferredSize(new java.awt.Dimension(0, 180));
        histogramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        centerPanel.add(histogramPanel, gridBagConstraints);

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/applyFilter.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyButton, org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.jApplyButton.text")); // NOI18N
        jApplyButton.setToolTipText(org.openide.util.NbBundle.getMessage(CSNAlgorithmPanel.class, "CSNAlgorithmPanel.jApplyButton.toolTipText")); // NOI18N
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        centerPanel.add(jApplyButton, gridBagConstraints);

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/info.png"))); // NOI18N
        infoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                infoLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                infoLabelMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        centerPanel.add(infoLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void cutoffSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_cutoffSliderStateChanged
        if (!cutoffSlider.getValueIsAdjusting() && csnAlgo != null) {
            csnAlgo.setCutoffValue(cutoffSlider.getValue());
            jCutoffValueLabel.setText(csnAlgo.getCutoffValue() + "%");
        }
    }//GEN-LAST:event_cutoffSliderStateChanged

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        if (richTooltip != null) {
            richTooltip.hideTooltip();
        }
    }//GEN-LAST:event_infoLabelMouseExited

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
        if (richTooltip != null) {
            richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
        }
    }//GEN-LAST:event_infoLabelMouseEntered

    private void jMoreCutoffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMoreCutoffButtonActionPerformed
        int cutoff = cutoffSlider.getValue();
        if (cutoff < cutoffSlider.getMaximum()) {
            cutoffSlider.setValue(cutoff + 1);
        }
    }//GEN-LAST:event_jMoreCutoffButtonActionPerformed

    private void jLessCutoffButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jLessCutoffButtonActionPerformed
        int cutoff = cutoffSlider.getValue();
        if (cutoff > cutoffSlider.getMinimum()) {
            cutoffSlider.setValue(cutoff - 1);
        }
    }//GEN-LAST:event_jLessCutoffButtonActionPerformed

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed
        if (csnAlgo != null && csnAlgo.getSimilarityMatrix() != null) {
            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                private final SimilarityMatrix matrix = csnAlgo.getSimilarityMatrix();
                private final AtomicBoolean stopRun = new AtomicBoolean(false);
                private final GraphModel graphModel = pc.getGraphModel();
                private final float cutoff = csnAlgo.getCutoffValue()/100.f;
                private final ProgressTicket ticket = new ProgressTicket("Applying similarity cutoff", new Cancellable() {
                    @Override
                    public boolean cancel() {
                        stopRun.set(true);
                        return true;
                    }
                });

                @Override
                protected Void doInBackground() throws Exception {
                    Edge graphEdge;
                    Float score;
                    Graph graph = graphModel.getGraphVisible();
                    AttributesModel attrModel = pc.getAttributesModel();
                    if (attrModel != null) {
                        Peptide[] peptides = attrModel.getPeptides().toArray(new Peptide[0]);
                        for (int i = 0; i < peptides.length - 1; i++) {
                            for (int j = i + 1; j < peptides.length; j++) {
                                score = matrix.getValue(peptides[i], peptides[j]);
                                if (score != null && score >= cutoff) {
                                    graphEdge = createGraphEdge(peptides[i], peptides[j], score);
                                    graph.writeLock();
                                    try {
                                        graph.addEdge(graphEdge);
                                    } finally {
                                        graph.writeUnlock();
                                    }
                                }
                            }
                        }
                    }
                    return null;
                }

                private Edge createGraphEdge(Peptide peptide1, Peptide peptide2, Float score) {
                    int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
                    String id = String.format("%s-%s", peptide1.getId(), peptide2.getId());

                    // Create Edge
                    Edge graphEdge = graphModel.factory().newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
                    graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

                    //Set color
                    graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
                    graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
                    graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
                    graphEdge.setAlpha(0f);

                    // Add edge to main graph
                    Graph mainGraph = graphModel.getGraph();
                    mainGraph.writeLock();
                    try {
                        mainGraph.addEdge(graphEdge);
                        graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);
                    } finally {
                        mainGraph.writeUnlock();
                    }

                    return graphEdge;
                }

                @Override
                protected void done() {
                    try {
                        get();
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

            };

            worker.execute();
        }
    }//GEN-LAST:event_jApplyButtonActionPerformed

    @Override
    public JPanel getSettingPanel(Algorithm algo
    ) {
        this.csnAlgo = (CSNAlgorithm) algo;
        int cutoff = csnAlgo.getCutoffValue();
        jCutoffValueLabel.setText(cutoff + "%");
        cutoffSlider.setValue(cutoff);
        setBusy(csnAlgo.isRunning());
        return this;
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JSlider cutoffSlider;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JLabel jCutoffInfoLabel;
    private javax.swing.JToolBar jCutoffToolBar;
    private javax.swing.JLabel jCutoffValueLabel;
    private javax.swing.JButton jLessCutoffButton;
    private javax.swing.JButton jMoreCutoffButton;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csnAlgo != null && evt.getSource().equals(csnAlgo)
                && evt.getPropertyName().equals(CSNAlgorithm.RUNNING)) {
            setBusy((boolean) evt.getNewValue());
        }
    }
}
