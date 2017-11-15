/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.concurrent.ExecutionException;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.jdesktop.swingx.JXBusyLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;

/**
 *
 * @author loge
 */
public class StatsPanel extends javax.swing.JPanel {

    /**
     * Creates new form StatsPanel
     *
     * @param attrModel
     * @param attribute
     */
    public StatsPanel(final AttributesModel attrModel, final MolecularDescriptor attribute) {
        initComponents();

        final JXBusyLabel busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        visualizationPanel.add(busyLabel, BorderLayout.CENTER);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        final DecimalFormat formatter = new DecimalFormat("#.###", symbols);

        SwingWorker sw = new SwingWorker<ChartPanel, Void>() {
            private double max, min, mean, std;

            @Override
            protected ChartPanel doInBackground() throws Exception {
                Peptide[] peptides = attrModel.getPeptides();
                attribute.resetSummaryStats(peptides);
                min = attribute.getMin();
                max = attribute.getMax();
                mean = attribute.getMean();
                std = attribute.getStd();
                return createHistogramPanel(peptides, attribute, min, max, visualizationPanel.getWidth(), visualizationPanel.getHeight());
            }

            @Override
            protected void done() {
                try {
                    ChartPanel chartPanel = get();
                    maxLabel.setText(formatter.format(max));
                    minLabel.setText(formatter.format(min));
                    meanLabel.setText(formatter.format(mean));
                    stdLabel.setText(formatter.format(std));

                    visualizationPanel.removeAll();
                    visualizationPanel.add(chartPanel, BorderLayout.CENTER);
                } catch (InterruptedException | ExecutionException ex) {
                    if (ex.getCause() instanceof MolecularDescriptorNotFoundException) {
                        NotifyDescriptor errorND = ((MolecularDescriptorNotFoundException) ex.getCause()).getErrorND();
                        DialogDisplayer.getDefault().notify(errorND);
                    } else {
                        Exceptions.printStackTrace(ex);
                    }
                    visualizationPanel.removeAll();
                    visualizationPanel.add(new JLabel("Error", new ImageIcon(ImageUtilities.loadImage("org/bapedis/core/resources/sad.png", true)), JLabel.CENTER));
                } finally {
                    busyLabel.setBusy(false);
                    visualizationPanel.revalidate();
                    visualizationPanel.repaint();
                }
            }

        };
        busyLabel.setBusy(true);
        sw.execute();
    }

    private ChartPanel createHistogramPanel(Peptide[] peptides, MolecularDescriptor attribute, double min, double max, int width, int height) throws MolecularDescriptorNotFoundException {
        double[] data = new double[peptides.length];
        int pos = 0;
        for (Peptide pept : peptides) {
            data[pos++] = MolecularDescriptor.getDoubleValue(pept, attribute);
        }

        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries("Histogram", data, 50, min, max);

        JFreeChart chart = ChartFactory.createHistogram(
                "", // chart title
                "", // domain axis label
                "Frequency", // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
                false, // include legend
                false, // tooltips?
                false // URLs?
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        chartPanel.setMinimumSize(new Dimension(width, height));

        return chartPanel;
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

        summaryPanel = new javax.swing.JPanel();
        maxLabel = new javax.swing.JLabel();
        minLabel = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        meanLabel = new javax.swing.JLabel();
        stdLabel = new javax.swing.JLabel();
        visualizationPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(275, 80));
        setPreferredSize(new java.awt.Dimension(275, 80));
        setLayout(new java.awt.GridBagLayout());

        summaryPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.summaryPanel.border.title"))); // NOI18N
        summaryPanel.setPreferredSize(new java.awt.Dimension(275, 51));
        summaryPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(maxLabel, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.maxLabel.text")); // NOI18N
        maxLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        maxLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        maxLabel.setPreferredSize(new java.awt.Dimension(70, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        summaryPanel.add(maxLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(minLabel, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.minLabel.text")); // NOI18N
        minLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        minLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        minLabel.setPreferredSize(new java.awt.Dimension(70, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        summaryPanel.add(minLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        summaryPanel.add(jLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        summaryPanel.add(jLabel2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        summaryPanel.add(jLabel3, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        summaryPanel.add(jLabel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(meanLabel, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.meanLabel.text")); // NOI18N
        meanLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        meanLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        meanLabel.setPreferredSize(new java.awt.Dimension(70, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        summaryPanel.add(meanLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(stdLabel, org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.stdLabel.text")); // NOI18N
        stdLabel.setMaximumSize(new java.awt.Dimension(90, 14));
        stdLabel.setMinimumSize(new java.awt.Dimension(50, 14));
        stdLabel.setPreferredSize(new java.awt.Dimension(70, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        summaryPanel.add(stdLabel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(summaryPanel, gridBagConstraints);

        visualizationPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(StatsPanel.class, "StatsPanel.visualizationPanel.border.title"))); // NOI18N
        visualizationPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(visualizationPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel maxLabel;
    private javax.swing.JLabel meanLabel;
    private javax.swing.JLabel minLabel;
    private javax.swing.JLabel stdLabel;
    private javax.swing.JPanel summaryPanel;
    private javax.swing.JPanel visualizationPanel;
    // End of variables declaration//GEN-END:variables
}
