/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.PkaValues;

/**
 *
 * @author beltran, loge
 */
public class IsoelectricPoint extends AbstractModamp {

    protected final String pI = "pI";

    public IsoelectricPoint(IsoelectricPointFactory factory) {
        super(factory);
    }

    @Override
    protected void initMD() {
        if (!hasAttribute(pI)) {
            addAttribute(pI, pI, Double.class);
        }
    }

    @Override
    protected void compute(Peptide peptide) {
        double val = MD.isoelectricPoint(peptide.getSequence(), PkaValues.IPC_peptide());
        peptide.setAttributeValue(getAttribute(pI), val);
    }

    @Override
    protected void endMD() {
    }

}
