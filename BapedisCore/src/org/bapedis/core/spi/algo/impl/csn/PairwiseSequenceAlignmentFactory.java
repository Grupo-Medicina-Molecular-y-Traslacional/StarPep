/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class PairwiseSequenceAlignmentFactory implements AlgorithmFactory {

    private final PairwiseSequenceAlignmentPanel panel = new PairwiseSequenceAlignmentPanel();
    
    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.NetworkSimilarity;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(PairwiseSequenceAlignment.class, "PairwiseSequenceAlignment.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(PairwiseSequenceAlignment.class, "PairwiseSequenceAlignment.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return panel;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new PairwiseSequenceAlignment(this);
    }

    @Override
    public int getQualityRank() {
        return -1;
    }

    @Override
    public int getSpeedRank() {
        return -1;
    }
    
}
