/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;

/**
 *
 * @author beltran
 */
public class AliphaticIndex  extends AbstractModamp{
    protected String AI= "aI";
    
    public AliphaticIndex(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null && !attrModel.hasAttribute(AI)){
            attrModel.addAttribute(AI, AI, Double.class);
        }
    }
    
    

    @Override
    public void compute(Peptide peptide) {
        double val = MD.aliphaticIndex(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(AI), val);
    }
    
}
