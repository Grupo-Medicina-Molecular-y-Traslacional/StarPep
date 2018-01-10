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
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
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
    protected static GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected final Workspace workspace;
    protected final FilterModel filterModel;
    protected final AttributesModel attrModel;
    protected final Graph graph;
    protected final ProgressTicket ticket;
    protected final AtomicBoolean stopRun;
    protected final String taskName = "Filter";

    public FilterExecutor() {
        this(pc.getCurrentWorkspace());
    }

    public FilterExecutor(Workspace workspace) {
        this.workspace = workspace;
        this.attrModel = pc.getAttributesModel(workspace);
        this.graph = pc.getGraphModel(workspace).getGraphVisible();
        this.filterModel = pc.getFilterModel(workspace);
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(FilterExecutor.class, "FilterWorker.name", workspace.getName()), new Cancellable() {
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
        pc.reportRunningTask(taskName, workspace);

        ticket.start(attrModel.getNodeList().size() + 1); // + refresh graph view
        ticket.progress(NbBundle.getMessage(FilterExecutor.class, "FilterWorker.running"));

        TreeSet<String> set = filterModel.isEmpty() ? null : new TreeSet<String>();

        List<Node> toAddNodes = new LinkedList<>();
        List<Node> toRemoveNodes = new LinkedList<>();

        Peptide peptide;
        Node graphNode;
        graph.readLock();
        try {
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

                    if (!graph.hasNode(graphNode.getId())) {
                        toAddNodes.add(graphNode);
                    }
                } else if (graph.hasNode(graphNode.getId())) {
                    toRemoveNodes.add(graphNode);
                }
                ticket.progress();
            }
        } finally {
            graph.readUnlock();
        }

        // To refresh graph view   
        graphWC.refreshGraphView(toAddNodes, toRemoveNodes);
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

//            attrModel.fireChangedGraphView();
            if (filterModel.isEmpty()) {
                pc.reportMsg(NbBundle.getMessage(FilterExecutor.class, "FilterExecutor.noFilter"), workspace);
            } else {
                pc.reportMsg(NbBundle.getMessage(FilterExecutor.class, "FilterExecutor.output.text", set.size()), workspace);
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            pc.reportError(ex.getCause().toString(), workspace);
        } finally {
            ticket.finish();
            workspace.remove(this);
            filterModel.setRunning(false);
            if (pc.getCurrentWorkspace() != workspace) {
                String txt = NbBundle.getMessage(FilterExecutor.class, "Workspace.notify.finishedTask", taskName);
                pc.workspaceChangeNotification(txt, workspace);
            }
            pc.reportFinishedTask(taskName, workspace);
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
