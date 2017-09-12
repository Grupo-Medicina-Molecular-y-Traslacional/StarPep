/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.task;

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
import org.gephi.graph.api.GraphView;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class QueryExecutor extends SwingWorker<AttributesModel, String> {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected final Workspace workspace;
    protected final QueryModel queryModel;
    protected final GraphModel graphModel;
    protected GraphView oldView;

    public QueryExecutor(){
        this(pc.getCurrentWorkspace());
    }
    
    public QueryExecutor(Workspace workspace) {
        this.workspace = workspace;
        queryModel = pc.getQueryModel(workspace);
        graphModel = pc.getGraphModel(workspace);
    }

    @Override
    protected AttributesModel doInBackground() throws Exception {
        publish("start");
        PeptideDAO dao = Lookup.getDefault().lookup(PeptideDAO.class);
        oldView = graphModel.getVisibleView();
        AttributesModel model = dao.getPeptides(queryModel, graphModel);
        Graph graph = graphModel.getGraphVisible();
        for (Iterator<Metadata> it = queryModel.getMetadataIterator(); it.hasNext();) {
            Metadata metadata = it.next();
            metadata.setGraphNode(graph.getNode(metadata.getUnderlyingNodeID()));
        }
        return model;
    }

    @Override
    protected void process(List<String> chunks) {
        queryModel.setRunning(true);
    }
        
    @Override
    protected void done() {
        try {
            AttributesModel newModel = get();
            AttributesModel oldModel = pc.getAttributesModel(workspace);
            if (oldModel != null) {
                workspace.remove(oldModel);
            }
            if (!oldView.isMainView()) {
                graphModel.destroyView(oldView);
            }
            workspace.add(newModel);
        } catch (InterruptedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ExecutionException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            queryModel.setRunning(false);
        }
    }

}
