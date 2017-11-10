/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = FeatureSelection.class)
public class FeatureSelection {

    private final ProjectManager pc;

    public FeatureSelection() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public void doSelection(FeatureSelectionModel fsModel) {
        if (!fsModel.isRemoveUseless()) {
            return;
        }
        AttributesModel attrModel = pc.getAttributesModel();
        List<MolecularDescriptor> allFeatures = new LinkedList<>();
        HashMap<String, MolecularDescriptor[]> mdMap = attrModel.getAllMolecularDescriptors();
        for (Map.Entry<String, MolecularDescriptor[]> entry : mdMap.entrySet()) {
            for (MolecularDescriptor attr : entry.getValue()) {
                allFeatures.add(attr);
            }
        }

        Peptide[] peptides = attrModel.getPeptides();
        Bin[] bins = new Bin[peptides.length];
        double maxScore = Math.log(peptides.length);
        double threshold = fsModel.getEntropyCutoff() * maxScore / 100;
        double score, min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        System.out.println("Max score: " + maxScore);
        System.out.println("cut off: " + threshold);
        for (MolecularDescriptor descriptor : allFeatures) {
            descriptor.resetSummaryStats(peptides);
            fillBins(descriptor, peptides, bins);
            score = calculateEntropy(bins);
            if (score < threshold) {
                System.out.println("Removed: " + descriptor.getDisplayName() + " - score: " + score);
            }
            if (score < min) {
                min = score;
            }
            if (score > max) {
                max = score;
            }
        }
        System.out.println("max: " + max);
        System.out.println("min: " + min);
    }

    private void fillBins(MolecularDescriptor descriptor, Peptide[] peptides, Bin[] bins) {
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

        for (int i = 0; i < peptides.length; i++) {
            binIndex = bins.length - 1;
            val = MolecularDescriptor.getDoubleValue(peptides[i], descriptor);
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
    }

    private double calculateEntropy(Bin[] bins) {
        double entropy = 0.;
        double prob;
        for (Bin bin : bins) {
            if (bin.getCount() > 0) {
                prob = (double) bin.getCount() / bins.length;
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy;
    }
}

class Bin {

    /**
     * The number of items in the bin.
     */
    private int count;

    /**
     * The start boundary.
     */
    private double startBoundary;

    /**
     * The end boundary.
     */
    private double endBoundary;

    /**
     * Creates a new bin.
     *
     * @param startBoundary the start boundary.
     * @param endBoundary the end boundary.
     */
    Bin(double startBoundary, double endBoundary) {
        if (startBoundary > endBoundary) {
            throw new IllegalArgumentException(
                    "Bin:  startBoundary > endBoundary.");
        }
        this.count = 0;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
    }

    /**
     * Returns the number of items in the bin.
     *
     * @return The item count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Increments the item count.
     */
    public void incrementCount() {
        this.count++;
    }

    /**
     * Returns the start boundary.
     *
     * @return The start boundary.
     */
    public double getStartBoundary() {
        return this.startBoundary;
    }

    /**
     * Returns the end boundary.
     *
     * @return The end boundary.
     */
    public double getEndBoundary() {
        return this.endBoundary;
    }

    /**
     * Returns the bin width.
     *
     * @return The bin width.
     */
    public double getBinWidth() {
        return this.endBoundary - this.startBoundary;
    }

}
