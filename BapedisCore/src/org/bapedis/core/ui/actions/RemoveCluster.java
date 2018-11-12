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
import org.bapedis.core.model.Cluster;
import org.bapedis.core.model.ClusterNavigatorModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.impl.AbstractCluster;
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
 * @author loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.RemoveCluster"
)
@ActionRegistration(
        displayName = "#CTL_RemoveCluster",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditCluster", position = 200)
})
@NbBundle.Messages("CTL_RemoveCluster=Remove clusters")
public class RemoveCluster extends GlobalContextSensitiveAction<Cluster> {

    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public RemoveCluster() {
        super(Cluster.class);
        String name = NbBundle.getMessage(RemoveFilter.class, "CTL_RemoveCluster");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace workspace = pc.getCurrentWorkspace();
        AttributesModel oldAttrModel = pc.getAttributesModel();
        ClusterNavigatorModel navModel = pc.getClusterNavModel();
        Cluster[] clusters = navModel.getClusters();

        List<Node> toAddNodes = new LinkedList<>();
        List<Node> toRemoveNodes = new LinkedList<>();
        List<Cluster> clusterList = new LinkedList<>();
        List<Integer> peptideIDs = new LinkedList<>();
        Collection<? extends Cluster> context = lkpResult.allInstances();
        if (!context.isEmpty()) {
            SwingWorker worker = new SwingWorker<AttributesModel, Void>() {
                @Override
                protected AttributesModel doInBackground() throws Exception {
                    boolean in;
                    for (int i = 0; i < clusters.length; i++) {
                        in = true;
                        for (Cluster c : context) {
                            if (clusters[i].getId() == c.getId()) {
                                in = false;
                                break;
                            }
                        }
                        if (in) {
                            clusterList.add(clusters[i]);
                            for (Peptide peptide : clusters[i].getMembers()) {
                                peptideIDs.add(peptide.getId());
                                toAddNodes.add(peptide.getGraphNode());
                            }
                        } else {
                            for (Peptide peptide : clusters[i].getMembers()) {
                                toRemoveNodes.add(peptide.getGraphNode());
                            }
                        }
                    }
                    AttributesModel newAttrModel = new AttributesModel();
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
                        navModel.setClusters(clusterList.toArray(new Cluster[0]));
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }finally{
                        pc.getGraphVizSetting(workspace).fireChangedGraphTable();
                    }
                }
            };
            worker.execute();
        }
    }

}
