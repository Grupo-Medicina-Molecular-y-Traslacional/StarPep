/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.model.WizardOptionModel;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.AllDescriptors;
import org.bapedis.core.spi.algo.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.algo.impl.FeatureFilteringAlgo;
import org.bapedis.core.spi.algo.impl.FeatureFilteringFactory;
import org.bapedis.core.spi.algo.impl.SequenceClustering;
import org.bapedis.core.spi.algo.impl.SequenceClusteringFactory;
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

    // Algorithms    
    private AllDescriptors featureExtraction;
    private FeatureFilteringAlgo featureFiltering;
//    private final SequenceClustering seqClustering;
    private AbstractEmbedder chemSpaceEmbedder;

    //Algorithm workflow
    private final List<Algorithm> workFlow;
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        workFlow = new LinkedList<>();

        // Algorithms
//        featureExtraction = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
//        featureFiltering = (FeatureFilteringAlgo) new FeatureFilteringFactory().createAlgorithm();
//        seqClustering = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
//        networkEmbeder = (NetworkEmbedder) new NetworkEmbedderFactory().createAlgorithm();
//        twoDEmbedder = (TwoDEmbedder) new TwoDEmbedderFactory().createAlgorithm();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;

        workFlow.clear();
        // Populate algorithm workflow        

        // Feature Extraction
        if (featureExtraction != null) {
            workFlow.add(featureExtraction);
        }

        // Feature Filtering
        if (featureFiltering != null) {
            workFlow.add(featureFiltering);
        }

        // Chemical Space Embedder
        if (chemSpaceEmbedder != null) {
            workFlow.add(chemSpaceEmbedder);
        }
    }

    @Override
    public void run() {
        String taskName;
        int count = 1;
        for (Algorithm algorithm : workFlow) {
            if (!stopRun) {
                currentAlg = algorithm;
                taskName = NbBundle.getMessage(MapperAlgorithm.class, "MapperAlgorithm.workflow.taskName", count++, workFlow.size(), algorithm.getFactory().getName());
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

    public void setFeatureExtraction(AllDescriptors featureExtraction) {
        this.featureExtraction = featureExtraction;
    }
    
    public AllDescriptors getFeatureExtraction() {
        return featureExtraction;
    }

    public void setFeatureFiltering(FeatureFilteringAlgo featureFiltering) {
        this.featureFiltering = featureFiltering;
    }
    
    public FeatureFilteringAlgo getFeatureSelection() {
        return featureFiltering;
    }

    public AbstractEmbedder getChemSpaceEmbedder() {
        return chemSpaceEmbedder;
    }

    public void setChemSpaceEmbedder(AbstractEmbedder chemSpaceEmbedder) {
        this.chemSpaceEmbedder = chemSpaceEmbedder;
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
