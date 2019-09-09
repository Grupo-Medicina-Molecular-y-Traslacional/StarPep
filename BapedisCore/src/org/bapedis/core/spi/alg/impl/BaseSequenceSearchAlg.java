/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import static org.bapedis.core.model.PeptideAttribute.RANK_ATTR;
import static org.bapedis.core.model.PeptideAttribute.SCORE_ATTR;
import org.bapedis.core.model.PeptideHit;
import org.bapedis.core.model.SequenceAlignmentModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Origin;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public abstract class BaseSequenceSearchAlg implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final SequenceAlignmentModel alignmentModel;
    protected final AlgorithmFactory factory;

    protected static final int MAX_REJECTS = 16;
    protected final PeptideDAO dao;
    protected final GraphWindowController graphWC;

    protected AttributesModel newAttrModel;
    protected List<Node> graphNodes;
    protected Workspace workspace;
    protected boolean stopRun, workspaceInput;
    protected PeptideHit[] results;

    public BaseSequenceSearchAlg(AlgorithmFactory factory) {
        this.factory = factory;
        alignmentModel = new SequenceAlignmentModel();

//        int percentIdentity = NbPreferences.forModule(SequenceSearch.class).getInt("PercentIdentity", 70);
        alignmentModel.setPercentIdentity(70);
        dao = Lookup.getDefault().lookup(PeptideDAO.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        workspaceInput = false;
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public boolean isWorkspaceInput() {
        return workspaceInput;
    }

    public void setWorkspaceInput(boolean workspaceInput) {
        this.workspaceInput = workspaceInput;
    }

    protected List<PeptideHit> searchSimilarTo(Peptide[] targets, ProteinSequence query) {
        List<PeptideHit> hits = new LinkedList<>();
        // Sort by decreasing common words
        Arrays.parallelSort(targets, new CommonKMersComparator(query.getSequenceAsString()));

        float identityScore = alignmentModel.getIndentityScore();
        double score;
        int rejections = 0;  // Stop if max rejects ocurred        
        for (int i = 0; i < targets.length && !stopRun && rejections < MAX_REJECTS; i++) {
            try {
                score = PairwiseSequenceAlignment.computeSequenceIdentity(query, targets[i].getBiojavaSeq(), alignmentModel);
                if (score >= identityScore) {
                    hits.add(new PeptideHit(targets[i], score));
                } else {
                    rejections++;
                }
            } catch (CompoundNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return hits;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        stopRun = false;
        newAttrModel = null;
        graphNodes = null;
        results = null;
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
        // Set new Model
        if (newAttrModel != null && graphNodes != null && !stopRun) {

            newAttrModel.addDisplayedColumn(PeptideAttribute.RANK_ATTR);
            newAttrModel.addDisplayedColumn(PeptideAttribute.SCORE_ATTR);

            //Save results
            Peptide peptide;
            for (int i = 0; i < results.length; i++) {
                peptide = results[i].getPeptide();
                newAttrModel.addPeptide(peptide);
                graphNodes.add(peptide.getGraphNode());
            }

            // To refresh graph view
            GraphModel graphModel = pc.getGraphModel(workspace);
            Graph graph = graphModel.getGraphVisible();
            graph.clear();
            graphWC.refreshGraphView(workspace, graphNodes, null);

            boolean fireEvent = false;
            Table nodeTable = graphModel.getNodeTable();
            if (!nodeTable.hasColumn(RANK_ATTR.getId())) {
                nodeTable.addColumn(RANK_ATTR.getId(), RANK_ATTR.getDisplayName(), RANK_ATTR.getType(), Origin.DATA, RANK_ATTR.getDefaultValue(), true);
                fireEvent = true;
            }
            if (!nodeTable.hasColumn(SCORE_ATTR.getId())) {
                nodeTable.addColumn(SCORE_ATTR.getId(), SCORE_ATTR.getDisplayName(), SCORE_ATTR.getType(), Origin.DATA, SCORE_ATTR.getDefaultValue(), true);
                fireEvent = true;
            }

            //Set default values            
            for (Peptide p : newAttrModel.getPeptideMap().values()) {
                p.setAttributeValue(RANK_ATTR, RANK_ATTR.getDefaultValue());
                p.setAttributeValue(SCORE_ATTR, SCORE_ATTR.getDefaultValue());
            }
            for (Node node : graphModel.getGraph().getNodes()) {
                node.setAttribute(RANK_ATTR.getId(), RANK_ATTR.getDefaultValue());
                node.setAttribute(SCORE_ATTR.getId(), SCORE_ATTR.getDefaultValue());
            }
            //Set values
            Node graphNode;
            int rank = 1;
            for (PeptideHit hit : results) {
                peptide = hit.getPeptide();
                peptide.setAttributeValue(RANK_ATTR, rank);
                peptide.setAttributeValue(SCORE_ATTR, hit.getScore());

                graphNode = peptide.getGraphNode();
                graphNode.setAttribute(RANK_ATTR.getId(), rank);
                graphNode.setAttribute(SCORE_ATTR.getId(), hit.getScore());
                rank++;
            }

            final Workspace ws = workspace;
            final AttributesModel modelToRemove = pc.getAttributesModel(workspace);
            final AttributesModel modelToAdd = newAttrModel;
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
//                NbPreferences.forModule(SequenceSearch.class).putInt("PercentIdentity", alignmentModel.getPercentIdentity());
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        workspace = null;
        graphNodes = null;
        newAttrModel = null;
        results = null;
    }

}

class SeqLengthComparator implements Comparator<Peptide> {

    @Override
    public int compare(Peptide o1, Peptide o2) {
        return o2.getLength() - o1.getLength();
    }
}

class CommonKMersComparator implements Comparator<Peptide> {

    private final Set<String> set;
    private final int k;

    public CommonKMersComparator(String query) {
        k = Math.min(5, query.length());
        set = new HashSet<>();
        for (int i = 0; i <= query.length() - k; i++) {
            set.add(query.substring(i, i + k));
        }
    }

    @Override
    public int compare(Peptide o1, Peptide o2) {
        int c1 = countCommonWords(o1);
        int c2 = countCommonWords(o2);
        return c2 - c1;
    }

    private int countCommonWords(Peptide peptide) {
        String seq = peptide.getSequence();
        int count = 0;
        for (int i = 0; i <= seq.length() - k; i++) {
            if (set.contains(seq.substring(i, i + k))) {
                count++;
            }
        }
        return count;
    }

}
