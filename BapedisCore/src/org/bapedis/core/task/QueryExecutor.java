/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.task;

import java.awt.Cursor;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import javax.swing.SwingWorker;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.data.PeptideDAO;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class QueryExecutor extends SwingWorker<AttributesModel, String> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final Workspace workspace;
    protected final QueryModel queryModel;
    protected final GraphModel graphModel;
    protected final AttributesModel oldModel;

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
        PeptideDAO dao = Lookup.getDefault().lookup(PeptideDAO.class);
        AttributesModel model = dao.getPeptides(queryModel, graphModel);
        Graph graph = graphModel.getGraph(model.getGraphDBView());
        for (Iterator<Metadata> it = queryModel.getMetadataIterator(); it.hasNext();) {
            Metadata metadata = it.next();
            metadata.setGraphNode(graph.getNode(metadata.getUnderlyingNodeID()));
        }

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
            newModel.fireChangedGraphView();

            // Destroy old graph model       
            if (oldModel != null) {
                graphModel.destroyView(oldModel.getCsnView());
                graphModel.destroyView(oldModel.getGraphDBView());
            }
        } catch (InterruptedException | ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            queryModel.setRunning(false);
            WindowManager.getDefault().getMainWindow().setCursor(Cursor.getDefaultCursor());
        }
    }

}
