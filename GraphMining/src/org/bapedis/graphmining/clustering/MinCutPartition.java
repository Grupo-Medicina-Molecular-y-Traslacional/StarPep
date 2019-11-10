/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.clustering;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.graphmining.model.BiGraph;
import org.bapedis.graphmining.model.Bucket;
import org.bapedis.graphmining.model.Partition;
import org.bapedis.graphmining.model.Vertex;

/**
 *
 * @author loge
 */
public class MinCutPartition extends BasePartition {

    public static final int K_MOVES = -1;
    private Boolean[] bestPartition;
    private int bestCost;
    private boolean currentSide;

    public MinCutPartition(BiGraph bigraph, int level, ProgressTicket ticket, AtomicBoolean stopRun) {
        super(bigraph, level, ticket, stopRun);
    }

    @Override
    protected Cluster[] compute() {
        if (!stopRun.get() && bigraph.size() > MIN_SIZE && level > 0) {
            findGraphPartition();
            MinCutPartition left = new MinCutPartition(bigraph.getLeftGraph(), level - 1, ticket, stopRun);
            MinCutPartition right = new MinCutPartition(bigraph.getRightGraph(), level - 1, ticket, stopRun);
            right.fork();
            return union(left.compute(), right.join());
        }
        return computeDirectly();
    }

    protected void findGraphPartition() {
        Partition p = bigraph.getPartition();
        p.initializePartition();
        p.randomizePartition();

        Bucket leftBucket = new Bucket(bigraph, Partition.LEFT_SIDE);
        Bucket rightBucket = new Bucket(bigraph, Partition.RIGHT_SIDE);

        bestCost = getCost(bigraph);
        bestPartition = p.getArray();

        //Set which side to begin, to maintain balance
        int leftCount = 0;
        int rightCount = 0;
        for (boolean side : bigraph.getPartition()) {
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
        while (iterations < 10 && doMoves(bigraph, leftBucket, rightBucket)) {
            iterations++;
        }

        p.setArray(bestPartition);
        bigraph.rearrange();
    }

    private boolean doMoves(BiGraph graph, Bucket leftBucket, Bucket rightBucket) {
        boolean improvement = false;

        //Initialize Buckets
        leftBucket.initialize();
        rightBucket.initialize();

        // Free all locked vertices.
        graph.freeLockedVertices();

        //The first k moves
        int cost, n;
        n = K_MOVES > 0 ? Math.min(K_MOVES, graph.size()) : graph.size();
        for (int i = 0; i < n; i++) {
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
            Vertex u;
            for (Iterator<Vertex> it = graph.getAllVertices(); it.hasNext();) {
                u = it.next();
                if (!u.isLocked() && graph.isNeighbour(vertex, u)) { // Consider only free vertices
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
        //only look at vertices in one partition to avoid checking edges twice
        Vertex v, u;
        for (Iterator<Vertex> left = graph.getLeftVertices(); left.hasNext();) {
            v = left.next();
            for (Iterator<Vertex> right = graph.getRightVertices(); right.hasNext();) {
                u = right.next();
                if (graph.isNeighbour(v, u)) {
                    cost++;
                }
            }
        }
        return cost;
    }

}
