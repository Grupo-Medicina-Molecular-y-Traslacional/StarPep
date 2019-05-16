/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.actions;

import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.ui.actions.NewWorkspace;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class CopyClusterToWorkspace extends AbstractAction {

    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final Cluster cluster;
    private Workspace workspace;

    public CopyClusterToWorkspace(Workspace workspace, Cluster cluster) {
        this.workspace = workspace;
        this.cluster = cluster;
        String name;
        if (workspace == null) {
            name = NbBundle.getMessage(CopyClusterToWorkspace.class, "CopyClusterToWorkspace.newWorkspace.name");
        } else {
            name = workspace.getName();
        }
        putValue(NAME, name);
//        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        AttributesModel currAttrModel = pc.getAttributesModel();
        final List<Integer> peptideIDs = new LinkedList<>();

        if (cluster != null) {
            SwingWorker worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws Exception {
                    for (Peptide peptide : cluster.getMembers()) {
                        peptideIDs.add(peptide.getId());
                    }
                    
                    AttributesModel newAttrModel;
                    if (workspace != null || ((workspace = createWorkspace()) != null)) {
                        if (workspace.isBusy()) {
                            throw new MyException(NbBundle.getMessage(CopyClusterToWorkspace.class, "CopyClusterToWorkspace.error.busyWorkspace", workspace.getName()));
                        }

                        newAttrModel = pc.getAttributesModel(workspace);
                        if (newAttrModel == null) {
                            newAttrModel = new AttributesModel(workspace);
                            workspace.add(newAttrModel);
                        }

                        //Check duplited peptide
                        Map<Integer, Peptide> map = newAttrModel.getPeptideMap();
                        if (map.size() > 0) {
                            for (Integer id : peptideIDs) {
                                if (map.containsKey(id)) {
                                    throw new MyException(NbBundle.getMessage(CopyClusterToWorkspace.class, "CopyClusterToWorkspace.error.duplicatedPeptide", id, workspace.getName()));
                                }
                            }
                        }

                        //Copy peptides and graph
                        currAttrModel.getBridge().copyTo(newAttrModel, peptideIDs);
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        get();
                        if (workspace != null) {
                            pc.setCurrentWorkspace(workspace);
                            pc.getGraphVizSetting(workspace).fireChangedGraphView();
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        if (ex.getCause() instanceof MyException) {
                            NotifyDescriptor errorND = new NotifyDescriptor.Message(((MyException) ex.getCause()).getErrorMsg(), NotifyDescriptor.ERROR_MESSAGE);
                            DialogDisplayer.getDefault().notify(errorND);
                        } else {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            };
            worker.execute();
        }
    }

    private Workspace createWorkspace() {
        String name = Workspace.getPrefixName() + " " + Workspace.getCount();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(NewWorkspace.class, "NewWorkspace.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            Workspace ws = new Workspace(name);
            pc.add(ws);
            return ws;
        }
        return null;
    }

    private class MyException extends RuntimeException {

        private final String errorMsg;

        MyException(String errorMsg) {
            this.errorMsg = errorMsg;
        }

        public String getErrorMsg() {
            return errorMsg;
        }
    }

}
