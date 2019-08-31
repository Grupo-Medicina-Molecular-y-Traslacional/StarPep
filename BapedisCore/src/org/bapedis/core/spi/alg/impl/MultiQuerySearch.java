/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.FASTASEQ;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.biojava.nbio.core.sequence.io.FastaWriterHelper;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class MultiQuerySearch extends BaseSequenceSearchAlg {

    protected String fasta;
    protected final NotifyDescriptor emptyQuery;

    public MultiQuerySearch(MultiQuerySearchFactory factory) {
        super(factory);
        emptyQuery = new NotifyDescriptor.Message(NbBundle.getMessage(SingleQuerySearch.class, "MultiQuerySearch.emptyQuery.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    public String getFasta() {
        return fasta;
    }

    public void setFasta(String fasta) {
        this.fasta = fasta;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        if (fasta == null || fasta.isEmpty()) {
            DialogDisplayer.getDefault().notify(emptyQuery);
            cancel();
        }
    }

    @Override
    public void run() {
        if (fasta != null) {
            List<ProteinSequence> queries = null;
            try {
                queries = FASTASEQ.load(fasta);
            } catch (Exception ex) {
                NotifyDescriptor nd = new NotifyDescriptor.Message(ex.getMessage(), NotifyDescriptor.ERROR_MESSAGE);
                DialogDisplayer.getDefault().notify(nd);
                cancel();
            }

            if (!stopRun && queries != null) {
                AttributesModel tmpAttrModel = dao.getPeptides(new QueryModel(workspace), pc.getGraphModel(workspace), pc.getAttributesModel(workspace));
                HashMap<Integer, SequenceHit> mapResult = new HashMap<>();
                Peptide[] targets = tmpAttrModel.getPeptides().toArray(new Peptide[0]);

                float identityScore = alignmentModel.getIndentityScore();
                TreeSet<SequenceHit> hits;
                double score;
                int rejections = 0;
                int count;
                for (ProteinSequence query : queries) {
                    if (!stopRun) {
                        // Sort by decreasing common words
                        Arrays.parallelSort(targets, new CommonKMersComparator(query.getSequenceAsString()));
                    }
                    hits = new TreeSet<>();
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

                    if (!stopRun) {
                        count = 0;
                        SequenceHit hit;
                        Peptide peptide;
                        for (Iterator<SequenceHit> it = hits.descendingIterator(); it.hasNext()
                                && (maximumResults == -1 || count < maximumResults);) {
                            hit = it.next();
                            peptide = hit.getPeptide();
                            //Data fusion
                            if (!mapResult.containsKey(peptide.getId())
                                    || hit.getScore() > mapResult.get(peptide.getId()).getScore()) {
                                mapResult.put(peptide.getId(), hit);
                            }
                            count++;
                        }
                    }
                }

                // Sort results
                SequenceHit[] results = mapResult.values().toArray(new SequenceHit[0]);
                Arrays.parallelSort(results, Collections.reverseOrder());

                //New model                 
                newAttrModel = new AttributesModel(workspace);
                tmpAttrModel.getBridge().copyTo(newAttrModel, null);

                graphNodes = new LinkedList<>();
                Peptide peptide;
                count = 0;
                for (SequenceHit hit : results) {
                    peptide = hit.getPeptide();
                    if (!newAttrModel.getPeptideMap().containsKey(peptide.getId())
                            && (maximumResults == -1 || count < maximumResults)) {
                        newAttrModel.addPeptide(peptide);
                        graphNodes.add(peptide.getGraphNode());
                        count++;
                    }
                }
            }
        }
    }

}
