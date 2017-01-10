/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.util.Collection;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.model.NeoNeighborsModel;
import org.bapedis.db.model.NeoPeptideModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.ProxyLookup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bapedis.db.ui//NeoPeptideModel//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "NeoPeptideModelTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.bapedis.db.ui.NeoPeptideModelTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_NeoPeptideModelAction",
        preferredID = "NeoPeptideModelTopComponent"
)
@Messages({
    "CTL_NeoPeptideModelAction=Peptide nodes",
    "CTL_NeoPeptideModelTopComponent=Peptide nodes",
    "HINT_NeoPeptideModelTopComponent=A peptide and neighbor nodes window"
})
public final class NeoPeptideModelTopComponent extends TopComponent implements
        WorkspaceEventListener, LookupListener {

    protected ProjectManager pc;
    protected Lookup.Result<NeoPeptideModel> peptideLkpResult;
    protected Lookup.Result<NeoNeighborsModel> neighborLkpResult;
    protected PeptideModelPanel peptideModelViewer;
    protected NeighborModelPanel neighborModelViwer;
    protected boolean showNeighbors;

    public NeoPeptideModelTopComponent() {
        initComponents();
        setName(Bundle.CTL_NeoPeptideModelTopComponent());
        setToolTipText(Bundle.HINT_NeoPeptideModelTopComponent());
        peptideModelViewer = new PeptideModelPanel();
        neighborModelViwer = new NeighborModelPanel();
        splitPane.setTopComponent(peptideModelViewer);
        splitPane.setBottomComponent(neighborModelViwer);
        showNeighbors = false;
        showNeighborsPanel(showNeighbors);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        associateLookup(new ProxyLookup(peptideModelViewer.getLookup(), neighborModelViwer.getLookup()));
    }

    public void showNeighborsPanel(boolean showNeighbors) {
        this.showNeighbors = showNeighbors;
        splitPane.setOneTouchExpandable(showNeighbors);
        neighborModelViwer.setVisible(showNeighbors);
        if (showNeighbors) {
            splitPane.setDividerLocation(0.6d);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splitPane = new javax.swing.JSplitPane();

        setMinimumSize(new java.awt.Dimension(331, 250));
        setPreferredSize(new java.awt.Dimension(514, 152));
        setLayout(new java.awt.BorderLayout());

        splitPane.setDividerLocation(108);
        splitPane.setDividerSize(7);
        splitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        add(splitPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JSplitPane splitPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
        if (neighborLkpResult != null) {
            neighborLkpResult.removeLookupListener(this);
            neighborLkpResult = null;
        }
    }
    
    public void setBusyLabel(){
        peptideModelViewer.getAttributesPanel().setBusyLabel();
        if (showNeighbors){
            neighborModelViwer.getAttributesPanel().setBusyLabel();
        }
    }
    
    public void setNeighborBusyLabel(){
        neighborModelViwer.getAttributesPanel().setBusyLabel();
    }
    
    public void setErrorLabel(){
        peptideModelViewer.getAttributesPanel().setErrorLabel();
        if (showNeighbors){
            neighborModelViwer.getAttributesPanel().setErrorLabel();
        }    
    }

    @Override
    public void componentClosed() {
        removeLookupListener();
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
        removeLookupListener();
        peptideLkpResult = newWs.getLookup().lookupResult(NeoPeptideModel.class);
        peptideLkpResult.addLookupListener(this);
        neighborLkpResult = newWs.getLookup().lookupResult(NeoNeighborsModel.class);
        neighborLkpResult.addLookupListener(this);
        NeoPeptideModel peptidesModel = newWs.getLookup().lookup(NeoPeptideModel.class);
        peptideModelViewer.getAttributesPanel().showData(peptidesModel);
        NeoNeighborsModel neighborsModel = newWs.getLookup().lookup(NeoNeighborsModel.class);
        if (showNeighbors) {
            neighborModelViwer.getAttributesPanel().showData(neighborsModel);
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends NeoPeptideModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                NeoPeptideModel attrModel = attrModels.iterator().next();
                peptideModelViewer.getAttributesPanel().showData(attrModel);
            }
        } else if (le.getSource().equals(neighborLkpResult)) {
            Collection<? extends NeoNeighborsModel> attrModels = neighborLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                NeoNeighborsModel neighborsModel = attrModels.iterator().next();
                if (showNeighbors) {
                    neighborModelViwer.getAttributesPanel().showData(neighborsModel);
                }
            }
        }

    }

}
