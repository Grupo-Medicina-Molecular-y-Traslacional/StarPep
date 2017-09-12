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
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.filters.Filter;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
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
    protected GraphView newView;
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
        ticket.start(attrModel.getNodeList().size());
        ticket.progress(NbBundle.getMessage(FilterExecutor.class, "FilterWorker.running"));

        TreeSet<String> set = filterModel.isEmpty() ? null : new TreeSet<String>();

        newView = graphModel.createView();
        Subgraph subGraph = graphModel.getGraph(newView);

        Peptide peptide;
        org.gephi.graph.api.Node graphNode;
        List<org.gephi.graph.api.Node> graphNeighbors;
        List<Edge> graphEdges;
        for (PeptideNode node : attrModel.getNodeList()) {
            if (stopRun.get()) {
                break;
            }
            peptide = node.getPeptide();
            if (isAccepted(peptide)) {
                if (!filterModel.isEmpty()) {
                    set.add(peptide.getId());
                }

                // Add graph node
                graphNode = peptide.getGraphNode();
                subGraph.addNode(graphNode);

                // Add neighbors and edges
                peptide.getGraph().readLock();
                try {
                    graphNeighbors = new LinkedList<>();
                    for (org.gephi.graph.api.Node neighbor : peptide.getGraph().getNeighbors(graphNode)) {
                        graphNeighbors.add(neighbor);
                    }

                    graphEdges = new LinkedList<>();
                    for (Edge edge : peptide.getGraph().getEdges(graphNode)) {
                        graphEdges.add(edge);
                    }
                } finally {
                    peptide.getGraph().readUnlock();
                }

                subGraph.addAllNodes(graphNeighbors);
                subGraph.addAllEdges(graphEdges);
            }
            ticket.progress();
        }
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
            attrModel.setQuickFilter(filterModel.isEmpty()? null:new QuickFilterImpl(set));
            GraphView oldView = graphModel.getVisibleView();
            if (!oldView.isMainView()) {
                graphModel.destroyView(oldView);
            }
            graphModel.setVisibleView(newView);
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
