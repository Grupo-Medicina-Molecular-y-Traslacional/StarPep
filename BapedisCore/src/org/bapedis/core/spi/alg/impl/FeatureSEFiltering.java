/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.ForkJoinPool;
import static java.util.concurrent.ForkJoinTask.invokeAll;
import java.util.concurrent.RecursiveAction;
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
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSEFiltering implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final FeatureSEFilteringFactory factory;
    private final NotifyDescriptor errorND;

    public static final int MIN_THRESHOLD_PERCENT = 1;
    public static final int MAX_THRESHOLD_PERCENT = 50;

    public static final int CORRELATION_NONE = 0;
    public static final int CORRELATION_PEARSON = 1;
    public static final int CORRELATION_SPEARMAN = 2;

    public static final int SELECT_ALL = 0;
    public static final int SELECT_TOP = 1;
    public static final int SELECT_BY_MI = 2;

    public enum MIThresholdOption {
        Left, Mean, Right
    };

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    protected final AtomicBoolean stopRun;
    protected ProgressTicket ticket;

    private FeatureDiscretization.BinsOption binsOption1, binsOption2;
    int numberOfBins1, numberOfBins2;
    private int correlationOption;
    private double correlationCutoff;
    private int thresholdPercent;
    private int selectionOption;
    private int topRank;
    private boolean debug;
    protected FeatureDiscretization preprocessing;
    private MIThresholdOption miThresholdOption;
    private final NotifyDescriptor emptyMDs;

    public FeatureSEFiltering(FeatureSEFilteringFactory factory) {
        this.factory = factory;
        preprocessing = (FeatureDiscretization) (new FeatureDiscretizationFactory()).createAlgorithm();
        thresholdPercent = 10;
        correlationOption = CORRELATION_SPEARMAN;
        correlationCutoff = 0.8;
        selectionOption = SELECT_ALL;
        topRank = 50;
        miThresholdOption = MIThresholdOption.Mean;
        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.errorND"), NotifyDescriptor.ERROR_MESSAGE);
        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.emptyMDs.info"), NotifyDescriptor.ERROR_MESSAGE);
        debug = true;
        stopRun = new AtomicBoolean();
        binsOption1 = FeatureDiscretization.BinsOption.Number_peptides;
        binsOption2 = FeatureDiscretization.BinsOption.Sturges_Rule;
        numberOfBins1 = 50;
        numberOfBins2 = 16;
    }

    private boolean isValid() {
        boolean isValid = thresholdPercent >= MIN_THRESHOLD_PERCENT && thresholdPercent <= MAX_THRESHOLD_PERCENT;
        if (correlationOption != CORRELATION_NONE) {
            isValid = isValid && (correlationCutoff >= 0 && correlationCutoff <= 1);
        }
        isValid = isValid && (selectionOption == SELECT_ALL || selectionOption == SELECT_TOP || selectionOption == SELECT_BY_MI);
        return isValid;
    }

    public int getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(int thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    public int getCorrelationOption() {
        return correlationOption;
    }

    public void setCorrelationOption(int correlationOption) {
        this.correlationOption = correlationOption;
    }

    private String getCorrelationMethod() {
        switch (correlationOption) {
            case CORRELATION_NONE:
                return "None";
            case CORRELATION_PEARSON:
                return "Pearson";
            case CORRELATION_SPEARMAN:
                return "Spearman";
        }
        throw new IllegalStateException("Unknown correlation option: " + correlationOption);
    }

    public double getCorrelationCutoff() {
        return correlationCutoff;
    }

    public void setCorrelationCutoff(double correlationCutoff) {
        this.correlationCutoff = correlationCutoff;
    }

    public int getSelectionOption() {
        return selectionOption;
    }

    public void setSelectionOption(int selectionOption) {
        this.selectionOption = selectionOption;
    }

    public int getTopRank() {
        return topRank;
    }

    public void setTopRank(int topRank) {
        this.topRank = topRank;
    }

    public MIThresholdOption getMiThresholdOption() {
        return miThresholdOption;
    }

    public void setMiThresholdOption(MIThresholdOption miThresholdOption) {
        this.miThresholdOption = miThresholdOption;
    }

    public FeatureDiscretization.BinsOption getBinsOption1() {
        return binsOption1;
    }

    public void setBinsOption1(FeatureDiscretization.BinsOption binsOption1) {
        this.binsOption1 = binsOption1;
    }

    public FeatureDiscretization.BinsOption getBinsOption2() {
        return binsOption2;
    }

    public void setBinsOption2(FeatureDiscretization.BinsOption binsOption2) {
        this.binsOption2 = binsOption2;
    }

    public int getNumberOfBins1() {
        return numberOfBins1;
    }

    public void setNumberOfBins1(int numberOfBins1) {
        this.numberOfBins1 = numberOfBins1;
    }

    public int getNumberOfBins2() {
        return numberOfBins2;
    }

    public void setNumberOfBins2(int numberOfBins2) {
        this.numberOfBins2 = numberOfBins2;
    }

    public boolean isDebug() {
        return debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
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
        //Set null to bins partition
        for (String key : attrModel.getMolecularDescriptorKeys()) {
            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                attr.setBinsPartition(null);
            }
        }
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
        if (!isValid()) {
            DialogDisplayer.getDefault().notify(errorND);
            return;
        }

        try {
            //----------Preprocessing all descriptors 
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    if (!stopRun.get()) {
                        allFeatures.add(attr);
                        attr.resetSummaryStats(attrModel.getPeptides());
                    }
                }
            }

            if (allFeatures.isEmpty()) {
                DialogDisplayer.getDefault().notify(emptyMDs);
                pc.reportError("There is not calculated molecular descriptors", workspace);
                stopRun.set(true);
            }

            if (!stopRun.get()) {
                //-----------Feature discretization
                preprocessing.setBinsOption(binsOption1);
                if (binsOption1 == FeatureDiscretization.BinsOption.User_Defined) {
                    preprocessing.setNumberOfBins(numberOfBins1);
                }
                preprocessing.setAllFeatures(allFeatures);
                executePreprocessing();

                double maxEntropy = preprocessing.getMaxEntropy();
                pc.reportMsg("Maximum entropy: " + maxEntropy, workspace);

                //-----------Removing useless features
                String taskName = "Removing useless features";
                ticket.progress(taskName);
                pc.reportMsg(taskName, workspace);

                List<MolecularDescriptor> features = new LinkedList<>();
                List<MolecularDescriptor> toRemove = new LinkedList<>();

                double threshold = ((double) thresholdPercent / 100.0) * maxEntropy;
                pc.reportMsg("Entropy cutoff value: " + threshold, workspace);
                double score;
                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        score = attr.getBinsPartition().getEntropy();
                        if (score < threshold) {
                            toRemove.add(attr);
                        } else {
                            attr.setScore(score);
                            features.add(attr);
                        }
                    }
                }

                // Removing Useless attribute
                for (MolecularDescriptor attr : toRemove) {
                    attrModel.deleteAttribute(attr);
                    if (debug) {
                        pc.reportMsg("Removed: " + attr.getDisplayName() + " - score: " + attr.getBinsPartition().getEntropy(), workspace);
                    }
                }
                int removed = toRemove.size();
                pc.reportMsg("Useless features removed: " + removed, workspace);
                toRemove = null;

                //----------Ranking all features
                pc.reportMsg("Ranking molecular features", workspace);
                MolecularDescriptor[] rankedFeatures = features.toArray(new MolecularDescriptor[0]);
                Arrays.parallelSort(rankedFeatures, new FeatureComparator());
                features = null;
                pc.reportMsg("Done", workspace);

                //-----------Removing redundant features
                taskName = "Removing redundant features";
                ticket.progress(taskName);
                pc.reportMsg(taskName, workspace);
                double[][] descriptorMatrix = new double[rankedFeatures.length][];
                int count = 0;
                int workUnits = rankedFeatures.length;
                ticket.switchToDeterminate(workUnits);

                pc.reportMsg("Correlation method: " + getCorrelationMethod(), workspace);
                pc.reportMsg("Correlation cutoff value: " + correlationCutoff, workspace);

                if (selectionOption == SELECT_TOP) {
                    pc.reportMsg("Ranking output: select top " + topRank, workspace);
                    for (int i = 0; i < rankedFeatures.length && !stopRun.get(); i++) {
                        if (rankedFeatures[i] != null) {
                            if (count < topRank) {
                                count++;
                                if (correlationOption != CORRELATION_NONE) {
                                    removed += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                                }
                            } else {
                                removed++;
                                attrModel.deleteAttribute(rankedFeatures[i]);
                                if (debug) {
                                    pc.reportMsg("Removed: " + rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getBinsPartition().getEntropy(), workspace);
                                }
                                rankedFeatures[i] = null;
                            }
                        }
                        ticket.progress();
                    }
                } else {
                    for (int i = 0; i < rankedFeatures.length && !stopRun.get(); i++) {
                        if (rankedFeatures[i] != null) {
                            count++;
                            if (correlationOption != CORRELATION_NONE) {
                                removed += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                            }
                        }
                        ticket.progress();
                    }

                    if (selectionOption == SELECT_BY_MI) {
                        pc.reportMsg("Ranking output: select by mutual information ", workspace);
                        allFeatures.clear();
                        count = 0;
                        for (int i = 0; i < rankedFeatures.length; i++) {
                            if (rankedFeatures[i] != null) {
                                allFeatures.add(rankedFeatures[i]);
                            }
                        }

                        if (allFeatures.size() > 1) {
                            //-----------Feature discretization
                            preprocessing.setBinsOption(binsOption2);
                            if (binsOption2 == FeatureDiscretization.BinsOption.User_Defined) {
                                preprocessing.setNumberOfBins(numberOfBins2);
                            }
                            preprocessing.setAllFeatures(allFeatures);
                            executePreprocessing();

                            MolecularDescriptor[] descriptors = allFeatures.toArray(new MolecularDescriptor[0]);
                            Peptide[] peptides = attrModel.getPeptides().toArray(new Peptide[0]);
                            MIMatrixBuilder task = createMatrixBuilder(peptides, descriptors);
                            fjPool.invoke(task);
                            task.join();
                            MIMatrix miMatrix = task.getMIMatrix();

                            double[] data = miMatrix.getValues();
                            double avg = MolecularDescriptor.mean(data);
                            double std = Math.sqrt(MolecularDescriptor.varp(data, avg));

                            pc.reportMsg("Avg: " + avg, workspace);
                            pc.reportMsg("Std: " + std, workspace);

                            switch (miThresholdOption) {
                                case Left:
                                    threshold = avg - std;
                                    break;
                                case Mean:
                                    threshold = avg;
                                    break;
                                case Right:
                                    threshold = avg + std;
                            }

                            BitSet subset = new BitSet(descriptors.length);
                            for (int j = 0; j < descriptors.length; j++){
                                subset.set(j);
                            }

                            for (int j = 0; j < descriptors.length; j++) {
                                if (subset.get(j)) {
                                    count++;
                                    for (int k = j + 1; k < descriptors.length; k++) {
                                        if (subset.get(k)) {
                                            if (miMatrix.getValue(j, k) < threshold) {
                                                removed++;
                                                attrModel.deleteAttribute(descriptors[k]);
                                                subset.flip(k);
                                                if (debug) {
                                                    pc.reportMsg("Removed: " + descriptors[k].getDisplayName() + " - score: " + descriptors[k].getBinsPartition().getEntropy(), workspace);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            rankedFeatures = descriptors;

                            pc.reportMsg("Computing merit....", workspace);
                            preprocessing.setBinsOption(FeatureDiscretization.BinsOption.One_third_number_peptides);
                            executePreprocessing();
                            
                            task = createMatrixBuilder(peptides, descriptors);
                            fjPool.invoke(task);
                            task.join();
                            miMatrix = task.getMIMatrix();                            
                            
                            //Included descriptors
                            pc.reportMsg("Included descriptors", workspace);
                            pc.reportMsg("Count: " + count(true, subset), workspace);
                            pc.reportMsg("Score (avg): " + avgScore(true, subset, descriptors), workspace);
                            pc.reportMsg("Merit: " + merit(true, subset, descriptors, miMatrix), workspace);

                            //Excluded descriptors
                            pc.reportMsg("Excluded descriptors", workspace);
                            pc.reportMsg("Count: " + count(false, subset), workspace);
                            pc.reportMsg("Score (avg): " + avgScore(false, subset, descriptors), workspace);
                            pc.reportMsg("Merit: " + merit(false, subset, descriptors, miMatrix), workspace);
                        }
                    }
                }

                //Print top 5 bottom 3
                FeatureComparator.printTop5Buttom3(rankedFeatures, workspace);

                pc.reportMsg("\nTotal of removed features: " + removed, workspace);
                pc.reportMsg("Total of remaining features: " + count, workspace);
            }
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
            pc.reportError(ex.getMessage(), workspace);
        }
    }

    protected void executePreprocessing() {
        String taskName = NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.preprocessing.taskName", preprocessing.getFactory().getName());
        ticket.progress(taskName);
        ticket.switchToIndeterminate();
        pc.reportMsg(taskName, workspace);

        preprocessing.initAlgo(workspace, ticket);
        preprocessing.run();
        preprocessing.endAlgo();
    }
    
    private int count(boolean flag, BitSet subset){
        int c = 0;
        for (int j = 0; j < subset.size(); j++) {
            if (subset.get(j) == flag){
                c++;
            }
        }
        return c;
    }

    private double merit(boolean flag, BitSet subset, MolecularDescriptor[] features, MIMatrix miMatrix) throws MolecularDescriptorNotFoundException {
        double relevance = 0, redundancy = 0;
        int n = 0;
        double entropy;
        double minVal;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j) == flag) {
                entropy = features[j].getBinsPartition().getEntropy() / preprocessing.getMaxEntropy();
                relevance += entropy;
                redundancy += entropy;
                n++;
                for (int k = 0; k < features.length; k++) {
                    if (j != k && subset.get(k) == flag) {
                        minVal = Math.min(features[j].getBinsPartition().getEntropy(),
                                features[k].getBinsPartition().getEntropy());
                        redundancy += miMatrix.getValue(j, k) / minVal;
                    }
                }
            }
        }
        return relevance / n - redundancy / (n * n);
    }

    private double avgScore(boolean flag, BitSet subset, MolecularDescriptor[] features) {
        double sum = 0;
        int n = 0;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j) == flag) {
                sum += features[j].getScore();
                n++;
            }
        }
        return sum / n;
    }

    private MIMatrixBuilder createMatrixBuilder(Peptide[] peptides, MolecularDescriptor[] features) throws MolecularDescriptorNotFoundException {
        int[][] binIndex = new int[peptides.length][features.length];
        for (int i = 0; i < peptides.length; i++) {
            for (int j = 0; j < features.length; j++) {
                binIndex[i][j] = FeatureDiscretization.getBinIndex(peptides[i], features[j], features[j].getBinsPartition().getBins().length);
            }
        }
        return new MIMatrixBuilder(features, binIndex, ticket, stopRun);
    }

    private int removeCorrelated(double[][] descriptorMatrix, int beginIndex, MolecularDescriptor[] rankedFeatures) throws MolecularDescriptorNotFoundException {
        if (descriptorMatrix[beginIndex] == null) {
            descriptorMatrix[beginIndex] = computeColumn(beginIndex, rankedFeatures);
        }
        double[] column1 = descriptorMatrix[beginIndex];
        double[] column2;
        double score;
        int removed = 0;
        for (int j = beginIndex + 1; j < rankedFeatures.length && !stopRun.get(); j++) {
            if (rankedFeatures[j] != null) {
                if (descriptorMatrix[j] == null) {
                    descriptorMatrix[j] = computeColumn(j, rankedFeatures);
                }
                column2 = descriptorMatrix[j];
                score = calculatePearsonCorrelation(column1, column2);
                if (Math.abs(score) >= correlationCutoff) {
                    attrModel.deleteAttribute(rankedFeatures[j]);
                    if (debug) {
                        pc.reportMsg("Removed: " + rankedFeatures[j].getDisplayName() + " - " + String.format("corr(%s) = %f", rankedFeatures[beginIndex].getDisplayName(), score), workspace);
                    }
                    removed++;
                    rankedFeatures[j] = null;
                    descriptorMatrix[j] = null;
                }
            }
        }
        return removed;
    }

    private double[] computeColumn(int index, MolecularDescriptor[] rankedFeatures) throws MolecularDescriptorNotFoundException {
        double[] column = null;
        if (correlationOption != CORRELATION_NONE) {
            int pos = 0;
            if (correlationOption == CORRELATION_PEARSON) {
                column = new double[peptides.size()];
                for (Peptide peptide : peptides) {
                    column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[index]);
                }
            } else if (correlationOption == CORRELATION_SPEARMAN) {
                column = new double[peptides.size()]; // Temporal column
                for (Peptide peptide : peptides) {
                    column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[index]);
                }
                column = rank(column);
            }
        }
        return column;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSEFiltering copy = (FeatureSEFiltering) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.preprocessing = (FeatureDiscretization) this.preprocessing.clone();
        return copy;
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

