/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.impl.AllDescriptors;
import org.bapedis.core.spi.algo.impl.AllDescriptorsFactory;
import org.bapedis.core.spi.algo.impl.FeatureSelectionAlgo;
import org.bapedis.core.spi.algo.impl.FeatureSelectionFactory;
import org.bapedis.core.spi.algo.impl.SequenceClustering;
import org.bapedis.core.spi.algo.impl.SequenceClusteringFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.network.model.WizardOptionModel;
import org.bapedis.network.model.SimilarityMatrix;
import org.bapedis.network.spi.SimilarityMeasure;
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
public class CSNAlgorithm implements Algorithm {

    public static final int MAX_NODES=100000;
    public static final int MAX_EDGES=600000;
    
    public static final int[] SIMILARITY_CUTOFF_REFS = new int[]{50, 70, 100};
    public static final int SIMILARITY_CUTOFF_MIN = 50;
    public static final int SIMILARITY_CUTOFF_MAX = 100;
    public static final int SIMILARITY_DEFAULT_VALUE = 70;
    public static final int SIMILARITY_MAJORTICKSPACING = 10;
    public static final int SIMILARITY_MINORTICKSPACING = 5;
    
    public static final String RUNNING = "running";

    private CSNAlgorithmFactory factory;
    private final SequenceClustering clusteringAlgo;
    private final WizardOptionModel optionModel;
    private final AllDescriptors descriptorAlgo;
    private final FeatureSelectionAlgo featureSelectionAlgo;
    private SimilarityMeasure simMeasure;
    private int cutoffValue;
    private SimilarityMatrix similarityMatrix;

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final PropertyChangeSupport propertyChangeSupport;

    protected final NotifyDescriptor emptyKeys, notEnoughFeatures;
    public static final int MIN_AVAILABLE_FEATURES = 2;

    protected Workspace workspace;
    protected AttributesModel attrModel;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun, running;

    public CSNAlgorithm(CSNAlgorithmFactory factory) {
        this.factory = factory;
        clusteringAlgo = (SequenceClustering) new SequenceClusteringFactory().createAlgorithm();
        optionModel = new WizardOptionModel();
        descriptorAlgo = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureSelectionAlgo = (FeatureSelectionAlgo) new FeatureSelectionFactory().createAlgorithm();
        cutoffValue = SIMILARITY_DEFAULT_VALUE;

        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(CSNAlgorithm.class, "ChemicalSpaceNetwork.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(CSNAlgorithm.class, "ChemicalSpaceNetwork.features.notEnough", MIN_AVAILABLE_FEATURES), NotifyDescriptor.ERROR_MESSAGE);
        propertyChangeSupport = new PropertyChangeSupport(this);
        running = false;
    }

    public SequenceClustering getSequenceClustering() {
        return clusteringAlgo;
    }

    public WizardOptionModel getMdOptionModel() {
        return optionModel;
    }

    public AllDescriptors getDescriptorAlgo() {
        return descriptorAlgo;
    }

    public FeatureSelectionAlgo getFeatureSelectionAlgo() {
        return featureSelectionAlgo;
    }

    public SimilarityMeasure getSimMeasure() {
        return simMeasure;
    }

    public void setSimMeasure(SimilarityMeasure simMeasure) {
        this.simMeasure = simMeasure;
    }

    public int getCutoffValue() {
        return cutoffValue;
    }

    public void setCutoffValue(int cutoffValue) {
        if (cutoffValue < SIMILARITY_CUTOFF_MIN || cutoffValue > SIMILARITY_CUTOFF_MAX) {
            throw new IllegalArgumentException("Invalid value for cutoff. It should be between " + SIMILARITY_CUTOFF_MIN + " and " + SIMILARITY_CUTOFF_MAX);
        }
        this.cutoffValue = cutoffValue;
    }

    public SimilarityMatrix getSimilarityMatrix() {
        return similarityMatrix;
    }

    public boolean isRunning() {
        return running;
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

            // Setup Similarity Matrix Builder
            SimilarityMatrixkBuilder.setStopRun(stopRun);                         
        }
        similarityMatrix = null;
        running = true;
        propertyChangeSupport.firePropertyChange(RUNNING, false, true);
    }

    @Override
    public void endAlgo() {
        workspace = null;
        attrModel = null;
        graphModel = null;
        graph = null;
        ticket = null;
        SimilarityMatrixkBuilder.setContext(null, null, null, null);
        
        if (stopRun){ // Cancelled
            similarityMatrix = null;
        }
        
        running = false;
        propertyChangeSupport.firePropertyChange(RUNNING, true, false);
    }

