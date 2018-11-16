/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.model;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author loge
 */
public class Bucket {

    private final BiGraph graph;
    private final int maxDegree;
    private final boolean currentSide;
    private final List<Vertex>[] array;
    private int maxGainIndex;

    public Bucket(BiGraph graph, boolean side) {
        this.graph = graph;
        this.currentSide = side;
        this.maxDegree = graph.getMaxDegree();
        array = new List[2 * maxDegree + 1];
        maxGainIndex = -1;
    }

    public int getMaxGainIndex() {
        return maxGainIndex;
    }

    public List<Vertex> getVeritcesAt(int gainIndex) {
        return array[gainIndex];
    }

    public void initialize() {
        for (int i = 0; i < array.length; i++) {
            array[i] = null;
        }
        Partition partition = graph.getPartition();
        int i, j;
        Vertex v, u;
        for (Iterator<Vertex> it1 = graph.getVerticesAtSide(currentSide); it1.hasNext();) {
            v = it1.next();
            i = v.getVertexIndex();
            int gain = 0;
            assert partition.getSideAt(i) == currentSide : "Incompatibility for vertex partition and current side";
            for (Iterator<Vertex> it2 = graph.getAllVertices(); it2.hasNext();) {
                u = it2.next();
                if (v != u && graph.isNeighbour(v, u)) {
                    j = u.getVertexIndex();
                    if (partition.getSideAt(j) == currentSide) {
                        gain--;
                    } else {
                        gain++;
                    }
                }
            }
            v.setGain(gain);
            add(v);
        }
    }

    private void add(Vertex vertex) {
        int gainIndex = maxDegree + vertex.getGain();
        maxGainIndex = Math.max(gainIndex, maxGainIndex);

        if (array[gainIndex] == null) {
            array[gainIndex] = new LinkedList<>();
        }

        array[gainIndex].add(vertex);
    }

    private void remove(Vertex vertex) {
        int gainIndex = maxDegree + vertex.getGain();
        List<Vertex> vertices = array[gainIndex];

        assert vertices.remove(vertex) : "Vertex not found in bucket data structure";
    }

    public void incrementGain(Vertex vertex) {
        //Remove Vertex from current Linked List
        remove(vertex);

        //Add vertex to new Linked List
        vertex.setGain(vertex.getGain() + 2);
        add(vertex);
    }

    public void decrementGain(Vertex vertex) {
        //Remove Vertex from current Linked List
        remove(vertex);

        //Add vertex to new Linked List        
        vertex.setGain(vertex.getGain() - 2);
        add(vertex);
    }
}
