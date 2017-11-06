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
 * @author beltran, loge
 */
public class InestabilityIndex extends AbstractModamp {

    protected final String II = "II";
    protected final String PS = "Stable";

    public InestabilityIndex(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    protected void initMD() {
        addAttribute(II, II, Double.class);
        addAttribute(PS, PS, Integer.class);
    }

    @Override
    protected void compute(Peptide peptide) {
        double val = MD.inestabilityIndex(peptide.getSequence());
        peptide.setAttributeValue(getAttribute(II), val);

        int isPS = MD.isProteinStable(val);
        peptide.setAttributeValue(getAttribute(PS), isPS);
    }

    @Override
    protected void endMD() {
    }

}
