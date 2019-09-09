/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.util.List;
import org.bapedis.core.model.PeptideHit;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.SingleQuery;

/**
 *
 * @author Loge
 */
public class ChemSingleSimilaritySearchAlg extends ChemBaseSimilaritySearchAlg implements SingleQuery {

    private String querySeq;

    public ChemSingleSimilaritySearchAlg(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void run() {
        if (queryList.size() > 0 && targetList.size() > 0) {
            assert (queryList.size() == 1);

            if (!stopRun) {
                Peptide query = queryList.get(0);
                List<PeptideHit> resultList = searchSimilarTo(query);

                //Save results
                Peptide peptide;
                for (PeptideHit hit : resultList) {
                    peptide = hit.getPeptide();
                    newAttrModel.addPeptide(peptide);
                    graphNodes.add(peptide.getGraphNode());
                }
                newAttrModel.addPeptide(query);
                graphNodes.add(query.getGraphNode());
            }

        }
    }

    @Override
    public void setQuery(String seq) {
        this.querySeq = seq;
    }

    @Override
    public String getQuery() {
        return querySeq;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ChemSingleSimilaritySearchAlg copy = (ChemSingleSimilaritySearchAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.
        if (querySeq != null) {
            copy.querySeq = String.copyValueOf(querySeq.toCharArray());
        }
        return copy;
    }
}
