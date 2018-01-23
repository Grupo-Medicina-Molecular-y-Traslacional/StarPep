/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.spi.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.network.spi.SimilarityMeasure;

/**
 *
 * @author loge
 */
public class TanimotoCoefficient implements SimilarityMeasure {

    @Override
    public float computeSimilarity(Peptide peptide1, Peptide peptide2) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
