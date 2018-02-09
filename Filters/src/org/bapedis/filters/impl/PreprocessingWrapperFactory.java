/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class PreprocessingWrapperFactory implements AlgorithmFactory {

    @Override
        public AlgorithmCategory getCategory() {
            return AlgorithmCategory.Sequence;
        }

        @Override
        public String getName() {
           return NbBundle.getMessage(SeqAlignmentFilter.class, "SeqAlignmentFilter.proprocessing.name");
        }

        @Override
        public String getDescription() {
            return NbBundle.getMessage(SeqAlignmentFilter.class, "SeqAlignmentFilter.proprocessing.desc");
        }

        @Override
        public AlgorithmSetupUI getSetupUI() {
            return null;
        }

        @Override
        public Algorithm createAlgorithm() {
            return new PreprocessingWrapper(this);
        }
    
}
