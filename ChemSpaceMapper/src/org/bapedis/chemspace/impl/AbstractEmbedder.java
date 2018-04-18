/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public abstract class AbstractEmbedder implements Algorithm, Cloneable {

    public static final int MIN_AVAILABLE_FEATURES = 2;
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final AlgorithmFactory factory;
    protected Workspace workspace;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun;
    protected final NotifyDescriptor notEnoughFeatures;
    

    public AbstractEmbedder(AlgorithmFactory factory) {
        this.factory = factory;
        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(AbstractEmbedder.class, "AbstractEmbedder.features.notEnoughHTML"), NotifyDescriptor.ERROR_MESSAGE);        
    }     

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        stopRun = false;
        this.workspace = workspace;
        this.ticket = progressTicket;
        attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            graphModel = pc.getGraphModel(workspace);
            graph = graphModel.getGraphVisible();
        }        
    }

    @Override
    public void run() {
        if (attrModel != null) {
            List<Peptide> peptides = attrModel.getPeptides();
            Set<String> descriptorKeys = attrModel.getMolecularDescriptorKeys();

            // Populate feature list 
            List<MolecularDescriptor> features = new LinkedList<>();
            for (String key : descriptorKeys) {
                for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                    features.add(desc);
                }
            }

            // Preprocessing and validate molecular features
            preprocessing(features, peptides);
            
            //Checking running state
            if (!stopRun){
                embed(peptides.toArray(new Peptide[0]), features.toArray(new MolecularDescriptor[0]));
            }
        }
    }

    private void preprocessing(List<MolecularDescriptor> features, List<Peptide> peptides) {
        // Check feature list size
        if (features.size() < MIN_AVAILABLE_FEATURES) {
            DialogDisplayer.getDefault().notify(notEnoughFeatures);
            pc.reportError(NbBundle.getMessage(AbstractEmbedder.class, "AbstractEmbedder.features.notEnough"), workspace);
            cancel();
        }

        // try/catch for molecular not found exception handling
        try {
            // Preprocessing of feature list. Compute max, min, mean and std
            for (MolecularDescriptor attr : features) {
                attr.resetSummaryStats(peptides);
            }

            // Validate molecular features
            for (MolecularDescriptor attr : features) {
                if (attr.getMax() == attr.getMin()) {
                    NotifyDescriptor invalidFeature = new NotifyDescriptor.Message(NbBundle.getMessage(AbstractEmbedder.class, "AbstractEmbedder.features.invalidFeatureHTML", attr.getDisplayName()), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(invalidFeature);
                    pc.reportError(NbBundle.getMessage(AbstractEmbedder.class, "AbstractEmbedder.features.invalidFeature", attr.getDisplayName()), workspace);
                    cancel();
                }
            }
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorND());
            pc.reportError(ex.getMessage(), workspace);
            cancel();
        }
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        graphModel = null;
        graph = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
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
    public Object clone() throws CloneNotSupportedException {
        return super.clone(); 
    }        
    
    protected abstract void embed(Peptide[] peptides, MolecularDescriptor[] features);

}
