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
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.RestrictionLevel;
import org.bapedis.core.task.QueryExecutor;
import org.bapedis.core.ui.actions.AddQuery;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
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
        dtd = "-//org.bapedis.core.ui//QueryExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "QueryExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true, position = 333)
@ActionID(category = "Window", id = "org.bapedis.core.ui.QueryExplorerTopComponent")
@ActionReference(path = "Menu/Tools", position = 10)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_QueryExplorerAction",
        preferredID = "QueryExplorerTopComponent"
)
@Messages({
    "CTL_QueryExplorerAction=Peptide retrieval",
    "CTL_QueryExplorerTopComponent=Query",
    "HINT_QueryExplorerTopComponent=Database Query window"
})
public final class QueryExplorerTopComponent extends TopComponent implements WorkspaceEventListener, PropertyChangeListener, ExplorerManager.Provider {

    protected final ProjectManager pc;
    protected final ExplorerManager explorerMgr;
    private static final String AUTO_APPLY = "AUTO_APPLY";
    private final RichTooltip richTooltip;

    public QueryExplorerTopComponent() {
        initComponents();

        pc = Lookup.getDefault().lookup(ProjectManager.class);

        setName(Bundle.CTL_QueryExplorerTopComponent());
        setToolTipText(Bundle.HINT_QueryExplorerTopComponent());

        explorerMgr = new ExplorerManager();
        BeanTreeView view = new BeanTreeView();
        scrollPane.setViewportView(view);

        associateLookup(new ProxyLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()),
                Lookups.singleton(new MetadataNavigatorLookupHint()), Lookups.singleton(new GraphElementNavigatorLookupHint())));

        applyCheckBox.setSelected(NbPreferences.forModule(QueryModel.class).getBoolean(AUTO_APPLY, true));

        DefaultComboBoxModel comboModel = (DefaultComboBoxModel) restrictiveComboBox.getModel();
        for (RestrictionLevel restriction : RestrictionLevel.values()) {
            comboModel.addElement(restriction);
        }

        queryToolBar.add(createAddQueryButton());
        List<? extends Action> actions = Utilities.actionsForPath("Actions/EditQuery");
        for (Action action : actions) {
            queryToolBar.add(action);
        }

        richTooltip = new RichTooltip(Bundle.CTL_QueryExplorerTopComponent(), NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.info.text"));
    }
    
    private JButton createAddQueryButton() {
        final JPopupMenu popup = new JPopupMenu();
        for (StarPepAnnotationType type: StarPepAnnotationType.values()) {
            popup.add(new AddQuery(type));
        }

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/add.png", false), popup);
        dropDownButton.setToolTipText(NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.addQuery.tooltiptext"));
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

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        applyCheckBox = new javax.swing.JCheckBox();
        runButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        restrictiveComboBox = new javax.swing.JComboBox();
        infoLabel = new javax.swing.JLabel();
        queryToolBar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(applyCheckBox, org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.applyCheckBox.text")); // NOI18N
        applyCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyCheckBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        add(applyCheckBox, gridBagConstraints);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/run.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.runButton.text")); // NOI18N
        runButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.runButton.toolTipText")); // NOI18N
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

        scrollPane.setBorder(null);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrollPane, gridBagConstraints);

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

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.infoLabel.text")); // NOI18N
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

        queryToolBar.setFloatable(false);
        queryToolBar.setRollover(true);
        queryToolBar.add(jSeparator1);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        add(queryToolBar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        runQuery();
    }//GEN-LAST:event_runButtonActionPerformed

    private void applyCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyCheckBoxActionPerformed
        NbPreferences.forModule(QueryModel.class).putBoolean(AUTO_APPLY, applyCheckBox.isSelected());
    }//GEN-LAST:event_applyCheckBoxActionPerformed

    private void restrictiveComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_restrictiveComboBoxActionPerformed
        QueryModel queryModel = pc.getQueryModel();
        RestrictionLevel restriction = (RestrictionLevel) restrictiveComboBox.getSelectedItem();
        if (queryModel.getRestriction() != restriction) {
            queryModel.setRestriction(restriction);
        }
    }//GEN-LAST:event_restrictiveComboBoxActionPerformed

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        richTooltip.hideTooltip();
    }//GEN-LAST:event_infoLabelMouseExited

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
        richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
    }//GEN-LAST:event_infoLabelMouseEntered

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox applyCheckBox;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar queryToolBar;
    private javax.swing.JComboBox restrictiveComboBox;
    private javax.swing.JButton runButton;
    private javax.swing.JScrollPane scrollPane;
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
        QueryModel model = pc.getQueryModel();
        model.removePropertyChangeListener(this);
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

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            QueryModel oldModel = pc.getQueryModel(oldWs);
            oldModel.removePropertyChangeListener(this);
        }
        QueryModel newModel = pc.getQueryModel(newWs);
        restrictiveComboBox.setSelectedItem(newModel.getRestriction());
        explorerMgr.setRootContext(newModel.getRootContext());
        refreshRunningState(newModel.isRunning());
        newModel.addPropertyChangeListener(this);        
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof QueryModel) {
            QueryModel queryModel = (QueryModel) evt.getSource();
            switch (evt.getPropertyName()) {
                case QueryModel.ADDED_METADATA:
                case QueryModel.REMOVED_METADATA:
                case QueryModel.CHANGED_RESTRICTION:
                    if (applyCheckBox.isSelected()) {
                        runQuery();
                    }
                    break;
                case QueryModel.RUNNING:
                    refreshRunningState(queryModel.isRunning());
                    break;
            }
        }
    }

    private void runQuery() {
        Workspace currentWS = pc.getCurrentWorkspace();
        if (currentWS.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
        } else {
            QueryExecutor worker = new QueryExecutor(currentWS);
            worker.execute();
        }
    }

    private void refreshRunningState(boolean running) {
        restrictiveComboBox.setEnabled(!running);
        runButton.setEnabled(!running);
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

}
