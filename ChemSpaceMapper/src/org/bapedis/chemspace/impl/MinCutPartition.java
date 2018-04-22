/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.Batch;
import org.bapedis.chemspace.model.BiGraph;
import org.bapedis.chemspace.model.Vertex;
import org.bapedis.chemspace.util.Bucket;

/**
 *
 * @author loge
 */
public class MinCutPartition extends RecursiveTask<Batch[]> {

    public static final int k_Moves = 100;
    private boolean[] bestPartition;
    private int bestCost;
    private boolean currentSide;
    private final AtomicBoolean stopRun; 

    protected final int cacheSize;
    protected final BiGraph bigraph;

    public MinCutPartition(BiGraph bigraph, int cacheSize, AtomicBoolean stopRun) {
        this.bigraph = bigraph;
        this.cacheSize = cacheSize;
        this.stopRun = stopRun;
    }

    @Override
    protected Batch[] compute() {
        if (!stopRun.get() && bigraph.size() > cacheSize ) {
            bigraph.initializePartition();
            findGraphPartition(bigraph);
            MinCutPartition left = new MinCutPartition(new BiGraph(bigraph.getLeftVertices(), bigraph.getSimMatrix(), bigraph.getThreshold()),cacheSize, stopRun);
            MinCutPartition right = new MinCutPartition(new BiGraph(bigraph.getRightVertices(), bigraph.getSimMatrix(), bigraph.getThreshold()), cacheSize, stopRun);
            Batch[] leftBatches = left.compute();
            Batch[] righBatchs = right.join();
            
            Batch[] batches = new Batch[leftBatches.length + righBatchs.length];
            System.arraycopy(leftBatches, 0, batches, 0, leftBatches.length);
            System.arraycopy(righBatchs, 0, batches, leftBatches.length, righBatchs.length);
            
            return batches;
        }

        Batch batch = new Batch(bigraph.size());
        for (Vertex u : bigraph.getVertices()) {
            batch.addPeptide(u.getPeptide());
        }
        return new Batch[]{batch};        
    }
    
    protected void findGraphPartition(BiGraph graph) {
        graph.randomizePartition();

        Bucket leftBucket = new Bucket(graph, BiGraph.LEFT_SIDE);
        Bucket rightBucket = new Bucket(graph, BiGraph.RIGHT_SIDE);

        bestCost = getCost(graph);
        bestPartition = Arrays.copyOf(graph.getPartition(), graph.size());

        //Set which side to begin, to maintain balance
        int leftCount = 0;
        int rightCount = 0;
        for (boolean side : graph.getPartition()) {
            if (side == BiGraph.LEFT_SIDE) {
                leftCount++;
            } else {
                rightCount++;
            }
        }
        if (rightCount > leftCount) {
            currentSide = BiGraph.RIGHT_SIDE;
        } else {
            currentSide = BiGraph.LEFT_SIDE;
        }

        int iterations = 0;
        boolean[] lockedVertices = new boolean[graph.size()];
        while (!stopRun.get() && iterations < 10 && doMoves(graph, lockedVertices, leftBucket, rightBucket)) {
            iterations++;
        }

        graph.setPartition(bestPartition);
    }

    private boolean doMoves(BiGraph graph, boolean[] lockedVertices, Bucket leftBucket, Bucket rightBucket) {
        boolean improvement = false;
        Vertex[] vertices = graph.getVertices();

        //Initialize Buckets
        leftBucket.initialize();
        rightBucket.initialize();

        // Free all locked vertices.
        resetLockedVertices(lockedVertices);

        //The first k moves
        int cost;
        for (int i = 0; i < Math.min(k_Moves, vertices.length) && !stopRun.get(); i++) {
            doMove(graph, lockedVertices, leftBucket, rightBucket);
            cost = getCost(graph);
            if (cost < bestCost) {
                bestPartition = Arrays.copyOf(graph.getPartition(), vertices.length);
                bestCost = cost;
                improvement = true;
            }
        }

        return improvement;
    }

    private void doMove(BiGraph graph, boolean[] locked, Bucket leftBucket, Bucket rightBucket) {
        boolean[] partition = graph.getPartition();
        Bucket currentBucket, complementBucket;

        if (currentSide == BiGraph.LEFT_SIDE) {
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
                    if (!locked[v.getVertexIndex()]) {
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
            assert partition[i] == currentSide : "Incompatibility for vertex partition and current side";
            partition[i] = !partition[i];

            // Lock Vertex
            locked[i] = true;

            //For each neighbour vertex update the gain
            int j;
            for (Vertex u : graph.getVertices()) {
                j = u.getVertexIndex();
                if (!locked[j] && graph.isNeighbour(vertex, u)) { // Consider only free vertices
                    if (partition[j] == currentSide) { // u is in current block
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
        Vertex[] vertices = graph.getVertices();
        boolean[] partition = graph.getPartition();
        //only look at vertices in one partition to avoid checking edges twice
        for (int i = 0; i < vertices.length; i++) {
            if (partition[i]) {
                for (int j = 0; j < vertices.length; j++) {
                    if (!partition[j] && graph.isNeighbour(vertices[i], vertices[j])) {
                        cost++;
                    }
                }
            }
        }
        return cost;
    }

    private void resetLockedVertices(boolean[] locked) {
        for (int i = 0; i < locked.length; i++) {
            locked[i] = false;
        }
    }
}
