/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author Home
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
        double val = MD.isoelectricPoint(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(pI), val);
    }
    
}
