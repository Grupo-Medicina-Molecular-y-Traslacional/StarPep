/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;

/**
 *
 * @author beltran, loge
 */
public class AliphaticIndex extends AbstractMD {

    protected String AI = "aI";

    public AliphaticIndex(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); 
         addAttribute(AI, AI, Double.class);
    }
    

    @Override
    protected void compute(Peptide peptide) {
        double val = MD.aliphaticIndex(peptide.getSequence());
        peptide.setAttributeValue(getAttribute(AI), val);
    }


}
