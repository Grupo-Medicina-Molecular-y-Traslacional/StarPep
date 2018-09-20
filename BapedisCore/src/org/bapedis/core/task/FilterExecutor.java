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
import org.bapedis.core.spi.alg.Algorithm;
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
public class FilterExecutor extends SwingWorker<TreeSet<Integer>, Object> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final AlgorithmExecutor executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);
    protected static GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected final Workspace workspace;
    protected final FilterModel filterModel;
    protected final AttributesModel attrModel;
    protected final Graph graph;
    protected final ProgressTicket ticket;
    protected final AtomicBoolean stopRun;
    protected Algorithm preprocessing;
    protected final String taskName;

    public FilterExecutor() {
        this(pc.getCurrentWorkspace());
    }

    public FilterExecutor(Workspace workspace) {
        this.workspace = workspace;
        this.attrModel = pc.getAttributesModel(workspace);
        this.graph = pc.getGraphModel(workspace).getGraphVisible();
        this.filterModel = pc.getFilterModel(workspace);
        stopRun = new AtomicBoolean(false);
        ticket = new ProgressTicket(NbBundle.getMessage(FilterExecutor.class, "FilterExecutor.task.name", workspace.getName()), new Cancellable() {
            @Override
            public boolean cancel() {
                return FilterExecutor.this.cancel();
            }
        });
        taskName = NbBundle.getMessage(FilterExecutor.class, "FilterExecutor.name");
    }

    public FilterModel getFilterModel() {
        return filterModel;
    }        

    private void runAlgorithm(Algorithm preprocessingAlgo) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                executor.execute(preprocessingAlgo, new AlgorithmListener() {
                    @Override
                    public void algorithmFinished(Algorithm algo) {
                        synchronized (FilterExecutor.this) {
                            FilterExecutor.this.notify();
                        }
                    }
                }, new AlgorithmErrorHandler() {
                    @Override
                    public void fatalError(Throwable t) {
                        synchronized (FilterExecutor.this) {
                            FilterExecutor.this.notify();
                        }
                        Exceptions.printStackTrace(t);
                    }
                });
            }
        }).start();
    }

    @Override
    protected TreeSet<Integer> doInBackground() throws Exception {
        pc.reportRunningTask(taskName, workspace);

        ticket.start();
        ticket.progress(NbBundle.getMessage(FilterExecutor.class, "FilterExecutor.running"));

        TreeSet<Integer> set = filterModel.isEmpty() ? null : new TreeSet<Integer>();

        List<PeptideNode> peptideNodes = attrModel.getNodeList();
        Peptide[] targets = new Peptide[peptideNodes.size()];
        int pos = 0;
        for (PeptideNode node : peptideNodes) {
            targets[pos++] = node.getPeptide();
        }

        // Run preprocessing algorithms
        for (Iterator<Filter> it = filterModel.getFilterIterator(); it.hasNext();) {
            Filter filter = it.next();
            preprocessing = filter.getPreprocessing(targets);
            if (preprocessing != null) {
                runAlgorithm(preprocessing);
                //wait for preprocessing...
                try {
                    synchronized (this) {
                        this.wait();
                    }
                } catch (InterruptedException ex) {
                }
            }
            preprocessing = null;
        }

        // Begin filtering process
        ticket.switchToDeterminate(targets.length);
        List<Node> toAddNodes = new LinkedList<>();
        List<Node> toRemoveNodes = new LinkedList<>();

        Node graphNode;
        graph.readLock();
        try {
            for (Peptide peptide : targets) {
                if (stopRun.get()) {
                    set = null;
                    toAddNodes = null;
                    toRemoveNodes = null;
                    break;
                }

                graphNode = peptide.getGraphNode();

                if (isAccepted(peptide)) {
                    if (!filterModel.isEmpty()) {
                        set.add(peptide.getId());
                    }

                    toAddNodes.add(graphNode);
                } else if (graph.hasNode(graphNode.getId())) {
                    toRemoveNodes.add(graphNode);
                }
                ticket.progress();
            }
        } finally {
            graph.readUnlock();
        }

        // To refresh graph view  
        ticket.progress(NbBundle.getMessage(FilterExecutor.class, "FilterExecutor.refreshingGView"));
        ticket.switchToIndeterminate();
        graphWC.refreshGraphView(workspace, toAddNodes, toRemoveNodes);

        return set;
    }

    public FilterExecutor(Workspace workspace, FilterModel filterModel, AttributesModel attrModel, Graph graph, ProgressTicket ticket, AtomicBoolean stopRun, String taskName) {
        this.workspace = workspace;
        this.filterModel = filterModel;
        this.attrModel = attrModel;
        this.graph = graph;
        this.ticket = ticket;
        this.stopRun = stopRun;
        this.taskName = taskName;
    }

    public boolean cancel() {
        if (preprocessing != null) {
            preprocessing.cancel();
        }
        stopRun.set(true);
        return true;
    }

    @Override
    protected void done() {
        try {
            TreeSet<Integer> set = get();
            attrModel.setQuickFilter(set == null || filterModel.isEmpty() ? null : new QuickFilterImpl(set));

//            attrModel.fireChangedGraphView();
            if (attrModel.getQuickFilter() == null) {
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

    private static class QuickFilterImpl implements QuickFilter {

        private final TreeSet<Integer> set;

        public QuickFilterImpl(TreeSet<Integer> set) {
            this.set = set;
        }

        @Override
        public boolean accept(Object obj) {
            PeptideNode node = ((PeptideNode) obj);
            Peptide peptide = node.getPeptide();
            return set.contains(peptide.getId());
        }

    }

}
