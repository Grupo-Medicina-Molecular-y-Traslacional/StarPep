/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.MIMatrix;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.FeatureComparator;
import org.bapedis.core.util.MIMatrixBuilder;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import static org.bapedis.core.spi.alg.impl.FilteringSubsetOptimization.DF;

/**
 *
 * @author loge
 */
public class FeatureSEFiltering implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final FeatureSEFilteringFactory factory;

    public static final int MIN_THRESHOLD_PERCENT = 1;
    public static final int MAX_THRESHOLD_PERCENT = 50;

    public static final int REDUNDANCY_NONE = 0;
    public static final int REDUNDANCY_PEARSON = 1;
    public static final int REDUNDANCY_SPEARMAN = 2;

    public static final int SELECT_ALL = 0;
    public static final int SELECT_TOP = 1;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    protected final AtomicBoolean stopRun;
    protected ProgressTicket ticket;
    private List<Peptide> peptides;

    private FeatureDiscretization.BinsOption binsOption1, binsOption2;
    int numberOfBins1, numberOfBins2;
    private int redundancyOption;
    private double redundancyCutoff;
    private int thresholdPercent;
    private int selectionOption;
    private int topRank;
    protected FeatureDiscretization preprocessing;
    private boolean meritOption;
    private boolean filterByMI;

    public FeatureSEFiltering(FeatureSEFilteringFactory factory) {
        this.factory = factory;
        preprocessing = (FeatureDiscretization) (new FeatureDiscretizationFactory()).createAlgorithm();
        thresholdPercent = 10;
        redundancyOption = REDUNDANCY_SPEARMAN;
        redundancyCutoff = 0.8;
        selectionOption = SELECT_ALL;
        topRank = 40;
        
        stopRun = new AtomicBoolean();
        binsOption1 = FeatureDiscretization.BinsOption.Number_peptides;
        numberOfBins1 = 50;
        binsOption2 = FeatureDiscretization.BinsOption.Square_root_number_peptides;
        numberOfBins2 = 50;
        filterByMI = true;
    }

    public boolean isValid() {
        boolean isValid = thresholdPercent >= MIN_THRESHOLD_PERCENT && thresholdPercent <= MAX_THRESHOLD_PERCENT;
        if (redundancyOption != REDUNDANCY_NONE) {
            isValid = isValid && (redundancyCutoff >= 0 && redundancyCutoff <= 1);
        }
        isValid = isValid && (selectionOption == SELECT_ALL || (selectionOption == SELECT_TOP && topRank > 0));
        return isValid;
    }

    public int getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(int thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    public int getRedundancyOption() {
        return redundancyOption;
    }

    public void setRedundancyOption(int redundancyOption) {
        this.redundancyOption = redundancyOption;
    }

    private String getRedundancyMethod() {
        switch (redundancyOption) {
            case REDUNDANCY_NONE:
                return "None";
            case REDUNDANCY_PEARSON:
                return "Pearson correlation coefficient";
            case REDUNDANCY_SPEARMAN:
                return "Spearman correlation coefficient";
        }
        throw new IllegalStateException("Unknown correlation option: " + redundancyOption);
    }

    public double getRedundancyCutoff() {
        return redundancyCutoff;
    }

    public void setRedundancyCutoff(double redundancyCutoff) {
        this.redundancyCutoff = redundancyCutoff;
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

    public FeatureDiscretization.BinsOption getBinsOption1() {
        return binsOption1;
    }

    public void setBinsOption1(FeatureDiscretization.BinsOption binsOption1) {
        this.binsOption1 = binsOption1;
    }

    public int getNumberOfBins1() {
        return numberOfBins1;
    }

    public void setNumberOfBins1(int numberOfBins1) {
        this.numberOfBins1 = numberOfBins1;
    }

    public FeatureDiscretization.BinsOption getBinsOption2() {
        return binsOption2;
    }

    public void setBinsOption2(FeatureDiscretization.BinsOption binsOption2) {
        this.binsOption2 = binsOption2;
    }

    public int getNumberOfBins2() {
        return numberOfBins2;
    }

    public void setNumberOfBins2(int numberOfBins2) {
        this.numberOfBins2 = numberOfBins2;
    }

    public boolean isMerit() {
        return meritOption;
    }

    public void setMerit(boolean merit) {
        this.meritOption = merit;
    }

    public boolean isFilterByMI() {
        return filterByMI;
    }

    public void setFilterByMI(boolean filterByMI) {
        this.filterByMI = filterByMI;
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
        preprocessing.cancel();
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
        try {
            //----------All descriptors 
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    if (!stopRun.get()) {
                        allFeatures.add(attr);
                    }
                }
            }

            if (!stopRun.get() && !allFeatures.isEmpty()) {
                //-----------Feature discretization
                preprocessing.setBinsOption(binsOption1);
                if (binsOption1 == FeatureDiscretization.BinsOption.User_Defined) {
                    preprocessing.setNumberOfBins(numberOfBins1);
                }
                preprocessing.setAllFeatures(allFeatures);
                executePreprocessing();

                double maxEntropy = preprocessing.getMaxEntropy();
                pc.reportMsg("Maximum entropy: " + DF.format(maxEntropy), workspace);

                //-----------Removing useless features
                String taskName = "Removing useless features";
                ticket.progress(taskName);
                pc.reportMsg(taskName, workspace);

                List<MolecularDescriptor> retainedFeatures = new LinkedList<>();

                double threshold = ((double) thresholdPercent / 100.0) * maxEntropy;
                pc.reportMsg("Entropy cutoff value: " + DF.format(threshold), workspace);
                double score;
                int useless = 0;
                for (MolecularDescriptor attr : allFeatures) {
                    score = attr.getBinsPartition().getEntropy();
                    if (score < threshold) {
                        useless++;
                        attrModel.deleteAttribute(attr);
                    } else {
                        attr.setScore(score);
                        retainedFeatures.add(attr);
                    }
                }

                //----------Ranking all retained features
                pc.reportMsg("Ranking molecular features", workspace);
                MolecularDescriptor[] rankedFeatures = retainedFeatures.toArray(new MolecularDescriptor[0]);
                Arrays.parallelSort(rankedFeatures, new FeatureComparator());
                pc.reportMsg("Done", workspace);

                //-----------Removing redundant features
                taskName = "Removing redundant features";
                pc.reportMsg(taskName, workspace);
                ticket.progress(taskName);
                double[][] descriptorMatrix = new double[rankedFeatures.length][];

                pc.reportMsg("Redundancy criteria: " + getRedundancyMethod(), workspace);
                pc.reportMsg("Cutoff value: " + redundancyCutoff, workspace);

                int redundant = 0;

                //Filtering by correlation
                if (redundancyOption != REDUNDANCY_NONE) {
                    if (redundancyOption == REDUNDANCY_PEARSON
                            || redundancyOption == REDUNDANCY_SPEARMAN) {
                        ticket.switchToDeterminate(rankedFeatures.length);
                        retainedFeatures.clear();

                        if (selectionOption == SELECT_TOP) {
                            pc.reportMsg("Ranking output: select top " + topRank, workspace);
                            for (int i = 0; i < rankedFeatures.length && !stopRun.get(); i++) {
                                if (rankedFeatures[i] != null) {
                                    if (retainedFeatures.size() < topRank) {
                                        redundant += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                                        retainedFeatures.add(rankedFeatures[i]);
                                    } else {
                                        attrModel.deleteAttribute(rankedFeatures[i]);
                                        rankedFeatures[i] = null;
                                    }
                                }
                                ticket.progress();
                            }
                        } else {
                            pc.reportMsg("Ranking output: select all filtered features ", workspace);
                            for (int i = 0; i < rankedFeatures.length && !stopRun.get(); i++) {
                                if (rankedFeatures[i] != null) {
                                    redundant += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                                    retainedFeatures.add(rankedFeatures[i]);
                                }
                                ticket.progress();
                            }
                        }
                    }
                }

                double meritValue = Double.NaN;
                if (meritOption) {
                    taskName = "Merit calculation...";
                    pc.reportMsg(taskName, workspace);
                    ticket.progress(taskName);
                    //-----------Feature discretization
                    preprocessing.setBinsOption(binsOption2);
                    if (binsOption2 == FeatureDiscretization.BinsOption.User_Defined) {
                        preprocessing.setNumberOfBins(numberOfBins2);
                    }
                    preprocessing.setAllFeatures(retainedFeatures);
                    executePreprocessing();

                    rankedFeatures = retainedFeatures.toArray(new MolecularDescriptor[0]);
                    MIMatrixBuilder task = MIMatrixBuilder.createMatrixBuilder(peptides.toArray(new Peptide[0]), rankedFeatures, ticket, stopRun);
                    ticket.progress(taskName);
                    ticket.switchToDeterminate(task.getWorkUnits() + rankedFeatures.length);
                    fjPool.invoke(task);
                    task.join();
                    MIMatrix miMatrix = task.getMIMatrix();

                    double relevance = 0, redundancy = 0;
                    int n = 0;
                    double entropy;
                    for (int j = 0; j < rankedFeatures.length && !stopRun.get(); j++) {
                        entropy = rankedFeatures[j].getBinsPartition().getEntropy();
                        relevance += entropy;
                        redundancy += entropy;
                        n++;
                        for (int k = 0; k < rankedFeatures.length; k++) {
                            if (j != k) {
                                redundancy += miMatrix.getValue(j, k);
                            }
                        }
                        ticket.progress();
                    }
                    meritValue = relevance / n - redundancy / (n * n);
                }

                //Print top 5 bottom 3
                FilteringSubsetOptimization.printTop5Buttom3(rankedFeatures, workspace);

                pc.reportMsg("Useless features: " + useless, workspace);
                pc.reportMsg("Redundant features: " + redundant, workspace);

                pc.reportMsg("\nTotal of removed features: " + (allFeatures.size() - retainedFeatures.size()), workspace);
                pc.reportMsg("Total of retained features: " + retainedFeatures.size(), workspace);
                if (meritOption) {
                    pc.reportMsg("Merit of retained features: " + DF.format(meritValue), workspace);
                }
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

    private int removeCorrelated(double[][] descriptorMatrix, int beginIndex, MolecularDescriptor[] rankedFeatures) throws MolecularDescriptorNotFoundException {
        if (descriptorMatrix[beginIndex] == null) {
            descriptorMatrix[beginIndex] = computeColumn(beginIndex, rankedFeatures);
        }
        double[] column1 = descriptorMatrix[beginIndex];
        double[] column2;
        double score;
        int redundant = 0;
        for (int j = beginIndex + 1; j < rankedFeatures.length && !stopRun.get(); j++) {
            if (rankedFeatures[j] != null) {
                if (descriptorMatrix[j] == null) {
                    descriptorMatrix[j] = computeColumn(j, rankedFeatures);
                }
                column2 = descriptorMatrix[j];
                score = calculatePearsonCorrelation(column1, column2);
                if (Math.abs(score) >= redundancyCutoff) {
                    attrModel.deleteAttribute(rankedFeatures[j]);
                    redundant++;
                    rankedFeatures[j] = null;
                    descriptorMatrix[j] = null;
                }
            }
        }
        return redundant;
    }

    private double[] computeColumn(int index, MolecularDescriptor[] rankedFeatures) throws MolecularDescriptorNotFoundException {
        double[] column = null;
        if (redundancyOption != REDUNDANCY_NONE) {
            int pos = 0;
            if (redundancyOption == REDUNDANCY_PEARSON) {
                column = new double[peptides.size()];
                for (Peptide peptide : peptides) {
                    column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[index]);
                }
            } else if (redundancyOption == REDUNDANCY_SPEARMAN) {
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
