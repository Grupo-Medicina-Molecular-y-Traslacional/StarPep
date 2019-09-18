/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.distance.DistanceFunction;
import org.bapedis.chemspace.distance.Euclidean;
import org.bapedis.chemspace.model.FeatureSelectionOption;
import org.bapedis.chemspace.model.FeatureExtractionOption;
import org.bapedis.chemspace.model.InputSequenceOption;
import org.bapedis.chemspace.model.RemovingRedundantOption;
import org.bapedis.chemspace.model.SimilaritySearchingOption;
import org.bapedis.chemspace.searching.ChemBaseSimilaritySearchAlg;
import org.bapedis.chemspace.searching.ChemMultiSimilaritySearchFactory;
import org.bapedis.chemspace.searching.EmbeddingInputSeqAlg;
import org.bapedis.chemspace.searching.EmbeddingInputSeqFactory;
import org.bapedis.chemspace.searching.EmbeddingQuerySeqAlg;
import org.bapedis.chemspace.searching.EmbeddingQuerySeqFactory;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorException;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AllDescriptors;
import org.bapedis.core.spi.alg.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlg;
import org.bapedis.core.spi.alg.impl.NonRedundantSetAlgFactory;
import org.bapedis.core.spi.alg.impl.UnsupervisedFeatureSelection;
import org.bapedis.core.spi.alg.impl.UnsupervisedFeatureSelectionFactory;
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

    private final MapperAlgorithmFactory factory;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    protected boolean stopRun;

    //Mapping Options   
    protected InputSequenceOption inputOption;
    protected RemovingRedundantOption nrdOption;
    protected FeatureExtractionOption feOption;
    protected FeatureSelectionOption fsOption;
    protected SimilaritySearchingOption searchingOption;

    //Mapping Algorithms   
    private EmbeddingInputSeqAlg embeddingInputAlg;
    private NonRedundantSetAlg nonRedundantAlg;
    private EmbeddingQuerySeqAlg embeddingQueryAlg;
    private ChemBaseSimilaritySearchAlg simSearchingAlg;
    private AllDescriptors featureExtractionAlg;
    private UnsupervisedFeatureSelection featureSelectionAlg;
    private AbstractDistance distFunction;
    private final WekaPCATransformer pcaTransformer;
    private final NetworkEmbedderAlg networkAlg;
    private final Modularity modularityOptimization;

    //Algorithm workflow
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;

        //Mapping Options    
        inputOption = InputSequenceOption.CURRENT_WORKSPACE;
        nrdOption = RemovingRedundantOption.NO;
        searchingOption = SimilaritySearchingOption.NO;
        feOption = FeatureExtractionOption.YES;
        fsOption = FeatureSelectionOption.YES;

        //Mapping algorithms
        embeddingInputAlg = (EmbeddingInputSeqAlg) new EmbeddingInputSeqFactory().createAlgorithm();
        nonRedundantAlg = (NonRedundantSetAlg) new NonRedundantSetAlgFactory().createAlgorithm();
        embeddingQueryAlg = (EmbeddingQuerySeqAlg) new EmbeddingQuerySeqFactory().createAlgorithm();
        simSearchingAlg = (ChemBaseSimilaritySearchAlg) new ChemMultiSimilaritySearchFactory().createAlgorithm();
        featureExtractionAlg = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureSelectionAlg = (UnsupervisedFeatureSelection) new UnsupervisedFeatureSelectionFactory().createAlgorithm();
        pcaTransformer = (WekaPCATransformer) new WekaPCATransformerFactory().createAlgorithm();
        networkAlg = (NetworkEmbedderAlg) new NetworkEmbedderFactory().createAlgorithm();
        modularityOptimization = (Modularity) new ModularityFactory().createAlgorithm();
        
        //Default distance function
        Collection<? extends DistanceFunction> factories = Lookup.getDefault().lookupAll(DistanceFunction.class);
        for (DistanceFunction distFunc : factories) {
            if (distFunc instanceof Euclidean) {
                this.distFunction = (AbstractDistance) distFunc;
            }
        }
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        stopRun = false;
    }

    @Override
    public void run() {

        // Embedding input sequences
        if (nrdOption == RemovingRedundantOption.YES) {
            if (nonRedundantAlg != null) {
                nonRedundantAlg.setWorkspaceInput(inputOption == InputSequenceOption.CURRENT_WORKSPACE);
                pc.reportMsg("Removing redundant sequences", workspace);
                currentAlg = nonRedundantAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Non redundant sequence algorithm is null");
            }
        } else if (inputOption == InputSequenceOption.EMBEDDED_DB) {
            if (embeddingInputAlg != null) {
                pc.reportMsg("Embedding input sequences", workspace);
                currentAlg = embeddingInputAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Embedding input sequences is null");
            }
        }

        // Embedding query sequences        
        if (searchingOption == SimilaritySearchingOption.YES && !stopRun) {
            pc.reportMsg("Embedding query sequences", workspace);
            if (embeddingQueryAlg != null) {
                currentAlg = embeddingQueryAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Embedding query algorithm is null");
            }
        }

        // Feature Extraction
        pc.reportMsg(String.format("Feature extraction: %s", feOption), workspace);
        if (feOption == FeatureExtractionOption.YES && !stopRun) {
            if (featureExtractionAlg != null) {
                currentAlg = featureExtractionAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Feature extraction algorithm is null");
            }
        }

        // Feature Selection
        pc.reportMsg(String.format("Feature selection: %s", fsOption), workspace);
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

        // Preprocessing of features. Computing max, min, mean and std
        pc.reportMsg("Preprocessing of features. Computing max, min, mean and std", workspace);
        if (!stopRun) {
            try {
                MolecularDescriptor.preprocessing(allFeatures, attrModel.getPeptides());
                if (distFunction != null) {
                    distFunction.setFeatures(allFeatures);
                } else {
                    throw new RuntimeException("Internal error: Distance function is null");
                }
            } catch (MolecularDescriptorException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                pc.reportError(ex.getMessage(), workspace);
                cancel();
            }
        }

        // Chemical similarity searching
        pc.reportMsg(String.format("Chemical similarity searching: %s", searchingOption), workspace);
        if (searchingOption == SimilaritySearchingOption.YES && !stopRun) {
            if (simSearchingAlg != null) {
                if (distFunction != null) {
                    pc.reportMsg(String.format("Distance Function: %s", distFunction.getName()), workspace);
                    simSearchingAlg.setDistanceFunction(distFunction);
                    currentAlg = simSearchingAlg;
                    execute();
                } else {
                    throw new RuntimeException("Internal error: Distance function is null");
                }
            } else {
                throw new RuntimeException("Internal error: Similarity searching algorithm is null");
            }
        }

        //WekaPCA Transformer
        pc.reportMsg("Applying PCA transformation", workspace);
        if (!stopRun) {
            if (distFunction != null && pcaTransformer.getOption() != distFunction.getOption()) {
                pcaTransformer.setOption(distFunction.getOption());
            }
            currentAlg = pcaTransformer;
            execute();
        }

        // Network construction
        pc.reportMsg("Network construction", workspace);
        if (!stopRun) {
            if (distFunction != null) {
                pc.reportMsg(String.format("Distance Function: %s", distFunction.getName()), workspace);
                networkAlg.setDistanceFunction(distFunction);
                networkAlg.setXyzSpace(pcaTransformer.getXYZSpace());
                currentAlg = networkAlg;
                execute();
            } else {
                throw new RuntimeException("Internal error: Distance function is null");
            }
        }
        
        //Modularity optimization
        pc.reportMsg("Modularity optimization", workspace);
        if (!stopRun){
            if (modularityOptimization != null){
                currentAlg = modularityOptimization;
                execute();
            } else {
                throw new RuntimeException("Internal error: Modularity optimization algorithm is null");
            }
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
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        if (currentAlg != null) {
            return currentAlg.cancel();
        }
        return stopRun;
    }

    public InputSequenceOption getInputOption() {
        return inputOption;
    }

    public void setInputOption(InputSequenceOption inputOption) {
        this.inputOption = inputOption;
    }

    public RemovingRedundantOption getNrdOption() {
        return nrdOption;
    }

    public void setNrdOption(RemovingRedundantOption nrdOption) {
        this.nrdOption = nrdOption;
    }

    public EmbeddingInputSeqAlg getEmbeddingInputAlg() {
        return embeddingInputAlg;
    }

    public void setEmbeddingInputAlg(EmbeddingInputSeqAlg embeddingInputAlg) {
        this.embeddingInputAlg = embeddingInputAlg;
    }

    public NonRedundantSetAlg getNonRedundantAlg() {
        return nonRedundantAlg;
    }

    public void setNonRedundantAlg(NonRedundantSetAlg nonRedundantAlg) {
        this.nonRedundantAlg = nonRedundantAlg;
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

    public SimilaritySearchingOption getSearchingOption() {
        return searchingOption;
    }

    public void setSearchingOption(SimilaritySearchingOption searchingOption) {
        this.searchingOption = searchingOption;
    }

    public EmbeddingQuerySeqAlg getEmbeddingQueryAlg() {
        return embeddingQueryAlg;
    }

    public void setEmbeddingQueryAlg(EmbeddingQuerySeqAlg embeddingQueryAlg) {
        this.embeddingQueryAlg = embeddingQueryAlg;
    }

    public ChemBaseSimilaritySearchAlg getSimSearchingAlg() {
        return simSearchingAlg;
    }

    public void setSimSearchingAlg(ChemBaseSimilaritySearchAlg simSearchingAlg) {
        this.simSearchingAlg = simSearchingAlg;
    }

    public void setFeatureExtractionAlg(AllDescriptors alg) {
        this.featureExtractionAlg = alg;
    }

    public AllDescriptors getFeatureExtractionAlg() {
        return featureExtractionAlg;
    }

    public void setFeatureSelectionAlg(UnsupervisedFeatureSelection alg) {
        this.featureSelectionAlg = alg;
    }

    public UnsupervisedFeatureSelection getFeatureSelectionAlg() {
        return featureSelectionAlg;
    }

    public WekaPCATransformer getPCATransformer() {
        return pcaTransformer;
    }

    public NetworkEmbedderAlg getNetworkEmbedderAlg() {
        return networkAlg;
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
}
