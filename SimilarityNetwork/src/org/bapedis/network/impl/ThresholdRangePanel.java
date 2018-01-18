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
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.jdesktop.swingx.JXBusyLabel;
import org.jfree.chart.ChartPanel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ThresholdRangePanel extends javax.swing.JPanel implements PropertyChangeListener {

    private ChartPanel chartPanel;
    protected final JXBusyLabel busyLabel;
    protected SimilarityNetworkBaseAlgo simNetAlgo;
    protected final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private RichTooltip richTooltip;
    private final DecimalFormat formatter;

    static {
        UIManager.put("Slider.paintValue", false);
    }

    public ThresholdRangePanel() {
        initComponents();

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("0.00", symbols);
    }

    public void setup(SimilarityNetworkBaseAlgo simNetAlgo) {
        if (this.simNetAlgo != null) {
            this.simNetAlgo.removeSimilarityChangeListener(this);
        }
        this.simNetAlgo = simNetAlgo;
        this.simNetAlgo.addSimilarityChangeListener(this);

        JQuickHistogram histogram = simNetAlgo.getHistogram();
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
        thresholdSlider = new javax.swing.JSlider();
        java.text.NumberFormat format = java.text.DecimalFormat.getInstance(Locale.ENGLISH);
        format.setMinimumFractionDigits(2);
        format.setMaximumFractionDigits(2);
        format.setRoundingMode(java.math.RoundingMode.HALF_UP);
        javax.swing.text.NumberFormatter textFormatter = new javax.swing.text.NumberFormatter(format);
        textFormatter.setValueClass(Float.class);
        textFormatter.setMinimum(0f);
        textFormatter.setMaximum(1f);
        textFormatter.setAllowsInvalid(false);
        newValueTextField = new javax.swing.JFormattedTextField(textFormatter);
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
        gridBagConstraints.gridy = 2;
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
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(infoLabel, gridBagConstraints);

        currentValuePanel.setLayout(new java.awt.GridBagLayout());

        thresholdSlider.setToolTipText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.thresholdSlider.toolTipText")); // NOI18N
        thresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                thresholdSliderStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        currentValuePanel.add(thresholdSlider, gridBagConstraints);

        newValueTextField.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.newValueTextField.text")); // NOI18N
        newValueTextField.setMinimumSize(new java.awt.Dimension(50, 26));
        newValueTextField.setPreferredSize(new java.awt.Dimension(50, 26));
        newValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newValueTextFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        currentValuePanel.add(newValueTextField, gridBagConstraints);

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/network/resources/refresh.png"))); // NOI18N
        jApplyButton.setText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.text")); // NOI18N
        jApplyButton.setToolTipText(org.openide.util.NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.jApplyButton.toolTipText")); // NOI18N
        jApplyButton.setFocusable(false);
        jApplyButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jApplyButton.setMaximumSize(new java.awt.Dimension(50, 26));
        jApplyButton.setMinimumSize(new java.awt.Dimension(50, 26));
        jApplyButton.setPreferredSize(new java.awt.Dimension(50, 26));
        jApplyButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        currentValuePanel.add(jApplyButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(currentValuePanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed
//        if (simMeasure != null) {
//            try {
//                float newValue = Float.parseFloat(newValueTextField.getText());
//                simMeasure.setThreshold(newValue);
//
//                int newVal = Math.round(newValue * 100);
//                if (thresholdSlider.getValue() != newVal) {
//                    thresholdSlider.setValue(newVal);
//                }
//            } catch (NumberFormatException ex) {
//                NotifyDescriptor d
//                        = new NotifyDescriptor.Message(NbBundle.getMessage(ThresholdRangePanel.class, "ThresholdRangePanel.threshold.invalid"), NotifyDescriptor.ERROR_MESSAGE);
//                DialogDisplayer.getDefault().notify(d);
//            }
//        }
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

    private void thresholdSliderStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_thresholdSliderStateChanged
        String newTextValue = formatter.format(thresholdSlider.getValue() / 100.);
        if (!newValueTextField.getText().equals(newTextValue)) {
            newValueTextField.setText(newTextValue);
        }
    }//GEN-LAST:event_thresholdSliderStateChanged

    private void newValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newValueTextFieldActionPerformed
        int newVal = Math.round(Float.parseFloat(newValueTextField.getText()) * 100);
        if (thresholdSlider.getValue() != newVal) {
            thresholdSlider.setValue(newVal);
        }
    }//GEN-LAST:event_newValueTextFieldActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel currentValuePanel;
    private javax.swing.JPanel histogramPanel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton jApplyButton;
    private javax.swing.JTextField newValueTextField;
    private javax.swing.JSlider thresholdSlider;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(simNetAlgo) &&
               evt.getPropertyName().equals(SimilarityNetworkBaseAlgo.CHANGED_SIMILARITY_VALUES)) {
            setupHistogram((evt.getNewValue() != null ? (JQuickHistogram) evt.getNewValue() : null)); 
        }
    }
}
