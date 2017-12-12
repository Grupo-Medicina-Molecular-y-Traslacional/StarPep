/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import java.io.File;
import org.bapedis.core.task.QueryExecutor;
import org.bapedis.core.ui.components.SetupDialog;
import org.neo4j.graphdb.GraphDatabaseService;
import org.openide.LifecycleManager;
import org.openide.modules.ModuleInstall;
import org.openide.util.Exceptions;
import org.openide.util.NbPreferences;

public class Installer extends ModuleInstall {

    public static final String DB_PATH = "DB_Path";    

    @Override
    public void restored() {
        try {
//            String dbPath = NbPreferences.forModule(Installer.class).get(DB_PATH, null);
//            File dbDir = (dbPath == null) ? null : new File(dbPath);
//            if (dbDir == null || !dbDir.exists()) {
//                DBDirUI dbDirUI = new DBDirUI();
//                SetupDialog dialog = new SetupDialog();
//                if (dialog.setup(dbDirUI, dbDirUI, "Extract database")) {
//                    dbDir = new File (dbDirUI.getSelectedDir(), DB_NAME);
//                    if (!dbDir.exists()){
//                        dbDir.mkdir();
//                    }
//                    Neo4jDB.extractDatabase(dbDir);
//                } else {
//                    LifecycleManager.getDefault().exit();
//                }
//            }
            File dbDir = new File(System.getProperty("netbeans.user"));
            Neo4jDB.extractDatabase(dbDir);
            Neo4jDB.loadDatabase(dbDir);
            NbPreferences.forModule(Installer.class).put(DB_PATH, dbDir.getAbsolutePath());

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
