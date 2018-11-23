/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public abstract class FeatureBasedClustering extends AbstractCluster {

    private final NotifyDescriptor emptyMDs;

    public FeatureBasedClustering(AlgorithmFactory factory) {
        super(factory);
        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureBasedClustering.class, "FeatureBasedClustering.emptyMDs"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    protected List<Cluster> cluterize() {
        try {
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
                List<Peptide> peptideList = Arrays.asList(peptides);
                for (MolecularDescriptor md : features) {
                    md.resetSummaryStats(peptideList);
                }
            }

            return cluterize(features);
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorND());
            pc.reportError(ex.getMessage(), workspace);
            cancel();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            cancel();
        } 
        return null;
    }
    
    protected abstract List<Cluster> cluterize(MolecularDescriptor[] features);

}
