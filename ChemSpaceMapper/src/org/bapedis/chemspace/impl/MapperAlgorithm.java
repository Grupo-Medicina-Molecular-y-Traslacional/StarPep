/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
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
import org.bapedis.core.spi.alg.impl.FilteringSubsetOptimization;
import org.bapedis.core.spi.alg.impl.FilteringSubsetOptimizationFactory;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.bapedis.chemspace.distance.EuclideanFactory;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class MapperAlgorithm implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    public static PeptideAttribute INDEX_ATTR = new PeptideAttribute("indexAttr", "indexAttr", Integer.class, false, true);

    private final MapperAlgorithmFactory factory;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    protected boolean stopRun;
    protected String networkModel;

    //Mapping Options   
    protected InputSequenceOption inputOption;
    protected RemovingRedundantOption nrdOption;
    protected FeatureExtractionOption feOption;
    protected FeatureSelectionOption fsOption;
    protected MD_OUTPUT_OPTION mdOption;

    //Mapping Algorithms   
    private EmbeddingInputSeqAlg embeddingInputAlg;
    private NonRedundantSetAlg nonRedundantAlg;
    private EmbeddingQuerySeqAlg embeddingQueryAlg;
    private ChemBaseSimilaritySearchAlg simSearchingAlg;
    private AllDescriptors featureExtractionAlg;
    private FilteringSubsetOptimization featureSelectionAlg;
    private AlgorithmFactory distFactory;
    private NetworkConstructionAlg networkAlg;
    private final WekaPCATransformer pcaTransformer;

    //Algorithm workflow
    private Algorithm currentAlg;

    public MapperAlgorithm(MapperAlgorithmFactory factory) {
        this.factory = factory;
        networkModel = "Network";
        
        //Mapping Options    
        inputOption = InputSequenceOption.CURRENT_WORKSPACE;
        nrdOption = RemovingRedundantOption.YES;
        feOption = FeatureExtractionOption.YES;
        fsOption = FeatureSelectionOption.YES;
        mdOption = MD_OUTPUT_OPTION.Z_SCORE;

        //Mapping algorithms
        embeddingInputAlg = (EmbeddingInputSeqAlg) new EmbeddingInputSeqFactory().createAlgorithm();
        nonRedundantAlg = (NonRedundantSetAlg) new NonRedundantSetAlgFactory().createAlgorithm();
        embeddingQueryAlg = (EmbeddingQuerySeqAlg) new EmbeddingQuerySeqFactory().createAlgorithm();
        simSearchingAlg = (ChemBaseSimilaritySearchAlg) new ChemMultiSimilaritySearchFactory().createAlgorithm();
        featureExtractionAlg = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureSelectionAlg = (FilteringSubsetOptimization) new FilteringSubsetOptimizationFactory().createAlgorithm();
        pcaTransformer = (WekaPCATransformer) new WekaPCATransformerFactory().createAlgorithm();
        networkAlg = (NetworkConstructionAlg) new HSPNetworkConstructionFactory().createAlgorithm();
        distFactory = new EuclideanFactory();
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        stopRun = false;
    }

    @Override
    public void run() {
        if (!stopRun) {
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
//            if (searchingOption == SimilaritySearchingOption.YES && !stopRun) {
//                pc.reportMsg("Embedding query sequences", workspace);
//                if (embeddingQueryAlg != null) {
//                    currentAlg = embeddingQueryAlg;
//                    execute();
//                } else {
//                    throw new RuntimeException("Internal error: Embedding query algorithm is null");
//                }
//            }

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

            // Set peptide index attribute
            AttributesModel attrModel = pc.getAttributesModel(workspace);
            List<Peptide> peptides = attrModel.getPeptides();
            int index = 0;
            for (Peptide peptide : peptides) {
                peptide.setAttributeValue(INDEX_ATTR, index++);
            }

            // Load all descriptors
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            if (!stopRun) {
                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                        allFeatures.add(attr);
                    }
                }
            }

            // Preprocessing all features. Computing max, min, mean and std            
            if (!stopRun) {
                pc.reportMsg("Preprocessing of features. Computing max, min, mean and std", workspace);
                try {
                    MolecularDescriptor.preprocessing(allFeatures, peptides);
                } catch (MolecularDescriptorException ex) {
                    DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                    pc.reportError(ex.getMessage(), workspace);
                    cancel();
                }
            }

            //Create descriptor matrix            
            double[][] descriptorMatrix = null;
            if (!stopRun) {
                pc.reportMsg("Descriptor matrix construction", workspace);
                descriptorMatrix = new double[peptides.size()][allFeatures.size()];
                try {
                    int i, j;
                    i = 0;
                    for (Peptide peptide : peptides) {
                        j = 0;
                        for (MolecularDescriptor md : allFeatures) {
                            descriptorMatrix[i][j] = normalizedValue(peptide, md);
                            j++;
                        }
                        i++;
                    }
                } catch (MolecularDescriptorNotFoundException ex) {
                        DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                        Exceptions.printStackTrace(ex);
                        cancel();
                }
            }

            // Chemical similarity searching
