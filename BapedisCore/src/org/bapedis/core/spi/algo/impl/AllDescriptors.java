/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author beltran, loge
 */
public class AllDescriptors extends AbstractMD implements PropertyChangeListener {

    private final List<AbstractMD> algorithms;
    private final Set<String> descriptorKeys;
    private int buttonGroupIndex;
    protected final NotifyDescriptor emptyKeys;

    public AllDescriptors(AlgorithmFactory factory) {
        super(factory);
        algorithms = new LinkedList<>();
        buttonGroupIndex = 0;
        descriptorKeys = new HashSet<>();
        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(AllDescriptors.class, "AllDescriptors.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
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

    public int getButtonGroupIndex() {
        return buttonGroupIndex;
    }

    public void setButtonGroupIndex(int buttonGroupIndex) {
        this.buttonGroupIndex = buttonGroupIndex;
    }

    @Override
    protected void initMD() {
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
                algo.initAlgo();
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
    public void endMD() {
        // Init all algoritms
        for (AbstractMD algo : algorithms) {
            algo.removeMolecularDescriptorChangeListener(this);
            algo.endMD();
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(AbstractMD.MD_ADDED)
                && evt.getNewValue() != null) {
            addAttribute((PeptideAttribute) evt.getNewValue());
        }
    }

}
