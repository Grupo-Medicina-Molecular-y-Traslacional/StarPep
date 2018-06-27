/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.neo4j.graphdb.factory.GraphDatabaseSettings;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class Neo4jDB {
    
    public static final String DB_NAME = "graph.db";
    public static final String ZIP_DB = "org/bapedis/db/resources/graph.db.zip";
    private static final int BUFFER_SIZE = 4096;
    private static GraphDatabaseService graphDb;

    @SuppressWarnings("empty-statement")
    public synchronized static void extractDatabase(File dbDir) throws IOException {
        ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);
        try (ZipInputStream zipIn = new ZipInputStream(cl.getResourceAsStream(ZIP_DB))) {
            ZipEntry zipEntry;
            File newFile;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                newFile = new File(dbDir, zipEntry.getName());
                if (zipEntry.isDirectory()) {
                    newFile.mkdir();
                } else {
                    extractFile(zipIn, newFile);
                }
            };
        }
    }

    private static void extractFile(ZipInputStream zipIn, File file) throws IOException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file))) {
            byte[] bytesIn = new byte[BUFFER_SIZE];
            int read;
            while ((read = zipIn.read(bytesIn)) != -1) {
                bos.write(bytesIn, 0, read);
            }
        }
    }

    public synchronized static GraphDatabaseService loadDatabase(File dbDir) throws IOException {
        graphDb = new GraphDatabaseFactory().newEmbeddedDatabaseBuilder(new File(dbDir, DB_NAME))
                .setConfig(GraphDatabaseSettings.read_only, "true")
                .newGraphDatabase();
        registerShutdownHook(graphDb);
        try (Transaction tx = graphDb.beginTx()) {
            tx.success();
        }

        return graphDb;
    }

    public synchronized static GraphDatabaseService getDbService() {
        return graphDb;
    }
      
    private static void registerShutdownHook(final GraphDatabaseService graphDb) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                graphDb.shutdown();
            }
        });
    }

}
