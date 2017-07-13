/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.LibraryNode;
import org.bapedis.core.model.QueryModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bapedis.db.ui//LibraryExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "LibraryExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.bapedis.db.ui.LibraryExplorerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_LibraryExplorerAction",
        preferredID = "LibraryExplorerTopComponent"
)
@Messages({
    "CTL_LibraryExplorerAction= Library",
    "CTL_LibraryExplorerTopComponent=Library",
    "HINT_LibraryExplorerTopComponent=Library window"
})
public final class LibraryExplorerTopComponent extends TopComponent implements WorkspaceEventListener, PropertyChangeListener {

    protected final ProjectManager pc;
    protected final QueryPanel queryPanel;
    protected final LibraryPanel libraryPanel;

    public LibraryExplorerTopComponent() {
        initComponents();

        pc = Lookup.getDefault().lookup(ProjectManager.class);

        setName(Bundle.CTL_LibraryExplorerTopComponent());
        setToolTipText(Bundle.HINT_LibraryExplorerTopComponent());

        libraryPanel = new LibraryPanel();
        splitPane.setLeftComponent(libraryPanel);
        
        queryPanel = new QueryPanel();
        splitPane.setRightComponent(queryPanel);

//        associateLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()));
        
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();

        setLayout(new java.awt.BorderLayout());

        splitPane.setBorder(null);
        splitPane.setDividerLocation(460);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
            highlightCategoryFor(oldWs, false);
        }
        QueryModel newModel = pc.getQueryModel(newWs);
        newModel.addPropertyChangeListener(this);
        queryPanel.setQueryModel(newModel);
        highlightCategoryFor(newWs, true);        
    }

    private void highlightCategoryFor(Workspace ws, boolean flag) {
        Collection<? extends Metadata> categories = ws.getLookup().lookupAll(Metadata.class);
        if (categories != null) {
            for (Metadata category : categories) {
                category.setSelected(flag);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        
    }

}
