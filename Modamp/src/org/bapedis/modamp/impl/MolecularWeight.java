package org.bapedis.modamp.impl;

import org.bapedis.modamp.impl.AbstractModamp;
import org.bapedis.core.model.Peptide;
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
public class MolecularWeight extends AbstractModamp{

    protected final String MW="mw";
    
    public MolecularWeight(MolecularWeightFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo(); 
        if (attrModel != null && !attrModel.hasAttribute(MW)){
            attrModel.addAttribute(MW, MW, Double.class);
        }
    }
    
    

    @Override
    public void compute(Peptide peptide) {
        double val = MD.mw(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(MW), val);
    }
    
}
