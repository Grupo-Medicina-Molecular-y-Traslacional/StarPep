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
import static org.bapedis.core.model.PeptideAttribute.SIMILARITY_RANK_ATTR;
import static org.bapedis.core.model.PeptideAttribute.SIMILARITY_SCORE_ATTR;
import org.openide.util.NbBundle;

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

        double identityScore = alignmentModel.getIndentityScore();
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

        pc.reportMsg("Alignment type: " + SequenceAlignmentModel.ALIGNMENT_TYPE[alignmentModel.getAlignmentTypeIndex()], workspace);
        pc.reportMsg("Substitution matrix: " + SequenceAlignmentModel.SUBSTITUTION_MATRIX[alignmentModel.getSubstitutionMatrixIndex()], workspace);
        pc.reportMsg(NbBundle.getMessage(BaseSequenceSearchAlg.class, "BaseSequenceSearchAlg.pid.text", alignmentModel.getPercentIdentity()), workspace);
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

            newAttrModel.addDisplayedColumn(PeptideAttribute.SIMILARITY_RANK_ATTR);
            newAttrModel.addDisplayedColumn(PeptideAttribute.SIMILARITY_SCORE_ATTR);

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
            if (!nodeTable.hasColumn(SIMILARITY_RANK_ATTR.getId())) {
                nodeTable.addColumn(SIMILARITY_RANK_ATTR.getId(), SIMILARITY_RANK_ATTR.getDisplayName(), SIMILARITY_RANK_ATTR.getType(), Origin.DATA, SIMILARITY_RANK_ATTR.getDefaultValue(), true);
                fireEvent = true;
            }
            if (!nodeTable.hasColumn(SIMILARITY_SCORE_ATTR.getId())) {
                nodeTable.addColumn(SIMILARITY_SCORE_ATTR.getId(), SIMILARITY_SCORE_ATTR.getDisplayName(), SIMILARITY_SCORE_ATTR.getType(), Origin.DATA, SIMILARITY_SCORE_ATTR.getDefaultValue(), true);
                fireEvent = true;
            }

            //Set default values            
            for (Peptide p : newAttrModel.getPeptideMap().values()) {
                p.setAttributeValue(SIMILARITY_RANK_ATTR, SIMILARITY_RANK_ATTR.getDefaultValue());
                p.setAttributeValue(SIMILARITY_SCORE_ATTR, SIMILARITY_SCORE_ATTR.getDefaultValue());
            }
            for (Node node : graphModel.getGraph().getNodes()) {
                node.setAttribute(SIMILARITY_RANK_ATTR.getId(), SIMILARITY_RANK_ATTR.getDefaultValue());
                node.setAttribute(SIMILARITY_SCORE_ATTR.getId(), SIMILARITY_SCORE_ATTR.getDefaultValue());
            }
            //Set values
            Node graphNode;
            int rank = 1;
            for (PeptideHit hit : results) {
                peptide = hit.getPeptide();
                peptide.setAttributeValue(SIMILARITY_RANK_ATTR, rank);
                peptide.setAttributeValue(SIMILARITY_SCORE_ATTR, hit.getScore());

                graphNode = peptide.getGraphNode();
                graphNode.setAttribute(SIMILARITY_RANK_ATTR.getId(), rank);
                graphNode.setAttribute(SIMILARITY_SCORE_ATTR.getId(), hit.getScore());
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
