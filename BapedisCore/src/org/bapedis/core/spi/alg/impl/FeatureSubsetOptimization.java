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
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import static org.bapedis.core.spi.alg.impl.FilteringSubsetOptimization.DF;

/**
 *
 * @author Loge
 */
public class FeatureSubsetOptimization implements Algorithm, Cloneable {

    protected ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final ForkJoinPool fjPool = new ForkJoinPool();

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
    private FeatureDiscretization.BinsOption binsOption2;
    private int numberOfBins2;
    protected FeatureDiscretization preprocessing;
    protected final FeatureSubsetOptimizationFactory factory;

    public FeatureSubsetOptimization(FeatureSubsetOptimizationFactory factory) {
        this.factory = factory;
        preprocessing = (FeatureDiscretization) (new FeatureDiscretizationFactory()).createAlgorithm();
        binsOption2 = FeatureDiscretization.BinsOption.Square_root_number_peptides;
        numberOfBins2 = 50;
        direction = Direction.Backward;
        debug = false;
        parallel = true;
        stopRun = new AtomicBoolean();
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
            List<MolecularDescriptor> retainedlFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    retainedlFeatures.add(attr);
                }
            }

            if (retainedlFeatures.size() > 1) {
                MolecularDescriptor[] descriptors = retainedlFeatures.toArray(new MolecularDescriptor[0]);
                Arrays.parallelSort(descriptors, new FeatureComparator());

                //-----------Feature discretization
                String taskName = NbBundle.getMessage(FeatureSubsetOptimization.class, "FeatureSubsetOptimization.preprocessing.taskName", preprocessing.getFactory().getName());
                ticket.progress(taskName);
                ticket.switchToIndeterminate();
                pc.reportMsg(taskName, workspace);

                preprocessing.setAllFeatures(retainedlFeatures);
                preprocessing.setBinsOption(binsOption2);
                if (binsOption2 == FeatureDiscretization.BinsOption.User_Defined) {
                    preprocessing.setNumberOfBins(numberOfBins2);
                }
                preprocessing.initAlgo(workspace, ticket);
                preprocessing.run();
                preprocessing.endAlgo();

                // Running subset optimization
                taskName = NbBundle.getMessage(FeatureSubsetOptimization.class, "FeatureSubsetOptimization.preprocessing.taskName", factory.getName());
                ticket.progress(taskName);
                ticket.switchToIndeterminate();
                pc.reportMsg(taskName, workspace);
                try {
                    MIMatrixBuilder task = MIMatrixBuilder.createMatrixBuilder(attrModel.getPeptides().toArray(new Peptide[0]), descriptors, ticket, stopRun);
                    ticket.progress(taskName);
                    ticket.switchToDeterminate(task.getWorkUnits() + descriptors.length);
                    fjPool.invoke(task);
                    task.join();
                    MIMatrix miMatrix = task.getMIMatrix();

                    int removed = 0;
                    double bestMerit = Double.NaN;
                    if (!stopRun.get()) {
                        pc.reportMsg("Direction: " + direction, workspace);
                        double initialMerit = 0;
                        if (direction == Direction.Backward) {
                            BitSet candidateSet = new BitSet(descriptors.length);
                            candidateSet.set(0, descriptors.length, true);
                            initialMerit = evaluateSubset(candidateSet, descriptors, miMatrix);
                        }
                        pc.reportMsg("The initial merit is " + DF.format(initialMerit), workspace);

                        //Subset optimization
                        BitSet subset = hillClimbingSearch(descriptors, miMatrix);

                        MolecularDescriptor attr;
                        retainedlFeatures.clear();
                        for (int i = 0; i < descriptors.length; i++) {
                            attr = descriptors[i];
                            if (!subset.get(i)) {
                                removed++;
                                attrModel.deleteAttribute(attr);
                            } else {
                                retainedlFeatures.add(attr);
                            }
                        }
                        bestMerit = evaluateSubset(subset, descriptors, miMatrix);
                    }
                    //Print top 5 bottom 3
                    descriptors = retainedlFeatures.toArray(new MolecularDescriptor[0]);
                    Arrays.sort(descriptors, new FeatureComparator());
                    FilteringSubsetOptimization.printTop5Buttom3(descriptors, workspace);
                    pc.reportMsg("\nTotal of removed features: " + removed, workspace);
                    pc.reportMsg("Total of retained features: " + retainedlFeatures.size(), workspace);
                    if (bestMerit != Double.NaN) {
                        pc.reportMsg("The best merit found is " + DF.format(bestMerit), workspace);
                    }
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    private int count(boolean flag, BitSet subset) {
        int c = 0;
        for (int j = 0; j < subset.size(); j++) {
            if (subset.get(j) == flag) {
                c++;
            }
        }
        return c;
    }

    private double avgScore(BitSet subset, MolecularDescriptor[] features) {
        double sum = 0;
        int n = 0;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                sum += features[j].getScore();
                n++;
            }
        }
        return sum / n;
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
            m_best_group.set(0, features.length, true);
        } else if (direction == Direction.Forward) {
            double maxScore = features[0].getScore();
            int cursor = 0;
            for (i = 0; i < features.length; i++) {
                if (features[i].getScore() > maxScore) {
                    maxScore = features[i].getScore();
                    cursor = i;
                }
            }
            m_best_group.set(cursor);
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
        double relevance = 0, redundancy = 0;
        int n = 0;
        double entropy;
        for (int j = 0; j < features.length; j++) {
            if (subset.get(j)) {
                entropy = features[j].getBinsPartition().getEntropy();
                relevance += entropy;
                redundancy += entropy;
                n++;
                for (int k = 0; k < features.length; k++) {
                    if (j != k && subset.get(k)) {
                        redundancy += miMatrix.getValue(j, k);
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
