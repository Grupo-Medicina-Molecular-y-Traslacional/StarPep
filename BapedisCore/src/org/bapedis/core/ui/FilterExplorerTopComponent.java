/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.ui.actions.AddFilter;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Subgraph;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ListView;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;
import org.openide.util.Utilities;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bapedis.core.ui//FilterExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "FilterExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = false)
@ActionID(category = "Window", id = "org.bapedis.core.ui.FilterExplorerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_FilterExplorerAction",
        preferredID = "FilterExplorerTopComponent"
)
@Messages({
    "CTL_FilterExplorerAction=FilterExplorer",
    "CTL_FilterExplorerTopComponent=Filter Explorer",
    "HINT_FilterExplorerTopComponent=This is a FilterExplorer window"
})
public final class FilterExplorerTopComponent extends TopComponent implements WorkspaceEventListener, PropertyChangeListener, ExplorerManager.Provider {

    protected final ExplorerManager explorerMgr;
    protected final ProjectManager pc;
    private static final String AUTO_APPLY = "AUTO_APPLY";

    public FilterExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_FilterExplorerTopComponent());
        setToolTipText(Bundle.HINT_FilterExplorerTopComponent());

        explorerMgr = new ExplorerManager();
        associateLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()));
        pc = Lookup.getDefault().lookup(ProjectManager.class);

        for (FilterModel.RestrictionLevel restriction : FilterModel.RestrictionLevel.values()) {
            restrictiveComboBox.addItem(restriction);
        }

        filterToolBar1.add(createAddFilterButton());
        List<? extends Action> actions = Utilities.actionsForPath("Actions/EditFilter");
        for (Action action : actions) {
            filterToolBar1.add(action);
        }
        viewerScrollPane.setViewportView(new ListView());

        applyCheckBox.setSelected(NbPreferences.forModule(FilterModel.class).getBoolean(AUTO_APPLY, true));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        viewerScrollPane = new javax.swing.JScrollPane();
        runButton = new javax.swing.JButton();
        applyCheckBox = new javax.swing.JCheckBox();
        filterToolBar1 = new javax.swing.JToolBar();
        restrictiveComboBox = new javax.swing.JComboBox();
        jSeparator1 = new javax.swing.JToolBar.Separator();

        setLayout(new java.awt.GridBagLayout());

        viewerScrollPane.setPreferredSize(new java.awt.Dimension(100, 177));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(viewerScrollPane, gridBagConstraints);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/run.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, org.openide.util.NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.text")); // NOI18N
        runButton.setFocusable(false);
        runButton.setPreferredSize(new java.awt.Dimension(68, 29));
        runButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(runButton, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(applyCheckBox, org.openide.util.NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.applyCheckBox.text")); // NOI18N
        applyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(applyCheckBox, gridBagConstraints);

        filterToolBar1.setFloatable(false);
        filterToolBar1.setRollover(true);

        restrictiveComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restrictiveComboBoxActionPerformed(evt);
            }
        });
        filterToolBar1.add(restrictiveComboBox);
        filterToolBar1.add(jSeparator1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(filterToolBar1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void restrictiveComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restrictiveComboBoxActionPerformed
        FilterModel filterModel = pc.getFilterModel();
        filterModel.setRestriction((FilterModel.RestrictionLevel) restrictiveComboBox.getSelectedItem());
    }//GEN-LAST:event_restrictiveComboBoxActionPerformed

    private void applyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyCheckBoxActionPerformed
        NbPreferences.forModule(FilterModel.class).putBoolean(AUTO_APPLY, applyCheckBox.isSelected());
    }//GEN-LAST:event_applyCheckBoxActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        FilterModel filterModel = pc.getFilterModel();
        if (filterModel.isRunning()) {
            stop();
        } else {
            runFilter();
        }
    }//GEN-LAST:event_runButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox applyCheckBox;
    private javax.swing.JToolBar filterToolBar1;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JComboBox restrictiveComboBox;
    private javax.swing.JButton runButton;
    private javax.swing.JScrollPane viewerScrollPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        pc.addWorkspaceEventListener(this);
        Workspace currentWs = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWs);
    }

    @Override
    public void componentClosed() {
        pc.removeWorkspaceEventListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private JButton createAddFilterButton() {
        final JPopupMenu popup = new JPopupMenu();
        FilterFactory[] factories = pc.getFilterFactories();
        for (final FilterFactory factory : factories) {
            popup.add(new AddFilter(factory));
        }

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/add.png", false), popup);
        dropDownButton.setToolTipText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.addFilter.tooltiptext"));
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.show(dropDownButton, 0, dropDownButton.getHeight());
            }
        });
        return dropDownButton;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            FilterModel oldModel = pc.getFilterModel(oldWs);
            oldModel.removePropertyChangeListener(this);
        }
        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);
        restrictiveComboBox.setSelectedItem(filterModel.getRestriction());
        setFilterModel(filterModel);
    }

    private void setFilterModel(FilterModel filterModel) {
        if (filterModel.isEmpty()) {
            explorerMgr.setRootContext(Node.EMPTY);
        } else {
            explorerMgr.setRootContext(filterModel.getRootContext());
        }
        setRunningState(filterModel.isRunning());
    }

    private void setEnableState(boolean enabled) {
        runButton.setEnabled(enabled);
        filterToolBar1.setEnabled(enabled);
        applyCheckBox.setEnabled(enabled);
        viewerScrollPane.setEnabled(enabled);
    }

    private void setRunningState(boolean running) {
        setEnableState(!running);
        if (running) {
            runButton.setText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.stopButton.text"));
            runButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/stop.png", false));
            runButton.setToolTipText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.stopButton.tooltip"));
        } else {
            runButton.setText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.text"));
            runButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/run.gif", false));
            runButton.setToolTipText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.tooltip"));
        }
    }

    private void runFilter() {
        AttributesModel atrrModel = pc.getAttributesModel();
        FilterModel filterModel = pc.getFilterModel();
        GraphModel graphModel = pc.getGraphModel();

        FilterWorker worker = new FilterWorker(atrrModel, graphModel, filterModel);

        Workspace workspace = pc.getCurrentWorkspace();
        workspace.add(worker);

        filterModel.setRunning(true);
        worker.execute();
    }

    private void stop() {
        Workspace workspace = pc.getCurrentWorkspace();
        Collection<? extends FilterWorker> savedWorker = workspace.getLookup().lookupAll(FilterWorker.class);
        if (!savedWorker.isEmpty()) {
            FilterWorker worker = savedWorker.iterator().next();
            worker.cancel(true);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof FilterModel) {
            FilterModel filterModel = (FilterModel) evt.getSource();            
            if (evt.getPropertyName().equals(FilterModel.RUNNING)) {
                setRunningState(filterModel.isRunning());
            } else if (!filterModel.isRunning()) {
                if (evt.getPropertyName().equals(FilterModel.ADDED_FILTER)
                        || evt.equals(FilterModel.CHANGED_RESTRICTION)
                        || evt.equals(FilterModel.EDITED_FILTER)) {
                    runFilter();

                } else if (evt.equals(FilterModel.REMOVED_FILTER)) {
                    if (filterModel.isEmpty()) {
                        AttributesModel attr = pc.getAttributesModel();
                        attr.setQuickFilter(null);
                        GraphModel graphModel = pc.getGraphModel();
                        GraphView graphView = graphModel.getGraph().getView();
                        
                        GraphView oldView = graphModel.getVisibleView();
                        if (!oldView.isMainView()) {
                            graphModel.destroyView(oldView);
                        }
                        graphModel.setVisibleView(graphView);
                    } else {
                        runFilter();
                    }
                }
            }
        }
    }
}

