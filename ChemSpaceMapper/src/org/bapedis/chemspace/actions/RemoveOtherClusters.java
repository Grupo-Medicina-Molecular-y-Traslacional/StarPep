/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.actions;

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
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.bapedis.core.ui.actions.GlobalContextSensitiveAction;
import org.bapedis.core.ui.actions.RemoveFilter;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class RemoveOtherClusters extends GlobalContextSensitiveAction<Cluster> {
    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    public RemoveOtherClusters() {
        super(Cluster.class);
        String name = NbBundle.getMessage(RemoveFilter.class, "CTL_RemoveOtherCluster");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/remove.png", false));
        putValue(SHORT_DESCRIPTION, name);        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace workspace = pc.getCurrentWorkspace();
        AttributesModel oldAttrModel = pc.getAttributesModel();
//        ClusterNavigatorModel navModel = pc.getClusterNavModel();
        
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
//                    for (Cluster cluster: navModel.getClusters()) {
//                        in = false;
//                        for (Cluster c : context) {
//                            if (cluster.getId() == c.getId()) {
//                                in = true;
//                                break;
//                            }
//                        }
//                        if (in) {
//                            clusterList.add(cluster);
//                            for (Peptide peptide : cluster.getMembers()) {
//                                peptideIDs.add(peptide.getId());
//                                toAddNodes.add(peptide.getGraphNode());
//                            }
//                        } else {
//                            for (Peptide peptide : cluster.getMembers()) {
//                                toRemoveNodes.add(peptide.getGraphNode());
//                            }
//                        }
//                    }
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
//                        navModel.setClusters(clusterList.toArray(new Cluster[0]));
                    } catch (InterruptedException | ExecutionException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }                                
            }; 
            worker.execute();
        }

    }
    
}
