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
import org.bapedis.core.model.AttributesModel;
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
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
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
    private final WizardOptionModel optionModel;

    // Algorithms    
    private final AllDescriptors featureExtraction;
    private final FeatureFilteringAlgo featureFiltering;
    private final SequenceClustering seqClustering;
    private final NetworkEmbedder networkEmbeder;
    private final TwoDEmbedder twoDEmbedder;

    //Algorithm workflow
    private final List<Algorithm> workFlow;
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        optionModel = new WizardOptionModel();
        workFlow = new LinkedList<>();

        // Algorithms
        featureExtraction = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureFiltering = (FeatureFilteringAlgo) new FeatureFilteringFactory().createAlgorithm();
        seqClustering = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        networkEmbeder = (NetworkEmbedder) new NetworkEmbedderFactory().createAlgorithm();
        twoDEmbedder = (TwoDEmbedder) new TwoDEmbedderFactory().createAlgorithm();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;

        workFlow.clear();
        // Populate algorithm workflow        

        // Feature Extraction
        if (optionModel.getMolecularDescriptorOption() == WizardOptionModel.MolecularDescriptorOption.NEW) {
            workFlow.add(featureExtraction);
        }

        // Feature Filtering
        if (optionModel.getFeatureFilteringOption() == WizardOptionModel.FeatureFiltering.YES) {
            workFlow.add(featureFiltering);
        }

        // Chemical Space Embedder
        if (optionModel.getRepresentationOption() == WizardOptionModel.RepresentationOption.CS2D) {
            workFlow.add(twoDEmbedder);
        } else if (optionModel.getRepresentationOption() == WizardOptionModel.RepresentationOption.CSN) {
            workFlow.add(networkEmbeder);
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

    public WizardOptionModel getOptionModel() {
        return optionModel;
    }

    public NetworkEmbedder getNetworkEmbeder() {
        return networkEmbeder;
    }

    public TwoDEmbedder getTwoDEmbedder() {
        return twoDEmbedder;
    }

    public AllDescriptors getFeatureExtraction() {
        return featureExtraction;
    }

    public FeatureFilteringAlgo getFeatureSelection() {
        return featureFiltering;
    }

    public SequenceClustering getSequenceClustering() {
        return seqClustering;
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
