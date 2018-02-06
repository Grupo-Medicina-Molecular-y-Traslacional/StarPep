package org.bapedis.modamp.impl;

import org.bapedis.core.spi.algo.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author beltran, loge
 */
public class MolecularWeight extends AbstractMD {

    protected final String MW = "mw";

    public MolecularWeight(MolecularWeightFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); //To change body of generated methods, choose Tools | Templates.
        addAttribute(MW, MW, Double.class);
    }
    
    @Override
    protected void compute(Peptide peptide) {
        double val = MD.mw(peptide.getSequence());
        peptide.setAttributeValue(getAttribute(MW), val);
    }

}
