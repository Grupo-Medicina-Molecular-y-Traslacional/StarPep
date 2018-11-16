/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.clustering.model.BiGraph;
import org.bapedis.clustering.model.Vertex;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.impl.AbstractCluster;

/**
 *
 * @author loge
 */
public class CommunityStructure extends AbstractCluster {

    protected static final ForkJoinPool fjPool = new ForkJoinPool();
    private int level;

    public CommunityStructure(AlgorithmFactory factory) {
        super(factory);
        level = 3;
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

        BasePartition partition = new MinCutPartition(bigraph, level, ticket, new AtomicBoolean(stopRun));

        fjPool.invoke(partition);
        Cluster[] batches = partition.join();
        for (Cluster c : batches) {
            clusterList.add(c);
        }
        return clusterList;
    }

}
