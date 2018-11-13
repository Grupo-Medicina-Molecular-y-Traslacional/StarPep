/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.appearance.plugin;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.appearance.api.AppearanceController;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.api.PartitionFunction;
import org.gephi.appearance.api.Ranking;
import org.gephi.appearance.api.RankingFunction;
import org.gephi.desktop.appearance.AppearanceUIModel;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.ElementIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class AppearanceAlgorithm implements Algorithm {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final AppearanceAlgorithmFactory factory;
    protected final AppearanceController appearanceController;
    protected Function selectedFunction;
    protected GraphModel graphModel;
    protected ProgressTicket ticket;
    protected boolean stopRun;

    public AppearanceAlgorithm(AppearanceAlgorithmFactory factory) {
        this.factory = factory;
        appearanceController = Lookup.getDefault().lookup(AppearanceController.class);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        AppearanceUIModel model = workspace.getLookup().lookup(AppearanceUIModel.class);
        if (model != null) {
            selectedFunction = model.getSelectedFunction();
        }

        graphModel = pc.getGraphModel(workspace);
        ticket = progressTicket;

        stopRun = false;
    }

    @Override
    public void endAlgo() {
        selectedFunction = null;
        graphModel = null;
        ticket = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    /**
     * Apply the function's transformer. If the function is for nodes all nodes
     * in the visible graph will be transformed. Similarly for edges.
     */
    @Override
    public void run() {
        if (selectedFunction != null) {
            Graph graph = graphModel.getGraphVisible();
            //Refresh ranking and partition
            Ranking ranking = null;
            Partition partition = null;
            if (selectedFunction instanceof RankingFunction) {
                ranking = ((RankingFunction) selectedFunction).getRanking();
            }
            if (selectedFunction instanceof PartitionFunction) {
                partition = ((PartitionFunction) selectedFunction).getPartition();
            }
            if (ranking != null) {
                ranking.refresh();
            }
            if (partition != null) {
                partition.refresh();
            }

            ElementIterable<? extends Element> iterable;
            int taskSize = 0;
            if (selectedFunction.getElementClass().equals(Node.class)) {
                iterable = graph.getNodes();
                taskSize = graph.getNodeCount();
            } else {
                iterable = graph.getEdges();
                taskSize = graph.getEdgeCount();
            }
            if (taskSize > 0) {
                try {
                    ticket.switchToDeterminate(taskSize);
                    for (Element element : iterable) {
                        if (!stopRun) {
                            selectedFunction.transform(element, graph);
                            ticket.progress();
                        } else {
                            break;
                        }
                    }
                } catch (Exception e) {
                    iterable.doBreak();
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    } else {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

}
