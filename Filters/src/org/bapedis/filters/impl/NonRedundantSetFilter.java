/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.impl.SequenceClustering;
import org.bapedis.core.spi.algo.impl.SequenceClusteringFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;

/**
 *
 * @author loge
 */
public class NonRedundantSetFilter implements Filter {

    private final NonRedundantSetFilterFactory factory;
    protected final SequenceClustering clustering;

    public NonRedundantSetFilter(NonRedundantSetFilterFactory factory) {
        this.factory = factory;
        this.clustering = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
    }
    
    public SequenceAlignmentModel getAlignmentModel() {
        return clustering.getAlignmentModel();
    }

    public void setAlignmentModel(SequenceAlignmentModel model) {
        clustering.setAlignmentModel(model);
    }
    
    @Override
    public String getHTMLDisplayName() {
        return null;
    }

    @Override
    public String getDisplayName() {
        return "Non-redundant set at " + clustering.getAlignmentModel().getPercentIdentity() + "%";
    }

    @Override
    public Algorithm getPreprocessing(Peptide[] targets) {
        return null;
    }

    @Override
    public boolean accept(Peptide peptide) {
        return true;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }
    
    
}
