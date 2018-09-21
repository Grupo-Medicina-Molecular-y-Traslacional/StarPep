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
public class QueryExecutor extends SwingWorker<Void, AttributesModel> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
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
    }

    public QueryModel getQueryModel() {
        return queryModel;
    }

    @Override
    protected Void doInBackground() throws Exception {
        pc.reportRunningTask(taskName, workspace);

        PeptideDAO dao = Lookup.getDefault().lookup(PeptideDAO.class);
        AttributesModel model = dao.getPeptides(queryModel, graphModel);
        //Publish AttributeModel
        publish(model);

        //Set graph node for metadata
        Graph graph = graphModel.getGraph();
        graph.readLock();
        try {
            for (Iterator<Metadata> it = queryModel.getMetadataIterator(); it.hasNext();) {
                Metadata metadata = it.next();
                metadata.setGraphNode(graph.getNode(metadata.getID()));
            }
        } finally {
            graph.readUnlock();
        }

        //Create graph node list
        List<Node> toAddNodes = new LinkedList<>();
        for (Peptide peptide : model.getPeptides()) {
            toAddNodes.add(peptide.getGraphNode());
        }

        // To refresh graph view
        GraphView graphView = pc.getGraphView(workspace);
        graphModel.setVisibleView(graphView);
        graph = graphModel.getGraph(graphView);
        graph.clear();
        graphWC.refreshGraphView(workspace, toAddNodes, null);

        return null;
    }

    @Override
    protected void process(List<AttributesModel> chunks) {
        AttributesModel attrModel = chunks.get(0);
        // Set new Model
        if (oldModel != null) {
            workspace.remove(oldModel);
        }
        workspace.add(attrModel);
        pc.reportMsg(NbBundle.getMessage(QueryExecutor.class, "QueryExecutor.output.text", attrModel.getNodeList().size()), workspace);
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
            pc.reportError(ex.getCause().toString(), workspace);
        } finally {
            queryModel.setRunning(false);
            pc.getGraphVizSetting().fireChangedGraphView();
            WindowManager.getDefault().getMainWindow().setCursor(Cursor.getDefaultCursor());
            if (pc.getCurrentWorkspace() != workspace) {
                String txt = NbBundle.getMessage(QueryExecutor.class, "Workspace.notify.finishedTask", taskName);
                pc.workspaceChangeNotification(txt, workspace);
            }
            pc.reportFinishedTask(taskName, workspace);
        }
    }

}
