/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.util.Iterator;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import org.bapedis.clustering.model.BiGraph;
import org.bapedis.clustering.model.Vertex;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public abstract class BasePartition extends RecursiveTask<Cluster[]> {

    protected static final AtomicInteger counter = new AtomicInteger(1);
    public static final int MIN_SIZE = 10;
    protected final ProgressTicket ticket;
    protected final AtomicBoolean stopRun;

    protected int level;
    protected final BiGraph bigraph;

    public BasePartition(BiGraph bigraph, int level, ProgressTicket ticket, AtomicBoolean stopRun) {
        this.bigraph = bigraph;
        this.level = level;
        this.ticket = ticket;
        this.stopRun = stopRun;
    }

    protected Cluster[] union(Cluster[] leftBatches, Cluster[] righBatchs) {
        Cluster[] batches = new Cluster[leftBatches.length + righBatchs.length];
        System.arraycopy(leftBatches, 0, batches, 0, leftBatches.length);
        System.arraycopy(righBatchs, 0, batches, leftBatches.length, righBatchs.length);

        return batches;
    }

    protected Cluster[] computeDirectly() {
        Cluster batch = new Cluster(counter.getAndIncrement());
        Vertex u;
        for (Iterator<Vertex> it = bigraph.getAllVertices(); it.hasNext();) {
            u = it.next();
            batch.addMember(u.getPeptide());
        }
        ticket.progress();
        return new Cluster[]{batch};
    }
}
