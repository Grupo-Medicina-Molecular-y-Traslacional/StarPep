/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlgFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author Loge
 */
public class EmbeddingInputSeqAlg implements Algorithm, Cloneable {

    private final AlgorithmFactory factory;
    private NonRedundantSetAlg nonRedundantAlg;
    private RemovingRedundantOption nrdOption;
    private boolean stopRun;

    public EmbeddingInputSeqAlg(AlgorithmFactory factory) {
        nonRedundantAlg = (NonRedundantSetAlg) new NonRedundantSetAlgFactory().createAlgorithm();
        this.factory = factory;
        nrdOption = RemovingRedundantOption.NO;
    }

    public NonRedundantSetAlg getNonRedundantAlg() {
        return nonRedundantAlg;
    }        

    public RemovingRedundantOption getNrdOption() {
        return nrdOption;
    }

    public void setNrdOption(RemovingRedundantOption nrdOption) {
        this.nrdOption = nrdOption;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        
    }

    @Override
    public void endAlgo() {
        
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        EmbeddingInputSeqAlg copy = (EmbeddingInputSeqAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.
        copy.nonRedundantAlg = (NonRedundantSetAlg) nonRedundantAlg.clone();
        return copy;
    }

}
