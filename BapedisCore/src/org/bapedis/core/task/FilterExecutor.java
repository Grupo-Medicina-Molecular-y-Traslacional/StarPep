/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.task;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.filters.Filter;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Subgraph;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FilterExecutor extends SwingWorker<TreeSet<String>, String> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final Workspace workspace;
    protected final FilterModel filterModel;
    protected final AttributesModel attrModel;
    protected final GraphModel graphModel;
    protected GraphView graphDBView, csnView;
    protected final ProgressTicket ticket;
    protected final AtomicBoolean stopRun;

    public FilterExecutor() {
        this(pc.getCurrentWorkspace());
    }

    public FilterExecutor(Workspace workspace) {
        this.workspace = workspace;
        this.attrModel = pc.getAttributesModel(workspace);
        this.graphModel = pc.getGraphModel(workspace);
        this.filterModel = pc.getFilterModel(workspace);
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(FilterExecutor.class, "FilterWorker.name"), new Cancellable() {
            @Override
            public boolean cancel() {
                stopRun.set(true);
                return true;
            }
        });
    }

    @Override
    protected TreeSet<String> doInBackground() throws Exception {
        publish("start");
        ticket.start(attrModel.getNodeList().size() + 1);
        ticket.progress(NbBundle.getMessage(FilterExecutor.class, "FilterWorker.running"));

        TreeSet<String> set = filterModel.isEmpty() ? null : new TreeSet<String>();

        graphDBView = graphModel.copyView(attrModel.getGraphDBView());
        Subgraph subGraphDB = graphModel.getGraph(graphDBView);

        csnView = graphModel.copyView(attrModel.getCsnView());
        Subgraph subGraphCSN = graphModel.getGraph(csnView);

        List<org.gephi.graph.api.Node> toAddNodes = new LinkedList<>();
        List<org.gephi.graph.api.Node> toRemoveNodes = new LinkedList<>();

        Peptide peptide;
        Node graphNode;
        for (PeptideNode node : attrModel.getNodeList()) {
            if (stopRun.get()) {
                break;
            }

            peptide = node.getPeptide();
            graphNode = peptide.getGraphNode();

            if (isAccepted(peptide)) {
                if (!filterModel.isEmpty()) {
                    set.add(peptide.getId());
                }

                if (!subGraphCSN.hasNode(graphNode.getId())) {
                    toAddNodes.add(graphNode);
                }
            } else if (subGraphCSN.hasNode(graphNode.getId())) {
                toRemoveNodes.add(graphNode);
            }
            ticket.progress();
        }

        // Add graph node to graph db and csn
        int relType;
        List<Node> metadataNodes = new LinkedList<>();
        List<Edge> graphEdges = new LinkedList<>();
        double score;
        for (Node node : toAddNodes) {
            // add nodes to csn
            subGraphCSN.addNode(node);
            graphEdges.clear();
            relType = graphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
            if (relType != -1) {
                for (Edge edge : graphModel.getGraph().getEdges(node, relType)) {
                    if (subGraphCSN.hasNode(edge.getSource().getId()) && subGraphCSN.hasNode(edge.getTarget().getId())) {
                        score = (double) edge.getAttribute(ProjectManager.EDGE_TABLE_PRO_SIMILARITY);
                        if (score >= attrModel.getSimilarityThreshold()) {
                            graphEdges.add(edge);
                        }
                    }
                }
            }
            subGraphCSN.addAllEdges(graphEdges);

            // add nodes to database
            subGraphDB.addNode(node);
            metadataNodes.clear();
            graphEdges.clear();
            for (AnnotationType aType : AnnotationType.values()) {
                relType = graphModel.getEdgeType(aType.getRelationType());
                if (relType != -1) {
                    for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                        metadataNodes.add(neighbor);
                        graphEdges.add(graphModel.getGraph().getEdge(node, neighbor, relType));
                    }
                }
            }
            subGraphDB.addAllNodes(metadataNodes);
            subGraphDB.addAllEdges(graphEdges);
        }

        // To remove node from graph db and csn
        for (Node node : toRemoveNodes) {
            subGraphCSN.removeNode(node);
            subGraphDB.removeNode(node);
            metadataNodes.clear();
            for (AnnotationType aType : AnnotationType.values()) {
                relType = graphModel.getEdgeType(aType.getRelationType());
                if (relType != -1) {
                    for (Node neighbor : graphModel.getGraph().getNeighbors(node, relType)) {
                        if (graphModel.getGraph().getDegree(neighbor) == 1) {
                            metadataNodes.add(neighbor);
                        }
                    }
                }
            }
            subGraphDB.removeAllNodes(metadataNodes);
        }
        ticket.progress();
        return set;
    }

    @Override
    protected void process(List<String> chunks) {
        workspace.add(this);
        filterModel.setRunning(true);
    }

    @Override
    protected void done() {
        try {
            TreeSet<String> set = get();
            attrModel.setQuickFilter(filterModel.isEmpty() ? null : new QuickFilterImpl(set));
            // destroy old view
            graphModel.destroyView(attrModel.getCsnView());
            graphModel.destroyView(attrModel.getGraphDBView());
            // set new view
            attrModel.setCsnView(csnView);
            attrModel.setGraphDBView(graphDBView);
            switch (attrModel.getMainGView()) {
                case AttributesModel.CSN_VIEW:
                    graphModel.setVisibleView(attrModel.getCsnView());
                    break;
                case AttributesModel.GRAPH_DB_VIEW:
                    graphModel.setVisibleView(attrModel.getGraphDBView());
                    break;
            }
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            ticket.finish();
            workspace.remove(this);
            filterModel.setRunning(false);
        }
    }

    private boolean isAccepted(Peptide peptide) {
        if (!filterModel.isEmpty()) {
            switch (filterModel.getRestriction()) {
                case MATCH_ALL:
                    for (Iterator<Filter> it = filterModel.getFilterIterator(); it.hasNext();) {
                        Filter filter = it.next();
                        if (!filter.accept(peptide)) {
                            return false;
                        }
                    }
                    return true;
                case MATCH_ANY:
                    for (Iterator<Filter> it = filterModel.getFilterIterator(); it.hasNext();) {
                        Filter filter = it.next();
                        if (filter.accept(peptide)) {
                            return true;
                        }
                    }
                    return false;
            }
        }
        return true;
    }

    public ProgressTicket getTicket() {
        return ticket;
    }

    public void setStopRun(boolean stopRun) {
        this.stopRun.set(stopRun);
    }

}

class QuickFilterImpl implements QuickFilter {

    private final TreeSet<String> set;

    public QuickFilterImpl(TreeSet<String> set) {
        this.set = set;
    }

    @Override
    public boolean accept(Object obj) {
        PeptideNode node = ((PeptideNode) obj);
        Peptide peptide = node.getPeptide();
        return set.contains(peptide.getId());
    }

}
