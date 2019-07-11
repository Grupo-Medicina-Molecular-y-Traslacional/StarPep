/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Bin;
import org.bapedis.core.model.BinsPartition;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Loge
 */
public class FeatureDiscretization implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    public enum BinsOption{User_Defined, Sturges_Rule, Rice_Rule}

    private final FeatureDiscretizationFactory factory;
    private int numberOfBins;
    private BinsOption binsOption;
    private double maxEntropy;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    protected final AtomicBoolean stopRun;
    ProgressTicket ticket;

    public FeatureDiscretization(FeatureDiscretizationFactory factory) {
        this.factory = factory;
        stopRun = new AtomicBoolean();
        binsOption = BinsOption.User_Defined;
        numberOfBins = 100;
    }

    public int getNumberOfBins() {
        return numberOfBins;
    }

    public void setNumberOfBins(int numberOfBins) {
        this.numberOfBins = numberOfBins;
    }

    public BinsOption getBinsOption() {
        return binsOption;
    }

    public void setBinsOption(BinsOption binsOption) {
        this.binsOption = binsOption;
    }    

    public double getMaxEntropy() {
        return maxEntropy;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        peptides = attrModel.getPeptides();
        stopRun.set(false);
        maxEntropy = Double.NaN;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        peptides = null;
        ticket = null;
    }

    @Override
    public boolean cancel() {
        stopRun.set(true);
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        int numberOfInstances = peptides.size();

        switch (binsOption) {
            case Sturges_Rule:
                numberOfBins = (int) (Math.log(numberOfInstances) / Math.log(2)) + 1;
                break;
            case Rice_Rule:
                numberOfBins = 2 * (int) Math.cbrt(numberOfInstances);
                break;
        }

        pc.reportMsg("Bins option: " + binsOption, workspace);
        pc.reportMsg("Number of bins: " + numberOfBins, workspace);

        List<MolecularDescriptor> allFeatures = new LinkedList<>();

        for (String key : attrModel.getMolecularDescriptorKeys()) {
            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                allFeatures.add(attr);
            }
        }

        ticket.switchToDeterminate(allFeatures.size());
        allFeatures.parallelStream().forEach(descriptor -> {
            if (!stopRun.get()) {
                try {
                    computeBinsPartition(descriptor);
                } catch (MolecularDescriptorNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                    DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                    stopRun.set(true);
                }
                ticket.progress();
            }
        });

        maxEntropy = Math.log(numberOfBins);
    }

    private void computeBinsPartition(MolecularDescriptor descriptor) throws MolecularDescriptorNotFoundException {
        Bin[] bins = new Bin[numberOfBins];
        Bin bin;
        double binWidth, lower, upper, min, max, val;
        int binIndex;
        min = descriptor.getMin();
        max = descriptor.getMax();
        binWidth = (max - min) / bins.length;
        lower = min;

        for (int i = 0; i < bins.length; i++) {
            if (i == bins.length - 1) {
                bin = new Bin(lower, max);
            } else {
                upper = min + (i + 1) * binWidth;
                bin = new Bin(lower, upper);
                lower = upper;
            }
            bins[i] = bin;
        }

        for (Peptide peptide : peptides) {
            binIndex = getBinIndex(peptide, descriptor, bins.length);
            bin = bins[binIndex];
            bin.incrementCount();
        }
        // Set the bins partition for later use  
        descriptor.setBinsPartition(new BinsPartition(bins, peptides.size()));
    }

    public static int getBinIndex(Peptide peptide, MolecularDescriptor descriptor, int numberOfBins) throws MolecularDescriptorNotFoundException {
        int binIndex = numberOfBins - 1;
        double val = MolecularDescriptor.getDoubleValue(peptide, descriptor);
        double min = descriptor.getMin();
        double max = descriptor.getMax();        
        if (val < max) {
            double fraction = (val - min) / (max - min);
            if (fraction < 0.0) {
                fraction = 0.0;
            }
            binIndex = (int) (fraction * numberOfBins);
            // rounding could result in binIndex being equal to bins
            // which will cause an IndexOutOfBoundsException - see bug
            // report 1553088
            if (binIndex >= numberOfBins) {
                binIndex = numberOfBins - 1;
            }
        }
        return binIndex;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureDiscretization copy = (FeatureDiscretization) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
    }

}
