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
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.data.MetadataDAO;
import org.bapedis.db.model.MyLabel;
import org.bapedis.db.model.MyRelationship;
import org.neo4j.graphdb.ResourceIterator;

/**
 *
 * @author loge
 */
@ServiceProvider(service = MetadataDAO.class)
public class MetadataDAOImpl implements MetadataDAO {

    private final String PRO_NAME = "name";
    private final GraphDatabaseService graphDb;

    public MetadataDAOImpl() {
        graphDb = Neo4jDB.getDbService();
    }

    @Override
    public List<Metadata> getMetadata(AnnotationType type) {
        switch (type) {
//            case NAME:
//                return getMetadata(type, MyLabel.Name);
            case ORIGIN:
                return getMetadata(type, MyLabel.Origin);
            case TARGET:
                return getMetadata(type, MyLabel.Target);
            case FUNCTION:
                return getBioCategory();
            case DATABASE:
                return getMetadata(type, MyLabel.Database);
//            case LITERATURE:
//                return getMetadata(type, MyLabel.Literature);
            case CROSSREF:
                return getMetadata(type, MyLabel.CrossRef);
        }
        return null;
    }

    protected List<Metadata> getMetadata(AnnotationType type, Label label) {
        List<Metadata> list = new LinkedList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterator<Node> nodes = graphDb.findNodes(label);
            if (nodes != null) {
                Node node;
                Metadata metadata;
                while (nodes.hasNext()) {
                    node = nodes.next();
                    metadata = new Metadata(String.valueOf(node.getId()), node.getProperty(PRO_NAME).toString(), type);
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
            Node node = graphDb.findNode(MyLabel.BioCategory, PRO_NAME, "Peptide");
            Metadata category = getBioCategory(null, node);
            tx.success();
            return category.getChilds();
        }
    }

    protected Metadata getBioCategory(Metadata parent, Node root) {
        Iterable<Relationship> rels = root.getRelationships(Direction.INCOMING, MyRelationship.is_a);
        boolean isLeaf = !rels.iterator().hasNext();
        Metadata category = new Metadata(parent, String.valueOf(root.getId()), root.getProperty(PRO_NAME).toString(), AnnotationType.FUNCTION, isLeaf);
        for (Relationship rel : rels) {
            Node startNode = rel.getStartNode();
            category.addChild(getBioCategory(category, startNode));
        }
        return category;
    }

}
