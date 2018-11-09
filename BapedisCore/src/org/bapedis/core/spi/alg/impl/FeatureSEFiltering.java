/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSEFiltering implements Algorithm, Cloneable {

    protected final ProjectManager pc;
    protected final FeatureSEFilteringFactory factory;
    private final NotifyDescriptor errorND;

    //Entropy cutoff for reference: 
    public static final int RANKING_SELECT_ALL = 0;
    public static final int RANKING_SELECT_TOP = 1;
    public static final int RANKING_ENTROPY_THRESHOLD = 2;
    public static final int RANKING_DEFAULT_OPTION = 1;
    public static final int RANKING_DEFAULT_TOP = 50;

    public static final String CORRELATION_NONE = "None";
    public static final String CORRELATION_PEARSON = "Pearson";
    public static final String CORRELATION_SPEARMAN = "Spearman";
    public static final String[] CORRELATION_METHODS = new String[]{CORRELATION_NONE, CORRELATION_PEARSON, CORRELATION_SPEARMAN};
    public static final int CORRELATION_DEFAULT_INDEX = 2;
    public static final float CORRELATION_DEFAULT_VALUE = 0.9f;

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    protected boolean stopRun;
    ProgressTicket ticket;

    public static final String RUNNING = "RUNNING";
    protected boolean running;
    protected transient final PropertyChangeSupport propertyChangeSupport;

    private int correlationIndex;
    private float correlationCutoff;
    private int rankingOption, topRank;
    private float threshold;
    private final NotifyDescriptor emptyMDs;

    private int width, height;
    private ChartPanel shannonEntropyPanel;

    public FeatureSEFiltering(FeatureSEFilteringFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;

        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.errorND"), NotifyDescriptor.ERROR_MESSAGE);

        rankingOption = RANKING_DEFAULT_OPTION;
        topRank = RANKING_DEFAULT_TOP;
        threshold = -1;

        correlationIndex = CORRELATION_DEFAULT_INDEX;
        correlationCutoff = CORRELATION_DEFAULT_VALUE;

        running = false;
        propertyChangeSupport = new PropertyChangeSupport(this);

        emptyMDs = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFiltering.class, "FeatureFiltering.emptyMDs.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public void reset() {
        rankingOption = RANKING_DEFAULT_OPTION;
        topRank = RANKING_DEFAULT_TOP;
        threshold = -1;

        correlationIndex = CORRELATION_DEFAULT_INDEX;
        correlationCutoff = CORRELATION_DEFAULT_VALUE;
    }

    private boolean isValid() {
        boolean isValid;
        switch (rankingOption) {
            case RANKING_SELECT_TOP:
                isValid = topRank > 0;
                break;
            case RANKING_ENTROPY_THRESHOLD:
                isValid = threshold > 0;
                break;
            default:
                isValid = true;

        }

        if (!CORRELATION_METHODS[correlationIndex].equals(CORRELATION_NONE)) {
            isValid = isValid && correlationCutoff >= 0 && correlationCutoff <= 1;
        }
        return isValid;
    }

    public ChartPanel getShannonEntropyPanel() {
        return shannonEntropyPanel;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getRankingOption() {
        return rankingOption;
    }

    public void setRankingOption(int rankingOption) {
        this.rankingOption = rankingOption;
    }

    public int getTopRank() {
        return topRank;
    }

    public void setTopRank(int topRank) {
        this.topRank = topRank;
    }

    public float getThreshold() {
        return threshold;
    }

    public void setThreshold(float threshold) {
        this.threshold = threshold;
    }

    public float getCorrelationCutoff() {
        return correlationCutoff;
    }

    public int getCorrelationIndex() {
        return correlationIndex;
    }

    public void setCorrelationIndex(int correlationIndex) {
        this.correlationIndex = correlationIndex;
    }

    public void setCorrelationCutoff(float correlationCutoff) {
        this.correlationCutoff = correlationCutoff;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running;
        this.running = running;
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
        peptides = attrModel.getPeptides();
        stopRun = false;
        shannonEntropyPanel = null;
        setRunning(true);
    }

    @Override
    public void endAlgo() {
        setRunning(false);
        workspace = null;
        attrModel = null;
        peptides = null;
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
        if (!isValid()) {
            DialogDisplayer.getDefault().notify(errorND);
            return;
        }

        try {
            //----------Load all descriptors
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

            //----------Computing shannon entropy                        
            String state1 = NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.task.ranking");
            pc.reportMsg(state1 + "\n", workspace);
            ticket.progress(state1);
            int workUnits = allFeatures.size();
            ticket.switchToDeterminate(workUnits);

            Bin[] bins = new Bin[peptides.size()];
            double maxScore = Math.log(peptides.size());
            double score;

            pc.reportMsg("Entropy max score: " + maxScore, workspace);

            for (MolecularDescriptor descriptor : allFeatures) {
                if (!stopRun) {
                    descriptor.resetSummaryStats(peptides);
                    fillBins(descriptor, peptides, bins);
                    score = calculateEntropy(bins);
                    descriptor.setScore(score);
                    ticket.progress();
                }
            }

            //----------Ranking all features
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
            allFeatures = null;

            //-----------Filtering features...                        
            double[][] descriptorMatrix = new double[rankedFeatures.length][];
            int count = 0;
            int removed = 0;
            workUnits += rankedFeatures.length;
            ticket.switchToDeterminate(workUnits);

//          workUnits += (rankedFeatures.length * (rankedFeatures.length - 1)) / 2;
//          ticket.switchToDeterminate(workUnits);
            String state2 = NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.task.filtering");
            ticket.progress(state2);
            pc.reportMsg("\n", workspace);
            pc.reportMsg(state2 + "\n", workspace);

            pc.reportMsg("Correlation method: " + CORRELATION_METHODS[correlationIndex], workspace);
            pc.reportMsg("Correlation cutoff value: " + correlationCutoff + "\n", workspace);

            switch (rankingOption) {
                case RANKING_SELECT_ALL:
                    pc.reportMsg("Ranking output: select all \n", workspace);
                    for (int i = 0; i < rankedFeatures.length && !stopRun; i++) {
                        if (rankedFeatures[i] != null) {
                            count++;
                            if (!CORRELATION_METHODS[correlationIndex].equals(CORRELATION_NONE)) {
                                removed += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                            }
                        }
                        ticket.progress();
                    }
                    break;
                case RANKING_SELECT_TOP:
                    pc.reportMsg("Ranking output: select top " + topRank + "\n", workspace);
                    for (int i = 0; i < rankedFeatures.length && !stopRun; i++) {
                        if (rankedFeatures[i] != null) {
                            if (count < topRank) {
                                count++;
                                if (!CORRELATION_METHODS[correlationIndex].equals(CORRELATION_NONE)) {
                                    removed += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                                }
                            } else {
                                removed++;
                                attrModel.deleteAttribute(rankedFeatures[i]);
                                pc.reportMsg("Removed: " + rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getScore(), workspace);
                                rankedFeatures[i] = null;
                            }
                        }
                        ticket.progress();
                    }
                    break;
                case RANKING_ENTROPY_THRESHOLD:
                    pc.reportMsg("Ranking output: shannon entropy >= " + threshold + "\n", workspace);
                    for (int i = 0; i < rankedFeatures.length && !stopRun; i++) {
                        if (rankedFeatures[i] != null) {
                            if (rankedFeatures[i].getScore() < threshold) {
                                removed++;
                                attrModel.deleteAttribute(rankedFeatures[i]);
                                pc.reportMsg("Removed: " + rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getScore(), workspace);
                                rankedFeatures[i] = null;
                            }
                        }
                    }
                    for (int i = 0; i < rankedFeatures.length && !stopRun; i++) {
                        if (rankedFeatures[i] != null) {
                            count++;
                            if (!CORRELATION_METHODS[correlationIndex].equals(CORRELATION_NONE)) {
                                removed += removeCorrelated(descriptorMatrix, i, rankedFeatures);
                            }
                        }
                        ticket.progress();
                    }
                    break;
            }

            //Print top 5 bottom 3
            pc.reportMsg("Top 5", workspace);
            int top5 = 0;
            for (int i = 0; i < rankedFeatures.length && top5 < 5; i++) {
                if (rankedFeatures[i] != null) {
                    pc.reportMsg(rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getScore(), workspace);
                    top5++;
                }
            }
            pc.reportMsg("...", workspace);
            pc.reportMsg("Bottom 3", workspace);
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
                pc.reportMsg(descriptor.getDisplayName() + " - score: " + descriptor.getScore(), workspace);
            }

            //Create chart panel
            shannonEntropyPanel = createChartPanel(rankedFeatures);

            pc.reportMsg("\nTotal of removed features: " + removed, workspace);
            pc.reportMsg("\nTotal of remaining features: " + count, workspace);
        } catch (MolecularDescriptorNotFoundException ex) {
            NotifyDescriptor errorND = ex.getErrorND();
            DialogDisplayer.getDefault().notify(errorND);
            pc.reportError(ex.getMessage(), workspace);
        }
    }

    private int removeCorrelated(double[][] descriptorMatrix, int beginIndex, MolecularDescriptor[] rankedFeatures) throws MolecularDescriptorNotFoundException {
        if (descriptorMatrix[beginIndex] == null) {
            descriptorMatrix[beginIndex] = computeColumn(beginIndex, rankedFeatures);
        }
        double[] column1 = descriptorMatrix[beginIndex];
        double[] column2;
        double score;
        int removed = 0;
        for (int j = beginIndex + 1; j < rankedFeatures.length && !stopRun; j++) {
            if (rankedFeatures[j] != null) {
                if (descriptorMatrix[j] == null) {
                    descriptorMatrix[j] = computeColumn(j, rankedFeatures);
                }
                column2 = descriptorMatrix[j];
                score = calculatePearsonCorrelation(column1, column2);
                if (Math.abs(score) >= correlationCutoff) {
                    attrModel.deleteAttribute(rankedFeatures[j]);
                    pc.reportMsg("Removed: " + rankedFeatures[j].getDisplayName() + " - " + String.format("corr(%s) = %f", rankedFeatures[beginIndex].getDisplayName(), score), workspace);
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
        if (!CORRELATION_METHODS[correlationIndex].equals(CORRELATION_NONE)) {
            int pos = 0;
            if (CORRELATION_METHODS[correlationIndex].equals(CORRELATION_PEARSON)) {
                column = new double[peptides.size()];
                for (Peptide peptide : peptides) {
                    column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[index]);
                }
            } else if (CORRELATION_METHODS[correlationIndex].equals(CORRELATION_SPEARMAN)) {
                column = new double[peptides.size()]; // Temporal column
                for (Peptide peptide : peptides) {
                    column[pos++] = MolecularDescriptor.getDoubleValue(peptide, rankedFeatures[index]);
                }
                column = rank(column);
            }
        }
        return column;
    }

    private double[] getData(MolecularDescriptor[] rankedFeatures) {
        int count = 0;
        for (int i = 0; i < rankedFeatures.length; i++) {
            if (rankedFeatures[i] != null) {
                count++;
            }
        }

        double[] data = new double[count];
        count = 0;
        for (int i = 0; i < rankedFeatures.length; i++) {
            if (rankedFeatures[i] != null) {
                data[count++] = rankedFeatures[i].getScore();
            }
        }
        return data;
    }

    private ChartPanel createChartPanel(MolecularDescriptor[] rankedFeatures) {
        double[] data = getData(rankedFeatures);
        double min = MolecularDescriptor.min(data);
        double max = MolecularDescriptor.max(data);

        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries serie = new XYSeries("SE");

        int y;
        for (double x = min; x <= max; x += 0.1) {
            y = 0;
            for (int i = 0; i < data.length; i++) {
                if (data[i] >= x) {
                    y++;
                }
            }
            serie.add(x, y);
        }
        dataset.addSeries(serie);

        JFreeChart chart = ChartFactory.createXYLineChart(
                "", // chart title
                "SE value", // domain axis label
                "No. of variables", // range axis label
                dataset, // data
                PlotOrientation.HORIZONTAL.VERTICAL, // orientation
                false, // include legend
                false, // tooltips?
                false // URLs?
        );

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(width, height));
        chartPanel.setMinimumSize(new Dimension(width, height));

        return chartPanel;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSEFiltering copy = (FeatureSEFiltering) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
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
