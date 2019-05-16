/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.bapedis.chemspace.model.FeatureFilteringOption;
import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.similarity.AbstractSimCoefficient;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractClusterizer;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.FeatureSEFiltering;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MapperAlgorithm implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    public static final String RUNNING = "running";

    protected final NotifyDescriptor notEnoughFeatures;

    private final MapperAlgorithmFactory factory;
    protected final PropertyChangeSupport propertyChangeSupport;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    protected boolean stopRun, running;

    //Mapping Options
    protected RemovingRedundantOption nrdOption;
    protected FeatureExtractionOption feOption;
    protected FeatureFilteringOption ffOption;

    //Mapping Algorithms   
    private NonRedundantSetAlg nrdAlg;
    private AllDescriptors featureExtractionAlg;
    private FeatureSEFiltering featureSelectionAlg;
    private AbstractClusterizer clusteringAlg;
    private AbstractSimCoefficient simCoefficientAlg;
    private final WekaPCATransformer pcaTransformer;
    private final NetworkEmbedderAlg networkAlg;

    //Algorithm workflow
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;

        //Mapping Options        
        nrdOption = RemovingRedundantOption.NO;
        feOption = FeatureExtractionOption.YES;
        ffOption = FeatureFilteringOption.NO;

        pcaTransformer = (WekaPCATransformer) new WekaPCATransformerFactory().createAlgorithm();
        networkAlg = (NetworkEmbedderAlg) new NetworkEmbedderFactory().createAlgorithm();

        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(MapperAlgorithm.class, "MapperAlgorithm.features.notEnoughHTML"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        stopRun = false;
        running = true;
        propertyChangeSupport.firePropertyChange(RUNNING, false, true);
    }

    @Override
    public void run() {
        //Non-redundant set
        if (nrdOption == RemovingRedundantOption.YES && !stopRun) {
            if (nrdAlg != null) {
                currentAlg = nrdAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Non-redundant algorithm is null");
            }
        }

        // Feature Extraction
        if (feOption == FeatureExtractionOption.YES && !stopRun) {
            if (featureExtractionAlg != null) {
                currentAlg = featureExtractionAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Feature extraction algorithm is null");
            }
        }

        // Feature Selection
        if (ffOption == FeatureFilteringOption.YES && !stopRun) {
            if (featureSelectionAlg != null) {
                currentAlg = featureSelectionAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Feature selection algorithm is null");
            }
        }
        
        // Load all descriptors
        List<MolecularDescriptor> allFeatures = new LinkedList<>();
        AttributesModel attrModel = pc.getAttributesModel();
        if (!stopRun) {            
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }

            // Check feature list size
            if (allFeatures.size() < ProjectManager.MIN_AVAILABLE_FEATURES) {
                DialogDisplayer.getDefault().notify(notEnoughFeatures);
                pc.reportError(NbBundle.getMessage(NetworkEmbedderAlg.class, "MapperAlgorithm.features.notEnough"), workspace);
                cancel();
            }
        }

        // Preprocessing of feature list. Compute max, min, mean and std
        try {            
            for (MolecularDescriptor attr : allFeatures) {
                attr.resetSummaryStats(attrModel.getPeptides());
            }

            // Validate molecular features
            for (MolecularDescriptor attr : allFeatures) {
                if (attr.getMax() == attr.getMin()) {
                    NotifyDescriptor invalidFeature = new NotifyDescriptor.Message(NbBundle.getMessage(NetworkEmbedderAlg.class, "MapperAlgorithm.features.invalidFeatureHTML", attr.getDisplayName()), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(invalidFeature);
                    pc.reportError(NbBundle.getMessage(NetworkEmbedderAlg.class, "MapperAlgorithm.features.invalidFeature", attr.getDisplayName()), workspace);
                    cancel();
                }
            }
        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorND());
            pc.reportError(ex.getMessage(), workspace);
            cancel();
        }

        // Clustering
        if (clusteringAlg != null && !stopRun) {
            currentAlg = clusteringAlg;
            execute();
        } else {
            throw new RuntimeException("Internal error: Clustering algorithm is null");
        }
        
        //WekaPCA Transformer
        if (!stopRun){
            currentAlg = pcaTransformer;
            execute();
            
            NetworkCoordinateUpdater updater = new NetworkCoordinateUpdater(pcaTransformer.getXYZSpace());
            updater.execute();
        }

        // Similarity
        if (simCoefficientAlg != null && !stopRun) {
            networkAlg.setSimCoefficient(simCoefficientAlg);
            currentAlg = networkAlg;
            execute();
        } else {
            throw new RuntimeException("Internal error: Similarity coefficient is null");
        }
    }

    private void execute() {
        if (!stopRun) {
            String taskName = NbBundle.getMessage(MapperAlgorithm.class, "MapperAlgorithm.workflow.taskName", currentAlg.getFactory().getName());
            progressTicket.progress(taskName);
            progressTicket.switchToIndeterminate();
            pc.reportMsg(taskName, workspace);

            currentAlg.initAlgo(workspace, progressTicket);
            currentAlg.run();
            currentAlg.endAlgo();
        }
    }

    @Override
    public void endAlgo() {
        if (!stopRun) {
            GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    graphWC.openGraphWindow();
                }
            });
        }
        workspace = null;
        progressTicket = null;

        running = false;
        propertyChangeSupport.firePropertyChange(RUNNING, true, false);
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        if (currentAlg != null) {
            return currentAlg.cancel();
        }
        return stopRun;
    }

    public FeatureExtractionOption getFEOption() {
        return feOption;
    }

    public void setFEOption(FeatureExtractionOption feOption) {
        this.feOption = feOption;
    }

    public FeatureFilteringOption getFFOption() {
        return ffOption;
    }

    public void setFFOption(FeatureFilteringOption ffOption) {
        this.ffOption = ffOption;
    }

    public RemovingRedundantOption getNrdOption() {
        return nrdOption;
    }

    public void setNrdOption(RemovingRedundantOption nrdOption) {
        this.nrdOption = nrdOption;
    }

    public NonRedundantSetAlg getNonRedundantSetAlg() {
        return nrdAlg;
    }

    public void setNonRedundantSetAlg(NonRedundantSetAlg nrdAlg) {
        this.nrdAlg = nrdAlg;
    }

    public void setFeatureExtractionAlg(AllDescriptors alg) {
        this.featureExtractionAlg = alg;
    }

    public AllDescriptors getFeatureExtractionAlg() {
        return featureExtractionAlg;
    }

    public void setFeatureSelectionAlg(FeatureSEFiltering alg) {
        this.featureSelectionAlg = alg;
    }

    public FeatureSEFiltering getFeatureSelectionAlg() {
        return featureSelectionAlg;
    }

    public AbstractClusterizer getClusteringAlg() {
        return clusteringAlg;
    }

    public void setClusteringAlg(AbstractClusterizer clusteringAlg) {
        this.clusteringAlg = clusteringAlg;
    }

    public WekaPCATransformer getPCATransformer() {
        return pcaTransformer;
    }        

    public NetworkEmbedderAlg getNetworkEmbedderAlg() {
        return networkAlg;
    }

    public AbstractSimCoefficient getSimCoefficientAlg() {
        return simCoefficientAlg;
    }

    public void setSimCoefficientAlg(AbstractSimCoefficient simCoefficientAlg) {
        this.simCoefficientAlg = simCoefficientAlg;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    public void addRunningListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeRunningListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
