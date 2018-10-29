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
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.io.impl.MyArffWritable;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
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
public class WekaClusterer<T extends Clusterer> extends AbstractCluster {

    protected final T clusterer;
    private final NotifyDescriptor emptyMDs;

    public WekaClusterer(T clusterer, AlgorithmFactory factory) {
        super(factory);
        this.clusterer = clusterer;
        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(WekaClusterer.class, "WekaClusterer.emptyMDs"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    protected void cluterize() {
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        BufferedReader reader = null;
        try {
            //----------Load all descriptors
            List<MolecularDescriptor> allFeatures = new LinkedList<>();

            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }

            if (allFeatures.isEmpty()) {
//                DialogDisplayer.getDefault().notify(emptyMDs);
                pc.reportError("There is not calculated molecular descriptors", workspace);
                return;
            }

            MolecularDescriptor[] features = allFeatures.toArray(new MolecularDescriptor[0]);

            //----Load instances
            ArffWriter.DEBUG = true;
            MyArffWritable writable = new MyArffWritable(peptides, features);
            writable.setOutputOption(MyArffWritable.OUTPUT_OPTION.MIN_MAX);
            File f = ArffWriter.writeToArffFile(writable);
            reader = new BufferedReader(new FileReader(f));
            Instances data = new Instances(reader);

            //Clustering dataset
            ClusterEvaluation eval = new ClusterEvaluation();
//            TaskProvider.debug("Building clusterer");
            clusterer.buildClusterer(data);
            eval.setClusterer(clusterer);
//            TaskProvider.debug("Clustering dataset");
            eval.evaluateClusterer(data);
//            Settings.LOGGER.info("# of clusters: " + eval.getNumClusters());

            List<Integer[]> clusterAssignements = new ArrayList<Integer[]>();
            for (int j = 0; j < eval.getClusterAssignments().length; j++) {
                clusterAssignements.add(new Integer[]{(int) eval.getClusterAssignments()[j]});
            }
            
            

        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                reader.close();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

}
