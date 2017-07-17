/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.db.Neo4jDB;
import org.bapedis.core.model.Metadata;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.data.MetadataDAO;
import org.neo4j.graphdb.ResourceIterator;

/**
 *
 * @author loge
 */
@ServiceProvider(service = MetadataDAO.class)
public class MetadataDAOImpl implements MetadataDAO{
    protected final GraphDatabaseService graphDb;
    
    public MetadataDAOImpl() {
        graphDb = Neo4jDB.getDbService();
    }
    
    @Override
    public List<Metadata> getMetadata(AnnotationType type) {
        switch(type){
            case NAME:
                return getMetadata(type, DynamicLabel.label("Name"));
            case ORIGIN:
                return getMetadata(type, DynamicLabel.label("Origin"));
            case TARGET:
                return getMetadata(type, DynamicLabel.label("Target"));
            case BIOCATEGORY:
                return getBioCategory();
            case DATABASE:
                return getMetadata(type, DynamicLabel.label("Database")); 
            case LITERATURE:
                return getMetadata(type, DynamicLabel.label("Literature"));
            case CROSSREF:
                return getMetadata(type, DynamicLabel.label("CrossRef"));
        }
        return null;
    }
    
    protected List<Metadata> getMetadata(AnnotationType type, Label label){
        List<Metadata> list = new LinkedList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterator<Node> nodes = graphDb.findNodes(label);
            if (nodes != null){
                Node node;
                Metadata metadata;
                while(nodes.hasNext()){
                    node = nodes.next();
                    metadata = new Metadata(node.getId(), node.getProperty("name").toString(), type);
                    list.add(metadata);
                }
                nodes.close();
            }
            tx.success();
        }        
        return list;
    }
    
    protected List<Metadata> getBioCategory() {
        try (Transaction tx = graphDb.beginTx()) {
            Label label = DynamicLabel.label("BioCategory");
            Node node = graphDb.findNode(label, "name", "Peptide");
            Metadata category = getBioCategory(node);
            tx.success();
            return category.getChilds();            
        }        
    }
    
    protected Metadata getBioCategory(Node root) {
        Metadata category = new Metadata(root.getId(), root.getProperty("name").toString(), AnnotationType.BIOCATEGORY);
        DynamicRelationshipType IS_A = DynamicRelationshipType.withName("is_a");
        Iterable<Relationship> rels = root.getRelationships(Direction.INCOMING, IS_A);
        for (Relationship rel : rels) {
            Node startNode = rel.getStartNode();
            category.addChild(getBioCategory(startNode));
        }        
        return category;
    }
    
}
