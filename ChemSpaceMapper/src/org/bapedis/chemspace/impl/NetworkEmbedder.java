/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public interface NetworkEmbedder {

    public static final ForkJoinPool fjPool = new ForkJoinPool();

    public float getSimilarityThreshold();

    public void setSimilarityThreshold(float similarityThreshold);

    public void setNetworkType(NetworkType networkType);

    public NetworkType getNetworkType();

    public SimilarityMatrix getSimilarityMatrix();

    public default void updateNetwork(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean stopRun) {
        switch (getNetworkType()) {
            case FULL:
                createFullNetwork(graphModel, ticket, stopRun);
                break;
            case HSP:
                createHSPNetwork(graphModel, ticket, stopRun);
                break;
        }
    }

    public default void createFullNetwork(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean stopRun) {
        NetworkEmbedder.clearSimilarityEdges(graphModel);
        Graph graph = graphModel.getGraphVisible();
        float similarityThreshold = getSimilarityThreshold();
        Peptide[] peptides = getSimilarityMatrix().getPeptides();
        Edge graphEdge;
        Float score;
        ticket.switchToDeterminate(peptides.length - 1);
        for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                score = getSimilarityMatrix().getValue(peptides[i], peptides[j]);
                if (score != null && score >= similarityThreshold) {
                    if (graph.contains(peptides[i].getGraphNode()) && graph.contains(peptides[j].getGraphNode())) {
                        graphEdge = NetworkEmbedder.createSimilarityEdge(graphModel, peptides[i].getGraphNode(), peptides[j].getGraphNode(), score);
                        graph.writeLock();
                        try {
                            graph.addEdge(graphEdge);
                        } finally {
                            graph.writeUnlock();
                        }
                    }
                }
            }
            ticket.progress();
        }
    }

    default public void createHSPNetwork(GraphModel graphModel, ProgressTicket ticket, AtomicBoolean stopRun) {
        
    }

    public static void clearSimilarityEdges(GraphModel graphModel) {
        // Remove all similarity edges..
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                mainGraph.clearEdges(node, relType);
            }
        } finally {
            mainGraph.writeUnlock();
        }
    }

    public static Edge createSimilarityEdge(GraphModel graphModel, Node node1, Node node2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        // Create Edge
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            mainGraph.addEdge(graphEdge);
            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);
        } finally {
            mainGraph.writeUnlock();
        }

        return graphEdge;
    }
}
