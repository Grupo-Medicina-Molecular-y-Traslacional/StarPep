/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.Batch;
import org.bapedis.chemspace.model.BiGraph;
import org.bapedis.chemspace.model.Partition;
import org.bapedis.chemspace.model.Vertex;
import org.bapedis.chemspace.util.Bucket;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class MinCutPartition extends RecursiveTask<Batch[]> {

    public static final int K_MOVES = 100;
    private Boolean[] bestPartition;
    private int bestCost;
    private boolean currentSide;
    private final ProgressTicket ticket;
    private final AtomicBoolean stopRun;

    protected final int cacheSize;
    protected final BiGraph bigraph;

    public MinCutPartition(BiGraph bigraph, int cacheSize, ProgressTicket ticket, AtomicBoolean stopRun) {
        this.bigraph = bigraph;
        this.cacheSize = cacheSize;
        this.ticket = ticket;
        this.stopRun = stopRun;
    }

    @Override
    protected Batch[] compute() {
        if (!stopRun.get() && bigraph.size() > cacheSize) {
            findGraphPartition(bigraph);
            MinCutPartition left = new MinCutPartition(bigraph.getLeftGraph(), cacheSize, ticket, stopRun);
            MinCutPartition right = new MinCutPartition(bigraph.getRightGraph(), cacheSize, ticket, stopRun);
            right.fork();
            Batch[] leftBatches = left.compute();
            Batch[] righBatchs = right.join();

            Batch[] batches = new Batch[leftBatches.length + righBatchs.length];
            System.arraycopy(leftBatches, 0, batches, 0, leftBatches.length);
            System.arraycopy(righBatchs, 0, batches, leftBatches.length, righBatchs.length);

            return batches;
        }

        Batch batch = new Batch(bigraph.size());
        for (Vertex u : bigraph) {
            batch.addPeptide(u.getPeptide());
        }        
        ticket.progress();
        return new Batch[]{batch};
    }

    protected void findGraphPartition(BiGraph graph) {
        graph.getPartition().initializePartition();
        graph.getPartition().randomizePartition();

        Bucket leftBucket = new Bucket(graph, Partition.LEFT_SIDE);
        Bucket rightBucket = new Bucket(graph, Partition.RIGHT_SIDE);

        bestCost = getCost(graph);
        bestPartition = graph.getPartition().getArray();

        //Set which side to begin, to maintain balance
        int leftCount = 0;
        int rightCount = 0;
        for (boolean side : graph.getPartition()) {
            if (side == Partition.LEFT_SIDE) {
                leftCount++;
            } else {
                rightCount++;
            }
        }
        if (rightCount > leftCount) {
            currentSide = Partition.RIGHT_SIDE;
        } else {
            currentSide = Partition.LEFT_SIDE;
        }

        int iterations = 0;
        while (!stopRun.get() && iterations < 10 && doMoves(graph, leftBucket, rightBucket)) {
            iterations++;
        }

        graph.getPartition().setArray(bestPartition);
        graph.rearrange();
    }

    private boolean doMoves(BiGraph graph, Bucket leftBucket, Bucket rightBucket) {
        boolean improvement = false;

        //Initialize Buckets
        leftBucket.initialize();
        rightBucket.initialize();

        // Free all locked vertices.
        graph.freeLockedVertices();

        //The first k moves
        int cost;
        for (int i = 0; i < Math.min(K_MOVES, graph.size()) && !stopRun.get(); i++) {
            doMove(graph, leftBucket, rightBucket);
            cost = getCost(graph);
            if (cost < bestCost) {
                bestPartition = graph.getPartition().getArray();
                bestCost = cost;
                improvement = true;
            }
        }

        return improvement;
    }

    private void doMove(BiGraph graph, Bucket leftBucket, Bucket rightBucket) {
        Partition partition = graph.getPartition();
        Bucket currentBucket, complementBucket;

        if (currentSide == Partition.LEFT_SIDE) {
            currentBucket = leftBucket;
            complementBucket = rightBucket;
        } else {
            currentBucket = rightBucket;
            complementBucket = leftBucket;
        }

        //Find the vertex to be moved
        boolean found = false;
        int gainIndex = currentBucket.getMaxGainIndex();
        List<Vertex> vertices;
        Vertex vertex = null;
        while (!found && gainIndex >= 0) {
            vertices = currentBucket.getVeritcesAt(gainIndex);
            if (vertices != null) {
                for (Vertex v : vertices) {
                    if (!v.isLocked()) {
                        found = true;
                        vertex = v;
                        break;
                    }
                }
            }
            gainIndex--;
        }

        if (vertex != null) {
            //Move the vertex
            int i = vertex.getVertexIndex();
            assert partition.getSideAt(i) == currentSide : "Incompatibility for vertex partition and current side";
            partition.doMoveAt(i);

            // Lock Vertex
            vertex.setLocked(true);

            //For each neighbour vertex update the gain
            int j;
            for (Vertex u : graph) {
                if (!stopRun.get() && !u.isLocked() && graph.isNeighbour(vertex, u)) { // Consider only free vertices
                    j = u.getVertexIndex();
                    if (partition.getSideAt(j) == currentSide) { // u is in current block
                        currentBucket.incrementGain(u);
                    } else { // u is in complementary block
                        complementBucket.decrementGain(u);
                    }
                }
            }
        }

        //Maitaining balance by flipping sides
        currentSide = !currentSide;
    }

    private int getCost(BiGraph graph) {
        int cost = 0;
        Partition partition = graph.getPartition();
        //only look at vertices in one partition to avoid checking edges twice
        for (Vertex v : graph) {
            if (partition.getSideAt(v.getVertexIndex())) {
                for (Vertex u : graph) {
                    if (!partition.getSideAt(u.getVertexIndex())
                            && graph.isNeighbour(v, u)) {
                        cost++;
                    }
                }
            }
        }
        return cost;
    }
}
