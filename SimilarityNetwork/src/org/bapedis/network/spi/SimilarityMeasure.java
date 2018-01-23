/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.spi;

import org.bapedis.core.model.Peptide;

/**
 *
 * @author loge
 */
public interface SimilarityMeasure {

    float computeSimilarity(Peptide peptide1, Peptide peptide2);
    
}
