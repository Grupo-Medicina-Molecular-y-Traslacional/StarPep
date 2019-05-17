/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.FeatureExtractionTag;

/**
 *
 * @author beltran, loge
 */
public class AllDescriptors extends AbstractMD implements PropertyChangeListener, Cloneable {

    private Set<String> descriptorKeys;
    private final List<AbstractMD> algorithms;    
    protected final NotifyDescriptor emptyKeys;

    public AllDescriptors(AlgorithmFactory factory) {
        super(factory);
        algorithms = new LinkedList<>();
        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(AllDescriptors.class, "AllDescriptors.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
        descriptorKeys = new LinkedHashSet<>();
        String key;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory f = it.next();
            if (!f.equals(factory) && f instanceof FeatureExtractionTag) {
                key = f.getName();
                descriptorKeys.add(key);
            }
        }
    }

    public List<AbstractMD> getAlgorithms() {
        return algorithms;
    }        

    public Set<String> getDescriptorKeys() {
        return descriptorKeys;
    }

    public void includeAlgorithm(String algoName) {
        descriptorKeys.add(algoName);
    }

    public void excludeAlgorithm(String algoName) {
        descriptorKeys.remove(algoName);
    }

    public boolean isIncluded(String algoName) {
        return descriptorKeys.contains(algoName);
    }

    public void excludeAll() {
        descriptorKeys.clear();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        algorithms.clear();
        if (descriptorKeys.isEmpty()) {
            DialogDisplayer.getDefault().notify(emptyKeys);
            cancel();
        } else {
            // fill hashmap
            Workspace currentWs = pc.getCurrentWorkspace();
            for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
                final AlgorithmFactory f = it.next();
                if (this.factory != f && descriptorKeys.contains(f.getName())) {
                    Algorithm algorithm = f.createAlgorithm();
                    if (algorithm instanceof AbstractMD) {
                        AbstractMD algoModamp = (AbstractMD) algorithm;
                        // check for an existing algorithm 
                        Collection<? extends Algorithm> savedAlgo = currentWs.getLookup().lookupAll(Algorithm.class);
                        for (Algorithm algo : savedAlgo) {
                            if (algo instanceof AbstractMD && algo.getFactory() == f) {
                                algoModamp = (AbstractMD) algo;
                                break;
                            }
                        }
                        algorithms.add(algoModamp);
                    }
                }
            }

            // Init all algoritms
            for (AbstractMD algo : algorithms) {
                algo.addMolecularDescriptorChangeListener(this);
                algo.initAlgo(workspace, null);
            }
        }
    }

    @Override
    protected void compute(Peptide peptide) {
        for (AbstractMD algo : algorithms) {
            algo.compute(peptide);
        }
    }

    @Override
    public void endAlgo() {
        super.endAlgo();
        // End all algoritms
        for (AbstractMD algo : algorithms) {
            algo.removeMolecularDescriptorChangeListener(this);
            algo.endAlgo();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AbstractMD.MD_ADDED)
                && evt.getNewValue() != null) {
            addAttribute((MolecularDescriptor) evt.getNewValue());
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AllDescriptors copy = (AllDescriptors) super.clone();
        copy.descriptorKeys = (Set) ((LinkedHashSet) descriptorKeys).clone();
        return copy;
    }

}
