/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Iterator;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.RestrictionLevel;
import org.bapedis.core.task.FilterExecutor;
import org.bapedis.core.ui.actions.AddFilter;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
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
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

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
@TopComponent.Registration(mode = "explorer", openAtStartup = true, position = 433)
@ActionID(category = "Window", id = "org.bapedis.core.ui.FilterExplorerTopComponent")
@ActionReference(path = "Menu/Tools", position = 20)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_FilterExplorerAction",
        preferredID = "FilterExplorerTopComponent"
)
@Messages({
    "CTL_FilterExplorerAction=Peptide filtering",
    "CTL_FilterExplorerTopComponent=Filter",
    "HINT_FilterExplorerTopComponent=Peptides filtering window"
})
public final class FilterExplorerTopComponent extends TopComponent implements WorkspaceEventListener, PropertyChangeListener, ExplorerManager.Provider {

    protected final ExplorerManager explorerMgr;
    protected final ProjectManager pc;
    private static final String AUTO_APPLY = "AUTO_APPLY";
    private final RichTooltip richTooltip;

    public FilterExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_FilterExplorerTopComponent());
        setToolTipText(Bundle.HINT_FilterExplorerTopComponent());

        explorerMgr = new ExplorerManager();
        associateLookup(new ProxyLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()),
                Lookups.singleton(new MetadataNavigatorLookupHint()), 
                Lookups.singleton(new GraphElementNavigatorLookupHint()),
                Lookups.singleton(new ClusterNavigatorLookupHint())));

        pc = Lookup.getDefault().lookup(ProjectManager.class);

        DefaultComboBoxModel comboModel = (DefaultComboBoxModel) restrictiveComboBox.getModel();
        for (RestrictionLevel restriction : RestrictionLevel.values()) {
            comboModel.addElement(restriction);
        }

        filterToolBar.add(createAddFilterButton());
        List<? extends Action> actions = Utilities.actionsForPath("Actions/EditFilter");
        for (Action action : actions) {
            filterToolBar.add(action);
        }
        viewerScrollPane.setViewportView(new ListView());

        applyCheckBox.setSelected(NbPreferences.forModule(FilterModel.class).getBoolean(AUTO_APPLY, true));
        richTooltip = new RichTooltip(Bundle.CTL_FilterExplorerTopComponent(), NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.info.text"));
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
        filterToolBar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        infoLabel = new javax.swing.JLabel();

        setLayout(new java.awt.GridBagLayout());

        viewerScrollPane.setBorder(null);
        viewerScrollPane.setPreferredSize(new java.awt.Dimension(100, 177));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
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
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
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
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        add(applyCheckBox, gridBagConstraints);

        restrictiveComboBox.setMinimumSize(new java.awt.Dimension(150, 25));
        restrictiveComboBox.setPreferredSize(new java.awt.Dimension(150, 25));
        restrictiveComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                restrictiveComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(restrictiveComboBox, gridBagConstraints);

        filterToolBar.setFloatable(false);
        filterToolBar.setRollover(true);
        filterToolBar.add(jSeparator1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(filterToolBar, gridBagConstraints);

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.infoLabel.text")); // NOI18N
        infoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                infoLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                infoLabelMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(infoLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void restrictiveComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restrictiveComboBoxActionPerformed
        FilterModel filterModel = pc.getFilterModel();
        RestrictionLevel restriction = (RestrictionLevel) restrictiveComboBox.getSelectedItem();
        if (filterModel.getRestriction() != restriction) {
            filterModel.setRestriction(restriction);
        }
    }//GEN-LAST:event_restrictiveComboBoxActionPerformed

    private void applyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyCheckBoxActionPerformed
        NbPreferences.forModule(FilterModel.class).putBoolean(AUTO_APPLY, applyCheckBox.isSelected());
    }//GEN-LAST:event_applyCheckBoxActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        FilterModel filterModel = pc.getFilterModel();
        if (filterModel.isEmpty()) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.filterModel.empty"), NotifyDescriptor.WARNING_MESSAGE));
        } else if (filterModel.isRunning()) {
            stop();
        } else {
            runFilter();
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        richTooltip.hideTooltip();
    }//GEN-LAST:event_infoLabelMouseExited

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
        richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
    }//GEN-LAST:event_infoLabelMouseEntered

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox applyCheckBox;
    private javax.swing.JToolBar filterToolBar;
    private javax.swing.JLabel infoLabel;
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
        FilterModel oldModel = pc.getFilterModel();
        oldModel.removePropertyChangeListener(this);
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
        for (Iterator<? extends FilterFactory> it = pc.getFilterFactoryIterator(); it.hasNext();) {
            FilterFactory factory = it.next();
            popup.add(new AddFilter(factory));
        }

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/add.png", false), popup);
        dropDownButton.setFocusable(false);
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
    public synchronized void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            FilterModel oldModel = pc.getFilterModel(oldWs);
            oldModel.removePropertyChangeListener(this);
        }
        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);
        restrictiveComboBox.setSelectedItem(filterModel.getRestriction());

        explorerMgr.setRootContext(filterModel.getRootContext());
        refreshRunningState(filterModel.isRunning());
    }

    private void refreshRunningState(boolean running) {
        restrictiveComboBox.setEnabled(!running);
        applyCheckBox.setEnabled(!running);
        viewerScrollPane.setEnabled(!running);
        setBusy(running);
        if (running) {
            runButton.setText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.stopButton.text"));
            runButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/stop.png", false));
            runButton.setToolTipText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.stopButton.tooltip"));
        } else {
            runButton.setText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.text"));
            runButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/run.gif", false));
            runButton.setToolTipText(NbBundle.getMessage(FilterExplorerTopComponent.class, "FilterExplorerTopComponent.runButton.toolTipText"));
            
            //Remove filter excecutor from current workspace
            Workspace workspace = pc.getCurrentWorkspace();
            FilterExecutor executor = workspace.getLookup().lookup(FilterExecutor.class);
            if (executor != null){
                workspace.remove(executor);
            }
        }
    }
    
    private void setBusy(boolean busy) {
        if (busy) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
           setCursor(Cursor.getDefaultCursor());
        }
    }    

    private void runFilter() {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            //Add filter executor to the current workspace
            FilterExecutor worker = new FilterExecutor(currentWS);
            currentWS.add(worker);
            
            FilterModel filterModel = worker.getFilterModel();
            filterModel.setRunning(true);
            
            worker.execute();
        }
    }

    private void stop() {
        Workspace workspace = pc.getCurrentWorkspace();
        FilterExecutor executor = workspace.getLookup().lookup(FilterExecutor.class);
        if (executor != null) {
            executor.cancel();
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof FilterModel) {
            FilterModel filterModel = (FilterModel) evt.getSource();
            if (evt.getPropertyName().equals(FilterModel.RUNNING)) {
                refreshRunningState(filterModel.isRunning());
            } else if (!filterModel.isRunning() && applyCheckBox.isSelected()) {
                switch (evt.getPropertyName()) {
                    case FilterModel.ADDED_FILTER:
                    case FilterModel.REMOVED_FILTER:
                    case FilterModel.EDITED_FILTER:
                        runFilter();
                        break;
                    case FilterModel.CHANGED_RESTRICTION:
                        if (!filterModel.isEmpty()) {
                            runFilter();
                        }
                        break;
                }
            }
        }
    }
}
