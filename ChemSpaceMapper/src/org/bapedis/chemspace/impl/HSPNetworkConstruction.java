/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.Collections;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.chemspace.model.CandidatePeptide;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;

/**
 *
 * @author Loge
 */
public class HSPNetworkConstruction extends NetworkConstructionAlg implements Cloneable {

    private boolean reverseOrder;

    public HSPNetworkConstruction(AlgorithmFactory factory) {
        super(factory);
        reverseOrder = false;
    }

    public boolean isReverseOrder() {
        return reverseOrder;
    }

    public void setReverseOrder(boolean reverseOrder) {
        this.reverseOrder = reverseOrder;
    }

    @Override
    protected double createNetwork() {
        // task size
        ticket.switchToDeterminate(peptides.length);

        // Create new edges...
        if (peptides != null && !stopRun.get()) {
            return Arrays.stream(peptides).parallel().mapToDouble(peptide -> {
                double maxDistance = 0;
                if (!stopRun.get()) {
                    try {
                        maxDistance = computeHSPNeighbors(peptide);
                        ticket.progress();
                    } catch (MolecularDescriptorNotFoundException ex) {
                        DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                        Exceptions.printStackTrace(ex);
                        cancel();
                    }
                }
                return maxDistance;
            }).max().getAsDouble();
        }
        return 0;
    }
    

    private double computeHSPNeighbors(Peptide peptide) throws MolecularDescriptorNotFoundException {
        Node node1, node2, node3;
        Edge graphEdge;
        Peptide closestPeptide;
        double distance, maxDistance = 0;
        int cursor;

        AbstractDistance distAlg = (AbstractDistance)distFactory.createAlgorithm();
        if (!stopRun.get()) {
            CandidatePeptide[] candidates = new CandidatePeptide[peptides.length - 1];
            node1 = peptide.getGraphNode();
            cursor = 0;
            for (int j = 0; j < peptides.length; j++) {
                if (peptide != peptides[j]) {
                    node2 = peptides[j].getGraphNode();
                    graphEdge = mainGraph.getEdge(node1, node2, relType);
                    if (graphEdge != null) {
                        distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                    } else {
                        distAlg.setContext(peptide, peptides[j], descriptorMatrix);
                        distAlg.run();
                        distance = distAlg.getDistance();
                    }
                    candidates[cursor++] = new CandidatePeptide(peptides[j], distance);
                }
            }

            if (reverseOrder) {
                Arrays.parallelSort(candidates, Collections.reverseOrder());
            } else {
                Arrays.parallelSort(candidates);
            }
            maxDistance = candidates[candidates.length - 1].getDistance();
            cursor = 0;
            while (cursor < candidates.length) {
                if (candidates[cursor] != null) {
                    //Create an edge to the closest peptide
                    closestPeptide = candidates[cursor].getPeptide();
                    node2 = closestPeptide.getGraphNode();
                    distance = candidates[cursor].getDistance();
                    mainGraph.writeLock();
                    try {
                        graphEdge = mainGraph.getEdge(node1, node2, relType);
                        if (graphEdge == null) {
                            graphEdge = createEdge(node1, node2);
                            mainGraph.addEdge(graphEdge);
                            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE, distance);
                        }
                    } finally {
                        mainGraph.writeUnlock();
                    }

                    // ignore elements in the forbidden area
                    for (int k = cursor + 1; k < candidates.length; k++) {
                        if (candidates[k] != null) {
                            node3 = candidates[k].getPeptide().getGraphNode();
                            graphEdge = mainGraph.getEdge(node2, node3, relType);
                            if (graphEdge != null) {
                                distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                            } else {
                                distAlg.setContext(closestPeptide, candidates[k].getPeptide(), descriptorMatrix);
                                distAlg.run();
                                distance = distAlg.getDistance();
                            }
                            if (distance < candidates[k].getDistance()) {
                                candidates[k] = null;
                            }
                        }
                    }
                }
                cursor++;
            }
        }
        distAlg.endAlgo();
        return maxDistance;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
