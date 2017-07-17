/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.spi.data.PeptideDAO;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.NbPreferences;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bapedis.db.ui//QueryExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "QueryExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.bapedis.db.ui.QueryExplorerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_QueryExplorerAction",
        preferredID = "QueryExplorerTopComponent"
)
@Messages({
    "CTL_QueryExplorerAction= Query",
    "CTL_QueryExplorerTopComponent=Query",
    "HINT_QueryExplorerTopComponent=Query window"
})
public final class QueryExplorerTopComponent extends TopComponent implements WorkspaceEventListener, PropertyChangeListener {

    protected final ProjectManager pc;
    protected final QueryPanel queryPanel;
    protected final MetadataPanel metadataPanel;
    private static final String AUTO_APPLY = "AUTO_APPLY";

    public QueryExplorerTopComponent() {
        initComponents();

        pc = Lookup.getDefault().lookup(ProjectManager.class);

        setName(Bundle.CTL_QueryExplorerTopComponent());
        setToolTipText(Bundle.HINT_QueryExplorerTopComponent());

        metadataPanel = new MetadataPanel();
        queryPanel = new QueryPanel();

        splitPane.setLeftComponent(queryPanel);
        splitPane.setRightComponent(metadataPanel);

//        associateLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()));
        applyCheckBox.setSelected(NbPreferences.forModule(QueryModel.class).getBoolean(AUTO_APPLY, true));
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
        splitPane = new javax.swing.JSplitPane();
        runButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(applyCheckBox, org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.applyCheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 2, 0);
        add(applyCheckBox, gridBagConstraints);

        splitPane.setBorder(null);
        splitPane.setDividerLocation(260);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(splitPane, gridBagConstraints);

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/run.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.runButton.text")); // NOI18N
        runButton.setToolTipText(org.openide.util.NbBundle.getMessage(QueryExplorerTopComponent.class, "QueryExplorerTopComponent.runButton.toolTipText")); // NOI18N
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(runButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        runQuery();
    }//GEN-LAST:event_runButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox applyCheckBox;
    private javax.swing.JButton runButton;
    private javax.swing.JSplitPane splitPane;
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

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            QueryModel oldModel = pc.getQueryModel(oldWs);
            oldModel.removePropertyChangeListener(this);
        }
        QueryModel newModel = pc.getQueryModel(newWs);
        newModel.addPropertyChangeListener(this);
        queryPanel.setQueryModel(newModel);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof QueryModel) {
            switch (evt.getPropertyName()) {
                case QueryModel.ADDED_METADATA:
                case QueryModel.REMOVED_METADATA:
                    if (applyCheckBox.isSelected()) {
                        runQuery();
                    }
            }
        }
    }

    private void runQuery() {
        SwingWorker<AttributesModel, Void> worker = new SwingWorker<AttributesModel, Void>() {
            private final Workspace workspace = pc.getCurrentWorkspace();
            private GraphView oldView;

            @Override
            protected AttributesModel doInBackground() throws Exception {
                PeptideDAO dao = Lookup.getDefault().lookup(PeptideDAO.class);
                QueryModel queryModel = pc.getQueryModel(workspace);
                GraphModel graphModel = pc.getGraphModel(workspace);
                oldView = graphModel.getVisibleView();
                return dao.loadPeptides(queryModel, graphModel);
            }

            @Override
            protected void done() {
                try {
                    AttributesModel newModel = get();
                    AttributesModel oldModel = pc.getAttributesModel(workspace);
                    if (oldModel != null) {
                        workspace.remove(oldModel);                                                
                    }
                    if (!oldView.isMainView()){
                        pc.getGraphModel(workspace).destroyView(oldView);
                    }
                    workspace.add(newModel);
                } catch (InterruptedException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        };
        worker.execute();
    }

}
