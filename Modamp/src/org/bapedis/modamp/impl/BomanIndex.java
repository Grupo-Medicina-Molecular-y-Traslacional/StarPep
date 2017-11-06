/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.Peptide;
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
    protected void initMD() {
        addAttribute(BM, BM, Double.class);
    }

    @Override
    protected void compute(Peptide peptide) {
        double val = MD.boman(peptide.getSequence());
        peptide.setAttributeValue(getAttribute(BM), val);

    }

    @Override
    protected void endMD() {
    }

}
