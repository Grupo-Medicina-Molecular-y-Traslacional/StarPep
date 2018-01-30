/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import org.bapedis.network.model.SeqClusteringModel;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
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
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.network.model.Cluster;
import org.bapedis.network.model.MDOptionModel;
import org.bapedis.network.model.SimilarityMatrixModel;
import org.bapedis.network.spi.SimilarityMeasure;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class CSNAlgorithm implements Algorithm {

    private CSNAlgorithmFactory factory;
    private final SeqClusteringModel clusteringModel;
    private final MDOptionModel mdOptionModel;
    private final AllDescriptors descriptorAlgo;
    private final FeatureSelectionAlgo featureSelectionAlgo;
    private SimilarityMeasure simMeasure;
    private int thresholdPercent;

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected final NotifyDescriptor emptyKeys, uselessFeatureWarning, notEnoughFeatures;
    public static final int MIN_AVAILABLE_FEATURES = 2;

    protected Workspace workspace;
    protected AttributesModel attrModel;
    protected Peptide[] peptides;
    protected GraphModel graphModel;
    protected Graph graph;
    protected ProgressTicket ticket;
    protected boolean stopRun;

    public CSNAlgorithm(CSNAlgorithmFactory factory) {
        this.factory = factory;
        clusteringModel = new SeqClusteringModel();
        mdOptionModel = new MDOptionModel(pc.getAttributesModel().getMolecularDescriptorKeys());
        descriptorAlgo = (AllDescriptors) new AllDescriptorsFactory().createAlgorithm();
        featureSelectionAlgo = (FeatureSelectionAlgo) new FeatureSelectionFactory().createAlgorithm();
        thresholdPercent = 70;

        emptyKeys = new NotifyDescriptor.Message(NbBundle.getMessage(CSNAlgorithm.class, "ChemicalSpaceNetwork.emptyKeys.info"), NotifyDescriptor.ERROR_MESSAGE);
        notEnoughFeatures = new NotifyDescriptor.Message(NbBundle.getMessage(CSNAlgorithm.class, "ChemicalSpaceNetwork.features.notEnough", MIN_AVAILABLE_FEATURES), NotifyDescriptor.ERROR_MESSAGE);
        uselessFeatureWarning = new NotifyDescriptor.Message(NbBundle.getMessage(CSNAlgorithm.class, "ChemicalSpaceNetwork.uselessFeature.warning"), NotifyDescriptor.WARNING_MESSAGE);

    }

    public SeqClusteringModel getSeqClustering() {
        return clusteringModel;
    }

    public MDOptionModel getMdOptionModel() {
        return mdOptionModel;
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

    public int getThresholdPercent() {
        return thresholdPercent;
    }

    public void setThresholdPercent(int thresholdPercent) {
        this.thresholdPercent = thresholdPercent;
    }

    @Override
    public void initAlgo(Workspace workspace) {
        stopRun = false;
        this.workspace = workspace;
        attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            graphModel = pc.getGraphModel(workspace);
            graph = graphModel.getGraphVisible();

            // Setup Similarity Matrix Builder
            SimilarityMatrixkBuilder.setStopRun(stopRun);

            // Remove all similarity edges..
            int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
            Graph mainGraph = graphModel.getGraph();
            mainGraph.writeLock();
            try {
                for (Node node : mainGraph.getNodes()) {
                    mainGraph.clearEdges(node, relType);
                }
            } finally {
                mainGraph.writeUnlock();
            }
        }

    }

    @Override
    public void endAlgo() {
        workspace = null;
        peptides = null;
        attrModel = null;
        graphModel = null;
        graph = null;
        ticket = null;
        SimilarityMatrixkBuilder.setContext(null, null, null);
    }

    @Override
    public boolean cancel() {
        stopRun = true;
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
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.ticket = progressTicket;
    }

    @Override
    public void run() {
        if (peptides != null) {
            Peptide[] representatives;
            
            //Get representative peptides
            if (clusteringModel.isClustering()) {
                ticket.progress(NbBundle.getMessage(CSNAlgorithm.class, "CSNAlgorithm.task.clusterize"));
                SeqClusterBuilder clusterBuilder = new SeqClusterBuilder(clusteringModel);
                clusterBuilder.setProgressTicket(ticket);
                List<Cluster> clusters = clusterBuilder.clusterize(peptides);
                representatives = new Peptide[clusters.size()];
                int pos = 0;
                for (Cluster c : clusters) {
                    representatives[pos++] = c.getCentroid();
                }
            } else {
                representatives = peptides;
            }

            // Compute molecular descriptors if needed            
            Set<String> descriptorKeys = null;
            if (mdOptionModel.getOptionIndex() == MDOptionModel.AVAILABLE_MD) {
                descriptorKeys = mdOptionModel.getDescriptorKeys();
            } else if (mdOptionModel.getOptionIndex() == MDOptionModel.NEW_MD) {
                descriptorAlgo.initAlgo(workspace);
                descriptorAlgo.setProgressTicket(ticket);
                descriptorAlgo.run();
                descriptorKeys = descriptorAlgo.getDescriptorKeys();
                descriptorAlgo.endAlgo();
            }

            // Validate descriptor keys
            if (descriptorKeys.isEmpty()) {
                DialogDisplayer.getDefault().notify(emptyKeys);
                pc.reportError("There is no molecular descriptor selected", workspace);
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

            //Feature selection
            featureSelectionAlgo.initAlgo(workspace);
            featureSelectionAlgo.run();
            featureSelectionAlgo.endAlgo();

            //Populate feature list
            List<MolecularDescriptor> featureList = new LinkedList<>();
            for (String key : descriptorKeys) {
                for (MolecularDescriptor desc : attrModel.getMolecularDescriptors(key)) {
                    featureList.add(desc);
                }
            }

            try {
                //Preprocess feature list
                // Compute max, min, mean and std
                for (MolecularDescriptor attr : featureList) {
                    attr.resetSummaryStats(Arrays.asList(representatives));
                }
                // Remove constant attributes
                List<MolecularDescriptor> toRemove = new LinkedList<>();
                for (MolecularDescriptor attr : featureList) {
                    if (attr.getMax() == attr.getMin()) {
                        toRemove.add(attr);
                    }
                }
                if (toRemove.size() > 0) {
                    DialogDisplayer.getDefault().notify(uselessFeatureWarning);
                    pc.reportMsg("Some molecular features remain constant for all peptides and they will be ignored.", workspace);
                }
                featureList.removeAll(toRemove);
                for (MolecularDescriptor descriptor : toRemove) {
                    pc.reportMsg("Ignored: " + descriptor.getDisplayName(), workspace);
                }

                //Check feature list size
                if (featureList.size() < MIN_AVAILABLE_FEATURES) {
                    DialogDisplayer.getDefault().notify(notEnoughFeatures);
                    pc.reportError("There is not enough number of available molecular features", workspace);
                    cancel();
                }
            } catch (MolecularDescriptorNotFoundException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorND());
                pc.reportError(ex.getMessage(), workspace);
                cancel();
            }

            //Output details
            if (!stopRun) {
                Map<String, List<MolecularDescriptor>> byCategory = featureList.parallelStream()
                        .collect(Collectors.groupingBy(MolecularDescriptor::getCategory));

                int maxKeyLength = 0;
                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    if (key.length() > maxKeyLength) {
                        maxKeyLength = key.length();
                    }
                }
                StringBuilder msg;
                for (String key : attrModel.getMolecularDescriptorKeys()) {
                    msg = new StringBuilder(key);
                    for (int i = key.length() + 1; i <= maxKeyLength; i++) {
                        msg.append(' ');
                    }
                    msg.append(" : ");
                    msg.append(byCategory.containsKey(key) ? byCategory.get(key).size() : 0);
                    pc.reportMsg(msg.toString(), workspace);
                }
                pc.reportMsg("\nTotal of available molecular features: " + featureList.size(), workspace);

//            pc.reportMsg("\nSimilarity Metric: " + similarityMetrics[metricIndex], workspace);
//            pc.reportMsg("Normalization: " + normalization[normalizationIndex], workspace);
            }

            //Set feature list
            simMeasure.setMolecularDescriptors(featureList);

            // Delete old similarity matrix
            SimilarityMatrixModel matrix = workspace.getLookup().lookup(SimilarityMatrixModel.class);
            if (matrix != null) {
                workspace.remove(matrix);
            }

            // Build new similarity matrix
            SimilarityMatrixkBuilder.setContext(representatives, ticket, simMeasure);
            SimilarityMatrixkBuilder task = new SimilarityMatrixkBuilder();
            int workunits = task.getWorkUnits();
            ticket.switchToDeterminate(workunits);
            fjPool.invoke(task);
            task.join();

            // Add the new similarity matrix to workspace
            matrix = task.getSimilarityMatrix();
            workspace.add(matrix);

            // Add similarity edge to graph
            Edge graphEdge;
            Float score;
            graph.writeLock();
            try {
                for (int i = 0; i < representatives.length - 1; i++) {
                    for (int j = i + 1; j < representatives.length; j++) {
                        score = matrix.getValue(representatives[i], representatives[j]);
                        if (score != null && score >= 0.3) {
                            graphEdge = createGraphEdge(representatives[i], representatives[j], score);
                            graph.addEdge(graphEdge);
                        }
                    }
                }
            } finally {
                graph.writeUnlock();
            }
        }

    }

    private Edge createGraphEdge(Peptide peptide1, Peptide peptide2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        String id = String.format("%s-%s", peptide1.getId(), peptide2.getId());

        // Create Edge
        Edge graphEdge = graphModel.factory().newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        graphModel.getGraph().addEdge(graphEdge);
        graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);

        return graphEdge;
    }

}
