/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.PkaValues;

/**
 *
 * @author beltran
 */
public class IsoelectricPoint extends AbstractModamp {

    protected final String pI = "pI";
    
    public IsoelectricPoint(IsoelectricPointFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null && !attrModel.hasAttribute(pI)){
            attrModel.addAttribute(pI, pI, Double.class);
        }
    }
    
    

    @Override
    public void compute(Peptide peptide) {
        double val = MD.isoelectricPoint(peptide.getSequence(), PkaValues.IPC_peptide());
        peptide.setAttributeValue(attrModel.getAttribute(pI), val);
    }
    
}
