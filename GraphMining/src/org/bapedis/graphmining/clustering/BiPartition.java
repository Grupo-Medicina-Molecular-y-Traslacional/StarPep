/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.clustering;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractClusterizer;
import org.bapedis.graphmining.model.BiGraph;
import org.bapedis.graphmining.model.Vertex;

/**
 *
 * @author loge
 */
public class BiPartition extends AbstractClusterizer {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    private int level;
    private final AtomicBoolean atomicRun;

    public BiPartition(AlgorithmFactory factory) {
        super(factory);
        level = 3;
        atomicRun = new AtomicBoolean();
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    protected List<Cluster> cluterize() {
        List<Cluster> clusterList = new LinkedList<>();
        Vertex[] vertices = new Vertex[peptides.length];
        //Create vertices
        for (int i = 0; i < peptides.length; i++) {
            vertices[i] = new Vertex(peptides[i]);
            vertices[i].setVertexIndex(i);
        }

        ticket.switchToDeterminate((int) Math.pow(2, level));
        BiGraph bigraph = new BiGraph(vertices, pc.getGraphVisible());

        atomicRun.set(stopRun);
        BasePartition partition = new MinCutPartition(bigraph, level, ticket, atomicRun);

        fjPool.invoke(partition);
        Cluster[] batches = partition.join();
        for (Cluster c : batches) {
            clusterList.add(c);
        }
        return clusterList;
    }

    @Override
    public boolean cancel() {
        super.cancel(); 
        atomicRun.set(stopRun);
        return stopRun;
    }
    
    

}
