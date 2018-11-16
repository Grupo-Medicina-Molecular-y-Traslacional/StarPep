/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.model;

import java.util.Iterator;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;

/**
 *
 * @author loge
 */
public class BiGraph {

    private final Vertex[] vertices;
    private final Graph graph;
    private final Partition partition;
    private final int maxDegree;

    public BiGraph(Vertex[] vertices, Graph graph) {
        this(vertices, new Partition(0, vertices.length - 1), graph);
    }

    private BiGraph(Vertex[] vertices, Partition partition, Graph graph) {
        this.vertices = vertices;
        this.graph = graph;
        this.partition = partition;
        maxDegree = calculateMaxDegree();
    }

    public void freeLockedVertices() {
        int lowerIndex = partition.getLowerIndex();
        int higherIndex = partition.getHigherIndex();
        for (int i = lowerIndex; i <= higherIndex; i++) {
            vertices[i].setLocked(false);
        }
    }

    public Graph getGraph() {
        return graph;
    }    

    public Partition getPartition() {
        return partition;
    }

    public boolean isNeighbour(Vertex vertex1, Vertex vertex2) {
        Edge edge = graph.getEdge(vertex1.getPeptide().getGraphNode(), vertex2.getPeptide().getGraphNode());
        return !vertex1.equals(vertex2) && edge != null;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    private int calculateMaxDegree() {
        int lowerIndex = partition.getLowerIndex();
        int higherIndex = partition.getHigherIndex();
        int maxValue = 0;
        int degree;
        for (int i = lowerIndex; i <= higherIndex; i++) {
            degree = 0;
            for (int j = lowerIndex; j <= higherIndex; j++) {
                if (j != i && isNeighbour(vertices[i], vertices[j])) {
                    degree++;
                }
            }
            if (degree > maxValue) {
                maxValue = degree;
            }
        }
        return maxValue;
    }

    public BiGraph getLeftGraph() {
        return new BiGraph(vertices, partition.getLeftPartition(), graph);
    }

    public void rearrange() {
        int lowerIndex = partition.getLowerIndex();
        int higherIndex = partition.getHigherIndex();
        int i = lowerIndex;
        int j = higherIndex;
        int middle = partition.getMiddle();
        boolean change;
        do {
            change = false;
            while (partition.getSideAt(i) == Partition.LEFT_SIDE && i <= middle) {
                i++;
            }
            while (partition.getSideAt(j) == Partition.RIGHT_SIDE && j > middle) {
                j--;
            }
            
            if (i < j && partition.getSideAt(i) == Partition.RIGHT_SIDE && partition.getSideAt(j) == Partition.LEFT_SIDE) {
                change = true;
                swapVertices(i, j);
                vertices[i].setVertexIndex(i);
                vertices[j].setVertexIndex(j);
                partition.swap(i, j);
            }
        } while (change);
        
    }

    private void swapVertices(int i, int j) {
        Vertex tmp = vertices[i];
        vertices[i] = vertices[j];
        vertices[j] = tmp;
    }

    public BiGraph getRightGraph() {
        return new BiGraph(vertices, partition.getRightPartition(), graph);
    }

    public int size() {
        return partition.getSize();
    }

    public Iterator<Vertex> getVerticesAtSide(boolean side) {
        if (side == Partition.LEFT_SIDE) {
            return getLeftVertices();
        }
        return getRightVertices();
    }

    public Iterator<Vertex> getAllVertices() {
        return new AllIterator();
    }

    public Iterator<Vertex> getLeftVertices() {
        return new LeftIterator();
    }

    public Iterator<Vertex> getRightVertices() {
        return new RightIterator();
    }

    private class AllIterator implements Iterator<Vertex> {

        int cursor = partition.getLowerIndex();

        @Override
        public boolean hasNext() {
            return cursor <= partition.getHigherIndex();
        }

        @Override
        public Vertex next() {
            return vertices[cursor++];
        }

    }

    private class LeftIterator implements Iterator<Vertex> {
        int cursor;

        public LeftIterator() {
            cursor = partition.getLowerIndex();
            findNext();
        }

        @Override
        public boolean hasNext() {
            return cursor != -1;
        }

        @Override
        public Vertex next() {
            Vertex v = vertices[cursor++];
            findNext();
            return v;
        }

        private void findNext() {
            boolean found = false;
            while (!found && cursor <= partition.getHigherIndex()) {
                if (partition.getSideAt(cursor) == Partition.LEFT_SIDE) {
                    found = true;
                } else {
                    cursor++;
                }
            }
            if (!found) {
                cursor = -1;
            }
        }
    }

    private class RightIterator implements Iterator<Vertex> {
        int cursor;

        public RightIterator() {
            cursor = partition.getHigherIndex();
            findNext();
        }

        @Override
        public boolean hasNext() {
            return cursor != -1;
        }

        @Override
        public Vertex next() {
            Vertex v = vertices[cursor--];
            findNext();
            return v;
        }

        private void findNext() {
            boolean found = false;
            while (!found && cursor >= partition.getLowerIndex()) {
                if (partition.getSideAt(cursor) == Partition.RIGHT_SIDE) {
                    found = true;
                } else {
                    cursor--;
                }
            }
            if (!found) {
                cursor = -1;
            }
        }
    }
}
