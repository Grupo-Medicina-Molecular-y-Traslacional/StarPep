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
import java.util.Map;
import java.util.concurrent.ExecutionException;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.ClusterNavigatorModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.CopyClusterToWorkspace"
)
@ActionReferences({
    @ActionReference(path = "Actions/EditCluster", position = 200)
})
public class CopyClusterToWorkspace extends GlobalContextSensitiveAction<Cluster> {

    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private Workspace workspace;

    public CopyClusterToWorkspace(Workspace workspace) {
        super(Cluster.class);
        this.workspace = workspace;
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
        ClusterNavigatorModel currNavModel = pc.getClusterNavModel();

        final List<Cluster> clusterList = new LinkedList<>();
        final List<Integer> peptideIDs = new LinkedList<>();

        Collection<? extends Cluster> context = lkpResult.allInstances();
        if (!context.isEmpty()) {
            SwingWorker worker = new SwingWorker<Cluster[], Void>() {
                @Override
                protected Cluster[] doInBackground() throws Exception {
                    boolean in;
                    for (Cluster cluster : currNavModel.getClusters()) {
                        in = false;
                        for (Cluster c : context) {
                            if (cluster.getId() == c.getId()) {
                                in = true;
                                break;
                            }
                        }
                        if (in) {
                            clusterList.add(cluster);
                            for (Peptide peptide : cluster.getMembers()) {
                                peptideIDs.add(peptide.getId());
                            }
                        }
                    }
                    AttributesModel newAttrModel;
                    if (workspace != null || ((workspace = createWorkspace()) != null)) {
                        if(workspace.isBusy()){
                            throw new MyException(NbBundle.getMessage(CopyClusterToWorkspace.class, "CopyClusterToWorkspace.error.busyWorkspace", workspace.getName()));
                        }
                        
                        newAttrModel = pc.getAttributesModel(workspace);
                        if (newAttrModel == null) {
                            newAttrModel = new AttributesModel(workspace);
                            workspace.add(newAttrModel);
                        }
                        //Validate
                        Map<Integer, Peptide> map = newAttrModel.getPeptideMap();
                        ClusterNavigatorModel navModel = pc.getClusterNavModel(workspace);
                        Cluster[] oldClusters = navModel.getClusters();
                        //Check duplicated clusters
                        if (oldClusters != null) {
                            for (Cluster c1 : clusterList) {
                                for (Cluster c2 : oldClusters) {
                                    if (c1.getId() == c2.getId()) {
                                        throw new MyException(NbBundle.getMessage(CopyClusterToWorkspace.class, "CopyClusterToWorkspace.error.duplicatedCluster", c1.getId(), workspace.getName()));
                                    }
                                }
                            }
                        }
                        //Check duplited peptide
                        if (map.size() > 0) {
                            for (Integer id : peptideIDs) {
                                if (map.containsKey(id)) {
                                    throw new MyException(NbBundle.getMessage(CopyClusterToWorkspace.class, "CopyClusterToWorkspace.error.duplicatedPeptide", id, workspace.getName()));
                                }
                            }
                        }

                        //Copy peptides and graph
                        currAttrModel.getBridge().copyTo(newAttrModel, peptideIDs);

                        //Copy clusters                        
                        if (oldClusters != null) {
                            for (Cluster cluster : oldClusters) {
                                clusterList.add(cluster);
                            }
                        }
                        Cluster[] newClusters = new Cluster[clusterList.size()];
                        int index = 0;
                        for (Cluster c : clusterList) {
                            newClusters[index] = new Cluster(c.getId());
                            for (Peptide p : c.getMembers()) {
                                newClusters[index].addMember(map.get(p.getId()));
                            }
                            index++;
                        }
                        return newClusters;
                    }
                    return null;
                }

                @Override
                protected void done() {
                    try {
                        Cluster[] newClusters = get();
                        if (workspace != null && newClusters != null) {
                            ClusterNavigatorModel navModel = pc.getClusterNavModel(workspace);
                            navModel.setClusters(newClusters);
                            
                            pc.setCurrentWorkspace(workspace);
                            pc.getGraphVizSetting(workspace).fireChangedGraphView();                            
                        }
                    } catch (InterruptedException | ExecutionException ex) {
                        if (ex.getCause() instanceof MyException) {
                            NotifyDescriptor errorND = new NotifyDescriptor.Message(((MyException)ex.getCause()).getErrorMsg(), NotifyDescriptor.ERROR_MESSAGE);
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

        public String getErrorMsg(){
            return errorMsg;
        }
    }

}
