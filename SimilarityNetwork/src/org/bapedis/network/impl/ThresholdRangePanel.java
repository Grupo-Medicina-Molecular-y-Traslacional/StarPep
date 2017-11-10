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
package org.bapedis.network.impl;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.jdesktop.swingx.JXBusyLabel;
import org.jfree.chart.ChartPanel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class ThresholdRangePanel extends javax.swing.JPanel implements PropertyChangeListener {

    private ChartPanel chartPanel;
    private final SpinnerNumberModel thresholdSpinnerModel;
    protected final JXBusyLabel busyLabel;
    protected SimilarityMeasure simMeasure;
    protected final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private RichTooltip richTooltip;
    private final DecimalFormat formatter;

    public ThresholdRangePanel() {
        initComponents();
        Float value = 0f;
        Float min = 0f;
        Float max = 1f;
        Float step = 0.01f;
        thresholdSpinnerModel = new SpinnerNumberModel(value, min, max, step);
        jThresholdSpinner.setModel(thresholdSpinnerModel);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("0.00", symbols);

        jApplyButton.setVisible(false);
        ((JSpinner.DefaultEditor) jThresholdSpinner.getEditor()).getTextField().addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                jApplyButton.setVisible(true);
            }
        });
    }

    public void setup(SimilarityMeasure measure) {
        if (this.simMeasure != null) {
            this.simMeasure.removePropertyChangeListener(this);
        }
        this.simMeasure = measure;
        this.simMeasure.addPropertyChangeListener(this);
        currentValueLabel.setText(formatter.format(measure.getThreshold()));
        thresholdSpinnerModel.setValue(measure.getThreshold());

        JQuickHistogram histogram = measure.getHistogram();
        histogram.setConstraintHeight(histogramPanel.getHeight());
        if (chartPanel != null) {
            histogramPanel.remove(chartPanel);
        }
        chartPanel = histogram.createChartPanel();

        setBusy(false);
        resetTooltip(histogram);
    }

    private void setBusy(boolean busy) {
        busyLabel.setBusy(busy);
        if (busy) {
            if (chartPanel != null) {
                histogramPanel.remove(chartPanel);
            }
            histogramPanel.add(busyLabel, BorderLayout.CENTER);
        } else {
            histogramPanel.remove(busyLabel);
            histogramPanel.add(chartPanel, BorderLayout.CENTER);
        }
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    private void setupHistogram(JQuickHistogram histogram) {
        if (histogram == null) {
            setBusy(true);
            infoLabel.setVisible(false);
        } else {
            if (chartPanel != null) {
                histogramPanel.remove(chartPanel);
            }
            chartPanel = histogram.createChartPanel();
            resetTooltip(histogram);
            infoLabel.setVisible(true);
            setBusy(false);
        }
    }

    private void resetTooltip(JQuickHistogram histogram) {
        richTooltip = new RichTooltip();
        richTooltip.setTitle(NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.info.title"));
        richTooltip.addDescriptionSection("Number of values: " + histogram.countValues());
        richTooltip.addDescriptionSection("Average: " + (histogram.countValues() > 0 ? formatter.format(histogram.getAverage()) : "NaN"));
        richTooltip.addDescriptionSection("Min: " + (histogram.countValues() > 0 ? formatter.format(histogram.getMinValue()) : "NaN"));
        richTooltip.addDescriptionSection("Max: " + (histogram.countValues() > 0 ? formatter.format(histogram.getMaxValue()) : "NaN"));
    }

    private void resetSimilarityThreshold(final float oldValue, final float newValue) {
        final AttributesModel attrModel = pc.getAttributesModel();
        SwingWorker sw = new SwingWorker() {
            @Override
            protected Object doInBackground() throws Exception {
                if (attrModel != null) {
                    attrModel.setSimilarityThreshold(newValue);
                    Graph csnGraph = pc.getGraphModel().getGraph(attrModel.getCsnView());
                    float score;
                    if (newValue < oldValue) { // to add edges
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
                                            if (csnGraph.hasNode(edge.getSource().getId()) && csnGraph.hasNode(edge.getTarget().getId())) {
                                                score = (float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                                                if (score >= newValue) {
                                                    toAdd.add(edge);
                                                }
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
                    } else if (newValue > oldValue) { // to romove edges
                        List<Edge> toRemove = new LinkedList<>();
                        csnGraph.writeLock();
                        try {
                            for (Edge edge : csnGraph.getEdges()) {
                                score = (float) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                                if (score < newValue) {
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
                    currentValueLabel.setText(formatter.format(newValue));
                    jApplyButton.setVisible(false);
                    attrModel.fireChangedGraphView();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    jApplyButton.setEnabled(true);
                }
            }
        };
        setCursor(new Cursor(Cursor.WAIT_CURSOR));
        jApplyButton.setEnabled(false);
        sw.execute();
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

        histogramPanel = new javax.swing.JPanel();
        infoLabel = new javax.swing.JLabel();
        currentValuePanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        currentValueLabel = new javax.swing.JLabel();
        newValueToolBar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        jLabel2 = new javax.swing.JLabel();
        jThresholdSpinner = new javax.swing.JSpinner();
        jApplyButton = new javax.swing.JButton();

        setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.border.title"))); // NOI18N
        setOpaque(false);
        setLayout(new java.awt.GridBagLayout());

        histogramPanel.setMinimumSize(new java.awt.Dimension(0, 180));
        histogramPanel.setOpaque(false);
        histogramPanel.setPreferredSize(new java.awt.Dimension(0, 180));
        histogramPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(histogramPanel, gridBagConstraints);

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
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(infoLabel, gridBagConstraints);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jLabel1.text")); // NOI18N
        currentValuePanel.add(jLabel1);

        currentValueLabel.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.currentValueLabel.text")); // NOI18N
        currentValuePanel.add(currentValueLabel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        add(currentValuePanel, gridBagConstraints);

        newValueToolBar.setFloatable(false);
        newValueToolBar.setRollover(true);
        newValueToolBar.add(jSeparator1);

        jLabel2.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jLabel2.text")); // NOI18N
        newValueToolBar.add(jLabel2);

        jThresholdSpinner.setMinimumSize(new java.awt.Dimension(60, 28));
        jThresholdSpinner.setPreferredSize(new java.awt.Dimension(64, 28));
        jThresholdSpinner.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jThresholdSpinnerStateChanged(evt);
            }
        });
        newValueToolBar.add(jThresholdSpinner);

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/refresh.png"))); // NOI18N
        jApplyButton.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.text")); // NOI18N
        jApplyButton.setToolTipText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.toolTipText")); // NOI18N
        jApplyButton.setFocusable(false);
        jApplyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jApplyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        newValueToolBar.add(jApplyButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(newValueToolBar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed
        if (simMeasure != null) {
            try {
                jThresholdSpinner.commitEdit();
                simMeasure.setThreshold((float) thresholdSpinnerModel.getValue());
                jApplyButton.setVisible(false);
            } catch (ParseException ex) {
                NotifyDescriptor d
                        = new NotifyDescriptor.Message(NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.threshold.invalid"), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(d);
            }
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
        float newValue = (float) thresholdSpinnerModel.getValue();
        jApplyButton.setVisible(simMeasure != null && simMeasure.getThreshold() != newValue);
    }//GEN-LAST:event_jThresholdSpinnerStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel currentValueLabel;
    private javax.swing.JPanel currentValuePanel;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSpinner jThresholdSpinner;
    private javax.swing.JToolBar newValueToolBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(simMeasure)) {
            if (evt.getPropertyName().equals(SimilarityMeasure.CHANGED_SIMILARITY_VALUES)) {
                setupHistogram((evt.getNewValue() != null ? (JQuickHistogram) evt.getNewValue() : null));
            } else if (evt.getPropertyName().equals(SimilarityMeasure.CHANGED_THRESHOLD_VALUE)) {
                resetSimilarityThreshold((float) evt.getOldValue(), (float) evt.getNewValue());
            }
        }
    }
}
