/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author loge
 */
public class BiGraph {

    public final static boolean LEFT_SIDE = false;
    public final static boolean RIGHT_SIDE = true;
    private final Vertex[] vertices;
    private final SimilarityMatrix simMatrix;
    protected final double threshold;
    private boolean[] partition;

    public BiGraph(Vertex[] vertices, SimilarityMatrix simMatrix, double threshold) {
        this.vertices = vertices;
        this.simMatrix = simMatrix;
        this.threshold = threshold;
        partition = new boolean[vertices.length];
        for (int i = 0; i < vertices.length; i++) {
            vertices[i].setVertexIndex(i);
            vertices[i].setGain(0);
        }
    }

    public SimilarityMatrix getSimMatrix() {
        return simMatrix;
    }        

    public double getThreshold() {
        return threshold;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public boolean[] getPartition() {
        return partition;
    }

    public void setPartition(boolean[] partition) {
        assert partition.length == vertices.length : "Incompatible partition size";
        this.partition = partition;
    }

    public void initializePartition() {
        int size = vertices.length;
        int mid = size / 2;

        for (int i = 0; i < mid; i++) {
            partition[i] = false;
        }
        for (int i = mid; i < size; i++) {
            partition[i] = true;
        }
    }

    public void randomizePartition() {
        Random rnd = new Random();
        // Shuffle array
        for (int i = partition.length; i > 1; i--) {
            swap(i - 1, rnd.nextInt(i));
        }
    }

    private void swap(int i, int j) {
        boolean tmp = partition[i];
        partition[i] = partition[j];
        partition[j] = tmp;
    }

    public boolean isNeighbour(Vertex vertex1, Vertex vertex2) {
        Float score = simMatrix.getValue(vertex1.getPeptide(), vertex2.getPeptide());
        return !vertex1.equals(vertex2) && score != null && score >= threshold;
    }

    public int calculateMaxDegree() {
        int maxDegree = 0;
        int degree;
        for (int i = 0; i < vertices.length; i++) {
            degree = 0;
            for (int j = 0; j < vertices.length; j++) {
                if (j != i && isNeighbour(vertices[i], vertices[j])) {
                    degree++;
                }
            }
            if (degree > maxDegree) {
                maxDegree = degree;
            }
        }
        return maxDegree;
    }

    public Vertex[] getLeftVertices() {
        return getVertices(LEFT_SIDE);
    }

    private Vertex[] getVertices(boolean side) {
        List<Vertex> vertexList = new LinkedList<>();
        for (int i = 0; i < partition.length; i++) {
            if (partition[i] == side) {
                vertexList.add(vertices[i]);
            }
        }
        return vertexList.toArray(new Vertex[0]);
    }

    public Vertex[] getRightVertices() {
        return getVertices(RIGHT_SIDE);
    }

    public int size() {
        return vertices.length;
    }
}
