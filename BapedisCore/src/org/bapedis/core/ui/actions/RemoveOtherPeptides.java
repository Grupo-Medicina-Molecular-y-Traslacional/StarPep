/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Node;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.RemoveOtherPeptides"
)
@ActionRegistration(
        displayName = "RemoveOtherPeptides.name",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditPeptides", position = 30)
})
public class RemoveOtherPeptides extends GlobalContextSensitiveAction<Peptide>{

    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public RemoveOtherPeptides() {
        super(Peptide.class);
        String name = NbBundle.getMessage(RemoveOtherPeptides.class, "RemoveOtherPeptides.name");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace workspace = pc.getCurrentWorkspace();
        final AttributesModel oldAttrModel = pc.getAttributesModel();
        
        List<Node> toAddNodes = new LinkedList<>();
        List<Node> toRemoveNodes = new LinkedList<>();        
        List<Integer> peptideIDs = new LinkedList<>();
        Collection<? extends Peptide> context = lkpResult.allInstances();
        if (!context.isEmpty()) {
            SwingWorker worker = new SwingWorker<AttributesModel, Void>() {
                @Override
                protected AttributesModel doInBackground() throws Exception {
                    for (Peptide peptide : oldAttrModel.getPeptides()) {
                        if (context.contains(peptide)) {
                            peptideIDs.add(peptide.getId());
                            toAddNodes.add(peptide.getGraphNode());
                        }else{
                            toRemoveNodes.add(peptide.getGraphNode());
                        }
                    }
                    AttributesModel newAttrModel = new AttributesModel(workspace);
                    oldAttrModel.getBridge().copyTo(newAttrModel, peptideIDs);
                    graphWC.refreshGraphView(workspace, toAddNodes, toRemoveNodes);
                    return newAttrModel;
                }

                @Override
                protected void done() {
                    try {
                        AttributesModel newAttrModel = get();
                        workspace.remove(oldAttrModel);
                        workspace.add(newAttrModel);
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }                                
            }; 
            worker.execute();
        }

    }
    
}
