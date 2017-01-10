/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.awt.BorderLayout;
import java.util.Collection;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.services.BioCategoryManager;
import org.bapedis.db.model.BioCategory;
import org.bapedis.db.model.BioCategoryNode;
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
        dtd = "-//org.bapedis.db.ui//PeptideExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PeptideExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true)
@ActionID(category = "Window", id = "org.bapedis.db.ui.PeptideExplorerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PeptideExplorerAction",
        preferredID = "PeptideExplorerTopComponent"
)
@Messages({
    "CTL_PeptideExplorerAction=Peptide Explorer",
    "CTL_PeptideExplorerTopComponent=Peptide Explorer",
    "HINT_PeptideExplorerTopComponent=This is a Peptide Explorer window"
})
public final class PeptideExplorerTopComponent extends TopComponent implements ExplorerManager.Provider, WorkspaceEventListener {

    private final ExplorerManager mgr = new ExplorerManager();

    public PeptideExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_PeptideExplorerTopComponent());
        setToolTipText(Bundle.HINT_PeptideExplorerTopComponent());

        add(new BeanTreeView(), BorderLayout.CENTER);

        associateLookup(ExplorerUtils.createLookup(mgr, getActionMap()));

        BioCategoryManager bcc = Lookup.getDefault().lookup(BioCategoryManager.class);
        mgr.setRootContext(new BioCategoryNode(bcc.getRootCategory()));
        ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
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
    public ExplorerManager getExplorerManager() {
        return mgr;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            highlightCategoryFor(oldWs, false);
        }
        highlightCategoryFor(newWs, true);
    }

    private void highlightCategoryFor(Workspace ws, boolean flag) {
        Collection<? extends BioCategory> categories = ws.getLookup().lookupAll(BioCategory.class);
        if (categories != null) {
            for (BioCategory category : categories) {
                category.setSelected(flag);
            }
        }
    }

}
