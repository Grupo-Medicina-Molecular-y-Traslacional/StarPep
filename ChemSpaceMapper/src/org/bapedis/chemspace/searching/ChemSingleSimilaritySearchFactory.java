/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */

public class ChemSingleSimilaritySearchFactory implements SimilaritySearchTag {

    private static ChemSimilaritySearchSetupUI setupUI = new ChemSimilaritySearchSetupUI();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ChemSingleSimilaritySearchAlg.class, "ChemSingleSimilaritySearch.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ChemSingleSimilaritySearchAlg.class, "ChemSingleSimilaritySearch.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new ChemSingleSimilaritySearchAlg(this);
    }
       
}
