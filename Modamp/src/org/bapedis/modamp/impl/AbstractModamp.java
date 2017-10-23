/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.LinkedList;
import java.util.List;
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
    boolean stopRun;
    protected final AlgorithmFactory factory;
    protected ProgressTicket progressTicket;
    protected final String PRO_CATEGORY = "Properties";
    protected final List<PeptideAttribute> descriptorList;

    public AbstractModamp(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
        descriptorList = new LinkedList<>();
    }
    
    public List<PeptideAttribute> getDescriptorList(){
        return descriptorList;
    }

    @Override
    public void initAlgo() {
        descriptorList.clear();
        attrModel = pc.getAttributesModel();
        stopRun = false;
    }

    @Override
    public void endAlgo() {
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
    public void run() {
        if (attrModel != null) {
            Peptide[] peptides = attrModel.getPeptides();
            progressTicket.switchToDeterminate(peptides.length);
            for (int i = 0; i < peptides.length && !stopRun; i++) {
                compute(peptides[i]);
                progressTicket.progress();
            }
            
            // Calculating max and min values for molecular descriptors
            double val, max, min;
            for (PeptideAttribute descriptor : descriptorList) {
                max = Double.MIN_VALUE;
                min = Double.MAX_VALUE;
                for (Peptide peptide : peptides) {
                    val = PeptideAttribute.convertToDouble(peptide.getAttributeValue(descriptor));
                    if (val < min) {
                        min = val;
                    }
                    if (val > max) {
                        max = val;
                    }
                }
                descriptor.setMaxValue(max);
                descriptor.setMinValue(min);
            }
        }
    }

    protected abstract void compute(Peptide peptide);

}
