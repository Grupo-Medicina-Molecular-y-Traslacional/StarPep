/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.TreeSet;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.impl.SequenceSearch;
import org.bapedis.core.spi.alg.impl.SequenceSearchFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.biojava.nbio.core.sequence.ProteinSequence;

/**
 *
 * @author loge
 */
public class SeqAlignmentFilter implements Filter, ActionListener {

    private final SeqAlignmentFilterFactory factory;
    private final SequenceSearch searchAlgo;
    private final TreeSet<Integer> accepted;

    public SeqAlignmentFilter(SeqAlignmentFilterFactory factory) {
        this.factory = factory;
        searchAlgo = (SequenceSearch) new SequenceSearchFactory().createAlgorithm();
        accepted = new TreeSet<>();
    }

    public SequenceSearch getSearchAlgorithm() {
        return searchAlgo;
    }

    public int getMaximumResuls() {
        return searchAlgo.getMaximumResults();
    }

    public void setMaximumResuls(int maximumResults) {
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
        return accepted.contains(peptide.getId());
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

    @Override
    public Algorithm getPreprocessing(Peptide[] targets) {
        searchAlgo.setTargets(targets);
        PreprocessingWrapper preprocessingAlgo = (PreprocessingWrapper) new PreprocessingWrapperFactory().createAlgorithm();
        preprocessingAlgo.setAlgorithm(searchAlgo);
        preprocessingAlgo.setActionListener(this);
        return preprocessingAlgo;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        accepted.clear();
        List<Peptide> resultList = searchAlgo.getResultList();
        for (Peptide p : resultList) {
            accepted.add(p.getId());
        }
    }

}
