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
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.impl.SequenceClustering;
import org.bapedis.core.spi.alg.impl.SequenceClusteringFactory;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;

/**
 *
 * @author loge
 */
public class NonRedundantSetFilter implements Filter, ActionListener {

    private final NonRedundantSetFilterFactory factory;
    protected final SequenceClustering clustering;
    private final TreeSet<Integer> accepted;

    public NonRedundantSetFilter(NonRedundantSetFilterFactory factory) {
        this.factory = factory;
        this.clustering = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        accepted = new TreeSet<>();
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
        clustering.setPeptides(targets);
        PreprocessingWrapper preprocessingAlgo = (PreprocessingWrapper) new PreprocessingWrapperFactory().createAlgorithm();
        preprocessingAlgo.setAlgorithm(clustering);
        preprocessingAlgo.setActionListener(this);
        return preprocessingAlgo;
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
    public void actionPerformed(ActionEvent e) {
        accepted.clear();
        List<Cluster> clusterList = clustering.getClusterList();
        for (Cluster cluster : clusterList) {
            accepted.add(cluster.getCentroid().getId());
        }
    }
    
    
}
