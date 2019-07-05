/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Bin;
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
public class FeatureDiscretization implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public static final int Bin_Number_Peptide_OPTION = 0;
    public static final int Bin_Square_OPTION = 1;
    public static final int Bin_Half_OPTION = 2;
    public static final int Bin_One_Thrid_OPTION = 3;
    public static final int Bin_Sturges_OPTION = 4;
    public static final int Bin_Rice_OPTION = 5;

    private final FeatureDiscretizationFactory factory;
    int numberOfBinsOption, numberOfBins;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    protected final AtomicBoolean stopRun;
    ProgressTicket ticket;

    public FeatureDiscretization(FeatureDiscretizationFactory factory) {
        this.factory = factory;
        stopRun = new AtomicBoolean();
        numberOfBinsOption = Bin_Number_Peptide_OPTION;
        numberOfBins = 0;
    }

    public int getNumberOfBinsOption() {
        return numberOfBinsOption;
    }

    public void setNumberOfBinsOption(int numberOfBinsOption) {
        this.numberOfBinsOption = numberOfBinsOption;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        peptides = attrModel.getPeptides();
        stopRun.set(false);
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
        
        switch (numberOfBinsOption) {
            case Bin_Number_Peptide_OPTION:
                numberOfBins = numberOfInstances;
                break;
            case Bin_Square_OPTION:
                numberOfBins = (int) Math.sqrt(numberOfInstances);
                break;
            case Bin_Half_OPTION:
                numberOfBins = numberOfInstances / 2;
                break;
            case Bin_One_Thrid_OPTION:
                numberOfBins = numberOfInstances / 3;
                break;
            case Bin_Sturges_OPTION:
                numberOfBins = (int) (Math.log(numberOfInstances) / Math.log(2)) + 1;
                break;
            case Bin_Rice_OPTION:
                numberOfBins = 2 * (int) Math.cbrt(numberOfInstances);
                break;
        }

        pc.reportMsg("Number of bins: " + numberOfBins, workspace);
        
        List<MolecularDescriptor> allFeatures = new LinkedList<>();

        for (String key : attrModel.getMolecularDescriptorKeys()) {
            if (!key.equals(MolecularDescriptor.DEFAULT_CATEGORY)) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }
        }

        ticket.switchToDeterminate(allFeatures.size());
        allFeatures.parallelStream().forEach(descriptor -> {
            if (!stopRun.get()) {
                try {
                    compute(descriptor);
                } catch (MolecularDescriptorNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                    DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());                    
                    stopRun.set(true);
                }
                ticket.progress();
            }
        });
    }

    private void compute(MolecularDescriptor descriptor) throws MolecularDescriptorNotFoundException {
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
            binIndex = bins.length - 1;
            val = MolecularDescriptor.getDoubleValue(peptide, descriptor);
            if (val < max) {
                double fraction = (val - min) / (max - min);
                if (fraction < 0.0) {
                    fraction = 0.0;
                }
                binIndex = (int) (fraction * bins.length);
                // rounding could result in binIndex being equal to bins
                // which will cause an IndexOutOfBoundsException - see bug
                // report 1553088
                if (binIndex >= bins.length) {
                    binIndex = bins.length - 1;
                }
            }
            bin = bins[binIndex];
            bin.incrementCount();
        }
        // Set the bins for later use  
        descriptor.setBins(bins);
    }

}
