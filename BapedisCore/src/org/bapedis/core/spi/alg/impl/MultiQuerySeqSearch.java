/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideHit;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.MultiQuery;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.util.FASTASEQ;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class MultiQuerySeqSearch extends BaseSequenceSearchAlg implements MultiQuery {

    protected String fasta;
    protected final NotifyDescriptor emptyQuery;

    public MultiQuerySeqSearch(MultiQuerySeqSearchFactory factory) {
        super(factory);
        emptyQuery = new NotifyDescriptor.Message(NbBundle.getMessage(SingleQuerySeqSearch.class, "MultiQuerySeqSearch.emptyQuery.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    public String getFasta() {
        return fasta;
    }

    @Override
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
                AttributesModel tmpAttrModel;
                if (workspaceInput) {
                    tmpAttrModel = pc.getAttributesModel(workspace);
                } else {
                    tmpAttrModel = dao.getPeptides(new QueryModel(workspace), pc.getGraphModel(workspace), pc.getAttributesModel(workspace));
                }
                HashMap<Integer, PeptideHit> mapResult = new HashMap<>();
                Peptide[] targets = tmpAttrModel.getPeptides().toArray(new Peptide[0]);

                List<PeptideHit> hits;
                for (ProteinSequence query : queries) {
                    if (!stopRun) {
                        //Searching
                        hits = searchSimilarTo(targets, query);

                        //Data fusion
                        Peptide peptide;
                        for (PeptideHit hit : hits) {
                            peptide = hit.getPeptide();
                            if (!mapResult.containsKey(peptide.getId())
                                    || hit.getScore() > mapResult.get(peptide.getId()).getScore()) {
                                mapResult.put(peptide.getId(), hit);
                            }
                        }

                    }
                }

                // Sort results
                results = mapResult.values().toArray(new PeptideHit[0]);
                Arrays.parallelSort(results, Collections.reverseOrder());

                //New model                 
                newAttrModel = new AttributesModel(workspace);
                tmpAttrModel.getBridge().copyTo(newAttrModel, null);
                graphNodes = new LinkedList<>();
            }
        }
    }
}
