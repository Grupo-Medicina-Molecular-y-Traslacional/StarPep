/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.model.FeatureFilteringOption;
import org.bapedis.chemspace.model.FeatureWeightingOption;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.model.ChemSpaceOption;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.alg.impl.FeatureFiltering;
import org.bapedis.core.spi.alg.impl.FeatureFilteringFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MapperAlgorithm implements Algorithm {

    public static final int MIN_AVAILABLE_FEATURES = 2;
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    private final MapperAlgorithmFactory factory;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    protected boolean stopRun;

    //Mapping Options
    protected ChemSpaceOption csOption;
    protected FeatureExtractionOption feOption;
    protected FeatureFilteringOption ffOption;
    protected FeatureWeightingOption fwOption;

    // Algorithms    
    private AllDescriptors featureExtractionAlg;
    private FeatureFiltering featureFilteringAlg;
    private AbstractEmbedder chemSpaceEmbedderAlg;
//    private final SequenceClustering seqClustering;

    //Algorithm workflow
    private final List<Algorithm> algorithms;
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        algorithms = new LinkedList<>();

        //Mapping Options        
        csOption = ChemSpaceOption.NONE;
        feOption = FeatureExtractionOption.NEW;
        ffOption = FeatureFilteringOption.YES;
        fwOption = FeatureWeightingOption.NO;

        // Algorithms
        featureExtractionAlg = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureFilteringAlg = (FeatureFiltering) new FeatureFilteringFactory().createAlgorithm();
        chemSpaceEmbedderAlg = (NetworkEmbedder) new NetworkEmbedderFactory().createAlgorithm();
//        seqClustering = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();        
//        twoDEmbedder = (TwoDEmbedder) new TwoDEmbedderFactory().createAlgorithm();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;

        algorithms.clear();
        // Populate algorithm workflow        

        // Feature Extraction
        if (feOption == FeatureExtractionOption.NEW) {
            algorithms.add(featureExtractionAlg);
        }

        // Feature Filtering
        if (ffOption == FeatureFilteringOption.YES) {
            algorithms.add(featureFilteringAlg);
        }

        // Chemical Space Embedder
        assert (chemSpaceEmbedderAlg != null) : "Internal error: Chemical Space Embedder is null";
        algorithms.add(chemSpaceEmbedderAlg);
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
        workspace = null;
        progressTicket = null;
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

    public void setFeatureFilteringAlg(FeatureFiltering alg) {
        this.featureFilteringAlg = alg;
    }

    public FeatureFiltering getFeatureFilteringAlg() {
        return featureFilteringAlg;
    }

    public AbstractEmbedder getChemSpaceEmbedderAlg() {
        return chemSpaceEmbedderAlg;
    }

    public void setChemSpaceEmbedderAlg(AbstractEmbedder alg) {
        this.chemSpaceEmbedderAlg = alg;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

}
