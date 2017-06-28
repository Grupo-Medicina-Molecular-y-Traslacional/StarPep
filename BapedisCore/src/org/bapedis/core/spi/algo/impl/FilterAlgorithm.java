/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import java.util.TreeSet;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.GraphModel;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.util.Lookup;

/**
 *
 * @author Home
 */
public class FilterAlgorithm implements Algorithm {

    private final ProjectManager pm;
    private TreeSet<String> set;
    private FilterModel filterModel;
    private GraphModel graphModel;
    private boolean stopRun;

    public FilterAlgorithm() {
        pm = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    public void initAlgo() {
        filterModel = pm.getFilterModel();
        graphModel = pm.getGraphModel();
        stopRun = false;
        set = new TreeSet<>();
    }

    @Override
    public void endAlgo() {
        filterModel = null;
        graphModel = null;
        set = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public AlgorithmFactory getFactory() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}

class QuickFilterImpl implements QuickFilter {

    private final TreeSet<String> set;

    public QuickFilterImpl(TreeSet<String> set) {
        this.set = set;
    }

    @Override
    public boolean accept(Object obj) {
        PeptideNode node = ((PeptideNode) obj);
        Peptide peptide = node.getPeptide();
        return set.contains(peptide.getId());
    }

}
