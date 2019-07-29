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
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }

            Peptide[] peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            MolecularDescriptor[] descriptors = allFeatures.toArray(new MolecularDescriptor[0]);

            try {
                MIMatrixBuilder task = createMatrixBuilder(peptides, descriptors);
                fjPool.invoke(task);
                task.join();
                MIMatrix miMatrix = task.getMIMatrix();

                BitSet subset = hillClimbingSearch(descriptors, miMatrix);
                List<MolecularDescriptor> remainingFeatures = new LinkedList<>();
                MolecularDescriptor attr;
                int removed = 0;
                for (int i = 0; i < descriptors.length; i++) {
                    attr = descriptors[i];
                    if (!subset.get(i)) {
                        removed++;
                        attrModel.deleteAttribute(attr);
                        if (debug) {
                            pc.reportMsg("Removed: " + attr.getDisplayName() + " - score: " + attr.getBinsPartition().getEntropy(), workspace);
                        }
                    } else {
                        remainingFeatures.add(attr);
                    }
                }

                //Print top 5 bottom 3
                descriptors = remainingFeatures.toArray(new MolecularDescriptor[0]);
                Arrays.sort(descriptors, new FeatureComparator());
                FeatureComparator.printTop5Buttom3(descriptors, workspace);

                pc.reportMsg("\nTotal of removed features: " + removed, workspace);
                pc.reportMsg("Total of remaining features: " + remainingFeatures.size(), workspace);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
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
    
     private double evaluateSubset(BitSet subset, MolecularDescriptor[] features, MIMatrix miMatrix) throws MolecularDescriptorNotFoundException{
         return evaluateSubset1(subset, features, miMatrix);
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
                        if (miMatrix.getValue(j, k) > maxMI){
                            maxMI = miMatrix.getValue(j, k);
                        }
                    }
                }
                // Score(fi) = Relevance(fi) - Redundancy(fi)
                score += entropy/maxEntropy - maxMI/entropy;
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
                entropy = features[j].getBinsPartition().getEntropy() / maxEntropy;
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

    private MIMatrixBuilder createMatrixBuilder(Peptide[] peptides, MolecularDescriptor[] features) throws MolecularDescriptorNotFoundException {
        int[][] binIndex = new int[peptides.length][features.length];
        for (int i = 0; i < peptides.length; i++) {
            for (int j = 0; j < features.length; j++) {
                binIndex[i][j] = FeatureDiscretization.getBinIndex(peptides[i], features[j], features[j].getBinsPartition().getBins().length);
            }
        }
        return new MIMatrixBuilder(features, binIndex, ticket, stopRun);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSubsetOptimization copy = (FeatureSubsetOptimization) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
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
