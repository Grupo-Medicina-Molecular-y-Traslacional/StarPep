/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import org.bapedis.core.task.QueryExecutor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        try {
            Neo4jDB.extractDatabase();
            Neo4jDB.loadDatabase();
            //Load all peptides into the default workspace
            QueryExecutor worker = new QueryExecutor();
            worker.execute();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    public void uninstalled() {
        GraphDatabaseService graphDb = Neo4jDB.getDbService();
        if (graphDb != null) {
            graphDb.shutdown();
        }
    }

}
