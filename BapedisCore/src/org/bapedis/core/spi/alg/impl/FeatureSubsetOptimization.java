/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Bin;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import static org.bapedis.core.spi.alg.impl.FeatureSEFiltering.pc;
import org.openide.util.Exceptions;

/**
 *
 * @author Loge
 */
public class FeatureSubsetOptimization implements Algorithm, Cloneable {

    public enum Direction {
        Backward, Forward
    };

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    private List<Peptide> peptides;
    private double maxEntropy;
    protected final AtomicBoolean stopRun;
    protected ProgressTicket ticket;
    protected Direction direction;
    private boolean debug, parallel;

    protected final FeatureSubsetOptimizationFactory factory;

    public FeatureSubsetOptimization(FeatureSubsetOptimizationFactory factory) {
        this.factory = factory;
        direction = Direction.Backward;
        maxEntropy = Double.NaN;
        debug = false;
        parallel = true;
        stopRun = new AtomicBoolean();
    }

    public Direction getDirection() {
        return direction;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public double getMaxEntropy() {
        return maxEntropy;
    }

    public void setMaxEntropy(double maxEntropy) {
        this.maxEntropy = maxEntropy;
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
        List<MolecularDescriptor> allFeatures = new LinkedList<>();
        for (String key : attrModel.getMolecularDescriptorKeys()) {
            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                allFeatures.add(attr);
            }
        }

        MolecularDescriptor[] descriptors = allFeatures.toArray(new MolecularDescriptor[0]);
        try {
            BitSet subset = search(descriptors);
            MolecularDescriptor attr;
            int count = 0;
            int removed = 0;
            for (int i = 0; i < descriptors.length; i++) {
                if (!subset.get(i)) {
                    removed++;
                    attr = descriptors[i];
                    attrModel.deleteAttribute(attr);
                    if (debug) {
                        pc.reportMsg("Removed: " + attr.getDisplayName() + " - score: " + attr.getBinsPartition().getEntropy(), workspace);
                    }
                } else {
                    count++;
                }
            }

            pc.reportMsg("\nTotal of removed features: " + removed, workspace);
            pc.reportMsg("Total of remaining features: " + count, workspace);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }

    private BitSet search(MolecularDescriptor[] features) throws Exception {
        int i;
        double best_merit;
        double temp_best, temp_merit;
        int temp_index = 0;
        BitSet temp_group;

        //Initialize
        ExecutorService m_pool = null;

        if (parallel) {
            m_pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        }

        BitSet m_best_group = new BitSet(features.length);
        if (direction == Direction.Backward) {
            for (i = 0; i < features.length; i++) {
                m_best_group.set(i);
            }
        }
        best_merit = evaluateSubset(m_best_group, features);

        // main search loop
        boolean done = false;
        boolean addone;
        boolean z;

        while (!done && !stopRun.get()) {
            List<Future<Double[]>> results = new ArrayList<Future<Double[]>>();
            temp_group = (BitSet) m_best_group.clone();
            temp_best = best_merit;

            done = true;
            addone = false;

            for (i = 0; i < features.length && !stopRun.get(); i++) {
                switch (direction) {
                    case Backward:
                        z = temp_group.get(i);
                        break;
                    case Forward:
                        z = !temp_group.get(i);
                        break;
                    default:
                        throw new IllegalStateException("Unknown direction");
                }

                if (z) {
                    // set/unset the bit
                    if (direction == Direction.Backward) {
                        temp_group.clear(i);
                    } else {
                        temp_group.set(i);
                    }

                    if (parallel) {
                        //Parallel run
                        final BitSet tempCopy = (BitSet) temp_group.clone();
                        final int attBeingEvaluated = i;

                        Future<Double[]> future = m_pool.submit(new Callable<Double[]>() {
                            @Override
                            public Double[] call() throws Exception {
                                Double[] r = new Double[2];
                                if (!stopRun.get()) {
                                    double e = evaluateSubset(tempCopy, features);
                                    r[0] = new Double(attBeingEvaluated);
                                    r[1] = e;
                                }
                                return r;
                            }
                        });

                        results.add(future);
                    } else {
                        temp_merit = evaluateSubset(temp_group, features);
                        if (direction == Direction.Backward) {
                            z = (temp_merit >= temp_best);
                        } else {
                            z = (temp_merit > temp_best);
                        }

                        if (z) {
                            temp_best = temp_merit;
                            temp_index = i;
                            addone = true;
                            done = false;
                        }
                    }

                    // unset this addition/deletion
                    if (direction == Direction.Backward) {
                        temp_group.set(i);
                    } else {
                        temp_group.clear(i);
                    }
                }
            }

            if (parallel) {
                for (int j = 0; j < results.size() && !stopRun.get(); j++) {
                    Future<Double[]> f = results.get(j);

                    int index = f.get()[0].intValue();
                    temp_merit = f.get()[1].doubleValue();

                    if (direction == Direction.Backward) {
                        z = (temp_merit >= temp_best);
                    } else {
                        z = (temp_merit > temp_best);
                    }

                    if (z) {
                        temp_best = temp_merit;
                        temp_index = index;
                        addone = true;
                        done = false;
                    }
                }
            }

            if (addone) {
                if (direction == Direction.Backward) {
                    m_best_group.clear(temp_index);
                } else {
                    m_best_group.set(temp_index);
                }
                best_merit = temp_best;
                if (debug) {
                    pc.reportMsg("Best merit found so far: " + best_merit, workspace);
                }
            }
        }

        if (parallel) {
            m_pool.shutdown();
        }

        return m_best_group;
    }

    private double evaluateSubset(BitSet subset, MolecularDescriptor[] features) throws MolecularDescriptorNotFoundException {
        double term1 = 0, term2 = 0;
        int n = 0;
        //First term
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                term1 += features[j].getBinsPartition().getEntropy() / maxEntropy;
                n++;
            }
        }
        term1 = term1 / n;

