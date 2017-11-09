/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author beltran, loge
 */
public abstract class AbstractMD implements Algorithm {

    protected final ProjectManager pc;
    protected AttributesModel attrModel;
    protected boolean stopRun;
    protected final AlgorithmFactory factory;
    protected ProgressTicket progressTicket;
    protected final String PRO_CATEGORY = "Properties";
    private final HashMap<String, MolecularDescriptor> map;
    public static final String MD_ADDED = "md_added";
    protected final PropertyChangeSupport propertyChangeSupport;

    public AbstractMD(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
        map = new LinkedHashMap<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }        

    protected MolecularDescriptor addAttribute(String id, String displayName, Class<?> type) {
        MolecularDescriptor attr = new MolecularDescriptor(id, displayName, type, factory.getName());
        addAttribute(attr);
        return attr;
    }

    protected void addAttribute(MolecularDescriptor attr) {
        if (map.containsKey(attr.getId())) {
            throw new IllegalArgumentException("Duplicated attribute: " + attr.getId());
        }
        map.put(attr.getId(), attr);
        propertyChangeSupport.firePropertyChange(MD_ADDED, null, attr);
    }

    public MolecularDescriptor getAttribute(String id) {
        return map.get(id);
    }

    @Override
    public final void initAlgo() {
        map.clear();
        attrModel = pc.getAttributesModel();
        stopRun = false;
        initMD();
    }

    @Override
    public final void endAlgo() {
        endMD();
        attrModel = null;
        progressTicket = null;
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
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    @Override
    public final void run() {
        if (attrModel != null) {
            Peptide[] peptides = attrModel.getPeptides();
            progressTicket.switchToDeterminate(peptides.length + 1);

            try {
                // Calculate molecular descriptors
                for (int i = 0; i < peptides.length && !stopRun; i++) {
                    compute(peptides[i]);
                    progressTicket.progress();
                }
            } finally {
                //Add molecular descriptors to attributes model
                HashMap<String, List<MolecularDescriptor>> mdMap = new LinkedHashMap<>();
                String category;
                List<MolecularDescriptor> list;
                for (MolecularDescriptor attr : map.values()) {
                    category = attr.getCategory();
                    if (!mdMap.containsKey(category)) {
                        mdMap.put(category, new LinkedList<MolecularDescriptor>());
                    }
                    list = mdMap.get(category);
                    list.add(attr);
                }
                for (Map.Entry<String, List<MolecularDescriptor>> entry : mdMap.entrySet()) {
                    attrModel.addMolecularDescriptors(entry.getKey(), entry.getValue().toArray(new MolecularDescriptor[0]));
                }
                progressTicket.progress();
            }
        }
    }
    
    public void addMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(MD_ADDED, listener);
    }

    public void removeMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(MD_ADDED, listener);
    }      

    protected abstract void initMD();

    protected abstract void compute(Peptide peptide);

    protected abstract void endMD();

}
