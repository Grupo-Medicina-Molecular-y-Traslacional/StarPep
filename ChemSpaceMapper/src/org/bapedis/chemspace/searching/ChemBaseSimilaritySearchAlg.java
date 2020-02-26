/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import javax.swing.SwingUtilities;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.model.CandidatePeptide;
import org.bapedis.core.model.PeptideHit;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Loge
 */
public abstract class ChemBaseSimilaritySearchAlg implements Algorithm, Cloneable {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);

    protected final AlgorithmFactory factory;
//    protected SimilaritySearchingModel searchingModel;
    protected GraphModel graphModel;
    protected Workspace workspace;
    protected ProgressTicket progressTicket;
    protected List<Node> graphNodes;
    protected List<Peptide> targetList, queryList;
    protected AttributesModel newAttrModel;
    protected boolean stopRun;
    protected AbstractDistance distFunc;
    protected double[][] descriptorMatrix;    

    public ChemBaseSimilaritySearchAlg(AlgorithmFactory factory) {
        this.factory = factory;
//        searchingModel = new SimilaritySearchingModel();

    }
    
    public double[][] getDescriptorMatrix() {
        return descriptorMatrix;
    }

    public void setDescriptorMatrix(double[][] descriptorMatrix) {
        this.descriptorMatrix = descriptorMatrix;
    }    


    public AbstractDistance getDistanceFunction() {
        return distFunc;
    }

    public void setDistanceFunction(AbstractDistance distFunc) {
        this.distFunc = distFunc;
    }

    protected List<PeptideHit> searchSimilarTo(Peptide query) {
        List<PeptideHit> resultList = new LinkedList<>();
        //Searching in target list
        CandidatePeptide[] candidates = new CandidatePeptide[targetList.size()];
        int cursor = 0;
        for (Peptide peptide : targetList) {
            distFunc.setContext(query, peptide, descriptorMatrix);
            distFunc.run();
            candidates[cursor++] = new CandidatePeptide(peptide, distFunc.getDistance());
        }
        //Sort candidates
        Arrays.parallelSort(candidates);

        double maxDistance = candidates[candidates.length - 1].getDistance();
        int topK = -1;
        double threshold = -1;
        double similarity;
//        switch (searchingModel.getOption()) {
//            case TOP_RANK_PERCENT_OPTION:
//                topK = (int) Math.round(((double) searchingModel.getTopPercentValue()) * candidates.length / 100);
//                break;
//            case TOP_RANK_VALUE_OPTION:
//                topK = searchingModel.getTopRank();
//                break;
//            case SIMILARITY_THRESHOD_VALUE_OPTION:
//                threshold = searchingModel.getThreshold();
//                break;
//            case SIMILARITY_THRESHOD_PERCENT_OPTION:
//                similarity = 1.0 - candidates[0].getDistance() / maxDistance;
//                threshold = searchingModel.getThresholdPercentValue() * similarity / 100;
//                break;
//        }

        boolean flag = true;
        for (int i = 0; i < candidates.length && flag; i++) {
            similarity = 1.0 - candidates[i].getDistance() / maxDistance;
            if ((threshold != -1 && similarity >= threshold)
                    || (topK != -1 && i < topK)) {
                resultList.add(new PeptideHit(candidates[i].getPeptide(), similarity));
            } else {
                flag = false;
            }
        }
        return resultList;
    }

    protected void dataFusion(List<PeptideHit> resultList, HashMap<String, PeptideHit> mapResult) {
        Peptide peptide;
        for (PeptideHit hit : resultList) {
            peptide = hit.getPeptide();
            if (!mapResult.containsKey(peptide.getID())
                    || hit.getScore() > mapResult.get(peptide.getID()).getScore()) {
                mapResult.put(peptide.getID(), hit);
            }
        }
    }

    protected void saveResults(HashMap<String, PeptideHit> mapResult) {
        PeptideHit[] results = mapResult.values().toArray(new PeptideHit[0]);
        Arrays.parallelSort(results, Collections.reverseOrder());
        int topK = -1;
        double threshold = -1;
        double similarity;
//        switch (searchingModel.getOption()) {
//            case TOP_RANK_PERCENT_OPTION:
//                topK = (int) Math.round(((double) searchingModel.getTopPercentValue()) * results.length / 100);
//                break;
//            case TOP_RANK_VALUE_OPTION:
//                topK = searchingModel.getTopRank();
//                break;
//            case SIMILARITY_THRESHOD_VALUE_OPTION:
//                threshold = searchingModel.getThreshold();
//                break;
//            case SIMILARITY_THRESHOD_PERCENT_OPTION:
//                similarity = results[0].getScore();
//                threshold = searchingModel.getThresholdPercentValue() * similarity / 100;
//                break;
//        }

        Peptide peptide;
        boolean flag = true;
        for (int i = 0; i < results.length && flag; i++) {
            similarity = results[i].getScore();
            if ((threshold != -1 && similarity >= threshold)
                    || (topK != -1 && i < topK)) {
                peptide = results[i].getPeptide();
                newAttrModel.addPeptide(peptide);
                graphNodes.add(peptide.getGraphNode());
            } else {
                flag = false;
            }
        }
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.progressTicket = progressTicket;
        graphModel = pc.getGraphModel(workspace);

        //Create target and query list
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        targetList = new LinkedList<>();
        queryList = new LinkedList<>();

        if (attrModel != null) {
            for (Peptide peptide : attrModel.getPeptides()) {
                if (peptide.getGraphNode().getLabel().equals(EmbeddingQuerySeqAlg.QUERY_LABEL)) {
                    queryList.add(peptide);
                } else {
                    targetList.add(peptide);
                }
            }
        }

        //Create new workspace
        newAttrModel = new AttributesModel(workspace);
        graphNodes = new LinkedList<>();
        stopRun = false;
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
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public void endAlgo() {
        if (newAttrModel != null && !stopRun) {
            // To refresh graph view
            Graph graph = graphModel.getGraphVisible();
            graph.clear();
            graphWC.refreshGraphView(workspace, graphNodes, null);

            final Workspace ws = workspace;
            final AttributesModel modelToRemove = pc.getAttributesModel(workspace);
            final AttributesModel modelToAdd = newAttrModel;
            modelToRemove.getBridge().copyTo(modelToAdd, null);
            try {
                SwingUtilities.invokeAndWait(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //To change attribute model
                            ws.remove(modelToRemove);
                            ws.add(modelToAdd);
                        } finally {
                            pc.getGraphVizSetting(ws).fireChangedGraphView();
                        }
                    }
                });
            } catch (InterruptedException | InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        workspace = null;
        progressTicket = null;
        graphModel = null;
        newAttrModel = null;
        graphNodes = null;
        targetList = null;
        queryList = null;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ChemBaseSimilaritySearchAlg copy = (ChemBaseSimilaritySearchAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.
//        copy.searchingModel = (SimilaritySearchingModel) this.searchingModel.clone();
        return copy;
    }
}
