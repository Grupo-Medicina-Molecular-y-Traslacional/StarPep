/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.model.CandidatePeptide;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.gephi.graph.api.Node;
import org.bapedis.graphmining.centrality.HubBridgeCentrality;
import org.bapedis.graphmining.centrality.HubBridgeCentralityFactory;
import org.bapedis.graphmining.clustering.Modularity;
import org.bapedis.graphmining.clustering.ModularityFactory;

/**
 *
 * @author Loge
 */
public class ScaffoldNetworkConstruction extends HSPNetworkConstruction implements Cloneable {

    protected double diversityRadio;

    public ScaffoldNetworkConstruction(AlgorithmFactory factory) {
        super(factory);
        diversityRadio = 0.8;
    }

    public double getDiversityRadio() {
        return diversityRadio;
    }

    public void setDiversityRadio(double diversityRadio) {
        this.diversityRadio = diversityRadio;
    }

    private void execute(Algorithm alg) {
        alg.initAlgo(workspace, ticket);
        alg.run();
        alg.endAlgo();
    }

    @Override
    protected double createNetwork() {
        pc.reportMsg("Diversity radio: " + String.format("%.2f", diversityRadio), workspace);

        Modularity modularity = (Modularity) new ModularityFactory().createAlgorithm();
        execute(modularity);

        HubBridgeCentrality nodeMeasure = (HubBridgeCentrality) new HubBridgeCentralityFactory().createAlgorithm();
        execute(nodeMeasure);

        //Sort peptides by intrinsic strength
        Arrays.parallelSort(peptides, new RankComparator());

        //Remove similar peptides under the diversity radio
        Peptide scaffold;
        int count = 0;
        for (int i = 0; i < peptides.length; i++) {
            if (peptides[i] != null) {
                scaffold = peptides[i];
                count++;
                if (!stopRun.get()) {
                    removeSimilarTo(scaffold, i + 1);
                }
            }
        }
        
        Peptide[] scaffolds = new Peptide[count];
        int cursor = 0;
        for (int i = 0; i < peptides.length; i++){
            if (peptides[i] != null){
                scaffolds[cursor++] = peptides[i];
            }
        }        
        peptides = scaffolds;
        scaffolds = null;
        return super.createNetwork();
    }

    @Override
    protected void updateNodePositions() {
        //super.updateNodePositions(); //To change body of generated methods, choose Tools | Templates.
    }
    
    

    private void removeSimilarTo(Peptide scaffold, int from) {
        List<CandidatePeptide> candidateList = new LinkedList<>();
        //Populate the candidate list for the scaffold
        for (int i = from; i < peptides.length; i++) {
            if (peptides[i] != null) {
                distFunc.setPeptides(scaffold, peptides[i]);
                distFunc.run();
                candidateList.add(new CandidatePeptide(i, peptides[i], distFunc.getDistance()));
            }
        }
        //Remove similar peptides
        if (candidateList.size() > 0) {
            CandidatePeptide[] candidates = candidateList.toArray(new CandidatePeptide[0]);
            //Sort candidate peptides
            Arrays.parallelSort(candidates);
            double maxDistance = candidates[candidates.length - 1].getDistance();
            double similarity;
            boolean flag = true;
            for(int i=0; i<candidates.length && flag; i++){
                similarity = 1.0 - candidates[i].getDistance() / maxDistance;
                if (similarity > diversityRadio){
                    peptides[candidates[i].getIndex()] = null;
                }else{
                    flag = false; 
               }
            }
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}

class RankComparator implements Comparator<Peptide> {

    private final String attribute;

    public RankComparator() {
        this.attribute = HubBridgeCentrality.RANKING_BY_HUB_BRIDGE;
    }

    @Override
    public int compare(Peptide peptide1, Peptide peptide2) {
        Node node1 = peptide1.getGraphNode();
        Node node2 = peptide2.getGraphNode();
        Integer val1 = (Integer) node1.getAttribute(attribute);
        Integer val2 = (Integer) node2.getAttribute(attribute);
        return val2.compareTo(val1);
    }

}
