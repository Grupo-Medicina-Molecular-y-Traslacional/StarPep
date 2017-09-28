/*
 Copyright 2008-2010 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2011 Gephi Consortium. All rights reserved.

 The contents of this file are subject to the terms of either the GNU
 General Public License Version 3 only ("GPL") or the Common
 Development and Distribution License("CDDL") (collectively, the
 "License"). You may not use this file except in compliance with the
 License. You can obtain a copy of the License at
 http://gephi.org/about/legal/license-notice/
 or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
 specific language governing permissions and limitations under the
 License.  When distributing the software, include this License Header
 Notice in each file and include the License files at
 /cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
 License Header, with the fields enclosed by brackets [] replaced by
 your own identifying information:
 "Portions Copyrighted [year] [name of copyright owner]"

 If you wish your version of this file to be governed by only the CDDL
 or only the GPL Version 3, indicate your decision by adding
 "[Contributor] elects to include this software in this distribution
 under the [CDDL or GPL Version 3] license." If you do not indicate a
 single choice of license, a recipient has the option to distribute
 your version of this file under either the CDDL, the GPL Version 3 or
 to extend the choice of license to its licensees as provided above.
 However, if you add GPL Version 3 code and therefore, elected the GPL
 Version 3 license, then the option applies only if the new code is
 made subject to such option by the copyright holder.

 Contributor(s):

 Portions Copyrighted 2011 Gephi Consortium.
 */
