/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.task;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class QueryExecutor extends SwingWorker<AttributesModel, Void> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final GraphWindowController graphWC;
    protected final Workspace workspace;
    protected final QueryModel queryModel;
    protected final GraphModel graphModel;
    protected final AttributesModel oldModel;
    protected final String taskName;

    public QueryExecutor() {
        this(pc.getCurrentWorkspace());        
    }

    public QueryExecutor(Workspace workspace) {
        this.workspace = workspace;
        queryModel = pc.getQueryModel(workspace);
        graphModel = pc.getGraphModel(workspace);
        oldModel = pc.getAttributesModel(workspace);
        taskName = NbBundle.getMessage(QueryExecutor.class, "QueryExecutor.name");
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    }

    public QueryModel getQueryModel() {
        return queryModel;
    }

    @Override
    protected AttributesModel doInBackground() throws Exception {
        pc.reportRunningTask(taskName, workspace);

        PeptideDAO dao = Lookup.getDefault().lookup(PeptideDAO.class);
        AttributesModel model = dao.getPeptides(queryModel, graphModel);

        //Create graph node list
        List<Node> toAddNodes = new LinkedList<>();
        for (Peptide peptide : model.getPeptides()) {
            toAddNodes.add(peptide.getGraphNode());
        }

        // To refresh graph view
        GraphView graphView = pc.getGraphView(workspace);
        graphModel.setVisibleView(graphView);
        Graph graph = graphModel.getGraph(graphView);
        graph.clear();
        graphWC.refreshGraphView(workspace, toAddNodes, null);

        return model;
    }

    @Override
    protected void done() {
        AttributesModel attrModel = null;
        try {
            attrModel = get();
            pc.reportMsg(NbBundle.getMessage(QueryExecutor.class, "QueryExecutor.output.text", attrModel.getNodeList().size()), workspace);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            pc.reportError(ex.getCause().toString(), workspace);
        } finally {
            queryModel.setRunning(false);
            // Set new Model
            if (oldModel != null) {
                workspace.remove(oldModel);
            }
            if (attrModel != null) {
                workspace.add(attrModel);
            }
            pc.getGraphVizSetting().fireChangedGraphView();            
            if (pc.getCurrentWorkspace() != workspace) {
                String txt = NbBundle.getMessage(QueryExecutor.class, "Workspace.notify.finishedTask", taskName);
                pc.workspaceChangeNotification(txt, workspace);
            }
            pc.reportFinishedTask(taskName, workspace);
        }
    }

}
