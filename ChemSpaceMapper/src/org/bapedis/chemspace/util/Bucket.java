/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.util;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.model.BiGraph;
import org.bapedis.chemspace.model.Partition;
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
        int i,j;
        for (Vertex v: graph) {
            i = v.getVertexIndex();
            if (partition.getSideAt(i) == side) {
                int gain = 0;
                for (Vertex u: graph) {
                    if (v != u && graph.isNeighbour(v,u)) {
                        j = u.getVertexIndex();
                        if (partition.getSideAt(i) == partition.getSideAt(j)) {
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
