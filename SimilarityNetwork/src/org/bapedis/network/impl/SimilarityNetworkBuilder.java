/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.concurrent.RecursiveAction;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.SimilarityMatrix;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;

/**
 *
 * @author Longendri Aguilera Mendoza
 */
class SimilarityNetworkBuilder extends RecursiveAction {

    protected static final int SEQUENTIAL_THRESHOLD = 10;
    protected static Peptide[] peptides;
    protected static Graph graph;
    protected static float threshold;
    protected static ProgressTicket progressTicket;
    protected static SimilarityMeasure similarityMeasure;
    protected static JQuickHistogram histogram;

    protected final SimilarityMatrix matrix;
    protected int xlow, xhigh, ylow, yhigh;

    protected final static Logger log = Logger.getLogger(SimilarityNetworkBuilder.class.getName());
    protected static AtomicBoolean stopRun = new AtomicBoolean(false);

    SimilarityNetworkBuilder() {
        this(new SimilarityMatrix(peptides), 0, peptides.length, 0, peptides.length);
    }

    private SimilarityNetworkBuilder(SimilarityMatrix matrix, int xlow, int xhigh, int ylow, int yhigh) {
        this.matrix = matrix;
        this.xlow = xlow;
        this.xhigh = xhigh;
        this.ylow = ylow;
        this.yhigh = yhigh;
    }

    static void setStopRun(boolean stop) {
        stopRun.set(stop);
    }

    public SimilarityMatrix getSimilarityMatrix() {
        return matrix;
    }

    public int getSize() {
        return matrix.getSize();
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
                invokeAll(new SimilarityNetworkBuilder(matrix, xlow, xhigh, ylow, middle),
                        new SimilarityNetworkBuilder(matrix, xlow, xhigh, middle, yhigh));
            }
        } else if (!stopRun.get()) {
            int middle = xlow + (xhigh - xlow) / 2;
            // left and right            
            invokeAll(new SimilarityNetworkBuilder(matrix, xlow, middle, ylow, yhigh),
                    new SimilarityNetworkBuilder(matrix, middle, xhigh, ylow, yhigh));
        }
    }

    private void computeDirectly() {
        float score;
        for (int y = ylow; y < yhigh; y++) {
            for (int x = xlow; x < Math.min(xhigh, y); x++) {
                if (!stopRun.get()) {
                    score = similarityMeasure.computeSimilarity(peptides[y], peptides[x]);
                    if (score >= 0.3) {
                        matrix.setValue(peptides[y], peptides[x], score);
                        histogram.addData(score);
                    }
                    progressTicket.progress();
                }
            }
        }

        // Add similarity edge to graph
        new Thread(new Runnable() {
            private final Graph graph = SimilarityNetworkBuilder.graph;

            @Override
            public void run() {
                Edge graphEdge;
                Float value;
                graph.writeLock();
                try {
                    for (int y = ylow; y < yhigh; y++) {
                        for (int x = xlow; x < Math.min(xhigh, y); x++) {
                            value = matrix.getValue(peptides[y], peptides[x]);
                            if (value != null && value >= threshold) {
                                graphEdge = createGraphEdge(peptides[y], peptides[x], value);
                                graph.addEdge(graphEdge);
                            }
                        }
                    }
                } finally {
                    graph.writeUnlock();
                }
            }

        }).start();
    }

    private Edge createGraphEdge(Peptide peptide1, Peptide peptide2, Float score) {
        GraphModel graphModel = graph.getModel();
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        String id = String.format("%s-%s", peptide1.getId(), peptide2.getId());

        // Create Edge
        Edge graphEdge = graphModel.factory().newEdge(id, peptide1.getGraphNode(), peptide2.getGraphNode(), relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        graphModel.getGraph().addEdge(graphEdge);
        graphEdge.setAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY, score);

        return graphEdge;
    }

}
