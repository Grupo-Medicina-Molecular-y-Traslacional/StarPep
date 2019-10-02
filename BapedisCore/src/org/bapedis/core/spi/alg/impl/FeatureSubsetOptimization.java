/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
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
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class FeatureSubsetOptimization implements Algorithm, Cloneable {

    protected ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public enum Direction {
        Backward, Forward
    };

    //To initialize
    protected Workspace workspace;
    private AttributesModel attrModel;
    protected final AtomicBoolean stopRun;
    protected ProgressTicket ticket;
    protected Direction direction;
    private boolean debug, parallel;
    protected FeatureDiscretization preprocessing;
    protected final FeatureSubsetOptimizationFactory factory;
    protected int seed;

    public FeatureSubsetOptimization(FeatureSubsetOptimizationFactory factory) {
        this.factory = factory;
        preprocessing = (FeatureDiscretization) (new FeatureDiscretizationFactory()).createAlgorithm();
        preprocessing.setBinsOption(FeatureDiscretization.BinsOption.Sturges_Rule);
        direction = Direction.Backward;
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

    public FeatureDiscretization getPreprocessingAlg() {
        return preprocessing;
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
        stopRun.set(false);
        seed = -1;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
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
        if (attrModel != null) {
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }

            if (allFeatures.size() > 1) {

//                Arrays.parallelSort(descriptors, new FeatureComparator());
                //-----------Feature discretization
                String taskName = NbBundle.getMessage(FeatureSubsetOptimization.class, "FeatureSubsetOptimization.preprocessing.taskName", preprocessing.getFactory().getName());
                ticket.progress(taskName);
                ticket.switchToIndeterminate();
                pc.reportMsg(taskName, workspace);

                preprocessing.initAlgo(workspace, ticket);
                preprocessing.run();
                preprocessing.endAlgo();

                // Running subset optimization
                taskName = NbBundle.getMessage(FeatureSubsetOptimization.class, "FeatureSubsetOptimization.preprocessing.taskName", factory.getName());
                ticket.progress(taskName);
                ticket.switchToIndeterminate();
                pc.reportMsg(taskName, workspace);
                try {

//                    BitSet subset = hillClimbingSearch(descriptors, miMatrix);
//                    
//                    MolecularDescriptor attr;
//                    int removed = 0;
//                    for (int i = 0; i < descriptors.length; i++) {
//                        attr = descriptors[i];
//                        if (!subset.get(i)) {
//                            removed++;
//                            attrModel.deleteAttribute(attr);
//                            if (debug) {
//                                pc.reportMsg("Removed: " + attr.getDisplayName() + " - score: " + attr.getBinsPartition().getEntropy(), workspace);
//                            }
//                        } else {
//                            remainingFeatures.add(attr);
//                        }
//                    }
                    //Print top 5 bottom 3
//                    descriptors = remainingFeatures.toArray(new MolecularDescriptor[0]);
//                    Arrays.sort(descriptors, new FeatureComparator());
//                    FeatureComparator.printTop5Buttom3(descriptors, workspace);
//                    pc.reportMsg("\nTotal of removed features: " + removed, workspace);
//                    pc.reportMsg("Total of remaining features: " + remainingFeatures.size(), workspace);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private BitSet hillClimbingSearch(MolecularDescriptor[] features, MIMatrix miMatrix) throws Exception {
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
        best_merit = evaluateSubset(m_best_group, features, miMatrix);

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
                                    double e = evaluateSubset(tempCopy, features, miMatrix);
                                    r[0] = new Double(attBeingEvaluated);
                                    r[1] = e;
                                }
                                return r;
                            }
                        });

                        results.add(future);
                    } else {
                        temp_merit = evaluateSubset(temp_group, features, miMatrix);
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

    private double evaluateSubset(BitSet subset, MolecularDescriptor[] features, MIMatrix miMatrix) throws MolecularDescriptorNotFoundException {
        return evaluateSubset1(subset, features, miMatrix);
    }

    private double evaluateSubset3(BitSet subset, MolecularDescriptor[] features, MIMatrix miMatrix) throws MolecularDescriptorNotFoundException {
        double relevance = 0, redundancy = 0;
        double score = 0;
        int n = 0;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                n++;
            }
        }
        double minVal;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                relevance = (seed == j) ? 1 : miMatrix.getValue(seed, j);
                for (int k = 0; k < features.length; k++) {
                    if (subset.get(k)) {
                        minVal = Math.min(features[j].getBinsPartition().getEntropy(),
                                features[k].getBinsPartition().getEntropy());
                        redundancy += (j == k) ? 1 : miMatrix.getValue(j, k) / minVal;
                    }
                }
                score += relevance - redundancy / n;
            }
        }
        return score;
    }

    private double evaluateSubset2(BitSet subset, MolecularDescriptor[] features, MIMatrix miMatrix) throws MolecularDescriptorNotFoundException {
        double score = 0;
        double entropy, maxMI;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                entropy = features[j].getBinsPartition().getEntropy();
                maxMI = 0.0;
                for (int k = 0; k < features.length; k++) {
                    if (j != k && subset.get(k)) {
                        if (miMatrix.getValue(j, k) > maxMI) {
                            maxMI = miMatrix.getValue(j, k);
                        }
                    }
                }
                // Score(fi) = Relevance(fi) - Redundancy(fi)
                score += entropy / preprocessing.getMaxEntropy() - maxMI / entropy;
            }
        }
        return score;
    }

    private double evaluateSubset1(BitSet subset, MolecularDescriptor[] features, MIMatrix miMatrix) throws MolecularDescriptorNotFoundException {
        double relevance = 0, redundancy = 0;
        int n = 0;
        double entropy;
        double minVal;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                entropy = features[j].getBinsPartition().getEntropy() / preprocessing.getMaxEntropy();
                relevance += entropy;
                redundancy += entropy;
                n++;
                for (int k = 0; k < features.length; k++) {
                    if (j != k && subset.get(k)) {
                        minVal = Math.min(features[j].getBinsPartition().getEntropy(),
                                features[k].getBinsPartition().getEntropy());
                        redundancy += miMatrix.getValue(j, k) / minVal;
                    }
                }
            }
        }
        return relevance / n - redundancy / (n * n);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSubsetOptimization copy = (FeatureSubsetOptimization) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.preprocessing = (FeatureDiscretization) this.preprocessing.clone();
        return copy;
    }

}
