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
 * @author loge
 */
public class InestabilityIndex extends AbstractModamp {

    protected final String II = "II";
    protected final String PS = "Stable";

    public InestabilityIndex(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null && !attrModel.hasAttribute(II)) {
            attrModel.addAttribute(II, II, Double.class);
        }
        if (attrModel != null && !attrModel.hasAttribute(PS)) {
            attrModel.addAttribute(PS, PS, Integer.class);
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val = MD.inestabilityIndex(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(II), val);
        
        int isPS = MD.isProteinStable(val);
        peptide.setAttributeValue(attrModel.getAttribute(PS), isPS);
    }

}
