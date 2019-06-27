package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.impl.AbstractMD;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.modamp.MD;
import org.bapedis.modamp.invariants.AggregationOperators;
import org.bapedis.modamp.invariants.GOWAWA;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author beltran, loge, Cesar
 */
public class AggregatedMolecularWeight extends AbstractMD 
{
    final private AggregationOperators operators;
    
    public AggregatedMolecularWeight( AggregatedMolecularWeightFactory factory ) 
    {
        super( factory );
        
        operators = new GOWAWA();
    }
    
    @Override
    public void initAlgo( Workspace workspace, ProgressTicket progressTicket )
    {
        super.initAlgo( workspace, progressTicket ); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    protected void compute(Peptide peptide) 
    {
        double[] lovis = MD.mwByAA( peptide.getSequence() );
        operators.applyAllOperators( lovis, "mw", peptide, this, true );
    }
}
