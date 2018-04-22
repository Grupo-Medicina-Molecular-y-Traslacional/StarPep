/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.model.BiGraph;
import org.bapedis.chemspace.model.Vertex;

/**
 *
 * @author loge
 */
public class Bucket {

    private final BiGraph graph;
    private final int maxDegree;
    private final boolean side;
    private final List<Vertex>[] array;
    private int maxGainIndex;

    public Bucket(BiGraph graph, boolean side) {
        this.graph = graph;
        this.side = side;
        maxDegree = graph.calculateMaxDegree();
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
        Vertex[] vertices = graph.getVertices();
        boolean[] partition = graph.getPartition();
        for (int i = 0; i < vertices.length; i++) {
            if (partition[i] == side) {
                int gain = 0;
                for (int j = 0; j < vertices.length; j++) {
                    if (i != j) {
                        if (graph.isNeighbour(vertices[i], vertices[j])) {
                            if (partition[i] == partition[j]) {
                                gain--;
                            } else {
                                gain++;
                            }
                        }
                    }
                }
                vertices[i].setGain(gain);
                add(vertices[i]);
            }
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
