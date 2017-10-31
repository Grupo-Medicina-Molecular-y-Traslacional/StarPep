package org.bapedis.modamp.impl;

import java.util.List;
import org.bapedis.modamp.impl.AbstractModamp;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.modamp.MD;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author beltran, loge
 */
public class MolecularWeight extends AbstractModamp {

    protected final String MW = "mw";

    public MolecularWeight(MolecularWeightFactory factory) {
        super(factory);
    }

    @Override
    public void initMD(List<PeptideAttribute> descriptorList) {
        if (attrModel != null) {
            PeptideAttribute descriptor;
            if (!attrModel.hasAttribute(MW)) {
                descriptor = attrModel.addAttribute(MW, MW, Double.class);
            } else {
                descriptor = attrModel.getAttribute(MW);
            }
            descriptorList.add(descriptor);
        }
    }

    @Override
    protected void compute(Peptide peptide) {
        double val = MD.mw(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(MW), val);
    }

    @Override
    protected void endMD() {
    }

    
}
