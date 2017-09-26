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
package org.bapedis.core.spi.algo.impl.csn;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.ui.components.JQuickHistogram;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.gephi.graph.api.Edge;
import org.jdesktop.swingx.JXBusyLabel;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.plot.CategoryMarker;
import org.jfree.chart.plot.CategoryPlot;
import org.openide.util.Exceptions;

/**
 *
 * @author Mathieu Bastian
 */
public class ThresholdRangePanel extends javax.swing.JPanel {

    private static final int MAXIMUM_VALUE = 100;
    private static final int DEFAULT_VALUE = 70;
    private final JQuickHistogram histogram;
    private ChartPanel chartPanel;
    private final SpinnerNumberModel thresholdSpinnerModel;
    protected final JXBusyLabel busyLabel;
    protected final double STEP_SIZE = 0.01;

    public ThresholdRangePanel() {
        initComponents();
        thresholdSpinnerModel = new SpinnerNumberModel((double) DEFAULT_VALUE / MAXIMUM_VALUE, 0, 1, STEP_SIZE);
        jThresholdSpinner.setModel(thresholdSpinnerModel);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        histogram = new JQuickHistogram();
        histogram.setConstraintHeight(histogramPanel.getHeight());

    }

    static {
        UIManager.put("Slider.paintValue", false);
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
            if (chartPanel != null) {
                histogramPanel.add(chartPanel, BorderLayout.CENTER);
            }                        
        }
        histogramPanel.revalidate();
        histogramPanel.repaint();
    }

    public void setup(final List<Edge> similarityEdges) {
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
                        if (chartPanel != null) {
                            histogramPanel.remove(chartPanel);
                        }
                        chartPanel = histogram.createChart();
                    } catch (InterruptedException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        setBusy(false);
                    }
                }
            };
            sw.execute();
        }
    }

    private RichTooltip buildTooltip(JQuickHistogram histogram) {
//        if (histogram.countValues() == 0) {
//            return null;
//        }
//        NumberFormat formatter = DecimalFormat.getNumberInstance();
//        formatter.setMaximumFractionDigits(3);
//        String average = formatter.format(histogram.getAverage());
//        String averageInRange = formatter.format(histogram.getAverageInRange());
//        RichTooltip richTooltip = new RichTooltip();
//        richTooltip.setTitle("Statistics (In-Range)");
//        richTooltip.addDescriptionSection("<html><b># of Values:</b> " + histogram.countValues() + " (" + histogram.countInRange() + ")");
//        richTooltip.addDescriptionSection("<html><b>Average:</b> " + average + " (" + averageInRange + ")");
//        richTooltip.addDescriptionSection("<html><b>Median:</b> " + histogram.getMedian() + " (" + histogram.getMedianInRange() + ")");
//        return richTooltip;
        return null;
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(histogramPanel, gridBagConstraints);

        jApplyButton.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.text")); // NOI18N
        jApplyButton.setPreferredSize(new java.awt.Dimension(60, 29));
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jApplyButton, gridBagConstraints);

        jThresholdSpinner.setMinimumSize(new java.awt.Dimension(60, 28));
        jThresholdSpinner.setPreferredSize(new java.awt.Dimension(60, 28));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(jThresholdSpinner, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed

    }//GEN-LAST:event_jApplyButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSpinner jThresholdSpinner;
    // End of variables declaration//GEN-END:variables
}
