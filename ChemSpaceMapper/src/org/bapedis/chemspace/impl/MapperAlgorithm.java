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
import org.bapedis.chemspace.model.FeatureWeightingOption;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.alg.impl.FeatureSEFiltering;
import org.bapedis.core.spi.alg.impl.FeatureSEFilteringFactory;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
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
    protected ChemSpaceOption csOption;
    protected FeatureExtractionOption feOption;
    protected FeatureFilteringOption ffOption;
    protected FeatureWeightingOption fwOption;

    // Algorithms    
    private AllDescriptors featureExtractionAlg;
    private FeatureSEFiltering featureFilteringAlg;
    private CSNEmbedder csnEmbedderAlg;
    private SSNEmbedder ssnEmbedderAlg;
    private AbstractCluster clusteringAlg;

    //Algorithm workflow
    private final List<Algorithm> algorithms;
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        algorithms = new LinkedList<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;

        //Mapping Options        
        csOption = ChemSpaceOption.NONE;
        feOption = FeatureExtractionOption.NEW;
        ffOption = FeatureFilteringOption.YES;
        fwOption = FeatureWeightingOption.NO;

        // Algorithms
        featureExtractionAlg = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureFilteringAlg = (FeatureSEFiltering) new FeatureSEFilteringFactory().createAlgorithm();        
//        twoDEmbedderAlg = (TwoDEmbedder) new TwoDEmbedderFactory().createAlgorithm();
        csnEmbedderAlg = (CSNEmbedder) new CSNEmbedderFactory().createAlgorithm();
        ssnEmbedderAlg = (SSNEmbedder) new SSNEmbedderFactory().createAlgorithm();
        clusteringAlg = null;
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
        
        switch (csOption) {
            case CHEM_SPACE_NETWORK:
                // Feature Extraction
                if (feOption == FeatureExtractionOption.NEW) {
                    algorithms.add(featureExtractionAlg);
                }

                // Feature Filtering
                if (ffOption == FeatureFilteringOption.YES) {
                    algorithms.add(featureFilteringAlg);
                }
                // Chemical Space Embedder
                DescriptorBasedEmbedder embedder = null;
                if (csOption == ChemSpaceOption.CHEM_SPACE_NETWORK) {
                    embedder = csnEmbedderAlg;
                }
                algorithms.add(embedder);
                break;
            case SEQ_SIMILARITY_NETWORK:
                algorithms.add(ssnEmbedderAlg);
                break;
            default:
                throw new RuntimeException("Internal error: Chemical Space Embedder is null");
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

    @Override
    public void endAlgo() {
        if (!stopRun){
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

    public ChemSpaceOption getChemSpaceOption() {
        return csOption;
    }

    public void setChemSpaceOption(ChemSpaceOption csOption) {
        this.csOption = csOption;
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

    public FeatureWeightingOption getFWOption() {
        return fwOption;
    }

    public void setFWOption(FeatureWeightingOption fwOption) {
        this.fwOption = fwOption;
    }

    public void setFeatureExtractionAlg(AllDescriptors alg) {
        this.featureExtractionAlg = alg;
    }

    public AllDescriptors getFeatureExtractionAlg() {
        return featureExtractionAlg;
    }

    public void setFeatureFilteringAlg(FeatureSEFiltering alg) {
        this.featureFilteringAlg = alg;
    }

    public FeatureSEFiltering getFeatureFilteringAlg() {
        return featureFilteringAlg;
    }

    public CSNEmbedder getCSNEmbedderAlg() {
        return csnEmbedderAlg;
    }

    public void setCSNEmbedderAlg(CSNEmbedder networkEmbedderAlg) {
        this.csnEmbedderAlg = networkEmbedderAlg;
    }

    public SSNEmbedder getSSNEmbedderAlg() {
        return ssnEmbedderAlg;
    }

    public void setSSNEmbedderAlg(SSNEmbedder ssnEmbedderAlg) {
        this.ssnEmbedderAlg = ssnEmbedderAlg;
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
