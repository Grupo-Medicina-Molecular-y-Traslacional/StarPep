/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.db.Neo4jDB;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.db.model.NeoPeptide;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Subgraph;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = PeptideDAO.class)
public class PeptideDAOImpl implements PeptideDAO {

    protected final ProjectManager pm;
    protected final GraphDatabaseService graphDb;
    public final String PRO_ID = "id";
    public final String PRO_SEQ = "seq";
    public final String PRO_LENGHT = "length";
    public final String PRO_XREF = "xref";
    public final String PRO_NAME = "name";
    public final String PRO_LABEL = "label";
    public static final float GRAPH_NODE_SIZE = 10f;
    public static final float GRAPH_EDGE_WEIGHT = 1f;

    public PeptideDAOImpl() {
        graphDb = Neo4jDB.getDbService();
        pm = Lookup.getDefault().lookup(ProjectManager.class);

//        Table nodeTable = graphModel.getNodeTable();
//        nodeTable.addColumn(PRO_ID, long.class);
//        Table edgeTable = graphModel.getEdgeTable();
//        edgeTable.addColumn(PRO_XREF, String[].class);
    }

    @Override
    public AttributesModel loadPeptides(GraphModel graphModel, QueryModel queryModel) {
        AttributesModel attrModel = new AttributesModel();
        attrModel.addAttribute(ID);
        attrModel.addAttribute(SEQ);
        attrModel.addAttribute(LENGHT);

        for (AnnotationType aType : AnnotationType.values()) {
            attrModel.addAttribute(new PeptideAttribute(aType.name(), aType.getDisplayName(), String.class));
        }

        GraphView gView = graphModel.createView();
        Subgraph graph = graphModel.getGraph(gView);

        try (Transaction tx = graphDb.beginTx()) {
            List<Node> startNodes = new LinkedList<>();
            for (Metadata metadata : queryModel.getMetadatas()) {
                startNodes.add(graphDb.getNodeById(metadata.getUnderlyingNodeID()));
            }

            Iterable<Node> peptideNodes = getPeptides(startNodes, queryModel.getLabels());
            NeoPeptide neoPeptide;
            org.gephi.graph.api.Node graphNode, graphNeighborNode;
            org.gephi.graph.api.Edge graphEdge;
            PeptideAttribute attr;
            String id, seq;
            for (Node neoNode : peptideNodes) {
                id = neoNode.getProperty(PRO_ID).toString();
                seq = neoNode.getProperty(PRO_SEQ).toString();
                // Fill graph
                graphNode = getGraphNodeFromNeoNode(neoNode, graphModel);
                graph.addNode(graphNode);
                for (Relationship relation : neoNode.getRelationships(Direction.OUTGOING)) {
                    graphNeighborNode = getGraphNodeFromNeoNode(relation.getEndNode(), graphModel);
                    graphEdge = getGraphEdgeFromNeoRelationship(graphNode, graphNeighborNode, relation, graphModel);
                    graph.addNode(graphNeighborNode);
                    graph.addEdge(graphEdge);
//                    if (relation.getEndNode().equals(endNode)) {
//                        neoNeighbor = new NeoNeighbor(endNode.getId(), endNode.getLabels().iterator().next().name(),
//                                endNode.getProperty(PRO_NAME).toString(), (String[]) relation.getProperty(PRO_XREF));
//                        neighbors.add(neoNeighbor);
//                    }
                }

                //Fill NeoPeptideModel
                neoPeptide = new NeoPeptide(neoNode.getId(), graphNode, graph);
                neoPeptide.setAttributeValue(ID, id);
                neoPeptide.setAttributeValue(SEQ, seq);
                neoPeptide.setAttributeValue(LENGHT, seq.length());

                for (String propertyKey : neoNode.getPropertyKeys()) {
                    if (!(propertyKey.equals(PRO_ID) || propertyKey.equals(PRO_SEQ)
                            || propertyKey.equals(PRO_LENGHT))) {
                        Object value = neoNode.getProperty(propertyKey);
                        if (!attrModel.hasAttribute(propertyKey)) {
                            attr = attrModel.addAttribute(propertyKey, propertyKey, value.getClass());
                        } else {
                            attr = attrModel.getAttribute(propertyKey);
                        }
                        neoPeptide.setAttributeValue(attr, value);
                    }
                }

                for (AnnotationType aType : AnnotationType.values()) {
                    attr = attrModel.getAttribute(aType.name());
                    neoPeptide.setAttributeValue(attr, neoPeptide.getAnnotationValues(aType));
                }
                attrModel.addPeptide(neoPeptide);
            }
            tx.success();
        }

        attrModel.setGraph(graph);
        return attrModel;
    }

    private enum RELS implements RelationshipType {
        is_a, instance_of
    }

    protected Iterable<Node> getPeptides(List<Node> startNodes, final Label[] labels) {
        Iterable<Node> nodes = graphDb.traversalDescription()
                .breadthFirst()
                .relationships(RELS.is_a, Direction.INCOMING)
                .relationships(RELS.instance_of, Direction.INCOMING)
                .evaluator(new Evaluator() {

                    @Override
                    public Evaluation evaluate(Path path) {
                        boolean accepted = false;
                        if (path.endNode().hasLabel(DynamicLabel.label("Peptide"))) {
                            accepted = true;
                            if (labels != null) {
                                for (Label label : labels) {
                                    if (!path.endNode().hasLabel(label)) {
                                        accepted = false;
                                        break;
                                    }
                                }
                            }

                        }
                        return accepted ? Evaluation.INCLUDE_AND_PRUNE : Evaluation.EXCLUDE_AND_CONTINUE;
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

    private org.gephi.graph.api.Node getGraphNodeFromNeoNode(Node neoNode, GraphModel graphModel) {
        String id = String.valueOf(neoNode.getId());
        org.gephi.graph.api.Node graphNode = graphModel.getGraph().getNode(id);
        if (graphNode == null) {
            GraphFactory factory = graphModel.factory();
            graphNode = factory.newNode(id);
            if (neoNode.hasProperty(PRO_NAME)) {
                graphNode.setLabel(neoNode.getProperty(PRO_NAME).toString());
            } else {
                graphNode.setLabel(id);
            }
            graphNode.setSize(GRAPH_NODE_SIZE);

            //Set random position
            graphNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
            graphNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

            graphModel.getGraph().addNode(graphNode);
        }
        return graphNode;
    }

    private org.gephi.graph.api.Edge getGraphEdgeFromNeoRelationship(org.gephi.graph.api.Node startNode, org.gephi.graph.api.Node endNode, Relationship relation, GraphModel graphModel) {
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
