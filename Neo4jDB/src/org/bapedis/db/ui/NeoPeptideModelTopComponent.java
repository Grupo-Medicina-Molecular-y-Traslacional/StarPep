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
import org.bapedis.core.ui.components.PeptideViewer;
import org.bapedis.db.model.NeoPeptideModel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

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
    protected final PeptideViewer peptideViewer;


    public NeoPeptideModelTopComponent() {
        initComponents();
        setName(Bundle.CTL_NeoPeptideModelTopComponent());
        setToolTipText(Bundle.HINT_NeoPeptideModelTopComponent());
        peptideViewer = new PeptideViewer();
        add(peptideViewer, BorderLayout.CENTER);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        associateLookup(peptideViewer.getLookup());
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(331, 250));
        setPreferredSize(new java.awt.Dimension(514, 152));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
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
    }
    
    public void setBusyLabel(){
        peptideViewer.setBusyLabel();
    }
    
    
    public void setErrorLabel(){
        peptideViewer.setErrorLabel();
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
        NeoPeptideModel peptidesModel = newWs.getLookup().lookup(NeoPeptideModel.class);
        peptideViewer.showData(peptidesModel);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends NeoPeptideModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                NeoPeptideModel attrModel = attrModels.iterator().next();
                peptideViewer.showData(attrModel);
            }
        } 
    }

}
