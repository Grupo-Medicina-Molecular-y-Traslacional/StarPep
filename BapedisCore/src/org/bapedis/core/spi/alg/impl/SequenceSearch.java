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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.QueryModel;
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
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author loge
 */
public class SequenceSearch implements Algorithm {

    private final ProjectManager pc;
    private ProteinSequence query;
    private AttributesModel newAttrModel;
    private List<Node> graphNodes;
    private Workspace workspace;
    private boolean stopRun;
    private final SequenceAlignmentModel alignmentModel;
    private final SequenceSearchFactory factory;
    protected static final int MAX_REJECTS = 16;
    protected int maximumResults;
    protected final PeptideDAO dao;
    protected final GraphWindowController graphWC;
    protected final NotifyDescriptor emptyQuery;

    public SequenceSearch(SequenceSearchFactory factory) {
        this.factory = factory;
        alignmentModel = new SequenceAlignmentModel();
        int percentIdentity = NbPreferences.forModule(SequenceSearch.class).getInt("PercentIdentity", 70);
        alignmentModel.setPercentIdentity(percentIdentity);
        
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        maximumResults = -1;
        dao = Lookup.getDefault().lookup(PeptideDAO.class);
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        emptyQuery = new NotifyDescriptor.Message(NbBundle.getMessage(SequenceSearch.class, "SequenceSearch.emptyQuery.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public SequenceAlignmentModel getAlignmentModel() {
        return alignmentModel;
    }

    public int getMaximumResults() {
        return maximumResults;
    }

    public void setMaximumResults(int maximumResults) {
        this.maximumResults = maximumResults;
    }

    public ProteinSequence getQuery() {
        return query;
    }

    public void setQuery(ProteinSequence query) {
        this.query = query;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        stopRun = false;
        newAttrModel = null;
        graphNodes = null;
        if(query == null){
            DialogDisplayer.getDefault().notify(emptyQuery);
            cancel();
        }        
    }

    @Override
    public void endAlgo() {
        // Set new Model
        if (newAttrModel != null && graphNodes != null && !stopRun) {
            // To refresh graph view
            GraphModel graphModel = pc.getGraphModel(workspace);
            Graph graph = graphModel.getGraphVisible();
            graph.clear();
            graphWC.refreshGraphView(workspace, graphNodes, null);

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
                NbPreferences.forModule(SequenceSearch.class).putInt("PercentIdentity", alignmentModel.getPercentIdentity());
            } catch (InterruptedException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        workspace = null;
        graphNodes = null;
        newAttrModel = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
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
        if (query != null) {
            AttributesModel tmpAttrModel = dao.getPeptides(new QueryModel(workspace), pc.getGraphModel(workspace), pc.getAttributesModel(workspace));
            if (!stopRun) {
                List<Peptide> resultList = new LinkedList<>();
                Peptide[] targets = tmpAttrModel.getPeptides().toArray(new Peptide[0]);
                
                // Sort by decreasing common words
                Arrays.parallelSort(targets, new CommonKMersComparator(query.getSequenceAsString()));

                // Assign peptide from targets to result list
                // Stop if max rejects ocurred
                float identityScore = alignmentModel.getIndentityScore();
                float score;
                int rejections = 0;
                TreeSet<SequenceHit> hits = new TreeSet<>();
                for (int i = 0; i < targets.length && !stopRun && rejections < MAX_REJECTS; i++) {
                    try {
                        score = PairwiseSequenceAlignment.computeSequenceIdentity(query, targets[i].getBiojavaSeq(), alignmentModel);
                        if (score >= identityScore) {
                            hits.add(new SequenceHit(targets[i], score));
                        } else {
                            rejections++;
                        }
                    } catch (CompoundNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }

                SequenceHit hit;
                for (Iterator<SequenceHit> it = hits.descendingIterator(); it.hasNext()
                        && (maximumResults == -1 || resultList.size() < maximumResults);) {
                    hit = it.next();
                    resultList.add(hit.getPeptide());
                }

                //New model                 
                newAttrModel = new AttributesModel(workspace);
                tmpAttrModel.getBridge().copyTo(newAttrModel, null);
                
                graphNodes = new LinkedList<>();
                for (Peptide peptide : resultList) {
                    newAttrModel.addPeptide(peptide);
                    graphNodes.add(peptide.getGraphNode());
                }
            }
        }
    }

    private static class SequenceHit implements Comparable<SequenceHit> {

        private final Peptide peptide;
        private final float score;

        public SequenceHit(Peptide peptide, float score) {
            this.peptide = peptide;
            this.score = score;
        }

        public Peptide getPeptide() {
            return peptide;
        }

        public float getScore() {
            return score;
        }

        @Override
        public int compareTo(SequenceHit other) {
            float diff = getScore() - other.getScore();
            if (diff > 0) {
                return 1;
            }
            return -1;
        }
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