    @Override
    public boolean cancel() {
        stopRun = true;        
        clusteringAlgo.cancel();
        descriptorAlgo.cancel();
        featureSelectionAlgo.cancel();
        SimilarityMatrixkBuilder.setStopRun(stopRun);
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
    public void run() {
        if (attrModel != null) {
            //Step 1.
            Peptide[] peptides = null;
            List<Cluster> clusterList = null;
            if (!stopRun) {
                switch (optionModel.getInputSequenceOption()) {
                    case AVAILABLE:
                        peptides = attrModel.getPeptides().toArray(new Peptide[0]);
                        break;
                    case NEW:
                        clusterList = clusterize();
                        peptides = new Peptide[clusterList.size()];
                        int pos = 0;
                        for (Cluster c : clusterList) {
                            peptides[pos++] = c.getCentroid();
                        }
                        break;
                }
            }

            //Setp 2.
            Set<String> descriptorKeys = null;
            if (!stopRun) {
                switch (optionModel.getMolecularDescriptorOption()) {
                    case AVAILABLE:
                        descriptorKeys = attrModel.getMolecularDescriptorKeys();
                        break;
                    case NEW:
                        descriptorKeys = computeMD();
                        break;
                }
                // Validate descriptor keys
                validateMD(descriptorKeys);
            }

            // Step 3.
            List<MolecularDescriptor> featureList = new LinkedList<>();
            if (!stopRun) {
                filterFeatures();

                // Populate feature list                
                for (String key : descriptorKeys) {
                    for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                        featureList.add(desc);
                    }
                }

                // Preprocessing and validate molecular features
                preprocessing(featureList, peptides);
            }

            //Step 4. Compute similarity matrix
            if (!stopRun) {
                //Set feature list to similarity measure
                simMeasure.setMolecularDescriptors(featureList);

                // Compute new similarity matrix
                similarityMatrix = computeSimilarityMatrix(peptides, clusterList != null? clusterList.toArray(new Cluster[0]): null);                
            }
        }

    }

    private List<Cluster> clusterize() {
        String msg = NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.task.clusterize");
        pc.reportMsg(msg, workspace);
        ticket.progress(msg);
        
        clusteringAlgo.initAlgo(workspace, ticket);
        clusteringAlgo.run();
        clusteringAlgo.endAlgo();
        return clusteringAlgo.getClusterList();
    }

    private Set<String> computeMD() {
        String msg = NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.task.calculatingMD");
        pc.reportMsg(msg, workspace);
        ticket.progress(msg);

        descriptorAlgo.initAlgo(workspace, ticket);
        descriptorAlgo.run();
        Set<String> descriptorKeys = descriptorAlgo.getDescriptorKeys();
        descriptorAlgo.endAlgo();
        return descriptorKeys;
    }

    private void validateMD(Set<String> descriptorKeys) {
        if (descriptorKeys == null || descriptorKeys.isEmpty()) {
            DialogDisplayer.getDefault().notify(emptyKeys);
            pc.reportError("There is no molecular descriptors", workspace);
            cancel();
        } else {
            for (String key : descriptorKeys) {
                if (!attrModel.hasMolecularDescriptors(key)) {
                    NotifyDescriptor notFound = new NotifyDescriptor.Message(NbBundle.getMessage(CSNAlgorithm.class, "ChemicalSpaceNetwork.key.notFound", key), NotifyDescriptor.ERROR_MESSAGE);
                    DialogDisplayer.getDefault().notify(notFound);
                    pc.reportError("Value not found for molecular descriptor: " + key, workspace);
                    cancel();
                    break;
                }
            }
        }
    }

    private void filterFeatures() {
        String msg = NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.task.filtering");
        pc.reportMsg(msg, workspace);
        ticket.progress(msg);

        featureSelectionAlgo.initAlgo(workspace, ticket);
        featureSelectionAlgo.run();
        featureSelectionAlgo.endAlgo();
    }

    private void preprocessing(List<MolecularDescriptor> featureList, Peptide[] peptides) {
        // Check feature list size
        if (featureList.size() < MIN_AVAILABLE_FEATURES) {
            DialogDisplayer.getDefault().notify(notEnoughFeatures);
            pc.reportError("There is not enough number of available molecular features", workspace);
            cancel();
        }

        // try/catch for molecular not found exception handling
        try {
            String msg = NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.task.filtering");
            pc.reportMsg(msg, workspace);
            ticket.progress(msg);

            // Preprocessing of feature list. Compute max, min, mean and std
            for (MolecularDescriptor attr : featureList) {
                attr.resetSummaryStats(Arrays.asList(peptides));
            }

            // Validate molecular features
            for (MolecularDescriptor attr : featureList) {
                assert attr.getMax() != attr.getMin() : "Some molecular features remain constant for all peptides.";
            }

        } catch (MolecularDescriptorNotFoundException ex) {
            DialogDisplayer.getDefault().notify(ex.getErrorND());
            pc.reportError(ex.getMessage(), workspace);
            cancel();
        }

    }

    private SimilarityMatrix computeSimilarityMatrix(Peptide[] peptides, Cluster[] cluster) {
        String msg = NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.task.simMatrix");
        pc.reportMsg(msg, workspace);
        ticket.progress(msg);

        SimilarityMatrixkBuilder.setContext(cluster, peptides, ticket, simMeasure);
        SimilarityMatrixkBuilder task = new SimilarityMatrixkBuilder();
        int workunits = task.getWorkUnits();
        ticket.switchToDeterminate(workunits);
        fjPool.invoke(task);
        task.join();
        return task.getSimilarityMatrix();
    }
    
    public void addRunningListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removeRunningListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }    

}
