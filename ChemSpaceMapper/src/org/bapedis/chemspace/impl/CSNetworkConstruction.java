/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import static org.bapedis.chemspace.impl.NetworkConstructionAlg.fjPool;
import org.bapedis.chemspace.model.DistanceMatrix;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;

/**
 *
 * @author Loge
 */
public class CSNetworkConstruction extends NetworkConstructionAlg implements Cloneable {

    private double defaultDensity;

    public CSNetworkConstruction(AlgorithmFactory factory) {
        super(factory);
        defaultDensity = 0.1;
    }

    public double getDefaultDensity() {
        return defaultDensity;
    }

    public void setDefaultDensity(double defaultDensity) {
        this.defaultDensity = defaultDensity;
    }

    @Override
    protected double createNetwork() {
        // Setup Similarity Matrix Builder
        DistanceMatrixBuilder task = new DistanceMatrixBuilder(peptides, distFactory, descriptorMatrix, ticket, stopRun);
        int workunits = task.getWorkUnits();
        ticket.switchToDeterminate(workunits + peptides.length - 1);

        // Compute new distance matrix        
        fjPool.invoke(task);
        task.join();
        DistanceMatrix distanceMatrix = task.getDistanceMatrix();

        //Create full network
        Node node1, node2;
        Edge graphEdge;
        double maxDistance = 0;
        double distance;
        for (int i = 0; i < peptides.length - 1 && !stopRun.get(); i++) {
            node1 = peptides[i].getGraphNode();
            for (int j = i + 1; j < peptides.length && !stopRun.get(); j++) {
                distance = distanceMatrix.getValue(peptides[i], peptides[j]);
                node2 = peptides[j].getGraphNode();
                mainGraph.writeLock();
                try {
                    graphEdge = mainGraph.getEdge(node1, node2, relType);
                    if (graphEdge == null) {
                        graphEdge = createEdge(node1, node2);
                        mainGraph.addEdge(graphEdge);
                        graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_DISTANCE, distance);
                    }
                } finally {
                    mainGraph.writeUnlock();
                }
                if (distance > maxDistance) {
                    maxDistance = distance;
                }
            }
            ticket.progress();
        }

        return maxDistance;
    }

    @Override
    protected void updateSimilarityEdges() {
        //Estimate current threshold
        int t = 0;
        while (t + 1 <= 100 && densityValues[t + 1] >= defaultDensity) {
            t++;
        }
        currentThreshold = t / 100.0;
        super.updateSimilarityEdges(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
