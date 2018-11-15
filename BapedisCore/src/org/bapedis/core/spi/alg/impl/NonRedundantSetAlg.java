/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.List;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NonRedundantSetAlg implements Algorithm {
    
    private final ProjectManager pc;
    private final NonRedundantSetAlgFactory factory;
    protected final SequenceClustering clusteringAlg;
    private AttributesModel newModel, oldModel;
    private Workspace workspace;
    private ProgressTicket ticket;
    private boolean stopRun;
    
    public NonRedundantSetAlg(NonRedundantSetAlgFactory factory) {
        this.factory = factory;
        this.clusteringAlg = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }
    
    public SequenceAlignmentModel getAlignmentModel() {
        return clusteringAlg.getAlignmentModel();
    }
    
    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.ticket = progressTicket;
        stopRun = false;
    }
    
    @Override
    public void endAlgo() {
        // Set new Model
        if (oldModel != null && newModel != null && !stopRun) {
            final Workspace ws = workspace;
            final AttributesModel modelToRemove = oldModel;
            final AttributesModel modelToAdd = newModel;
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    ws.remove(modelToRemove);
                    ws.add(modelToAdd);
                }
            });
            
        }
        workspace = null;
        ticket = null;
        newModel = null;
        oldModel = null;
    }
    
    @Override
    public boolean cancel() {
        stopRun = true;
        if (clusteringAlg != null) {
            return clusteringAlg.cancel();
        }
        return stopRun;
    }
    
    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }
    
    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }
    
    private Cluster[] clusterize() {
        String msg = NbBundle.getMessage(NonRedundantSetAlg.class, "NonRedundantSetAlg.task.clusterize");
        pc.reportMsg(msg, workspace);
        ticket.progress(msg);

        clusteringAlg.initAlgo(workspace, ticket);
        clusteringAlg.run();
        clusteringAlg.endAlgo();
        return clusteringAlg.getClusters();
    }
    
    @Override
    public void run() {
        oldModel = pc.getAttributesModel(workspace);
        if (oldModel != null) {
            Peptide[] targets = oldModel.getPeptides().toArray(new Peptide[0]);
            clusteringAlg.setPeptides(targets);
            
            TreeSet<Integer> accepted = new TreeSet<>();
            Cluster[] clusters = clusterize();
            
            for (Cluster cluster : clusters) {
                accepted.add(cluster.getCentroid().getId());
            }

            //New model
            newModel = new AttributesModel(workspace);
            oldModel.getBridge().copyTo(newModel, null);
            for (Peptide peptide : targets) {
                if (accepted.contains(peptide.getId())) {
                    newModel.addPeptide(peptide);
                }
            }
        }
    }
    
}
