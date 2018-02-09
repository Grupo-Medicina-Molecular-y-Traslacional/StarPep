/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.impl.SequenceSearch;
import org.bapedis.core.spi.algo.impl.SequenceSearchFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.biojava.nbio.core.sequence.ProteinSequence;

/**
 *
 * @author loge
 */
public class SeqAlignmentFilter implements Filter {

    private final SeqAlignmentFilterFactory factory;
    private final SequenceSearch searchAlgo;
    private PreprocessingWrapper preprocessingAlgo;        

    public SeqAlignmentFilter(SeqAlignmentFilterFactory factory) {
        this.factory = factory;
        searchAlgo = (SequenceSearch) new SequenceSearchFactory().createAlgorithm();
    }

    public SequenceSearch getSearchAlgorithm() {
        return searchAlgo;
    }
    
    public int getMaximumResuls(){
        return searchAlgo.getMaximumResults();
    }
    
    public void setMaximumResuls(int maximumResults){
        searchAlgo.setMaximumResults(maximumResults);
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return searchAlgo.getAlignmentModel();
    }

    public void setAlignmentModel(SequenceAlignmentModel model) {
        searchAlgo.setAlignmentModel(model);
    }

    public ProteinSequence getQuery() {
        return searchAlgo.getQuery();
    }

    public void setQuery(ProteinSequence query) {
        searchAlgo.setQuery(query);
    }

    @Override
    public String getHTMLDisplayName() {
        return "<html><u>query</u>: " + searchAlgo.getQuery().getSequenceAsString() + "</p></html>";
    }

    @Override
    public String getDisplayName() {
        return factory.getName();
    }

    @Override
    public boolean accept(Peptide peptide) {
        if (preprocessingAlgo != null) {
            return preprocessingAlgo.contains(peptide);
        }        
        return false;
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

    @Override
    public Algorithm getPreprocessing(Peptide[] targets) {
        searchAlgo.setTargets(targets);
        preprocessingAlgo = (PreprocessingWrapper)new PreprocessingWrapperFactory().createAlgorithm();
        return preprocessingAlgo;
    }
           
}