class FilterWorker extends SwingWorker<Void, Void> {

    private final FilterModel filterModel;
    private final AttributesModel attrModel;
    private final GraphModel graphModel;
    private TreeSet<String> set;
    private GraphView newView;
    private final ProgressTicket progress;

    public FilterWorker(AttributesModel attrModel, GraphModel graphModel, FilterModel filterModel) {
        this.attrModel = attrModel;
        this.graphModel = graphModel;
        this.filterModel = filterModel;

        progress = new ProgressTicket(NbBundle.getMessage(FilterWorker.class, "FilterWorker.name"), new Cancellable() {
            @Override
            public boolean cancel() {
                return FilterWorker.this.cancel(true);
            }
        });
    }

    @Override
    protected Void doInBackground() throws Exception {
        List<PeptideNode> nodeList = attrModel.getNodeList();

        set = new TreeSet<>();
        newView = graphModel.createView();
        Subgraph subGraph = graphModel.getGraph(newView);

        progress.start(nodeList.size());

        Peptide peptide;
        org.gephi.graph.api.Node graphNode;
        List<org.gephi.graph.api.Node> graphNeighbors;
        List<Edge> graphEdges;
        for (PeptideNode node : nodeList) {
            peptide = node.getPeptide();
            if (!isCancelled() && isAccepted(peptide)) {
                set.add(peptide.getId());

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
            progress.progress();
        }
        return null;
    }

    @Override
    protected void done() {
        try {
            get();
            attrModel.setQuickFilter(new QuickFilterImpl(set));
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
            progress.finish();
            filterModel.setRunning(false);
        }
    }

    private boolean isAccepted(Peptide peptide) {
        if (!filterModel.isEmpty()) {
            switch (filterModel.getRestriction()) {
                case MATCH_ALL:
                    for (Filter filter : filterModel.getFilters()) {
                        if (!filter.accept(peptide)) {
                            return false;
                        }
                    }
                    return true;
                case MATCH_ANY:
                    for (Filter filter : filterModel.getFilters()) {
                        if (filter.accept(peptide)) {
                            return true;
                        }
                    }
                    return false;
            }
        }
        return true;
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
