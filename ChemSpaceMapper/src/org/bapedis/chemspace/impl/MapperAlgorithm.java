/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.distance.DistanceFunction;
import org.bapedis.chemspace.distance.Euclidean;
import org.bapedis.chemspace.model.FeatureSelectionOption;
import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorException;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.alg.impl.FeatureSEFiltering;
import org.bapedis.core.spi.alg.impl.FeatureSEFilteringFactory;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlgFactory;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MapperAlgorithm implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    public static final String RUNNING = "running";

    private final MapperAlgorithmFactory factory;
    protected final PropertyChangeSupport propertyChangeSupport;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    protected boolean stopRun, running;

    //Mapping Options
    protected RemovingRedundantOption nrdOption;
    protected FeatureExtractionOption feOption;
    protected FeatureSelectionOption fsOption;

    //Mapping Algorithms   
    private NonRedundantSetAlg nrdAlg;
    private AllDescriptors featureExtractionAlg;
    private FeatureSEFiltering featureSelectionAlg;
    private AbstractDistance distFunction;
    private final WekaPCATransformer pcaTransformer;
    private final NetworkEmbedderAlg networkAlg;
    private final NetworkReport networkReport;

    //Algorithm workflow
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;

        //Mapping Options        
        nrdOption = RemovingRedundantOption.NO;
        feOption = FeatureExtractionOption.YES;
        fsOption = FeatureSelectionOption.NO;

        //Mapping algorithms
        nrdAlg = (NonRedundantSetAlg) new NonRedundantSetAlgFactory().createAlgorithm();
        featureExtractionAlg = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureSelectionAlg = (FeatureSEFiltering) new FeatureSEFilteringFactory().createAlgorithm();
        pcaTransformer = (WekaPCATransformer) new WekaPCATransformerFactory().createAlgorithm();
        networkAlg = (NetworkEmbedderAlg) new NetworkEmbedderFactory().createAlgorithm();
        networkReport = (NetworkReport) new NetworkReportFactory().createAlgorithm();

        //Default distance function
        Collection<? extends DistanceFunction> factories = Lookup.getDefault().lookupAll(DistanceFunction.class);
        for (DistanceFunction distFunc : factories) {
            if (distFunc instanceof Euclidean) {
                this.distFunction = (AbstractDistance) distFunc;
            }
        }
    }

    public boolean isRunning() {
        return running;
    }

    public String getSettingsReport() {
        String report = getNrdSettings()
                + "<br />"
                + getFESettings()
                + "<br />"
                + getFSSettings()
                + "<br />"
                + getDistanceSettings();
        return report;
    }

    private String getNrdSettings() {
        StringBuilder reportBuilder = new StringBuilder(String.format("<b> Removing redundant sequences</b> (%s)<br />", nrdOption));
        if (nrdOption == RemovingRedundantOption.YES) {
            reportBuilder.append(String.format("Alignment type: %s <br />", SequenceAlignmentModel.ALIGNMENT_TYPE[nrdAlg.getAlignmentModel().getAlignmentTypeIndex()]));
            reportBuilder.append(String.format("Substitution matrix: %s <br />", SequenceAlignmentModel.SUBSTITUTION_MATRIX[nrdAlg.getAlignmentModel().getSubstitutionMatrixIndex()]));
            reportBuilder.append(String.format("Percent identity: %s <br />", nrdAlg.getAlignmentModel().getPercentIdentity() + "%"));
        }
        return reportBuilder.toString();

    }

    private String getFESettings() {
        StringBuilder reportBuilder = new StringBuilder(String.format("<b> Feature extraction</b> (%s)<br />", feOption));
        if (feOption == FeatureExtractionOption.YES) {
            for (Algorithm alg : featureExtractionAlg.getAlgorithms()) {
                reportBuilder.append(String.format("%s <br />", alg.getFactory().getName()));
            }
        }
        return reportBuilder.toString();
    }

    private String getFSSettings() {
        StringBuilder reportBuilder = new StringBuilder(String.format("<b> Feature selection</b> (%s)<br />", fsOption));
        if (fsOption == FeatureSelectionOption.YES) {
            reportBuilder.append(String.format("%s <br />", ""));
        }
        return reportBuilder.toString();
    }

    private String getDistanceSettings() {
        String reportBuilder = String.format("<b> Distance Function</b> (%s)<br />", distFunction.getName());
        return reportBuilder;
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
                nrdAlg.setWorkspaceInput(true);
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
        if (fsOption == FeatureSelectionOption.YES && !stopRun) {
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
        }

        // Preprocessing of feature list. Compute max, min, mean and std
        if (!stopRun) {
            try {
                MolecularDescriptor.preprocessing(allFeatures, attrModel.getPeptides());
            } catch (MolecularDescriptorException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                pc.reportError(ex.getMessage(), workspace);
                cancel();
            }
        }

        //WekaPCA Transformer
        if (!stopRun) {
            currentAlg = pcaTransformer;
            execute();

            NetworkCoordinateUpdater updater = new NetworkCoordinateUpdater(pcaTransformer.getXYZSpace());
            updater.execute();
        }

        // Network construction
        if (!stopRun) {
            if (distFunction != null) {
                networkAlg.setDistanceFunction(distFunction);
                currentAlg = networkAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Distance function is null");
            }
        }

        //Network report
//        if (!stopRun) {
//            currentAlg = networkReport;
//            execute();
//        }
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

    public FeatureSelectionOption getFSOption() {
        return fsOption;
    }

    public void setFSOption(FeatureSelectionOption fsOption) {
        this.fsOption = fsOption;
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

    public WekaPCATransformer getPCATransformer() {
        return pcaTransformer;
    }

    public NetworkEmbedderAlg getNetworkEmbedderAlg() {
        return networkAlg;
    }

    public NetworkReport getNetworkReport() {
        return networkReport;
    }

    public AbstractDistance getDistanceFunction() {
        return distFunction;
    }

    public void setDistanceFunction(AbstractDistance distFunction) {
        this.distFunction = distFunction;
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
