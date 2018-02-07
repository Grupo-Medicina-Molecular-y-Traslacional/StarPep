/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.util.List;
import java.util.TreeSet;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.impl.SequenceSearch;
import org.bapedis.core.spi.algo.impl.SequenceSearchFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.task.AlgorithmErrorHandler;
import org.bapedis.core.task.AlgorithmExecutor;
import org.bapedis.core.task.AlgorithmListener;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class SeqAlignmentFilter implements Filter {

    private final ProjectManager pc;
    private final SeqAlignmentFilterFactory factory;
    private final SequenceSearch searchAlgo;
    protected final AlgorithmExecutor executor;
    protected TreeSet<String> searchResult;

    public SeqAlignmentFilter(SeqAlignmentFilterFactory factory) {
        this.factory = factory;
        searchAlgo = (SequenceSearch) new SequenceSearchFactory().createAlgorithm();
        executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public SequenceSearch getSearchAlgorithm() {
        return searchAlgo;
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

    public void resetSearch() {
        searchResult = null;
    }

    @Override
    public boolean accept(Peptide peptide) {
        if (searchResult == null) {
            runSearchAlgorithm();
            //wait...
            try {
                synchronized (this) {
                    this.wait();
                }
            } catch (InterruptedException ex) {
            }

            searchResult = new TreeSet<>();
            List<Peptide> resultList = searchAlgo.getResultList();
            for (Peptide p : resultList) {
                searchResult.add(p.getId());
            }
        }
        
        return searchResult.contains(peptide.getId());
    }

    @Override
    public FilterFactory getFactory() {
        return factory;
    }

    private void runSearchAlgorithm() {        
        new Thread(new Runnable() {
            @Override
            public void run() {
                executor.execute(searchAlgo, new AlgorithmListener() {
                    @Override
                    public void algorithmFinished(Algorithm algo) {
                        synchronized (SeqAlignmentFilter.this) {
                            SeqAlignmentFilter.this.notify();
                        }
                    }
                }, new AlgorithmErrorHandler() {
                    @Override
                    public void fatalError(Throwable t) {
                        synchronized (SeqAlignmentFilter.this) {
                            SeqAlignmentFilter.this.notify();
                        }
                        Exceptions.printStackTrace(t);
                    }
                });
            }
        }).start();
    }

    @Override
    public Algorithm getPreprocessing(Peptide[] targets) {
        searchAlgo.setTargets(targets);
        return searchAlgo;
    }

}
