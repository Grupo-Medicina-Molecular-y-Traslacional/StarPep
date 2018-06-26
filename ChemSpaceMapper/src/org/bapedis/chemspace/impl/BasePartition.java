/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.Iterator;
import java.util.concurrent.RecursiveTask;
import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.chemspace.model.Batch;
import org.bapedis.chemspace.model.BiGraph;
import org.bapedis.chemspace.model.Vertex;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public abstract class BasePartition extends RecursiveTask<Batch[]> {

    public static final int MIN_SIZE=10;
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

    protected Batch[] union(Batch[] leftBatches, Batch[] righBatchs) {
        Batch[] batches = new Batch[leftBatches.length + righBatchs.length];
        System.arraycopy(leftBatches, 0, batches, 0, leftBatches.length);
        System.arraycopy(righBatchs, 0, batches, leftBatches.length, righBatchs.length);

        return batches;
    }

    protected Batch[] computeDirectly() {
        Batch batch = new Batch(bigraph.size());
        Vertex u;
        for (Iterator<Vertex> it = bigraph.getAllVertices(); it.hasNext();) {
            u = it.next();
            batch.addPeptide(u.getPeptide());
        }
        ticket.progress();
        return new Batch[]{batch};
    }
}