//        pc.reportMsg(String.format("Chemical similarity searching: %s", searchingOption), workspace);
//        if (searchingOption == SimilaritySearchingOption.YES && !stopRun) {
//            if (simSearchingAlg != null) {
//                if (distFactory != null) {
//                    pc.reportMsg(String.format("Distance Function: %s", distFactory.getID()), workspace);
//                    AbstractDistance distFunction = (AbstractDistance)distFactory.createAlgorithm();
//                    simSearchingAlg.setDistanceFunction(distFunction);
//                    currentAlg = simSearchingAlg;
//                    execute();
//                } else {
//                    throw new RuntimeException("Internal error: Distance factory is null");
//                }
//            } else {
//                throw new RuntimeException("Internal error: Similarity searching algorithm is null");
//            }
//        }


            //WekaPCA Transformer            
            if (!stopRun) {
                pc.reportMsg("Applying PCA transformation", workspace);
                pcaTransformer.setOption(mdOption);
                currentAlg = pcaTransformer;
                execute();
            }

            // Network construction            
            if (!stopRun) {
                pc.reportMsg("Network construction", workspace);
                if (distFactory != null) {
                    pc.reportMsg(String.format("Distance Function: %s", distFactory.getName()), workspace);
                    networkAlg.setDistanceFactory(distFactory);
                    networkAlg.setDescriptorMatrix(descriptorMatrix);
                    networkAlg.setXyzSpace(pcaTransformer.getXYZSpace());
                    currentAlg = networkAlg;
                    execute();
                    networkModel = networkAlg.getFactory().getName();
                    networkAlg.setDescriptorMatrix(null);
                } else {
                    throw new RuntimeException("Internal error: Distance function is null");
                }
            }
        }
    }

    protected double normalizedValue(Peptide peptide, MolecularDescriptor attr) throws MolecularDescriptorNotFoundException {
        switch (mdOption) {
            case Z_SCORE:
                return attr.getNormalizedZscoreValue(peptide);
            case MIN_MAX:
                return attr.getNormalizedMinMaxValue(peptide);
        }
        throw new IllegalArgumentException("Unknown value for normalization index: " + mdOption);
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

    public String getNetworkType() {
        return networkModel;
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

    public MD_OUTPUT_OPTION getMdOption() {
        return mdOption;
    }

    public void setMdOption(MD_OUTPUT_OPTION option) {
        this.mdOption = option;
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

    public void setFeatureSelectionAlg(FilteringSubsetOptimization alg) {
        this.featureSelectionAlg = alg;
    }

    public FilteringSubsetOptimization getFeatureSelectionAlg() {
        return featureSelectionAlg;
    }

    public WekaPCATransformer getPCATransformer() {
        return pcaTransformer;
    }

    public NetworkConstructionAlg getNetworkAlg() {
        return networkAlg;
    }

    public void setNetworkAlg(NetworkConstructionAlg alg) {
        this.networkAlg = alg;
    }

    public AlgorithmFactory getDistanceFactory() {
        return distFactory;
    }

    public void setDistanceFactory(AlgorithmFactory distFactory) {
        this.distFactory = distFactory;
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
