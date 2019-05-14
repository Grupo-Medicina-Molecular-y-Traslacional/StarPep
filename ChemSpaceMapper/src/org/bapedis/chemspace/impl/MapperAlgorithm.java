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
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
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
    private AbstractCluster clusteringAlg;
    private AbstractSimCoefficient simCoefficientAlg;
    private NetworkEmbedderAlg networkAlg;

    //Algorithm workflow
    private final List<Algorithm> algorithms;
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        algorithms = new LinkedList<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;

        //Mapping Options        
        nrdOption = RemovingRedundantOption.NO;
        feOption = FeatureExtractionOption.NO;
        ffOption = FeatureFilteringOption.NO;

        networkAlg = (NetworkEmbedderAlg) new NetworkEmbedderFactory().createAlgorithm();
//        twoDEmbedderAlg = (TwoDEmbedder) new TwoDEmbedderFactory().createAlgorithm();
        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(MapperAlgorithm.class, "DescriptorBasedEmbedder.features.notEnoughHTML"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public boolean isRunning() {
        return running;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        stopRun = false;

        // Populate algorithm workflow        
        algorithms.clear();

        //Non-redundant set
        if (nrdOption == RemovingRedundantOption.YES) {
            if (nrdAlg != null) {
                algorithms.add(nrdAlg);
            } else {
                throw new RuntimeException("Internal error: Non-redundant algorithm is null");
            }
        }

        // Feature Extraction
        if (feOption == FeatureExtractionOption.YES) {
            if (featureExtractionAlg != null) {
                algorithms.add(featureExtractionAlg);
            } else {
                throw new RuntimeException("Internal error: Feature extraction algorithm is null");
            }
        }

        // Feature Filtering
        if (ffOption == FeatureFilteringOption.YES) {
            if (featureSelectionAlg != null) {
                algorithms.add(featureSelectionAlg);
            } else {
                throw new RuntimeException("Internal error: Feature selection algorithm is null");
            }
        }

        // Clustering
        if (clusteringAlg != null) {
            algorithms.add(clusteringAlg);
        } else {
            throw new RuntimeException("Internal error: Clustering algorithm is null");
        }

        // Similarity
        if (simCoefficientAlg != null) {
            networkAlg.setSimCoefficient(simCoefficientAlg);
            algorithms.add(networkAlg);
        } else {
            throw new RuntimeException("Internal error: Similarity coefficient is null");
        }

        running = true;
        propertyChangeSupport.firePropertyChange(RUNNING, false, true);
    }

    @Override
    public void run() {
        String taskName;
        int count = 1;
        for (Algorithm algorithm : algorithms) {
            if (!stopRun) {
                currentAlg = algorithm;
                taskName = NbBundle.getMessage(MapperAlgorithm.class, "MapperAlgorithm.workflow.taskName", count++, algorithms.size(), algorithm.getFactory().getName());
                progressTicket.progress(taskName);
                progressTicket.switchToIndeterminate();
                pc.reportMsg(taskName, workspace);
                algorithm.initAlgo(workspace, progressTicket);
                algorithm.run();
                algorithm.endAlgo();
            }
        }
    }

    private void preprocessing(List<MolecularDescriptor> features, List<Peptide> peptides) {
        // Check feature list size
        if (features.size() < ProjectManager.MIN_AVAILABLE_FEATURES) {
            DialogDisplayer.getDefault().notify(notEnoughFeatures);
            pc.reportError(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.notEnough"), workspace);
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
                    NotifyDescriptor invalidFeature = new NotifyDescriptor.Message(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.invalidFeatureHTML", attr.getDisplayName()), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(invalidFeature);
                    pc.reportError(NbBundle.getMessage(NetworkEmbedderAlg.class, "DescriptorBasedEmbedder.features.invalidFeature", attr.getDisplayName()), workspace);
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

    public AbstractCluster getClusteringAlg() {
        return clusteringAlg;
    }

    public void setClusteringAlg(AbstractCluster clusteringAlg) {
        this.clusteringAlg = clusteringAlg;
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
