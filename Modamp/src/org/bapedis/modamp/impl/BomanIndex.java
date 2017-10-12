/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.modamp.MD;

/**
 *
 * @author beltran, loge
 */
public class BomanIndex extends AbstractModamp {

    protected final String BM = "Boman";

    public BomanIndex(BomanIndexFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        PeptideAttribute descriptor;
        if (attrModel != null && !attrModel.hasAttribute(BM)) {
            descriptor = attrModel.addAttribute(BM, BM, Double.class);
        } else {
            descriptor = attrModel.getAttribute(BM);
        }
        descriptorList.add(descriptor);
    }

    @Override
    public void compute(Peptide peptide) {
        double val = MD.boman(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(BM), val);

    }

}
