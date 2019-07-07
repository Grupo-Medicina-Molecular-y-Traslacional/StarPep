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
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import static org.bapedis.core.spi.alg.impl.FeatureSEFiltering.pc;

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
    protected boolean stopRun;
    protected ProgressTicket ticket;
    protected Direction direction;

    protected final FeatureSubsetOptimizationFactory factory;

    public FeatureSubsetOptimization(FeatureSubsetOptimizationFactory factory) {
        this.factory = factory;
        direction = Direction.Backward;
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
        List<MolecularDescriptor> allFeatures = new LinkedList<>();

        for (String key : attrModel.getMolecularDescriptorKeys()) {
            for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                allFeatures.add(attr);
            }
        }

    }

    private BitSet search(MolecularDescriptor[] features) throws Exception {
        int i;
        double best_merit;
        double temp_best, temp_merit;
        int temp_index = 0;
        BitSet temp_group;

        //Initialize
        ExecutorService m_pool = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        BitSet m_best_group = new BitSet(features.length);
        if (direction == Direction.Backward) {
            for (i = 0; i < features.length; i++) {
                m_best_group.set(i);
            }
        }
        best_merit = evaluateSubset(m_best_group);

        // main search loop
        boolean done = false;
        boolean addone;
        boolean z;

        while (!done && !stopRun) {
            List<Future<Double[]>> results = new ArrayList<Future<Double[]>>();
            temp_group = (BitSet) m_best_group.clone();
            temp_best = best_merit;

            done = true;
            addone = false;

            for (i = 0; i < features.length && !stopRun; i++) {
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

                    //Parallel run
                    final BitSet tempCopy = (BitSet) temp_group.clone();
                    final int attBeingEvaluated = i;

                    Future<Double[]> future = m_pool.submit(new Callable<Double[]>() {
                        @Override
                        public Double[] call() throws Exception {
                            Double[] r = new Double[2];
                            double e = evaluateSubset(tempCopy);
                            r[0] = new Double(attBeingEvaluated);
                            r[1] = e;
                            return r;
                        }
                    });

                    results.add(future);

                    // unset this addition/deletion
                    if (direction == Direction.Backward) {
                        temp_group.set(i);
                    } else {
                        temp_group.clear(i);
                    }
                }
            }

            for (int j = 0; j < results.size() && !stopRun; j++) {
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

            if (addone) {
                if (direction == Direction.Backward) {
                    m_best_group.clear(temp_index);
                } else {
                    m_best_group.set(temp_index);
                }
                best_merit = temp_best;
            }
        }

        m_pool.shutdown();
        return m_best_group;
    }

    private double evaluateSubset(BitSet subset) {
        return 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FeatureSubsetOptimization copy = (FeatureSubsetOptimization) super.clone(); //To change body of generated methods, choose Tools | Templates.
        return copy;
    }

}