class CorrelationPair implements Comparable<CorrelationPair> {

    private final int i, j;
    private final double val;

    CorrelationPair(int i, int j, double val) {
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

class FeatureComparator implements Comparator<MolecularDescriptor> {

    @Override
    public int compare(MolecularDescriptor o1, MolecularDescriptor o2) {
        double entropy1 = o1.getScore();
        double entropy2 = o2.getScore();
        if (entropy1 > entropy2) {
            return -1;
        }
        if (entropy1 < entropy2) {
            return 1;
        }
        return 0;
    }

    public static void printTop5Buttom3(MolecularDescriptor[] rankedFeatures, Workspace workspace) {
        //Print top 5 bottom 3
        FeatureSEFiltering.pc.reportMsg("Top 5", workspace);
        int top5 = 0;
        for (int i = 0; i < rankedFeatures.length && top5 < 5; i++) {
            if (rankedFeatures[i] != null) {
                FeatureSEFiltering.pc.reportMsg(rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getScore(), workspace);
                top5++;
            }
        }
        FeatureSEFiltering.pc.reportMsg("...", workspace);
        FeatureSEFiltering.pc.reportMsg("Bottom 3", workspace);
        Stack<MolecularDescriptor> stack = new Stack<>();
        int bottom3 = 0;
        for (int i = rankedFeatures.length - 1; i >= 0 && bottom3 < 3; i--) {
            if (rankedFeatures[i] != null) {
                stack.push(rankedFeatures[i]);
                bottom3++;
            }
        }
        MolecularDescriptor descriptor;
        while (!stack.isEmpty()) {
            descriptor = stack.pop();
            FeatureSEFiltering.pc.reportMsg(descriptor.getDisplayName() + " - score: " + descriptor.getScore(), workspace);
        }
    }
}

class MIMatrix {

    protected double[] data;

    public MIMatrix(int size) {
        data = new double[size * (size - 1) / 2];
    }

    public void setValue(int j, int k, double value) {
        assert j != k;
        data[pos(j, k)] = value;
    }

    public double getValue(int j, int k) {
        assert j != k;
        return data[pos(j, k)];
    }

    public double[] getValues() {
        return data;
    }

    public int getSize() {
        return data.length;
    }

    private int pos(int j, int k) {
        int a = j > k ? j : k;
        int b = j < k ? j : k;
        return a * (a - 1) / 2 + b;
    }

}

class MIMatrixBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private int[][] binIndex;
    private MolecularDescriptor[] features;
    private ProgressTicket progressTicket;
    private AtomicBoolean stopRun;

    private final MIMatrix matrix;
    private int xlow, xhigh, ylow, yhigh;

    MIMatrixBuilder(MolecularDescriptor[] features, int[][] binIndex, ProgressTicket progressTicket, AtomicBoolean stopRun) {
        this(features, binIndex, new MIMatrix(features.length),
                0, features.length, 0, features.length,
                progressTicket, stopRun);
    }

    private MIMatrixBuilder(MolecularDescriptor[] features, int[][] binIndex, MIMatrix matrix,
            int xlow, int xhigh, int ylow, int yhigh,
            ProgressTicket progressTicket, AtomicBoolean stopRun) {
        this.binIndex = binIndex;
        this.features = features;
        this.matrix = matrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
        this.progressTicket = progressTicket;
        this.stopRun = stopRun;
    }

    void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    public MIMatrix getMIMatrix() {
        return matrix;
    }

    public int getWorkUnits() {
        return matrix.getSize();
    }

    @Override
    protected void compute() {
        if (xlow >= yhigh || stopRun.get()) {
            return; // Discard the elements above the diagonal
        }
        if (xhigh - xlow <= SEQUENTIAL_THRESHOLD) {
            if (yhigh - ylow <= SEQUENTIAL_THRESHOLD) {
                if (!stopRun.get()) {
                    computeDirectly();
                }
            } else if (!stopRun.get()) {
                int middle = ylow + (yhigh - ylow) / 2;
                // up and down
                MIMatrixBuilder up = new MIMatrixBuilder(features, binIndex, matrix, xlow, xhigh, ylow, middle, progressTicket, stopRun);
                MIMatrixBuilder down = new MIMatrixBuilder(features, binIndex, matrix, xlow, xhigh, middle, yhigh, progressTicket, stopRun);
                invokeAll(up, down);
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            MIMatrixBuilder left = new MIMatrixBuilder(features, binIndex, matrix, xlow, middle, ylow, yhigh, progressTicket, stopRun);
            MIMatrixBuilder right = new MIMatrixBuilder(features, binIndex, matrix, middle, xhigh, ylow, yhigh, progressTicket, stopRun);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() {
        double mi;
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    try {
                        mi = mutualInformation(y, x);
                        matrix.setValue(y, x, mi);
//                        progressTicket.progress();
                    } catch (Exception ex) {
                        stopRun.set(true);
                        throw new RuntimeException(ex);
                    }
                }
            }
        }
    }

    private double mutualInformation(int j, int k) throws MolecularDescriptorNotFoundException {
        double sum = 0.;

        Bin[] bj = features[j].getBinsPartition().getBins();
        Bin[] bk = features[k].getBinsPartition().getBins();

        //Calculating mutual information
        int numberOfInstances = binIndex.length;
        double pj, pk, pjk;
        for (int i = 0; i < bj.length; i++) {
            pj = (double) bj[i].getCount() / numberOfInstances;
            for (int l = 0; l < bk.length; l++) {
                pk = (double) bk[l].getCount() / numberOfInstances;
                pjk = (double) countInstances(binIndex, j, i, k, l) / numberOfInstances;
                if (pj > 0 && pk > 0 && pjk > 0) {
                    sum += pjk * Math.log(pjk / (pj * pk));
                }
            }
        }

        return sum;
    }

    private int countInstances(int[][] binIndex, int j, int i, int k, int l) throws MolecularDescriptorNotFoundException {
        int count = 0;
        for (int p = 0; p < binIndex.length; p++) {
            if (binIndex[p][j] == i && binIndex[p][k] == l) {
                count++;
            }
        }
        return count;
    }

}
