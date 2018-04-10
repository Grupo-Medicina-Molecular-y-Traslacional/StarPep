/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.scales.PkaValues;

/**
 *
 * @author beltran, loge
 */
public class IsoelectricPoint extends AbstractMD {

    protected final String pI = "pI";

    public IsoelectricPoint(IsoelectricPointFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); //To change body of generated methods, choose Tools | Templates.
        addAttribute(pI, pI, Double.class);
    }
    
    @Override
    protected void compute(Peptide peptide) {
        double val = MD.isoelectricPoint(peptide.getSequence(), PkaValues.IPC_peptide());
        peptide.setAttributeValue(getAttribute(pI), val);
    }

}
