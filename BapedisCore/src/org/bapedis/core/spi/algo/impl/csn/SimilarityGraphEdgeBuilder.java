/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.csn;

import java.util.List;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class SimilarityGraphEdgeBuilder extends RecursiveAction {

    protected static final int SEQUENTIAL_THRESHOLD = 10;
    protected static AttributesModel attrModel;
    protected static GraphModel graphModel;
    protected static Graph mainGraph, csnGraph;
    protected static Peptide[] peptides;
    protected static ProgressTicket progressTicket;
    protected static SimilarityProvider similarityProvider;
    protected static List<Edge> edgeList;

    protected int xlow, xhigh, ylow, yhigh;

    protected final static Logger log = Logger.getLogger(SimilarityGraphEdgeBuilder.class.getName());
    protected static AtomicBoolean stopRun = new AtomicBoolean(false);

    SimilarityGraphEdgeBuilder() {
        this(0, peptides.length, 0, peptides.length);
    }

    private SimilarityGraphEdgeBuilder(int xlow, int xhigh, int ylow, int yhigh) {
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
    }

    static void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    @Override
    protected void compute() {
        if (xlow >= yhigh || stopRun.get()) {
            return; // Discard the elements above the diagonal
        }
        if (xhigh - xlow <= SEQUENTIAL_THRESHOLD) {
            if (yhigh - ylow <= SEQUENTIAL_THRESHOLD) {
                if (!stopRun.get()) {
                    computeDirectly();
                }
            } else if (!stopRun.get()) {
                int middle = ylow + (yhigh - ylow) / 2;
                // up and down
                invokeAll(new SimilarityGraphEdgeBuilder(xlow, xhigh, ylow, middle),
                        new SimilarityGraphEdgeBuilder(xlow, xhigh, middle, yhigh));
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            invokeAll(new SimilarityGraphEdgeBuilder(xlow, middle, ylow, yhigh),
                    new SimilarityGraphEdgeBuilder(middle, xhigh, ylow, yhigh));
        }
    }

    private void computeDirectly() {
        Peptide peptide1, peptide2;
        double score;
        for (int y = ylow; y < yhigh; y++) {
            peptide1 = peptides[y];
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                peptide2 = peptides[x];
                if (!stopRun.get()) {
                    score = similarityProvider.computeSimilarity(peptide1, peptide2);
                    createGraphEdge(peptide1, peptide2, score);
                }
                progressTicket.progress();
            }
        }
    }

    private void createGraphEdge(Peptide peptide1, Peptide peptide2, double score) {
        Edge graphEdge;
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        String id = String.format("%s-%s", peptide1.getGraphNode().getId(), peptide2.getGraphNode().getId());
        // Add edge to main graph
        mainGraph.writeLock();
        try {
            graphEdge = mainGraph.getEdge(id);
            if (graphEdge == null) {
                graphEdge = graphModel.factory().newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
                graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

                //Set color
                graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
                graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
                graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
                graphEdge.setAlpha(0f);

                mainGraph.addEdge(graphEdge);
                edgeList.add(graphEdge);
            }
            graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);
        } finally {
            mainGraph.writeUnlock();
        }

        // Add edge to csn graph
        csnGraph.writeLock();
        try {
            if (!csnGraph.hasEdge(id) && score >= 0.7) {
                csnGraph.addEdge(graphEdge);
            }
        } finally {
            csnGraph.writeUnlock();
        }
    }

}
