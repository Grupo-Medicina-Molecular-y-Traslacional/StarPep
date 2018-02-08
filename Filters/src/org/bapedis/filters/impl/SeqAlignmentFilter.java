/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.filters.impl;

import java.util.List;
import java.util.TreeSet;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.core.spi.algo.impl.SequenceSearch;
import org.bapedis.core.spi.algo.impl.SequenceSearchFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class SeqAlignmentFilter implements Filter {

    private final ProjectManager pc;
    private final SeqAlignmentFilterFactory factory;
    private final SequenceSearch searchAlgo;
    private MyPreprocessingAlgo preprocessingAlgo;        

    public SeqAlignmentFilter(SeqAlignmentFilterFactory factory) {
        this.factory = factory;
        searchAlgo = (SequenceSearch) new SequenceSearchFactory().createAlgorithm();
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
        preprocessingAlgo = (MyPreprocessingAlgo)new MyPreprocessingAlgoFactory().createAlgorithm();
        return preprocessingAlgo;
    }

    private class MyPreprocessingAlgo implements Algorithm{
        private final TreeSet<String> searchResult;
        private final AlgorithmFactory factory;

        public MyPreprocessingAlgo(AlgorithmFactory factory) {
            searchResult = new TreeSet<>();
            this.factory = factory;
        }        
        
        @Override
        public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
            searchAlgo.initAlgo(workspace, progressTicket);
        }

        @Override
        public void endAlgo() {
            searchAlgo.endAlgo();
            List<Peptide> resultList = searchAlgo.getResultList();
            for (Peptide p : resultList) {
                searchResult.add(p.getId());
            }            
        }

        @Override
        public boolean cancel() {
            return searchAlgo.cancel();            
        }

        @Override
        public AlgorithmProperty[] getProperties() {
            return null;
        }

        @Override
        public AlgorithmFactory getFactory() {
            return factory;
        }

        @Override
        public void run() {
            searchAlgo.run();
        }
        
        public boolean contains(Peptide peptide){
            return searchResult.contains(peptide.getId());
        }
    
    }
    
    private class MyPreprocessingAlgoFactory implements AlgorithmFactory{
        AlgorithmFactory factory = searchAlgo.getFactory();
        @Override
        public AlgorithmCategory getCategory() {
            return factory.getCategory();
        }

        @Override
        public String getName() {
           return factory.getName();
        }

        @Override
        public String getDescription() {
            return factory.getDescription();
        }

        @Override
        public AlgorithmSetupUI getSetupUI() {
            return null;
        }

        @Override
        public Algorithm createAlgorithm() {
            return new MyPreprocessingAlgo(this);
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
}
