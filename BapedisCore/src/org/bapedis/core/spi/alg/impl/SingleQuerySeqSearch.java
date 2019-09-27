/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideHit;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.SingleQuery;
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
public class SingleQuerySeqSearch extends BaseSequenceSearchAlg implements SingleQuery {

    private String querySeq;
    protected final NotifyDescriptor emptyQuery;

    public SingleQuerySeqSearch(SingleQuerySeqSearchFactory factory) {
        super(factory);
        emptyQuery = new NotifyDescriptor.Message(NbBundle.getMessage(SingleQuerySeqSearch.class, "SingleQuerySeqSearch.emptyQuery.info"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    public String getQuery() {
        return querySeq;
    }

    @Override
    public void setQuery(String querySeq) {
        this.querySeq = querySeq;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);
        if (querySeq == null) {
            DialogDisplayer.getDefault().notify(emptyQuery);
            cancel();
        }
    }

    @Override
    public void run() {
        if (querySeq != null) {
            ProteinSequence query = null;
            try {
                query = new ProteinSequence(querySeq);
            } catch (CompoundNotFoundException ex) {
                Exceptions.printStackTrace(ex);
                cancel();
            }
            if (!stopRun) {
                AttributesModel tmpAttrModel;
                if (workspaceInput) {
                    tmpAttrModel = pc.getAttributesModel(workspace);
                } else {
                    tmpAttrModel = dao.getPeptides(new QueryModel(workspace), pc.getGraphModel(workspace), pc.getAttributesModel(workspace));
                }
                Peptide[] targets = tmpAttrModel.getPeptides().toArray(new Peptide[0]);

                // Assign peptide from targets to result list
                List<PeptideHit> hits = searchSimilarTo(targets, query);

                // Sort results
                results = hits.toArray(new PeptideHit[0]);
                Arrays.parallelSort(results, Collections.reverseOrder());

                //New model                 
                newAttrModel = new AttributesModel(workspace);
                tmpAttrModel.getBridge().copyTo(newAttrModel, null);
                graphNodes = new LinkedList<>();
            }
        }
    }

}
