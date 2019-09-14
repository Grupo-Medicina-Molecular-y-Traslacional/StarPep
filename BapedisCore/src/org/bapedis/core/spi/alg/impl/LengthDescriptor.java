/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author Loge
 */
public class LengthDescriptor extends AbstractMD {
    protected final String SIZE = "size";
    protected final String LENGTH = "length";
    
    public LengthDescriptor(AlgorithmFactory factory) {
        super(factory);
    }
    
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); 
        addAttribute(SIZE, LENGTH, Integer.class);
    }    

    @Override
    protected void compute(Peptide peptide) {
        peptide.setAttributeValue(getAttribute(SIZE), peptide.getLength());
    }
    
}
