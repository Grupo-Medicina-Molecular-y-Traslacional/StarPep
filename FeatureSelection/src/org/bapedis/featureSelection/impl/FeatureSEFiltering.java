/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.impl;

import java.util.Arrays;
import java.util.Comparator;
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
import org.bapedis.core.model.Bin;
import org.bapedis.featureSelection.util.CorrelationUtility;
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
    protected ProgressTicket ticket;

    private int correlationIndex;
    private float correlationCutoff;
    private int rankingOption, topRank;
    private float threshold;
    private final NotifyDescriptor emptyMDs;

    public FeatureSEFiltering(FeatureSEFilteringFactory factory) {
        
        this.factory = factory;

        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.errorND"), NotifyDescriptor.ERROR_MESSAGE);

        rankingOption = RANKING_DEFAULT_OPTION;
        topRank = RANKING_DEFAULT_TOP;
        threshold = -1;

        correlationIndex = CORRELATION_DEFAULT_INDEX;
        correlationCutoff = CORRELATION_DEFAULT_VALUE;

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

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        peptides = attrModel.getPeptides();
        stopRun = false;
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


            double maxScore = Math.log(peptides.size());
            double score;

            pc.reportMsg("Entropy max score: " + maxScore, workspace);

            for (MolecularDescriptor descriptor : allFeatures) {
                if (!stopRun) {
                    descriptor.resetSummaryStats(peptides);
//                    fillBins(descriptor, peptides, bins);
//                    score = calculateEntropy(bins);
//                    descriptor.setEntropy(score);
                    ticket.progress();
                }
            }

            //----------Ranking all features
            MolecularDescriptor[] rankedFeatures = allFeatures.toArray(new MolecularDescriptor[0]);
            Arrays.parallelSort(rankedFeatures, new Comparator<MolecularDescriptor>() {
                @Override
                public int compare(MolecularDescriptor o1, MolecularDescriptor o2) {
                    if (o1.getEntropy()> o2.getEntropy()) {
                        return -1;
                    }
                    if (o1.getEntropy() < o2.getEntropy()) {
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
                                pc.reportMsg("Removed: " + rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getEntropy(), workspace);
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
                            if (rankedFeatures[i].getEntropy() < threshold) {
                                removed++;
                                attrModel.deleteAttribute(rankedFeatures[i]);
                                pc.reportMsg("Removed: " + rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getEntropy(), workspace);
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
                    pc.reportMsg(rankedFeatures[i].getDisplayName() + " - score: " + rankedFeatures[i].getEntropy(), workspace);
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
                pc.reportMsg(descriptor.getDisplayName() + " - score: " + descriptor.getEntropy(), workspace);
            }

            pc.reportMsg("\nTotal of removed features: " + removed, workspace);
            pc.reportMsg("\nTotal of remaining features: " + count, workspace);
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
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
                score = CorrelationUtility.calculatePearsonCorrelation(column1, column2);
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
                column = CorrelationUtility.rank(column);
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
                data[count++] = rankedFeatures[i].getEntropy();
            }
        }
        return data;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSEFiltering copy = (FeatureSEFiltering) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
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
