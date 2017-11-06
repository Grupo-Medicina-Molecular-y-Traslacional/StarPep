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
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.jdesktop.swingx.JXBusyLabel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.statistics.HistogramType;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class StatsPanel extends javax.swing.JPanel {

    protected final AttributesModel attrModel;
    protected final PeptideAttribute attribute;
    protected final JXBusyLabel busyLabel;
    private final DecimalFormat formatter;

    /**
     * Creates new form StatsPanel
     *
     * @param attrModel
     * @param attribute
     */
    public StatsPanel(final AttributesModel attrModel, final PeptideAttribute attribute) {
        initComponents();
        this.attrModel = attrModel;
        this.attribute = attribute;

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        visualizationPanel.add(busyLabel, BorderLayout.CENTER);

        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        formatter = new DecimalFormat("#.###", symbols);

        SwingWorker sw = new SwingWorker<ChartPanel, Void>() {
            private double max, min, mean, std;

            @Override
            protected ChartPanel doInBackground() throws Exception {
                Peptide[] peptides = attrModel.getPeptides();
                double[] data = new double[peptides.length];
                int pos = 0;
                Object val;
                for (Peptide pept : peptides) {
                    val = pept.getAttributeValue(attribute);
                    data[pos++] = val == null ? Double.NaN: PeptideAttribute.convertToDouble(val);
                }
                max = Stats.max(data);
                min = Stats.min(data);
                mean = Stats.mean(data);
                std = Stats.stddevp(data);
                return Stats.createHistogramPanel(data, visualizationPanel.getWidth(), visualizationPanel.getHeight());
            }

            @Override
            protected void done() {
                try {
                    ChartPanel chartPanel = get();
                    maxLabel.setText(formatter.format(max));
                    minLabel.setText(formatter.format(min));
                    meanLabel.setText(formatter.format(mean));
                    stdLabel.setText(formatter.format(std));
                    
                    visualizationPanel.remove(busyLabel);
                    visualizationPanel.add(chartPanel, BorderLayout.CENTER);
                    visualizationPanel.revalidate();
                    visualizationPanel.repaint();
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally{
                    busyLabel.setBusy(false);
                }
            }

        };
        busyLabel.setBusy(true);
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

/**
 * The {@code StdStats} class provides static methods for computing statistics
 * such as min, max, mean, sample standard deviation, and sample variance.
 * <p>
 * For additional documentation, see
 * <a href="https://introcs.cs.princeton.edu/22library">Section 2.2</a> of
 * <i>Computer Science: An Interdisciplinary Approach</i>
 * by Robert Sedgewick and Kevin Wayne.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 */
final class Stats {

    /**
     * Returns the maximum value in the specified array.
     *
     * @param a the array
     * @return the maximum value in the array {@code a[]};
     *         {@code Double.NEGATIVE_INFINITY} if no such value
     */
    public static double max(double[] a) {
        double max = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if (Double.isNaN(a[i])) {
                return Double.NaN;
            }
            if (a[i] > max) {
                max = a[i];
            }
        }
        return max;
    }

    /**
     * Returns the minimum value in the specified array.
     *
     * @param a the array
     * @return the minimum value in the array {@code a[]};
     *         {@code Double.POSITIVE_INFINITY} if no such value
     */
    public static double min(double[] a) {
        double min = Double.POSITIVE_INFINITY;
        for (int i = 0; i < a.length; i++) {
            if (Double.isNaN(a[i])) {
                return Double.NaN;
            }
            if (a[i] < min) {
                min = a[i];
            }
        }
        return min;
    }

    /**
     * Returns the average value in the specified array.
     *
     * @param a the array
     * @return the average value in the array {@code a[]};
     *         {@code Double.NaN} if no such value
     */
    public static double mean(double[] a) {
        if (a.length == 0) {
            return Double.NaN;
        }
        double sum = sum(a);
        return sum / a.length;
    }

    /**
     * Returns the sample variance in the specified array.
     *
     * @param a the array
     * @return the sample variance in the array {@code a[]};
     *         {@code Double.NaN} if no such value
     */
    public static double var(double[] a) {
        if (a.length == 0) {
            return Double.NaN;
        }
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / (a.length - 1);
    }

    /**
     * Returns the population variance in the specified array.
     *
     * @param a the array
     * @return the population variance in the array {@code a[]};
     *         {@code Double.NaN} if no such value
     */
    public static double varp(double[] a) {
        if (a.length == 0) {
            return Double.NaN;
        }
        double avg = mean(a);
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += (a[i] - avg) * (a[i] - avg);
        }
        return sum / a.length;
    }

    /**
     * Returns the sample standard deviation in the specified array.
     *
     * @param a the array
     * @return the sample standard deviation in the array {@code a[]};
     *         {@code Double.NaN} if no such value
     */
    public static double stddev(double[] a) {
        return Math.sqrt(var(a));
    }

    /**
     * Returns the population standard deviation in the specified array.
     *
     * @param a the array
     * @return the population standard deviation in the array;
     * {@code Double.NaN} if no such value
     */
    public static double stddevp(double[] a) {
        return Math.sqrt(varp(a));
    }

    /**
     * Returns the sum of all values in the specified array.
     *
     * @param a the array
     * @return the sum of all values in the array {@code a[]};
     *         {@code 0.0} if no such value
     */
    private static double sum(double[] a) {
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum += a[i];
        }
        return sum;
    }

    public static ChartPanel createHistogramPanel(double[] data, int width, int height) {
        HistogramDataset dataset = new HistogramDataset();
        dataset.setType(HistogramType.FREQUENCY);
        dataset.addSeries("Histogram", data, 50);

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

}