/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author beltran, loge
 */
public abstract class AbstractModamp implements Algorithm {

    protected final ProjectManager pc;
    protected AttributesModel attrModel;
    protected boolean stopRun;
    protected final AlgorithmFactory factory;
    protected ProgressTicket progressTicket;
    protected final String PRO_CATEGORY = "Properties";
    private final HashMap<String, PeptideAttribute> map;

    public AbstractModamp(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
        map = new LinkedHashMap<>();
    }
    
    protected PeptideAttribute addAttribute(String id, String displayName, Class<?> cclass) {
        PeptideAttribute attr = new PeptideAttribute(id, displayName, cclass);
        addAttribute(attr);
        return attr;
    }

    protected void addAttribute(PeptideAttribute attr) {
        if (hasAttribute(attr.getId())) {
            throw new IllegalArgumentException("Duplicated attribute: " + attr.getId());
        }
        map.put(attr.getId(), attr);
    }    
    
    public PeptideAttribute getAttribute(String id) {
        if (!hasAttribute(id)) {
            throw new IllegalArgumentException("Attribute doesn't exist: " + id);
        }
        return map.get(id);
    }
    
    public boolean hasAttribute(String id) {
        return map.containsKey(id);
    }       

    public Collection<PeptideAttribute> getMolecularDescriptors() {
        return map.values();
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
            progressTicket.switchToDeterminate(peptides.length);
            for (int i = 0; i < peptides.length && !stopRun; i++) {
                compute(peptides[i]);
                progressTicket.progress();
            }

            // Calculating max and min values for molecular descriptors
            // Usefull to normalize values
            double val, max, min;
            for (PeptideAttribute descriptor : map.values()) {
                max = Double.MIN_VALUE;
                min = Double.MAX_VALUE;
                for (Peptide peptide : peptides) {
                    if (peptide.getAttributeValue(descriptor) != null) {
                        val = PeptideAttribute.convertToDouble(peptide.getAttributeValue(descriptor));
                        if (val < min) {
                            min = val;
                        }
                        if (val > max) {
                            max = val;
                        }
                    }
                }
                descriptor.setMaxValue(max);
                descriptor.setMinValue(min);
            }
            
        }
    }

    protected abstract void initMD();

    protected abstract void compute(Peptide peptide);

    protected abstract void endMD();

}
