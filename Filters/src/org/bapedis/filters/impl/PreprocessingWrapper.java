/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.util.List;
import java.util.TreeSet;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class PreprocessingWrapper implements Algorithm {

    private Algorithm preprocessing;
    private final TreeSet<String> accepted;
    private final PreprocessingWrapperFactory factory;

    public PreprocessingWrapper(PreprocessingWrapperFactory factory) {
        accepted = new TreeSet<>();
        this.factory = factory;
    }

    public Algorithm getPreprocessing() {
        return preprocessing;
    }

    public void setPreprocessing(Algorithm preprocessing) {
        this.preprocessing = preprocessing;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
//            searchAlgo.initAlgo(workspace, progressTicket);
    }

    @Override
    public void endAlgo() {
//            searchAlgo.endAlgo();
//            List<Peptide> resultList = searchAlgo.getResultList();
//            for (Peptide p : resultList) {
//                searchResult.add(p.getId());
//            }            
    }

    @Override
    public boolean cancel() {
//            return searchAlgo.cancel();
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
//        searchAlgo.run();
    }

    public boolean contains(Peptide peptide) {
        return accepted.contains(peptide.getId());
    }

}
