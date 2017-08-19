/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public class BomanIndex extends AbstractModamp{

    protected final String BM = "Boman";
    
    public BomanIndex(BomanIndexFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo(); 
        if (attrModel != null && !attrModel.hasAttribute(BM))
            attrModel.addAttribute(BM, BM, Double.class);
    }
    
    

    @Override
    public void compute(Peptide peptide) {
        double val = MD.boman(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(BM), val);

    }
    
}
