/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import java.util.concurrent.atomic.AtomicBoolean;
import org.bapedis.clustering.model.BiGraph;
import org.bapedis.clustering.model.Partition;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.task.ProgressTicket;

/**
 *
 * @author loge
 */
public class RandomPartition extends BasePartition {

    public RandomPartition(BiGraph bigraph, int level, ProgressTicket ticket, AtomicBoolean stopRun) {
        super(bigraph, level, ticket, stopRun);
    }

    @Override
    protected Cluster[] compute() {
        if (!stopRun.get() && bigraph.size() > MIN_SIZE  && level > 0) {
            Partition p = bigraph.getPartition();
            p.initializePartition();
            p.randomizePartition();
            bigraph.rearrange();
            MinCutPartition left = new MinCutPartition(bigraph.getLeftGraph(), level - 1, ticket, stopRun);
            MinCutPartition right = new MinCutPartition(bigraph.getRightGraph(), level - 1, ticket, stopRun);
            right.fork();
            return union(left.compute(), right.join());
        }
        return computeDirectly();
    }

}
