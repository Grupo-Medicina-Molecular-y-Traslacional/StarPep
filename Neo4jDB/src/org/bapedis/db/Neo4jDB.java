/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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

    public static final String DB_NAME = "neo4j";
    public static final File DB_DIR = new File (System.getProperty("netbeans.user"), "db");
    public static final String ZIP_DB = "org/bapedis/db/resources/neo4j.zip";
    private static final int BUFFER_SIZE = 4096;
    private static GraphDatabaseService graphDb;
//    private static ArrayList<String> representatives, allPeptides;
//    private static AttributeModelDAO modelDAO;

    @SuppressWarnings("empty-statement")
    public static void extractDatabase() throws IOException {
        ClassLoader cl = Lookup.getDefault().lookup(ClassLoader.class);
        try (ZipInputStream zipIn = new ZipInputStream(cl.getResourceAsStream(ZIP_DB))) {
            if (!DB_DIR.exists()) {
                DB_DIR.mkdir();
            }
            ZipEntry zipEntry;
            File newFile;
            while ((zipEntry = zipIn.getNextEntry()) != null) {
                newFile = new File(DB_DIR, zipEntry.getName());
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

    public static GraphDatabaseService loadDatabase() throws IOException {
        graphDb = new GraphDatabaseFactory()
                .newEmbeddedDatabaseBuilder(DB_DIR.getCanonicalPath() + File.separator + DB_NAME)
                .setConfig(GraphDatabaseSettings.read_only, "true")
                .newGraphDatabase();
        registerShutdownHook(graphDb);
        try (Transaction tx = graphDb.beginTx()) {
            tx.success();
        }

        return graphDb;
    }

//    public static void loadPwIdMatrix() throws Exception {
//        File fileBase = new File("pwidmtx");
//        try (ObjectInputStream objIn = new ObjectInputStream(new GZIPInputStream(new BufferedInputStream(new FileInputStream(new File(fileBase, "data.dat")))))) {
//            pwIdMatrix = (PairwiseIdentityMatrix) objIn.readObject();
//        }
//    }

//    private static ArrayList<String> loadFastaFile(FileReader fr) throws Exception {
//        ArrayList<String> sequences = new ArrayList<>();
//        try (BufferedReader br = new BufferedReader(fr)) {
//            StringBuilder sb = new StringBuilder();
//            String line = br.readLine();
//
//            while (line != null) {
//                if (line.charAt(0) == '>') {
//                    if (sb.length() > 0) {
//                        sequences.add(sb.toString());
//                        sb.delete(0, sb.length());
//                    }
//                } else {
//                    sb.append(line);
//                }
//                line = br.readLine();
//            }
//            String genome = sb.toString();
//        }
//        return sequences;
//    }
//
//    public static void loadRepresentatives() throws Exception {
//        File fileBase = new File("pwidmtx");
//        representatives = loadFastaFile(new FileReader(new File(fileBase, "AMPs-0.30.fasta")));
//    }
//
//    public static void loadAllPeptides() throws Exception {
//        File fileBase = new File("pwidmtx");
//        allPeptides = loadFastaFile(new FileReader(new File(fileBase, "AMPs.fasta")));
//    }

    public static GraphDatabaseService getDbService() {
        return graphDb;
    }

//    public static PairwiseIdentityMatrix getPwIdMatrix() {
//        return pwIdMatrix;
//    }
//
//    public static ArrayList<String> getRepresentatives() {
//        return representatives;
//    }
//
//    public static ArrayList<String> getAllPeptides() {
//        return allPeptides;
//    }

//    public static AttributeModelDAO getAttributeModelDAO(){
//        if (modelDAO == null){
//            modelDAO = new AttributeModelDAO(graphDb);
//        }
//        return modelDAO;
//    }        
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
