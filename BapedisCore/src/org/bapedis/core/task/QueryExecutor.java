/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.task;

import java.awt.Cursor;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Metadata;
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
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class QueryExecutor extends SwingWorker<AttributesModel, String> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected final Workspace workspace;
    protected final QueryModel queryModel;
    protected final GraphModel graphModel;
    protected final AttributesModel oldModel;
    protected final String taskName = "Query";

    public QueryExecutor() {
        this(pc.getCurrentWorkspace());
    }

    public QueryExecutor(Workspace workspace) {
        this.workspace = workspace;
        queryModel = pc.getQueryModel(workspace);
        graphModel = pc.getGraphModel(workspace);
        oldModel = pc.getAttributesModel(workspace);
    }

    @Override
    protected AttributesModel doInBackground() throws Exception {
        publish("start");
        pc.reportRunningTask(taskName, workspace);

        PeptideDAO dao = Lookup.getDefault().lookup(PeptideDAO.class);
        AttributesModel model = dao.getPeptides(queryModel, graphModel);

        //Set graph node for metadata
        Graph graph = graphModel.getGraph();
        graph.readLock();
        try {
            for (Iterator<Metadata> it = queryModel.getMetadataIterator(); it.hasNext();) {
                Metadata metadata = it.next();
                metadata.setGraphNode(graph.getNode(metadata.getUnderlyingNodeID()));
            }
        } finally {
            graph.readUnlock();
        }

        //Create graph node list
        List<Node> toAddNodes = new LinkedList<>();
        for (Peptide peptide : model.getPeptides()) {
            toAddNodes.add(peptide.getGraphNode());
        }

        // Reset graph view
        GraphView oldView = graphModel.getVisibleView();
        GraphView newView = graphModel.createView();
        graphModel.setVisibleView(newView);
        if (!oldView.isMainView()) {
            graphModel.destroyView(oldView);
        }

        // To refresh graph view     
        graphWC.refreshGraphView(toAddNodes, null);

        return model;
    }

    @Override
    protected void process(List<String> chunks) {
        queryModel.setRunning(true);
        WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    protected void done() {
        try {
            AttributesModel newModel = get();
            // Set new Model
            if (oldModel != null) {
                workspace.remove(oldModel);
            }
            workspace.add(newModel);

//            newModel.fireChangedGraphView();
            pc.reportMsg(NbBundle.getMessage(QueryExecutor.class, "QueryExecutor.output.text", newModel.getNodeList().size()), workspace);
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            pc.reportError(ex.getCause().toString(), workspace);
        } finally {
            queryModel.setRunning(false);
            WindowManager.getDefault().getMainWindow().setCursor(Cursor.getDefaultCursor());
            if (pc.getCurrentWorkspace() != workspace) {
                String txt = NbBundle.getMessage(QueryExecutor.class, "Workspace.notify.finishedTask", taskName);
                pc.workspaceChangeNotification(txt, workspace);
            }
            pc.reportFinishedTask(taskName, workspace);
        }
    }

}
