/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.util.ArrayList;
import java.util.concurrent.ForkJoinPool;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class NetworkSimilarityBuilder implements Algorithm {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected final ProjectManager pc;
    protected final AlgorithmFactory factory;
    protected AttributesModel attrModel;
    protected ProgressTicket progressTicket;    

    public NetworkSimilarityBuilder(AlgorithmFactory factory) {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        this.factory = factory;
    }

    @Override
    public void initAlgo() {
        attrModel = pc.getAttributesModel();
        PairwiseSimMatrixBuilder.setStopRun(false);
    }

    @Override
    public void endAlgo() {
        attrModel = null;
    }

    @Override
    public boolean cancel() {
        PairwiseSimMatrixBuilder.setStopRun(true);
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
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    @Override
    public void run() {
        Peptide[] peptides = attrModel.getPeptides();
        // Workunits for pairwise sim matrix builder
        int workunits = peptides.length * (peptides.length - 1) / 2;
        progressTicket.switchToDeterminate(workunits);
        PairwiseSimMatrix idMatrix = new PairwiseSimMatrix(peptides);

    }

}
