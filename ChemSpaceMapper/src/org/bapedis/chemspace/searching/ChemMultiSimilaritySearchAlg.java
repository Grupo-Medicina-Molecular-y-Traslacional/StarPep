/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.searching;

import java.util.HashMap;
import java.util.List;
import org.bapedis.core.model.PeptideHit;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.MultiQuery;

/**
 *
 * @author Loge
 */
public class ChemMultiSimilaritySearchAlg extends ChemBaseSimilaritySearchAlg implements MultiQuery {

    private String fasta;
    
    public ChemMultiSimilaritySearchAlg(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void run() {
        if (queryList.size() > 0 && targetList.size() > 0) {
            HashMap<Integer, PeptideHit> mapResult = new HashMap<>();
            List<PeptideHit> resultList;
            for (Peptide query : queryList) {
                if (!stopRun) {
                    resultList = searchSimilarTo(query);
                    dataFusion(resultList, mapResult);
                }
            }
            saveResults(mapResult);

            for (Peptide query : queryList) {
                newAttrModel.addPeptide(query);
                graphNodes.add(query.getGraphNode());
            }
        }
    }

    @Override
    public void setFasta(String fasta) {
        this.fasta = fasta;
    }

    @Override
    public String getFasta() {
        return fasta;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        ChemMultiSimilaritySearchAlg copy = (ChemMultiSimilaritySearchAlg) super.clone(); //To change body of generated methods, choose Tools | Templates.        
        if (fasta != null) {
            copy.fasta = String.copyValueOf(fasta.toCharArray());
        }        
        return copy;
    }
}
