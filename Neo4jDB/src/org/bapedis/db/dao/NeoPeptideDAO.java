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
import org.bapedis.db.model.BioCategory;
import org.bapedis.core.model.Peptide;
import org.bapedis.db.model.AnnotationType;
import org.bapedis.db.model.NeoPeptideModel;
import org.bapedis.db.model.NeoPeptide;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Table;
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
    public final String PRO_SEQ = "seq";
    public final String PRO_LENGHT = "length";
    public final String PRO_XREF = "xref";
    public final String PRO_NAME = "name";
    public final String PRO_LABEL = "label";
    public static final float GRAPH_NODE_SIZE = 10f;
    public static final float GRAPH_EDGE_WEIGHT = 1f;

    public NeoPeptideDAO() {
        graphDb = Neo4jDB.getDbService();
    }

    private enum RELS implements RelationshipType {

        is_a, instance_of
    }

    public NeoPeptideModel getNeoPeptidesBy(BioCategory[] categories) {
        GraphModel graphModel = GraphModel.Factory.newInstance();
//        Table nodeTable = graphModel.getNodeTable();
//        nodeTable.addColumn(PRO_ID, long.class);
        Table edgeTable = graphModel.getEdgeTable();
        edgeTable.addColumn(PRO_XREF, String[].class);

        NeoPeptideModel neoModel = new NeoPeptideModel();
        neoModel.setGraphModel(graphModel);

        neoModel.addAttribute(Peptide.ID);
        neoModel.addAttribute(Peptide.SEQ);
        neoModel.addAttribute(Peptide.LENGHT);
        for (AnnotationType aType : AnnotationType.values()) {
            neoModel.addAttribute(new PeptideAttribute(aType.name(), aType.getDisplayName(), String.class));
        }

        try (Transaction tx = graphDb.beginTx()) {
            List<Node> startNodes = new LinkedList<>();
            for (BioCategory category : categories) {
                startNodes.add(graphDb.getNodeById(category.getUnderlyingNodeID()));
            }

            Iterable<Node> peptideNodes = getPeptides(startNodes);
            NeoPeptide neoPeptide;
            org.gephi.graph.api.Node graphNode, graphNeighborNode;
            org.gephi.graph.api.Edge graphEdge;
            PeptideAttribute attr;
            String id, seq;
            for (Node neoNode : peptideNodes) {
                id = neoNode.getProperty(PRO_ID).toString();
                seq = neoNode.getProperty(PRO_SEQ).toString();
                // Fill graph
                graphNode = addGraphNodeFromNeoNode(neoNode, graphModel);
                graphNode.setLabel(id);
                for (Relationship relation : neoNode.getRelationships(Direction.OUTGOING)) {
                    graphNeighborNode = addGraphNodeFromNeoNode(neoNode, graphModel);
                    graphEdge = addGraphEdgeFromNeoRelationship(graphNode, graphNeighborNode, relation, graphModel);
//                    if (relation.getEndNode().equals(endNode)) {
//                        neoNeighbor = new NeoNeighbor(endNode.getId(), endNode.getLabels().iterator().next().name(),
//                                endNode.getProperty(PRO_NAME).toString(), (String[]) relation.getProperty(PRO_XREF));
//                        neighbors.add(neoNeighbor);
//                    }
                }

                //Fill NeoPeptideModel
                neoPeptide = new NeoPeptide(neoNode.getId(), graphNode, graphModel.getGraph());
                neoPeptide.setAttributeValue(Peptide.ID, id);
                neoPeptide.setAttributeValue(Peptide.SEQ, seq);
                neoPeptide.setAttributeValue(Peptide.LENGHT, seq.length());

                for (String propertyKey : neoNode.getPropertyKeys()) {
                    if (!(propertyKey.equals(PRO_ID) || propertyKey.equals(PRO_SEQ)
                            || propertyKey.equals(PRO_LENGHT))) {
                        Object value = neoNode.getProperty(propertyKey);
                        if (!neoModel.hasAttribute(propertyKey)) {
                            attr = neoModel.addAttribute(propertyKey, propertyKey, value.getClass());
                        } else {
                            attr = neoModel.getAttribute(propertyKey);
                        }
                        neoPeptide.setAttributeValue(attr, value);
                    }
                }

                for (AnnotationType aType : AnnotationType.values()) {
                    attr = neoModel.getAttribute(aType.name());
                    neoPeptide.setAttributeValue(attr, neoPeptide.getAnnotationValues(aType));
                }
                neoModel.addPeptide(neoPeptide);
            }
            tx.success();
        }
        return neoModel;
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

//    private Iterable<Node> getNeighbors(Node startNode) {
//        Iterable<Node> nodes = graphDb.traversalDescription()
//                .breadthFirst()
//                .evaluator(Evaluators.atDepth(1))
//                .evaluator(Evaluators.excludeStartPosition())
//                .uniqueness(Uniqueness.NODE_GLOBAL)
//                .traverse(startNode)
//                .nodes();
//
//        return nodes;
//    }
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

    private org.gephi.graph.api.Node addGraphNodeFromNeoNode(Node neoNode, GraphModel graphModel) {
        String id = String.valueOf(neoNode.getId());
        org.gephi.graph.api.Node graphNode = graphModel.getGraph().getNode(id);
        if (graphNode == null) {
            GraphFactory factory = graphModel.factory();
            graphNode = factory.newNode(id);
            if (neoNode.hasProperty(PRO_NAME)) {
                graphNode.setLabel(neoNode.getProperty(PRO_NAME).toString());
            }
            graphNode.setSize(GRAPH_NODE_SIZE);

            //Set random position to the neoNode:
            graphNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
            graphNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

            graphModel.getGraph().addNode(graphNode);
        }
        return graphNode;
    }

    private org.gephi.graph.api.Edge addGraphEdgeFromNeoRelationship(org.gephi.graph.api.Node startNode, org.gephi.graph.api.Node endNode, Relationship relation, GraphModel graphModel) {
        String id = String.valueOf(relation.getId());
        org.gephi.graph.api.Edge graphEdge = graphModel.getGraph().getEdge(id);
        if (graphEdge == null) {
            GraphFactory factory = graphModel.factory();
            String relName = relation.getType().name();
            int relType = graphModel.addEdgeType(relName);

            graphEdge = factory.newEdge(id, startNode, endNode, relType, GRAPH_EDGE_WEIGHT, false);
            graphEdge.setLabel(relName);
            graphEdge.setAttribute(PRO_XREF, relation.getProperty(PRO_XREF));
            
            graphModel.getGraph().addEdge(graphEdge);
        }
        return graphEdge;
    }

}
