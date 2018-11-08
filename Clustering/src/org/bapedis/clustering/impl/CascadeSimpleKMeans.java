/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.awt.Component;
import java.awt.Dimension;
import java.util.Random;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import weka.clusterers.Clusterer;
import weka.clusterers.RandomizableClusterer;
import weka.clusterers.SimpleKMeans;
import weka.core.Capabilities;
import weka.core.DenseInstance;
import weka.core.DistanceFunction;
import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author loge
 */
public class CascadeSimpleKMeans extends RandomizableClusterer implements Clusterer {

    private static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected int minNumClusters = 2;
    protected int maxNumClusters = 10;
    protected int restarts = 10;
    protected int maxIterations = 500;
    protected boolean manuallySelectNumClusters=false;
    protected DistanceFunction distanceFunction;
    protected Workspace workspace;

    protected final SimpleKMeans kMeans = new SimpleKMeans();
    protected Instance meanInstance;
    protected int numInstances;

    public Workspace getWorkspace() {
        return workspace;
    }

    public void setWorkspace(Workspace workspace) {
        this.workspace = workspace;
    }

    public int getMinNumClusters() {
        return minNumClusters;
    }

    public void setMinNumClusters(int minNumClusters) {
        this.minNumClusters = minNumClusters;
    }

    public int getMaxNumClusters() {
        return maxNumClusters;
    }

    public void setMaxNumClusters(int maxNumClusters) {
        this.maxNumClusters = maxNumClusters;
    }

    public int getRestarts() {
        return restarts;
    }

    public void setRestarts(int restarts) {
        this.restarts = restarts;
    }

    public DistanceFunction getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(DistanceFunction distanceFunction) {
        this.distanceFunction = distanceFunction;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public boolean isManuallySelectNumClusters() {
        return manuallySelectNumClusters;
    }

    public void setManuallySelectNumClusters(boolean manuallySelectNumClusters) {
        this.manuallySelectNumClusters = manuallySelectNumClusters;
    }        

    @Override
    public void buildClusterer(Instances data) throws Exception {               
        kMeans.setDistanceFunction(distanceFunction);
        kMeans.setMaxIterations(maxIterations);
        kMeans.setDontReplaceMissingValues(true);
        numInstances = data.numInstances();

        meanInstance = new DenseInstance(data.numAttributes());
        for (int i = 0; i < data.numAttributes(); i++) {
            meanInstance.setValue(i, data.meanOrMode(i));
        }

        Random r = new Random(m_Seed);
        double meanCHs[] = new double[maxNumClusters + 1 - minNumClusters];
        double maxCHs[] = new double[maxNumClusters + 1 - minNumClusters];
        int maxSeed[] = new int[maxNumClusters + 1 - minNumClusters];

        for (int i = 0; i < restarts; i++) {
            for (int k = minNumClusters; k <= maxNumClusters; k++) {
                int seed = r.nextInt();
                kMeans.setSeed(seed);
                kMeans.setNumClusters(k);
                kMeans.buildClusterer(data);
                double ch = getCalinskiHarabasz();

                int index = k - minNumClusters;
                meanCHs[index] = (meanCHs[index] * i + ch) / (double) (i + 1);
                if (i == 0 || ch > maxCHs[index]) {
                    maxCHs[index] = ch;
                    maxSeed[index] = seed;
                }

            }
        }

        int bestK = -1;
        double maxCH = -1;
        for (int k = minNumClusters; k <= maxNumClusters; k++) {
            int index = k - minNumClusters;
            if (bestK == -1 || meanCHs[index] > maxCH) {
                maxCH = meanCHs[index];
                bestK = k;
            }
        }
        if (manuallySelectNumClusters) {
            int selectedK = selectKManually(meanCHs, bestK);
            if (selectedK != -1) {
                bestK = selectedK;
            }
        }
        int bestSeed = maxSeed[bestK - minNumClusters];

        pc.reportMsg("k (yields highest mean CH): " + bestK, workspace);
        pc.reportMsg("seed (highest CH for k=" + bestK + ") : " + bestSeed, workspace);

        kMeans.setSeed(bestSeed);
        kMeans.setNumClusters(bestK);
        kMeans.buildClusterer(data);
    }

    @Override
    public int numberOfClusters() throws Exception {
        return kMeans.numberOfClusters();
    }

    @Override
    public int clusterInstance(Instance instance) throws Exception {
        return kMeans.clusterInstance(instance);
    }

    @Override
    public double[] distributionForInstance(Instance instance) throws Exception {
        return kMeans.distributionForInstance(instance);
    }

    @Override
    public Capabilities getCapabilities() {
        return kMeans.getCapabilities();
    }

    private int selectKManually(double[] meanCHs, int bestK) {
        DefaultTableModel m = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        JTable t = new JTable(m);
        t.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        t.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int row, int column) {
                return super.getTableCellRendererComponent(table, column == 1 ? String.format("%.2f", (Double) value) : value,
                        isSelected, hasFocus, row, column);
            }
        });
        m.addColumn("Num clusters");
        m.addColumn("Mean CH score");
        for (int i = 0; i < meanCHs.length; i++) {
            m.addRow(new Object[]{new Integer(minNumClusters + i), new Double(meanCHs[i])});
        }
        t.setRowSelectionInterval(bestK - minNumClusters, bestK - minNumClusters);
        JScrollPane pane = new JScrollPane(t);
        if (meanCHs.length < 20) {
            pane.setPreferredSize(new Dimension(300, t.getRowHeight() * (meanCHs.length + 2)));
        }
        DialogDescriptor dd = new DialogDescriptor(pane, "Select number of clusters");
        Object result = DialogDisplayer.getDefault().notify(dd);
        if (result == NotifyDescriptor.OK_OPTION){
            return (t.getSelectedRow() + minNumClusters);
        }
        return -1;
    }

    /**
     * see Calinski, T. and J. Harabasz. 1974. A dendrite method for cluster
     * analysis. Commun. Stat. 3: 1-27. quoted in German:
     * http://books.google.com/books?id=-f9Ox0p1-D4C&lpg=PA394&ots=SV3JfRIkQn&dq=Calinski%20and%20Harabasz&hl=de&pg=PA394#v=onepage&q&f=false
     *
     * @param kMeans
     * @param data
     * @return
     */
    private double getCalinskiHarabasz() {
        double betweenClusters = getSquaredErrorBetweenClusters() / (double) (kMeans.getNumClusters() - 1);
        double withinClusters = kMeans.getSquaredError() / (double) (numInstances - kMeans.getNumClusters());
        return betweenClusters / withinClusters;
    }

    private double getSquaredErrorBetweenClusters() {
        double errorSum = 0;
        double dist;
        for (int i = 0; i < kMeans.getNumClusters(); i++) {
            dist = kMeans.getDistanceFunction().distance(kMeans.getClusterCentroids().instance(i), meanInstance);
            if (kMeans.getDistanceFunction() instanceof EuclideanDistance)//Euclidean distance to Squared Euclidean distance
            {
                dist *= dist;
            }
            dist *= kMeans.getClusterSizes()[i];
            errorSum += dist;
        }
        return errorSum;
    }

}
