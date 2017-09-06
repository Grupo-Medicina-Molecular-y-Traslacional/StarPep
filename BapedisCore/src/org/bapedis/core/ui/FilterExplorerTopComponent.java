/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.RestrictionLevel;
import org.bapedis.core.task.FilterWorker;
import org.bapedis.core.task.ProgressTicket;
import org.bapedis.core.ui.actions.AddFilter;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.ListView;
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
@TopComponent.Registration(mode = "explorer", openAtStartup = false, position = 433)
@ActionID(category = "Window", id = "org.bapedis.core.ui.FilterExplorerTopComponent")
@ActionReference(path = "Menu/Window" , position = 433 )
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_FilterExplorerAction",
        preferredID = "FilterExplorerTopComponent"
)
@Messages({
    "CTL_FilterExplorerAction=Filter",
    "CTL_FilterExplorerTopComponent=Filter",
    "HINT_FilterExplorerTopComponent=This is a Filter window"
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

        for (RestrictionLevel restriction : RestrictionLevel.values()) {
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
        restrictiveComboBox = new javax.swing.JComboBox();
        filterToolBar1 = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();

        setLayout(new java.awt.GridBagLayout());

        viewerScrollPane.setBorder(null);
        viewerScrollPane.setPreferredSize(new java.awt.Dimension(100, 177));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(viewerScrollPane, gridBagConstraints);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/run.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, org.openide.util.NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.text")); // NOI18N
        runButton.setToolTipText(org.openide.util.NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.toolTipText")); // NOI18N
        runButton.setFocusable(false);
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
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
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        add(applyCheckBox, gridBagConstraints);

        restrictiveComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restrictiveComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.ipady = 5;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(restrictiveComboBox, gridBagConstraints);

        filterToolBar1.setFloatable(false);
        filterToolBar1.setRollover(true);
        filterToolBar1.add(jSeparator1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        add(filterToolBar1, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void restrictiveComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restrictiveComboBoxActionPerformed
        FilterModel filterModel = pc.getFilterModel();
        filterModel.setRestriction((RestrictionLevel) restrictiveComboBox.getSelectedItem());
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
                if (dropDownButton.isEnabled()) {
                    popup.show(dropDownButton, 0, dropDownButton.getHeight());
                }
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
        explorerMgr.setRootContext(filterModel.getRootContext());
        refreshRunningState(filterModel.isRunning());
    }

    private void refreshRunningState(boolean running) {
        restrictiveComboBox.setEnabled(!running);
        for (Component c : filterToolBar1.getComponents()) {
            c.setEnabled(!running);
        }
        applyCheckBox.setEnabled(!running);
        viewerScrollPane.setEnabled(!running);
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

        Workspace workspace = pc.getCurrentWorkspace();
        FilterWorker worker = new FilterWorker(workspace, atrrModel, graphModel, filterModel);
        workspace.add(worker);

        filterModel.setRunning(true);
        ProgressTicket progress = worker.getTicket();
        progress.start(worker.getTotalProgress());
        worker.execute();
    }

    private void stop() {
        Workspace workspace = pc.getCurrentWorkspace();
        Collection<? extends FilterWorker> savedWorker = workspace.getLookup().lookupAll(FilterWorker.class);
        if (!savedWorker.isEmpty()) {
            FilterWorker worker = savedWorker.iterator().next();
            worker.setStopRun(true);
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
                refreshRunningState(filterModel.isRunning());
            } else if (!filterModel.isRunning() && applyCheckBox.isSelected()) {
                switch (evt.getPropertyName()) {
                    case FilterModel.ADDED_FILTER:
                    case FilterModel.EDITED_FILTER:
                        runFilter();
                        break;
                    case FilterModel.REMOVED_FILTER:
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
                        break;
                    case FilterModel.CHANGED_RESTRICTION:
                        if (!filterModel.isEmpty()) {
                            runFilter();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
