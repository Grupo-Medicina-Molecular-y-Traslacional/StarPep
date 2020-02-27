/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.subnet;

import java.awt.Color;
import java.util.Stack;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.graphmining.algorithms.AbstractShortestPathAlgorithm;
import org.bapedis.graphmining.algorithms.DijkstraShortestPathAlgorithm;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class ShortestPath implements Algorithm {

    static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private Workspace workspace;
    private ProgressTicket ticket;
    private GraphModel graphModel;
    private boolean stopRun;
    private String sourceNodeName, targetNodeName;
    private final AlgorithmFactory factory;
    private Color color;
    protected final NotifyDescriptor noPath;

    public ShortestPath(AlgorithmFactory factory) {
        this.factory = factory;
        color = Color.RED;
        noPath = new NotifyDescriptor.Message(NbBundle.getMessage(ShortestPath.class, "ShortestPath.none"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        this.ticket = progressTicket;
        graphModel = pc.getGraphModel(workspace);
        stopRun = false;
    }

    public String getSourceNodeName() {
        return sourceNodeName;
    }

    public void setSourceNodeName(String sourceNodeName) {
        this.sourceNodeName = sourceNodeName;
    }

    public String getTargetNodeName() {
        return targetNodeName;
    }

    public void setTargetNodeName(String targetNodeName) {
        this.targetNodeName = targetNodeName;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void endAlgo() {
        this.workspace = null;
        this.ticket = null;
        graphModel = null;
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

    @Override
    public void run() {
        Graph graph = graphModel.getGraphVisible();

        Node sourceNode = null;
        Node targetNode = null;
        NodeIterable nodeIterable = graph.getNodes();

        for (Node node : nodeIterable) {
            if (sourceNode == null && node.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME).equals(sourceNodeName)) {
                sourceNode = node;
            }
            if (targetNode == null && node.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME).equals(targetNodeName)) {
                targetNode = node;
            }
            if (sourceNode != null && targetNode != null) {
                nodeIterable.doBreak();
                break;
            }
        }

        if (!stopRun && sourceNode == null) {
            NotifyDescriptor invalidName = new NotifyDescriptor.Message(NbBundle.getMessage(ShortestPath.class, "ShortestPath.invalidInput", sourceNodeName), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(invalidName);
            cancel();
        }
        if (!stopRun && targetNode == null) {
            NotifyDescriptor invalidName = new NotifyDescriptor.Message(NbBundle.getMessage(ShortestPath.class, "ShortestPath.invalidInput", targetNodeName), NotifyDescriptor.ERROR_MESSAGE);
            DialogDisplayer.getDefault().notify(invalidName);
            cancel();
        }

        if (!stopRun) {
            AbstractShortestPathAlgorithm algorithm = new DijkstraShortestPathAlgorithm(graphModel.getGraphVisible(), sourceNode);
            algorithm.compute();

            double distance;
            if ((distance = algorithm.getDistances().get(targetNode)) != Double.POSITIVE_INFINITY) {
                targetNode.setColor(color);
                Stack<String> pathway = new Stack<>();
                pathway.push((String) targetNode.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
                Edge predecessorEdge = algorithm.getPredecessorIncoming(targetNode);
                Node predecessor = algorithm.getPredecessor(targetNode);
                while (predecessorEdge != null && predecessor != sourceNode) {
                    predecessorEdge.setColor(color);
                    predecessor.setColor(color);
                    pathway.push(String.format(" -%.2f-> ", predecessorEdge.getWeight()));
                    pathway.push((String) predecessor.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
                    predecessorEdge = algorithm.getPredecessorIncoming(predecessor);
                    predecessor = algorithm.getPredecessor(predecessor);
                }
                if (predecessorEdge != null) {
                    predecessorEdge.setColor(color);
                    pathway.push(String.format(" -%.2f-> ", predecessorEdge.getWeight()));
                }
                if (sourceNode != targetNode) {
                    sourceNode.setColor(color);
                    pathway.push((String) sourceNode.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
                }
                String path = shortestPathResult(pathway);
                pc.reportMsg(NbBundle.getMessage(ShortestPath.class, "ShortestPath.result", path), workspace);
                pc.reportMsg(NbBundle.getMessage(ShortestPath.class, "ShortestPath.distance", distance), workspace);
            } else {
                DialogDisplayer.getDefault().notify(noPath);
            }
        }
    }

    private String shortestPathResult(Stack<String> pathway) {
        StringBuilder sb = new StringBuilder();
        while (!pathway.empty()) {
            sb.append(pathway.pop());
        }
        return sb.toString();
    }

}
