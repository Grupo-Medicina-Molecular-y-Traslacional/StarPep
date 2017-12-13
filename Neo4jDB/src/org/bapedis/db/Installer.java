/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import java.io.File;
import org.bapedis.core.task.QueryExecutor;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;

public class Installer extends ModuleInstall {

    private static final File DB_DIR = new File(System.getProperty("netbeans.user"));

    @Override
    public void restored() {          
        try {
            // Extract and load database 
            Neo4jDB.extractDatabase(DB_DIR);
            Neo4jDB.loadDatabase(DB_DIR);
            
            //Load all peptides into the default workspace
            QueryExecutor worker = new QueryExecutor();
            worker.execute();            
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

}
