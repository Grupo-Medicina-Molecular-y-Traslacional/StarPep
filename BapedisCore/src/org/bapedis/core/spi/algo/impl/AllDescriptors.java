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
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
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
    private final Set<String> descriptorKeys, allDescriptorKey;
    private int buttonGroupIndex;
    protected final NotifyDescriptor emptyKeys;

    public AllDescriptors(AlgorithmFactory factory) {
        super(factory);
        algorithms = new LinkedList<>();
        buttonGroupIndex = 0;
        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(AllDescriptors.class, "AllDescriptors.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
        allDescriptorKey = new HashSet<>();
        descriptorKeys = new HashSet<>();
        String key;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory f = it.next();
            if (!f.equals(factory) && f.getCategory() == AlgorithmCategory.MolecularDescriptor) {
                key = f.getName();
                descriptorKeys.add(key);
                allDescriptorKey.add(key);
            }
        }
    }

    public Set<String> getAllDescriptorKey() {
        return allDescriptorKey;
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

    public int getButtonGroupIndex() {
        return buttonGroupIndex;
    }

    public void setButtonGroupIndex(int buttonGroupIndex) {
        this.buttonGroupIndex = buttonGroupIndex;
    }

    @Override
    public void initAlgo(Workspace workspace) {
        super.initAlgo(workspace); 
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
                algo.initAlgo(workspace);
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

}
