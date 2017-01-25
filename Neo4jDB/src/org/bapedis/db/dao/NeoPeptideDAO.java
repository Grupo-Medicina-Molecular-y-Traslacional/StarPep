/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.db.Neo4jDB;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.model.BioCategory;
import org.bapedis.db.model.FilterModel;
import org.bapedis.db.model.NeoPeptideModel;
import org.bapedis.db.model.NeoNeighbor;
import org.bapedis.db.model.NeoPeptide;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Uniqueness;

/**
 *
 * @author loge
 */
public class NeoPeptideDAO {

    protected final GraphDatabaseService graphDb;
    public final String PRO_ID = "id";
    public final String PRO_SEQUENCE = "seq";
    public final String PRO_XREF = "xref";
    public final String PRO_NAME = "name";
    public final String PRO_LABEL = "label";

    public NeoPeptideDAO() {
        graphDb = Neo4jDB.getDbService();
    }

    private enum RELS implements RelationshipType {

        is_a, instance_of
    }

    public NeoPeptideModel getNeoPeptidesBy(BioCategory[] categories, PeptideAttribute[] attributes, FilterModel filterModel) {
        NeoPeptideModel neoModel = new NeoPeptideModel();
        if (attributes != null) {
            for (PeptideAttribute attr : attributes) {
                neoModel.addAttribute(attr);
            }
        }
        if (!neoModel.hasAttribute(PRO_ID)) {
            PeptideAttribute attr = neoModel.addAttribute(PRO_ID, PRO_ID, String.class);
            attr.setVisible(false);
        }
        if (!neoModel.hasAttribute(PRO_XREF)) {
            PeptideAttribute attr = neoModel.addAttribute(PRO_XREF, PRO_XREF, String[].class);
            attr.setVisible(false);
        }
        try (Transaction tx = graphDb.beginTx()) {
            List<Node> startNodes = new LinkedList<>();
            for (BioCategory category : categories) {
                startNodes.add(graphDb.getNodeById(category.getUnderlyingNodeID()));
            }
            Iterable<Node> peptideNodes = getPeptides(startNodes);
            NeoPeptide neoPeptide;
            PeptideAttribute attr;
            String id, seq;
            String[] xref;
            for (Node node : peptideNodes) {
                id = node.getProperty(PRO_ID).toString();
                seq = node.getProperty(PRO_SEQUENCE).toString();
                xref = (String[]) node.getProperty(PRO_XREF);
                neoPeptide = new NeoPeptide(node.getId(), id, seq, xref, this);
                for (String propertyKey : node.getPropertyKeys()) {
                    if (!propertyKey.equals(PRO_XREF)) {
                        Object value = node.getProperty(propertyKey);
                        if (!neoModel.hasAttribute(propertyKey)) {
                            attr = neoModel.addAttribute(propertyKey, propertyKey, value.getClass());
                        } else {
                            attr = neoModel.getAttribute(propertyKey);
                        }
                        neoPeptide.setAttributeValue(attr, value);
                    }
                }
                if (filterModel == null || isAccepted(neoPeptide, filterModel)) {
                    neoModel.addPeptide(neoPeptide);
                }
            }
            tx.success();
        }
        return neoModel;
    }

    protected boolean isAccepted(NeoPeptide neoPeptide, FilterModel filterModel) {
        FilterModel.RestrictionLevel restriction = filterModel.getRestriction();
        switch (restriction) {
            case MATCH_ALL:
                for (Filter filter : filterModel.getFilters()) {
                    if (!filter.accept(neoPeptide)) {
                        return false;
                    }
                }
                return true;
            case MATCH_ANY:
                for (Filter filter : filterModel.getFilters()) {
                    if (filter.accept(neoPeptide)) {
                        return true;
                    }
                }
                return false;
        }
        return false;
    }

    protected Iterable<Node> getPeptides(List<Node> startNodes) {
        Iterable<Node> nodes = graphDb.traversalDescription()
                .breadthFirst()
                .relationships(RELS.is_a, Direction.INCOMING)
                .relationships(RELS.instance_of, Direction.INCOMING)
                .evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path path) {
                        if (path.endNode().hasLabel(DynamicLabel.label("Peptide"))) {
                            return Evaluation.INCLUDE_AND_PRUNE;
                        } else {
                            return Evaluation.EXCLUDE_AND_CONTINUE;
                        }
                    }
                })
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .traverse(startNodes)
                .nodes();
        return nodes;
    }

    protected Iterable<Node> getNeighbors(Node startNode) {
        Iterable<Node> nodes = graphDb.traversalDescription()
                .breadthFirst()
                .evaluator(Evaluators.atDepth(1))
                .evaluator(Evaluators.excludeStartPosition())
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .traverse(startNode)
                .nodes();
        return nodes;
    }

    public List<NeoNeighbor> getNeoNeighbors(NeoPeptide neoPeptide) {
        List<NeoNeighbor> neighbors = new LinkedList<>();
        try (Transaction tx = graphDb.beginTx()) {
            Iterable<Node> neighborNodes = getNeighbors(graphDb.getNodeById(neoPeptide.getNeoId()));
            NeoNeighbor neoNeighbor;
            for (Node node : neighborNodes) {
                neoNeighbor = new NeoNeighbor(node.getId(), node.getLabels().iterator().next().name(), node.getProperty(PRO_NAME).toString());
                neighbors.add(neoNeighbor);
//                for (String propertyKey : node.getPropertyKeys()) {
//                    Object value = node.getProperty(propertyKey);
//                    if (!neoNeighborsModel.hasAttribute(propertyKey)) {
//                        attr = neoNeighborsModel.addAttribute(propertyKey, propertyKey, value.getClass());
//                        attr.setVisible(false);
//                    } else {
//                        attr = neoNeighborsModel.getAttribute(propertyKey);
//                    }
//                    neoNeighbor.setAttributeValue(attr, value);
//                }
//                neoNeighborsModel.addNeighbor(neoNeighbor);
            }
            tx.success();
        }
        return neighbors;
    }

    protected Iterable<Relationship> getRelationships(Node startNode) {
        Iterable<Relationship> edges = graphDb.traversalDescription()
                .breadthFirst()
                .evaluator(Evaluators.atDepth(1))
                .evaluator(Evaluators.excludeStartPosition())
                .uniqueness(Uniqueness.NODE_PATH)
                .traverse(startNode)
                .relationships();

        return edges;
    }

}
