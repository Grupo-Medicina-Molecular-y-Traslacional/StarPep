/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.StarPepAnnotationType;
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
import org.bapedis.db.model.StarPepRelationships;
import org.neo4j.graphdb.ResourceIterator;

/**
 *
 * @author loge
 */
@ServiceProvider(service = MetadataDAO.class)
public class MetadataDAOImpl implements MetadataDAO {

    static final String PRO_NAME = "name";
    static final  String ROOT_METADATA = "RootMetaData";
    private final GraphDatabaseService graphDb;

    public MetadataDAOImpl() {
        graphDb = Neo4jDB.getDbService();
    }

    @Override
    public List<Metadata> getMetadata(StarPepAnnotationType type) {
        switch (type) {
//            case NAME:
//                return getMetadata(type, MyLabel.Name);
            case ORIGIN:
                return getMetadataTree(type, MyLabel.Origin);
            case TARGET:
                return getMetadataTree(type, MyLabel.Target);
            case FUNCTION:
                return getMetadataTree(type, MyLabel.Function);
            case DATABASE:
                return getMetadata(type, MyLabel.Database);
//            case LITERATURE:
//                return getMetadata(type, MyLabel.Literature);
            case CROSSREF:
                return getMetadata(type, MyLabel.CrossRef);
        }
        return null;
    }

    protected List<Metadata> getMetadata(StarPepAnnotationType type, Label label) {
        List<Metadata> list = new LinkedList<>();
        try (Transaction tx = graphDb.beginTx()) {
            ResourceIterator<Node> nodes = graphDb.findNodes(label);
            if (nodes != null) {
                Node node;
                Metadata metadata;
                String id;
                while (nodes.hasNext()) {
                    node = nodes.next();
                    id = String.valueOf(node.getId());
                    metadata = new Metadata(id, node.getProperty(PRO_NAME).toString(), type);
                    list.add(metadata);
                }
                nodes.close();
            }
            tx.success();
        }
        return list;
    }

    protected List<Metadata> getMetadataTree(StarPepAnnotationType type, Label label) {
        try (Transaction tx = graphDb.beginTx()) {
            Node node = graphDb.findNode(label, PRO_NAME, ROOT_METADATA);
            Metadata category = getMetadataTree(type, null, node);
            tx.success();
            return category.getChilds();
        }
    }

    protected Metadata getMetadataTree(StarPepAnnotationType type, Metadata parent, Node root) {
        Iterable<Relationship> rels = root.getRelationships(Direction.INCOMING, StarPepRelationships.is_a);
        boolean isLeaf = !rels.iterator().hasNext();
        String id = String.valueOf(root.getId());
        Metadata category = new Metadata(parent, id, root.getProperty(PRO_NAME).toString(), type, isLeaf);
        for (Relationship rel : rels) {
            Node startNode = rel.getStartNode();
            category.addChild(getMetadataTree(type, category, startNode));
        }
        return category;
    }

}
