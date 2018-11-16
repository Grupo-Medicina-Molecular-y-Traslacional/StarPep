/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import org.bapedis.core.io.impl.MyArffWritable;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
import org.bapedis.core.util.ArffWriter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import weka.clusterers.ClusterEvaluation;
import weka.clusterers.Clusterer;
import weka.core.Instances;

/**
 *
 * @author loge
 */
public abstract class WekaClusterer<T extends Clusterer> extends AbstractCluster {

    static protected final String PRO_CATEGORY = "Properties";

    static public final String CLUSTER_DISTANCE = "-A";
    static public final String CLUSTER_DISTANCE_MANHATTAN = "weka.core.ManhattanDistance -R first-last";
    static public final String CLUSTER_DISTANCE_EUCLIDEAN = "weka.core.EuclideanDistance -R first-last";
    static public final String CLUSTER_DISTANCE_CHEBYSHEV = "weka.core.ChebyshevDistance -R first-last";

    static public final String CLUSTER_LINK_TYPE = "-L";
    static public final String CLUSTER_LINK_WARD = "WARD";
    static public final String CLUSTER_LINK_MEAN = "MEAN";
    static public final String CLUSTER_LINK_SINGLE = "SINGLE";
    static public final String CLUSTER_LINK_AVERAGE = "AVERAGE";
    static public final String CLUSTER_LINK_COMPLETE = "COMPLETE";
    static public final String CLUSTER_LINK_CENTROID = "CENTROID";

    static public final String CLUSTER_SEED = "-S";
    static public final String CLUSTER_NUMBER = "-N";
    static public final String CLUSTER_STD_DEV = "-M";
    static public final String CLUSTER_ITERATION = "-I";
    static public final String CLUSTER_DISPLAY_MODEL_IN_OLD_FORMAT = "-O";
    static public final String CLUSTER_DNOT_REPLACE_MISS_VALUES = "-M";
    static public final String CLUSTER_DIST_BRANCH_LENGTH = "-B";

    protected final T clusterer;
    private final NotifyDescriptor emptyMDs;

    public WekaClusterer(T clusterer, AlgorithmFactory factory) {
        super(factory);
        this.clusterer = clusterer;
        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(WekaClusterer.class, "WekaClusterer.emptyMDs"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    protected List<Cluster> cluterize() {
        BufferedReader reader = null;
        try {
            List<Cluster> clusterList = new LinkedList<>();
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            // Configure a cluster instance
            try {
                configureClusterer();
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                cancel();
            }

            // Load all descriptors
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            if (!stopRun) {

                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        allFeatures.add(attr);
                    }
                }

                if (allFeatures.size() <= 1) {
                    DialogDisplayer.getDefault().notify(emptyMDs);
                    pc.reportError("There is not enough molecular descriptors", workspace);
                    cancel();
                }
            }

            // Preprocessing of moleculas features
            MolecularDescriptor[] features = allFeatures.toArray(new MolecularDescriptor[0]);
            if (!stopRun) {
                pc.reportMsg("Preprocessing of moleculas features: calculating max, min, mean and std.\n", workspace);
                ticket.progress("Preprocessing");
                for (MolecularDescriptor md : features) {
                    md.resetSummaryStats(peptides);
                }
            }

            // Load instances
            Instances data = null;
            if (!stopRun) {
                ArffWriter.DEBUG = true;
                MyArffWritable writable = new MyArffWritable(peptides, features);
                writable.setOutputOption(MyArffWritable.OUTPUT_OPTION.MIN_MAX);
                File f = ArffWriter.writeToArffFile(writable);
                reader = new BufferedReader(new FileReader(f));
                data = new Instances(reader);
            }

            // Clustering dataset 
            if (!stopRun && data != null) {
                ticket.progress("Building clusterer");
                pc.reportMsg("Building clusterer\n", workspace);
                clusterer.buildClusterer(data);
            }

            // Create cluster evaluation
            ClusterEvaluation eval = new ClusterEvaluation();
            if (!stopRun && data != null) {
                eval.setClusterer(clusterer);
            }

            if (!stopRun && data != null) {
                ticket.progress("Clustering dataset");
                pc.reportMsg("Clustering dataset\n", workspace);
                eval.evaluateClusterer(data);
                pc.reportMsg("No. of clusters: " + eval.getNumClusters() + "\n", workspace);
            }

            // Populate cluster list
            if (!stopRun && data != null) {
                int clusterID;
                Cluster cluster;
                TreeMap<Integer, Cluster> clusterMap = new TreeMap<>();
                for (int j = 0; j < eval.getClusterAssignments().length; j++) {
                    clusterID = (int) eval.getClusterAssignments()[j];
                    if (clusterMap.containsKey(clusterID)) {
                        cluster = clusterMap.get(clusterID);
                    } else {
                        cluster = new Cluster(clusterID);
                        clusterMap.put(clusterID, cluster);
                    }
                    cluster.addMember(peptides[j]);
                }
                for (Map.Entry<Integer, Cluster> entry : clusterMap.entrySet()) {
                    clusterList.add(entry.getValue());
                }
            }
            
            return clusterList;
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorND());
            pc.reportError(ex.getMessage(), workspace);
            cancel();
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
            cancel();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
            cancel();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            cancel();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    protected abstract void configureClusterer() throws Exception;

}
