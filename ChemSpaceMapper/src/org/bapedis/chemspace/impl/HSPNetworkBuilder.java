/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.distance.AbstractDistance;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.DialogDisplayer;
import org.openide.util.Exceptions;

/**
 *
 * @author Loge
 */
public class HSPNetworkBuilder extends RecursiveAction {

    private static final int SEQUENTIAL_THRESHOLD = 10;
    private final Peptide[] peptides;
    private final GraphModel graphModel;
    private final ProgressTicket progressTicket;
    private final AbstractDistance distFunc;
    private final AtomicBoolean stopRun;
    private final int xlow, xhigh;
    private final double maxDistance, currentThreshold;  

    public HSPNetworkBuilder(Peptide[] peptides, GraphModel graphModel, AbstractDistance distFunc, double maxDistance, double currentThreshold, ProgressTicket ticket, AtomicBoolean stopRun) {
        this(0, peptides.length, peptides, graphModel, distFunc, maxDistance, currentThreshold, ticket, stopRun);
    }

    private HSPNetworkBuilder(int xlow, int xhigh, Peptide[] peptides, GraphModel graphModel, AbstractDistance distFunc, double maxDistance, double currentThreshold, ProgressTicket ticket, AtomicBoolean stopRun) {
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.peptides = peptides;
        this.graphModel = graphModel;
        this.distFunc = distFunc;
        this.maxDistance = maxDistance;
        this.currentThreshold = currentThreshold;
        this.progressTicket = ticket;
        this.stopRun = stopRun;
    }

    @Override
    protected void compute() {
        if (xhigh - xlow <= SEQUENTIAL_THRESHOLD) {
            try {
                computeDirectly();
            } catch (MolecularDescriptorNotFoundException ex) {
                DialogDisplayer.getDefault().notify(ex.getErrorNotifyDescriptor());
                Exceptions.printStackTrace(ex);
                stopRun.set(true);
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            HSPNetworkBuilder left = new HSPNetworkBuilder(xlow, middle, peptides, graphModel, distFunc, maxDistance, currentThreshold, progressTicket, stopRun);
            HSPNetworkBuilder right = new HSPNetworkBuilder(middle, xhigh, peptides, graphModel, distFunc, maxDistance, currentThreshold, progressTicket, stopRun);
            invokeAll(left, right);
        }
    }

    private void computeDirectly() throws MolecularDescriptorNotFoundException {
        Graph graph, mainGraph;
        graph = graphModel.getGraphVisible();
        mainGraph = graphModel.getGraph();
        int relType = graphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        Node node1, node2, node3;
        Edge graphEdge;
        Peptide closestPeptide;
        double distance, similarity;
        int cursor;

        CandidatePeptide[] candidates = new CandidatePeptide[peptides.length - 1];
        for (int i = xlow; i < xhigh && !stopRun.get(); i++) {
            node1 = peptides[i].getGraphNode();
            cursor = 0;
            for (int j = 0; j < peptides.length; j++) {
                if (i != j) {
                    node2 = peptides[j].getGraphNode();
                    graphEdge = graph.getEdge(node1, node2, relType);
                    if (graphEdge != null) {
                        distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                    } else {
                        distance = distFunc.distance(peptides[i], peptides[j]);
                    }
                    candidates[cursor++] = new CandidatePeptide(peptides[j], distance);
                }
            }

            Arrays.parallelSort(candidates);
            cursor = 0;
            while (cursor < candidates.length) {
                if (candidates[cursor] != null) {
                    //Create an edge to the closest peptide
                    closestPeptide = candidates[cursor].getPeptide();
                    node2 = closestPeptide.getGraphNode();
                    distance = candidates[cursor].getDistance();
                    similarity = 1.0 - distance / maxDistance;
                    mainGraph.writeLock();
                    try {
                        graphEdge = mainGraph.getEdge(node1, node2, relType);
                        if (graphEdge == null) {
                            graphEdge = createEdge(graphModel, node1, node2, relType);
                            mainGraph.addEdge(graphEdge);
                            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, similarity);
                            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE, distance);
                        }
                    } finally {
                        mainGraph.writeUnlock();
                    }
                    // Add the edge to the graph view
                    if (similarity >= currentThreshold) {
                        graph.writeLock();
                        try {
                            if (!graph.hasEdge(graphEdge.getId())) {
                                graph.addEdge(graphEdge);
                            }
                        } finally {
                            graph.writeUnlock();
                        }
                    }

                    // ignore elements in the forbidden area
                    for (int k = cursor + 1; k < candidates.length; k++) {
                        if (candidates[k] != null) {
                            node3 = candidates[k].getPeptide().getGraphNode();
                            graphEdge = graph.getEdge(node2, node3, relType);
                            if (graphEdge != null) {
                                distance = (double) graphEdge.getAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE);
                            } else {
                                distance = distFunc.distance(closestPeptide, candidates[k].getPeptide());
                            }
                            if (distance < candidates[k].getDistance()) {
                                candidates[k] = null;
                            }
                        }
                    }
                }
                cursor++;
            }
            progressTicket.progress();
        }

    }

    private Edge createEdge(GraphModel graphModel, Node node1, Node node2, int relType) {
        // Create an edge between two nodes
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        return graphEdge;
    }    
}

class CandidatePeptide implements Comparable<CandidatePeptide> {

    private final double distance;
    private final Peptide peptide;

    public CandidatePeptide(Peptide peptide, double distance) {
        this.peptide = peptide;
        this.distance = distance;
    }

    public Peptide getPeptide() {
        return peptide;
    }

    public double getDistance() {
        return distance;
    }

    @Override
    public int compareTo(CandidatePeptide o) {
        if (distance < o.distance) {
            return -1;
        }
        if (distance > o.distance) {
            return 1;
        }
        return 0;
    }
}