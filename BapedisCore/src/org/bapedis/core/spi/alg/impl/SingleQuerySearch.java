/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class SingleQuerySearch extends BaseSequenceSearchAlg {

    private ProteinSequence query;
    protected final NotifyDescriptor emptyQuery;

    public SingleQuerySearch(SingleQuerySearchFactory factory) {
        super(factory);
        emptyQuery = new NotifyDescriptor.Message(NbBundle.getMessage(SingleQuerySearch.class, "SingleQuerySearch.emptyQuery.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public ProteinSequence getQuery() {
        return query;
    }

    public void setQuery(ProteinSequence query) {
        this.query = query;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        if (query == null) {
            DialogDisplayer.getDefault().notify(emptyQuery);
            cancel();
        }
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
                double score;
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

}