        //Second term
        MolecularDescriptor fj, fk;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                fj = features[j];
                for (int k = j + 1; k < features.length; k++) {
                    if (subset.get(k)) {
                        fk = features[k];
//                        term2 += mutualInformation(fj, fk) / Math.min(fj.getBinsPartition().getEntropy(), fk.getBinsPartition().getEntropy());
                    }

                }
            }
        }

        return term1 - term2;
    }

    private double mutualInformation(MolecularDescriptor fj, MolecularDescriptor fk) throws MolecularDescriptorNotFoundException {
        double sum = 0.;

        Bin[] bj = fj.getBinsPartition().getBins();
        Bin[] bk = fk.getBinsPartition().getBins();

        //Calculating mutual information
        int numberOfInstances = peptides.size();
        double pj, pk, pjk;
        for (int i = 0; i < bj.length; i++) {
            pj = (double) bj[i].getCount() / numberOfInstances;
            for (int l = i + 1; l < bk.length; l++) {
                pk = (double) bk[l].getCount() / numberOfInstances;
                pjk = (double) countInstances(fj, i, fk, l) / numberOfInstances;
                sum += pjk * Math.log(pjk / (pj * pk));
            }
        }

        return sum;
    }

    private int countInstances(MolecularDescriptor fj, int index1, MolecularDescriptor fk, int index2) throws MolecularDescriptorNotFoundException {
        int count = 0;
        for (Peptide peptide : peptides) {
            if (index1 == FeatureDiscretization.getBinIndex(peptide, fj, fj.getBinsPartition().getBins().length)
                    && index2 == FeatureDiscretization.getBinIndex(peptide, fk, fk.getBinsPartition().getBins().length)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Jensen-Shannon divergence JS(P||Q) = (KL(P||M) + KL(Q||M)) / 2, where M =
     * (P+Q)/2. The Jensen-Shannon divergence is a popular method of measuring
     * the similarity between two probability distributions. It is also known as
     * information radius or total divergence to the average. It is based on the
     * Kullback-Leibler divergence, with the difference that it is always a
     * finite value. The square root of the Jensen-Shannon divergence is a
     * metric.
     */
    public static double JensenShannonDivergence(double[] x, double[] y) {
        double[] m = new double[x.length];
        for (int i = 0; i < m.length; i++) {
            m[i] = (x[i] + y[i]) / 2;
        }

        return (KullbackLeiblerDivergence(x, m) + KullbackLeiblerDivergence(y, m)) / 2;
    }

    /**
     * Kullback-Leibler divergence. The Kullback-Leibler divergence (also
     * information divergence, information gain, relative entropy, or KLIC) is a
     * non-symmetric measure of the difference between two probability
     * distributions P and Q. KL measures the expected number of extra bits
     * required to code samples from P when using a code based on Q, rather than
     * using a code based on P. Typically P represents the "true" distribution
     * of data, observations, or a precise calculated theoretical distribution.
     * The measure Q typically represents a theory, model, description, or
     * approximation of P.
     * <p>
     * Although it is often intuited as a distance metric, the KL divergence is
     * not a true metric - for example, the KL from P to Q is not necessarily
     * the same as the KL from Q to P.
     */
    public static double KullbackLeiblerDivergence(double[] x, double[] y) {
        boolean intersection = false;
        double kl = 0.0;

        for (int i = 0; i < x.length; i++) {
            if (x[i] != 0.0 && y[i] != 0.0) {
                intersection = true;
                kl += x[i] * Math.log(x[i] / y[i]);
            }
        }

        if (intersection) {
            return kl;
        } else {
            return Double.POSITIVE_INFINITY;
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSubsetOptimization copy = (FeatureSubsetOptimization) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
    }

}
