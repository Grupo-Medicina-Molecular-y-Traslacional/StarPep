/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import org.bapedis.db.Neo4jDB;
import org.bapedis.db.model.Metadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

/**
 *
 * @author loge
 */
public class BioCategoryDAO{
    protected final String rootName = "Peptide";
    protected final GraphDatabaseService graphDb;
    
    public BioCategoryDAO() {
        graphDb = Neo4jDB.getDbService();
    }
    
    public Metadata getRootBioCategory(){
        return getBioCategory(rootName);
    }
    
    public Metadata getBioCategory(String name) {
        try (Transaction tx = graphDb.beginTx()) {
            Label TYPE = DynamicLabel.label("BioCategory");
            Node node = graphDb.findNode(TYPE, "name", name);
            Metadata category = getBioCategory(node);
            tx.success();
            return category;            
        }        
    }
    
    protected Metadata getBioCategory(Node root) {
        Metadata category = new Metadata(root.getId(), root.getProperty("name").toString());
        DynamicRelationshipType IS_A = DynamicRelationshipType.withName("is_a");
        Iterable<Relationship> rels = root.getRelationships(Direction.INCOMING, IS_A);
        for (Relationship rel : rels) {
            Node startNode = rel.getStartNode();
            category.addChild(getBioCategory(startNode));
        }        
        return category;
    }
    
}
