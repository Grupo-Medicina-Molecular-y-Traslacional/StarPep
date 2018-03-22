/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureFilteringAlgo implements Algorithm {

    protected final ProjectManager pc;
    protected final FeatureFilteringFactory factory;

    //Entropy cutoff for reference: 
    public static final int[] ENTROPY_CUTOFF_REFS = new int[]{10, 25, 50};
    public static final int ENTROPY_CUTOFF_MIN = 10;
    public static final int ENTROPY_CUTOFF_MAX = 50;
    public static final int ENTROPY_DEFAULT_VALUE = 20;
    public static final int ENTROPY_MAJORTICKSPACING = 15;
    public static final int ENTROPY_MINORTICKSPACING = 5;

    private static final String PEARSON = "Pearson";
    private static final String SPEARMAN = "Spearman";
    public static final String[] CORRELATION_METHODS = new String[]{PEARSON, SPEARMAN};
    public static final int CORRELATION_DEFAULT_INDEX = 0;

    //Correlation cutoff for references: 
    public static final int[] CORRELATION_CUTOFF_REFS = new int[]{50, 70, 100};
    public static final int CORRELATION_CUTOFF_MIN = 50;
    public static final int CORRELATION_CUTOFF_MAX = 100;
    public static final int CORRELATION_DEFAULT_VALUE = 95;
    public static final int CORRELATION_MAJORTICKSPACING = 10;
    public static final int CORRELATION_MINORTICKSPACING = 5;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    protected boolean stopRun;
    ProgressTicket ticket;

    public static final String RUNNING = "RUNNING";
    protected boolean running;
    protected transient final PropertyChangeSupport propertyChangeSupport;

    private boolean removeUseless, removeRedundant;
    private int entropyCutoff, correlationIndex, correlationCutoff;
    private NotifyDescriptor emptyMDs;

    public FeatureFilteringAlgo(FeatureFilteringFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;

        entropyCutoff = ENTROPY_DEFAULT_VALUE;
        correlationIndex = CORRELATION_DEFAULT_INDEX;
        correlationCutoff = CORRELATION_DEFAULT_VALUE;
        removeUseless = true;
        removeRedundant = false;

        running = false;
        propertyChangeSupport = new PropertyChangeSupport(this);

        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureFilteringAlgo.class, "FeatureFilteringAlgo.emptyMDs.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public boolean isRemoveUseless() {
        return removeUseless;
    }

    public void setRemoveUseless(boolean removeUseless) {
        this.removeUseless = removeUseless;
    }

    public boolean isRemoveRedundant() {
        return removeRedundant;
    }

    public void setRemoveRedundant(boolean removeRedundant) {
        this.removeRedundant = removeRedundant;
    }

    public int getEntropyCutoff() {
        return entropyCutoff;
    }

    public void setEntropyCutoff(int entropyCutoff) {
        if (entropyCutoff < ENTROPY_CUTOFF_MIN || entropyCutoff > ENTROPY_CUTOFF_MAX) {
            throw new IllegalArgumentException("Invalid value for entropy cutoff. It should be between " + ENTROPY_CUTOFF_MIN + " and " + ENTROPY_CUTOFF_MAX);
        }
        this.entropyCutoff = entropyCutoff;
    }

    public int getCorrelationCutoff() {
        return correlationCutoff;
    }

    public int getCorrelationIndex() {
        return correlationIndex;
    }

    public void setCorrelationIndex(int correlationIndex) {
        if (correlationIndex < 0 || correlationIndex >= CORRELATION_METHODS.length) {
            throw new IllegalArgumentException("Invalid value for correlation index. It should be between " + 0 + " and " + (CORRELATION_METHODS.length - 1));
        }
        this.correlationIndex = correlationIndex;
    }

    public void setCorrelationCutoff(int correlationCutoff) {
        if (correlationCutoff < CORRELATION_CUTOFF_MIN || correlationCutoff > CORRELATION_CUTOFF_MAX) {
            throw new IllegalArgumentException("Invalid value for correlation cutoff. It should be between " + CORRELATION_CUTOFF_MIN + " and " + CORRELATION_CUTOFF_MAX);
        }
        this.correlationCutoff = correlationCutoff;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running;
        this.running = running;
        if (workspace != null) {
            workspace.setBusy(running);
        }
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        stopRun = false;
        setRunning(true);
    }

    @Override
    public void endAlgo() {
        setRunning(false);
        workspace = null;
        attrModel = null;
        ticket = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
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
        if (!isRemoveUseless()) {
            return;
        }

        try {
            List<MolecularDescriptor> allFeatures = new LinkedList<>();

            for (String key : attrModel.getMolecularDescriptorKeys()) {
                if (!key.equals(MolecularDescriptor.DEFAULT_CATEGORY)) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        allFeatures.add(attr);
                    }
                }
            }

            if (allFeatures.isEmpty()) {
                DialogDisplayer.getDefault().notify(emptyMDs);
                pc.reportError("There is not calculated molecular descriptors", workspace);
                return;
            }

            //---------------Remove Useless--------------
            List<Peptide> peptides = attrModel.getPeptides();
            String state1 = NbBundle.getMessage(FeatureFilteringAlgo.class, "FeatureFilteringAlgo.task.removeUseless");
            pc.reportMsg(state1 + "\n", workspace);
            ticket.progress(state1);
            ticket.switchToDeterminate(allFeatures.size());

            Bin[] bins = new Bin[peptides.size()];
            double maxScore = Math.log(peptides.size());
            double threshold = getEntropyCutoff() * maxScore / 100;
            double score;

            pc.reportMsg("Entropy max score: " + maxScore, workspace);
            pc.reportMsg("Entropy cutoff value: " + threshold + "\n", workspace);
            LinkedList<MolecularDescriptor> toRemove = new LinkedList<>();
            for (MolecularDescriptor descriptor : allFeatures) {
                if (!stopRun) {
                    descriptor.resetSummaryStats(peptides);
                    fillBins(descriptor, peptides, bins);
                    score = calculateEntropy(bins);
                    descriptor.setScore(score);
                    if (score < threshold) {
                        toRemove.add(descriptor);
                        attrModel.deleteAttribute(descriptor);
                        pc.reportMsg("Removed: " + descriptor.getDisplayName() + " - score: " + score, workspace);
                    }
                    ticket.progress();
                }
            }
            allFeatures.removeAll(toRemove);
            int uselessRemovedSize = toRemove.size();
            pc.reportMsg("Useless features removed: " + uselessRemovedSize + "\n", workspace);

            pc.reportMsg("Top 5", workspace);
            MolecularDescriptor[] rankedFeatures = allFeatures.toArray(new MolecularDescriptor[0]);
            Arrays.parallelSort(rankedFeatures, new Comparator<MolecularDescriptor>() {
                @Override
                public int compare(MolecularDescriptor o1, MolecularDescriptor o2) {
                    if (o1.getScore() > o2.getScore()) {
                        return -1;
                    }
                    if (o1.getScore() < o2.getScore()) {
                        return 1;
                    }
                    return 0;
                }
            });
            for (int i = 0; i < rankedFeatures.length && i < 5; i++) {
                pc.reportMsg(rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getScore(), workspace);
            }
            pc.reportMsg("...", workspace);
            pc.reportMsg("Bottom 3", workspace);
            for (int i = Math.max(rankedFeatures.length - 3, 0); i < rankedFeatures.length; i++) {
                pc.reportMsg(rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getScore(), workspace);
            }

            //---------------Remove Redundant--------------
            int redundantRemoveSize = 0;
            if (isRemoveRedundant()) {
                toRemove.clear();
                int workUnits = rankedFeatures.length + (rankedFeatures.length * (rankedFeatures.length - 1)) / 2;
                ticket.switchToDeterminate(workUnits);

                String state2 = NbBundle.getMessage(FeatureFilteringAlgo.class, "FeatureFilteringAlgo.task.removeRedundant");
                ticket.progress(state2);
                pc.reportMsg("\n", workspace);
                pc.reportMsg(state2 + "\n", workspace);

                threshold = getCorrelationCutoff() / 100.;
                pc.reportMsg("Correlation cutoff value: " + threshold + "\n", workspace);

                pc.reportMsg("Calculating descriptor matrix", workspace);
                double[][] descriptorMatrix = new double[rankedFeatures.length][];
                double[] column;
                int pos;
                if (CORRELATION_METHODS[correlationIndex].equals(PEARSON)) {
                    for (int i = 0; i < rankedFeatures.length && !stopRun; i++) {
                        column = new double[peptides.size()];
                        pos = 0;
                        for (Peptide peptide : peptides) {
                            column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[i]);
                        }
                        descriptorMatrix[i] = column;
                        ticket.progress();
                    }
                } else if (CORRELATION_METHODS[correlationIndex].equals(SPEARMAN)) {
                    column = new double[peptides.size()]; // Temporal column
                    for (int i = 0; i < rankedFeatures.length && !stopRun; i++) {
                        pos = 0;
                        for (Peptide peptide : peptides) {
                            column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[i]);
                        }
                        descriptorMatrix[i] = rank(column);
                        ticket.progress();
                    }
                }

                pc.reportMsg("Calculating correlation: " + CORRELATION_METHODS[correlationIndex], workspace);
                double[] rank1, rank2;
                for (int i = 0; i < rankedFeatures.length - 1 && !stopRun; i++) {
                    if (rankedFeatures[i] != null) {
                        rank1 = descriptorMatrix[i];
                        for (int j = i + 1; j < rankedFeatures.length && !stopRun; j++) {
                            if (rankedFeatures[j] != null) {
                                rank2 = descriptorMatrix[j];
                                score = calculatePearsonCorrelation(rank1, rank2);
                                if (Math.abs(score) >= threshold) {
                                    attrModel.deleteAttribute(rankedFeatures[j]);
                                    pc.reportMsg("Removed: " + rankedFeatures[j].getDisplayName() + " - " + String.format("corr(%s) = %f", rankedFeatures[i].getDisplayName(), score), workspace);
                                    toRemove.add(rankedFeatures[j]);
                                    rankedFeatures[j] = null;
                                    descriptorMatrix[j] = null;
                                }
                            }
                            ticket.progress();
                        }
                    } else {
                        //Report progress of units
                        ticket.progress(rankedFeatures.length - (i + 1));
                    }
                }

                redundantRemoveSize = toRemove.size();
                pc.reportMsg("Redundant features removed: " + redundantRemoveSize, workspace);
            }
            pc.reportMsg("\nTotal of removed features: " + (uselessRemovedSize + redundantRemoveSize), workspace);
        } catch (MolecularDescriptorNotFoundException ex) {
            NotifyDescriptor errorND = ex.getErrorND();
            DialogDisplayer.getDefault().notify(errorND);
            pc.reportError(ex.getMessage(), workspace);
        }
    }

    private void fillBins(MolecularDescriptor descriptor, List<Peptide> peptides, Bin[] bins) throws MolecularDescriptorNotFoundException {
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

    private double[] rank(double[] data) {
        // Array recording initial positions of data to be ranked
        IntDoublePair[] ranks = new IntDoublePair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntDoublePair(data[i], i);
        }

        // Sort the IntDoublePairs
        Arrays.parallelSort(ranks);

        // Walk the sorted array, filling output array using sorted positions,
        // resolving ties as we go
        double[] out = new double[ranks.length];
        int pos = 1;  // position in sorted array
        out[ranks[0].getPosition()] = pos;
        List<Integer> tiesTrace = new LinkedList<>();
        tiesTrace.add(ranks[0].getPosition());
        for (int i = 1; i < ranks.length; i++) {
            if (Double.compare(ranks[i].getValue(), ranks[i - 1].getValue()) > 0) {
                // tie sequence has ended (or had length 1)
                pos = i + 1;
                if (tiesTrace.size() > 1) {  // if seq is nontrivial, resolve
                    resolveTie(out, tiesTrace);
                }
                tiesTrace = new LinkedList<>();
                tiesTrace.add(ranks[i].getPosition());
            } else {
                // tie sequence continues
                tiesTrace.add(ranks[i].getPosition());
            }
            out[ranks[i].getPosition()] = pos;
        }
        if (tiesTrace.size() > 1) {  // handle tie sequence at end
            resolveTie(out, tiesTrace);
        }
        return out;
    }

    private double calculatePearsonCorrelation(double[] xArray, double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new IllegalArgumentException("Array dimensions mismatch");
        } else if (xArray.length < 2) {
            throw new IllegalArgumentException("Insufficient array dimension");
        }

        double diff1, diff2, num = 0.0, sx = 0.0, sy = 0.0;
        double xMean = MolecularDescriptor.mean(xArray), yMean = MolecularDescriptor.mean(yArray);

        for (int i = 0; i < xArray.length; i++) {
            diff1 = xArray[i] - xMean;
            diff2 = yArray[i] - yMean;
            num += (diff1 * diff2);
            sx += (diff1 * diff1);
            sy += (diff2 * diff2);
        }
        return num / Math.sqrt(sx * sy);
    }

    /**
     * Resolve a sequence of ties, using the configured {@link TiesStrategy}.
     * The input <code>ranks</code> array is expected to take the same value for
     * all indices in <code>tiesTrace</code>. The common value is recoded
     * according to the tiesStrategy. For example, if ranks = <5,8,2,6,2,7,1,2>,
     * tiesTrace = <2,4,7> and tiesStrategy is MINIMUM, ranks will be unchanged.
     * The same array and trace with tiesStrategy AVERAGE will come out
     * <5,8,3,6,3,7,1,3>.
     *
     * @param ranks array of ranks
     * @param tiesTrace list of indices where <code>ranks</code> is constant --
     * that is, for any i and j in TiesTrace, <code> ranks[i] == ranks[j]
     * </code>
     */
    private void resolveTie(double[] ranks, List<Integer> tiesTrace) {
//        // constant value of ranks over tiesTrace
        final double c = ranks[tiesTrace.get(0)];

        // length of sequence of tied ranks
        final int length = tiesTrace.size();

        // Replace ranks with average 
        double value = (2 * c + length - 1) / 2d;
//        double sum = 0;
//        for (int pos : tiesTrace) {
//            sum += pos;
//        }
//        double avg = sum / length;

        Iterator<Integer> iterator = tiesTrace.iterator();
        while (iterator.hasNext()) {
            ranks[iterator.next()] = value;
        }
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

/**
 * Represents the position of a double value in an ordering. Comparable
 * interface is implemented so Arrays.sort can be used to sort an array of
 * IntDoublePairs by value. Note that the implicitly defined natural ordering is
 * NOT consistent with equals.
 */
class IntDoublePair implements Comparable<IntDoublePair> {

    /**
     * Value of the pair
     */
    private final double value;

    /**
     * Original position of the pair
     */
    private final int position;

    /**
     * Construct an IntDoublePair with the given value and position.
     *
     * @param value the value of the pair
     * @param position the original position
     */
    IntDoublePair(double value, int position) {
        this.value = value;
        this.position = position;
    }

    /**
     * Compare this IntDoublePair to another pair. Only the
     * <strong>values</strong> are compared.
     *
     * @param other the other pair to compare this to
     * @return result of <code>Double.compare(value, other.value)</code>
     */
    public int compareTo(IntDoublePair other) {
        return Double.compare(value, other.value);
    }

    // N.B. equals() and hashCode() are not implemented; see MATH-610 for discussion.
    /**
     * Returns the value of the pair.
     *
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the original position of the pair.
     *
     * @return position
     */
    public int getPosition() {
        return position;
    }
}

class CorrelationPair implements Comparable<CorrelationPair> {

    private final int i, j;
    private final double val;

    public CorrelationPair(int i, int j, double val) {
        this.i = i;
        this.j = j;
        this.val = val;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }

    public double getVal() {
        return val;
    }

    @Override
    public int compareTo(CorrelationPair other) {
        return Double.compare(val, other.val);
    }

}
