/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.Batch;
import org.bapedis.chemspace.model.BiGraph;
import org.bapedis.chemspace.model.NetworkType;
import org.bapedis.chemspace.model.SimilarityMatrix;
import org.bapedis.chemspace.model.Vertex;
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
        Graph mainGraph = graphModel.getGraph();
        Graph graph = graphModel.getGraphVisible();
        graph.clear();
        NetworkEmbedder.clearSuperNodes(graphModel);
        float similarityThreshold = getSimilarityThreshold();
        SimilarityMatrix simMatrix = getSimilarityMatrix();
        Peptide[] peptides = getSimilarityMatrix().getPeptides();

        Vertex[] vertices = new Vertex[peptides.length];
        //Create vertices
        for (int i = 0; i < peptides.length; i++) {
            vertices[i] = new Vertex(peptides[i]);
            vertices[i].setVertexIndex(i);
        }

////        ticket.switchToDeterminate(compressedModel.getMaxSuperNodes());

        BiGraph bigraph = new BiGraph(vertices, simMatrix, similarityThreshold);
//        int cacheSize = (int) Math.ceil((double) peptides.length / compressedModel.getMaxSuperNodes());
        int level = 64;
        BasePartition partition = null;


        fjPool.invoke(partition);
        Batch[] batches = partition.join();

        Batch b1, b2;
        Node superNode1, superNode2;
        Edge superEdge;
        boolean isSuperEdge;
        Peptide peptide1, peptide2;
        Float score;
        float sum;
        int count;

        //Create super nodes            
        for (int i = 0; i < batches.length && !stopRun.get(); i++) {
            NetworkEmbedder.createSuperNode(graphModel, i);
        }

        //Create super edges
        String id;
        for (int i = 0; i < batches.length - 1 && !stopRun.get(); i++) {
            b1 = batches[i];
            id = String.format("superNode_%d", i);
            superNode1 = mainGraph.getNode(id);
            graph.writeLock();
            try {
                graph.addNode(superNode1);
            } finally {
                graph.writeUnlock();
            }
            for (int j = i + 1; j < batches.length && !stopRun.get(); j++) {
                b2 = batches[j];
                id = String.format("superNode_%d", j);
                superNode2 = mainGraph.getNode(id);
                graph.writeLock();
                try {
                    graph.addNode(superNode2);
                } finally {
                    graph.writeUnlock();
                }
                isSuperEdge = false;
                sum = 0;
                count = 0;
                for (int l = 0; l < b1.getSize() && !stopRun.get(); l++) {
                    peptide1 = b1.getPeptide(l);
                    for (int k = 0; k < b2.getSize() && !stopRun.get(); k++) {
                        peptide2 = b2.getPeptide(k);
                        score = simMatrix.getValue(peptide1, peptide2);
                        if (score != null) {
                            if (score >= similarityThreshold) {
                                isSuperEdge = true;
                            }
                            sum += score;
                            count++;
                        }
                    }
                    if (isSuperEdge) {
                        break;
                    }
                }
                if (isSuperEdge) {
                    superEdge = NetworkEmbedder.createSuperEdge(graphModel, superNode1, superNode2, sum / count);
                    graph.writeLock();
                    try {
                        graph.addEdge(superEdge);
                    } finally {
                        graph.writeUnlock();
                    }

                }
            }
        }
    }

    public static Node createSuperNode(GraphModel graphModel, int index) {
        String id = String.format("superNode_%d", index);
        Node superNode = graphModel.factory().newNode(id);

        superNode.setAttribute(ProjectManager.NODE_TABLE_PRO_NAME, id);
        superNode.setLabel("SuperNode");
        superNode.setSize(ProjectManager.GRAPH_SUPER_NODE_SIZE);

        //Set random position
        superNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
        superNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

        //Set color
        superNode.setR(ProjectManager.GRAPH_SUPER_NODE_COLOR.getRed() / 255f);
        superNode.setG(ProjectManager.GRAPH_SUPER_NODE_COLOR.getGreen() / 255f);
        superNode.setB(ProjectManager.GRAPH_SUPER_NODE_COLOR.getBlue() / 255f);
        superNode.setAlpha(1f);

        Graph mainGraph = graphModel.getGraph();
        mainGraph.addNode(superNode);

        return superNode;
    }

    public static void clearSuperNodes(GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        List<Node> toRemoveNodes = new LinkedList<>();
        mainGraph.readLock();
        try {
            for (Node node : mainGraph.getNodes()) {
                if (((String) node.getId()).startsWith("superNode")) {
                    toRemoveNodes.add(node);
                }
            }
        } finally {
            mainGraph.readUnlock();
        }
        mainGraph.removeAllNodes(toRemoveNodes);
    }

    public static Edge createSuperEdge(GraphModel graphModel, Node superNode1, Node superNode2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_SUPER_EDGE_SIMALIRITY);

        // Create Edge
        String id = String.format("%s-%s", superNode1.getId(), superNode2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, superNode1, superNode2, relType, ProjectManager.GRAPH_SUPER_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_SUPER_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_SUPER_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_SUPER_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_SUPER_NODE_COLOR.getBlue() / 255f);
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
