/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import org.neo4j.graphdb.GraphDatabaseService;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        try {
            Neo4jDB.extractDatabase();
            Neo4jDB.loadDatabase();
        } catch (Exception ex) {
            System.out.println(ex);
            ex.printStackTrace();
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
