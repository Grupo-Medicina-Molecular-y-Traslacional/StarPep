/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.modamp.MD;

/**
 *
 * @author beltran, loge
 */
public class AliphaticIndex extends AbstractModamp {

    protected String AI = "aI";

    public AliphaticIndex(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo() {
        super.initAlgo();
        if (attrModel != null) {
            PeptideAttribute descriptor;
            if (!attrModel.hasAttribute(AI)) {
                descriptor = attrModel.addAttribute(AI, AI, Double.class);
            } else {
                descriptor = attrModel.getAttribute(AI);
            }
            descriptorList.add(descriptor);
        }
    }

    @Override
    public void compute(Peptide peptide) {
        double val = MD.aliphaticIndex(peptide.getSequence());
        peptide.setAttributeValue(attrModel.getAttribute(AI), val);
    }

}
