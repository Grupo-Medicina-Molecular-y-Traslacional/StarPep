/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;

/**
 *
 * @author beltran, loge
 */
public class InestabilityIndex extends AbstractModamp {

    protected final String II = "II";
    protected final String PS = "Stable";

    public InestabilityIndex(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initMD(List<PeptideAttribute> descriptorList) {
        if (attrModel != null) {
            PeptideAttribute descriptor;
            if (!attrModel.hasAttribute(II)) {
                descriptor = attrModel.addAttribute(II, II, Double.class);
            } else {
                descriptor = attrModel.getAttribute(II);
            }
            descriptorList.add(descriptor);
            
            if (!attrModel.hasAttribute(PS)) {
                descriptor = attrModel.addAttribute(PS, PS, Integer.class);
            } else {
                descriptor = attrModel.getAttribute(PS);
            }
            descriptorList.add(descriptor);
        }
    }

    @Override
    protected void compute(Peptide peptide) {
        double val = MD.inestabilityIndex(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(II), val);

        int isPS = MD.isProteinStable(val);
        peptide.setAttributeValue(attrModel.getAttribute(PS), isPS);
    }

    @Override
    protected void endMD() {
    }
    
    

}
