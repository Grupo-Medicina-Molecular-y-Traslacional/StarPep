/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import java.io.File;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.QueryExecutor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

public class Installer extends ModuleInstall implements WorkspaceEventListener {

    private ProjectManager pc;
    private static final File DB_DIR = new File(System.getProperty("netbeans.user"));

    @Override
    public void restored() {
        try {
            // Extract and load database 
            Neo4jDB.extractDatabase(DB_DIR);
            Neo4jDB.loadDatabase(DB_DIR);

            pc = Lookup.getDefault().lookup(ProjectManager.class);
            pc.addWorkspaceEventListener(this);
            workspaceChanged(null, pc.getCurrentWorkspace());
        } catch (Throwable error) {
            Exceptions.printStackTrace(error);
        }

    }

    @Override
    public void uninstalled() {
        GraphDatabaseService graphDb = Neo4jDB.getDbService();
        if (graphDb != null) {
            graphDb.shutdown();
        }
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (newWs.getLookup().lookup(AttributesModel.class) == null) {
            //Load all peptides into the default workspace
            QueryExecutor worker = new QueryExecutor(newWs);
            worker.execute();
        }
    }

}