package org.bapedis.csn.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.jdesktop.swingx.JXBusyLabel;
import org.jfree.chart.ChartPanel;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class ThresholdRangePanel extends javax.swing.JPanel {

    private final JQuickHistogram histogram;
    private ChartPanel chartPanel;
    private final SpinnerNumberModel thresholdSpinnerModel;
    protected final JXBusyLabel busyLabel;
    protected SimilarityMeasure simMeasure;
    protected final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private RichTooltip richTooltip;
    private final NumberFormat formatter;

    public ThresholdRangePanel() {
        initComponents();
        thresholdSpinnerModel = new SpinnerNumberModel(0, 0, 1, 0.01);
        jThresholdSpinner.setModel(thresholdSpinnerModel);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        histogram = new JQuickHistogram();
        histogram.setConstraintHeight(histogramPanel.getHeight());
        
        chartPanel = histogram.createChartPanel();

        formatter = DecimalFormat.getNumberInstance();
        formatter.setMaximumFractionDigits(2);
        
        setBusy(false);
    }

    static {
        UIManager.put("Slider.paintValue", false);
    }

    public void setup(SimilarityMeasure measure) {
        this.simMeasure = measure;        
        thresholdSpinnerModel.setValue(measure.getThreshold());
        resetTooltip();
    }

    private void setBusy(boolean busy) {
        busyLabel.setBusy(busy);
        if (busy) {
            histogramPanel.remove(chartPanel);
            histogramPanel.add(busyLabel, BorderLayout.CENTER);
        } else {
            histogramPanel.remove(busyLabel);
            histogramPanel.add(chartPanel, BorderLayout.CENTER);
        }
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    public void setupHistogram(final List<Edge> similarityEdges) {
        if (similarityEdges == null) {
            setBusy(true);
        } else {
            SwingWorker sw = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    histogram.clear();
                    for (Edge edge : similarityEdges) {
                        histogram.addData((Double) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY));
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        histogramPanel.remove(chartPanel);
                        chartPanel = histogram.createChartPanel();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        setBusy(false);
                        resetTooltip();
                    }
                }
            };
            sw.execute();
        }
    }

    private void resetTooltip() {
        richTooltip = new RichTooltip();
        richTooltip.setTitle(NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.info.title"));
        richTooltip.addDescriptionSection("Current value: " + formatter.format(simMeasure.getThreshold()));
        if (histogram.countValues() > 0) {
            richTooltip.addDescriptionSection("Number of values: " + histogram.countValues());
            richTooltip.addDescriptionSection("Average: " + formatter.format(histogram.getAverage()));
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

        jLabel1 = new javax.swing.JLabel();
        histogramPanel = new javax.swing.JPanel();
        jApplyButton = new javax.swing.JButton();
        jThresholdSpinner = new javax.swing.JSpinner();
        infoLabel = new javax.swing.JLabel();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.border.title"))); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(5, 0, 0, 0);
        add(jLabel1, gridBagConstraints);

        histogramPanel.setMinimumSize(new java.awt.Dimension(0, 180));
        histogramPanel.setOpaque(false);
        histogramPanel.setPreferredSize(new java.awt.Dimension(0, 180));
        histogramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(histogramPanel, gridBagConstraints);

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/csn/resources/refresh.png"))); // NOI18N
        jApplyButton.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.text")); // NOI18N
        jApplyButton.setToolTipText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.toolTipText")); // NOI18N
        jApplyButton.setMinimumSize(new java.awt.Dimension(80, 29));
        jApplyButton.setPreferredSize(new java.awt.Dimension(85, 29));
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 15, 0, 0);
        add(jApplyButton, gridBagConstraints);

        jThresholdSpinner.setMinimumSize(new java.awt.Dimension(60, 28));
        jThresholdSpinner.setPreferredSize(new java.awt.Dimension(64, 28));
        jThresholdSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jThresholdSpinnerStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jThresholdSpinner, gridBagConstraints);

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/csn/resources/info.png"))); // NOI18N
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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(infoLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed
        if (simMeasure != null) {
            final double threshold = (double) thresholdSpinnerModel.getValue();
            final double oldValue = simMeasure.getThreshold();
            simMeasure.setThreshold(threshold);
            SwingWorker sw = new SwingWorker() {
                @Override
                protected Object doInBackground() throws Exception {
                    AttributesModel attrModel = pc.getAttributesModel();
                    if (attrModel != null) {
                        Graph csnGraph = pc.getGraphModel().getGraph(attrModel.getCsnView());
                        double score;
                        if (threshold < oldValue) { // to add edges
                            List<Edge> toAdd = new LinkedList<>();
                            Graph mainGraph = pc.getGraphModel().getGraph();
                            int relType = pc.getGraphModel().getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
                            if (relType != -1) {
                                csnGraph.writeLock();
                                try {
                                    for (Node node : csnGraph.getNodes()) {
                                        mainGraph.readLock();
                                        try {
                                            for (Edge edge : mainGraph.getEdges(node, relType)) {
                                                score = (double) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                                                if (score >= threshold) {
                                                    toAdd.add(edge);
                                                }
                                            }
                                        } finally {
                                            mainGraph.readUnlock();
                                        }
                                    }
                                    csnGraph.addAllEdges(toAdd);
                                } finally {
                                    csnGraph.writeUnlock();
                                }
                            }
                        } else if (threshold > oldValue) { // to romove edges
                            List<Edge> toRemove = new LinkedList<>();
                            csnGraph.writeLock();
                            try {
                                for (Edge edge : csnGraph.getEdges()) {
                                    score = (double) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                                    if (score < threshold) {
                                        toRemove.add(edge);
                                    }
                                }
                                csnGraph.removeAllEdges(toRemove);
                            } finally {
                                csnGraph.writeUnlock();
                            }
                        }
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        jApplyButton.setEnabled(false);
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        resetTooltip();
                    }
                }
            };            
            sw.execute();
        }
    }//GEN-LAST:event_jApplyButtonActionPerformed

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
        if (richTooltip != null) {
            richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
        }
    }//GEN-LAST:event_infoLabelMouseEntered

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        if (richTooltip != null) {
            richTooltip.hideTooltip();
        }
    }//GEN-LAST:event_infoLabelMouseExited

    private void jThresholdSpinnerStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jThresholdSpinnerStateChanged
        double newValue = (double)thresholdSpinnerModel.getValue();
        jApplyButton.setEnabled(simMeasure != null && simMeasure.getThreshold() != newValue);
    }//GEN-LAST:event_jThresholdSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner jThresholdSpinner;
    // End of variables declaration//GEN-END:variables
}
