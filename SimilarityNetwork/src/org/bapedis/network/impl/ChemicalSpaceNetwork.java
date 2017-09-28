/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;

/**
 *
 * @author loge
 */
public class ChemicalSpaceNetwork extends SimilarityNetworkAlgo {

    public ChemicalSpaceNetwork(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public double computeSimilarity(Peptide peptide1, Peptide peptide2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
