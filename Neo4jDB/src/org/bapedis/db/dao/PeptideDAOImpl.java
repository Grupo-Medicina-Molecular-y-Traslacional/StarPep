/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import java.awt.Color;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.db.Neo4jDB;
import org.bapedis.db.model.MyLabel;
import org.bapedis.db.model.MyRelationship;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Subgraph;
import org.gephi.graph.api.Table;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluator;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Uniqueness;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = PeptideDAO.class)
public class PeptideDAOImpl implements PeptideDAO {

    protected final GraphDatabaseService graphDb;
    protected final ProjectManager pm;

    private final String PRO_ID = "id";
    private final String PRO_SEQ = "seq";
    private final String PRO_LENGHT = "length";
    private final String PRO_NAME = "name";
    private final String PRO_XREF = "xref";

    private final float GRAPH_NODE_SIZE = 10f;
    private final float GRAPH_EDGE_WEIGHT = 1f;
    private final Color color = new Color(0.6f, 0.6f, 0.6f);

    public PeptideDAOImpl() {
        graphDb = Neo4jDB.getDbService();
        pm = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    public AttributesModel getPeptides(QueryModel queryModel, GraphModel graphModel) {
        AttributesModel attrModel = new AttributesModel();
        attrModel.addAttribute(ID);
        attrModel.addAttribute(SEQ);
        attrModel.addAttribute(LENGHT);

        for (AnnotationType aType : AnnotationType.values()) {
            attrModel.addAttribute(new PeptideAttribute(aType.name(), aType.getDisplayName(), String.class));
        }

        try (Transaction tx = graphDb.beginTx()) {
            // Get peptides
            ResourceIterator<Node> peptideNodes;
            if (queryModel.countElements() > 0) {
                List<Node> startNodes = new LinkedList<>();
                for (Metadata metadata : queryModel.getMetadatas()) {
                    startNodes.add(graphDb.getNodeById(metadata.getUnderlyingNodeID()));
                }
                peptideNodes = getPeptides(startNodes);
            } else {
                peptideNodes = getPeptides();
            }

            // Write lock
            graphModel.getGraph().writeLock();
            checkColumns(graphModel);
            GraphView gView = graphModel.createView();
            Subgraph subGraph = graphModel.getGraph(gView);

            Peptide peptide;
            org.gephi.graph.api.Node graphNode, graphNeighborNode;
            org.gephi.graph.api.Edge graphEdge;
            PeptideAttribute attr;
            String id, seq;
            try {
                while (peptideNodes.hasNext()) {
                    Node neoNode = peptideNodes.next();
                    id = neoNode.getProperty(PRO_ID).toString();
                    seq = neoNode.getProperty(PRO_SEQ).toString();
                    // Fill graph
                    graphNode = getGraphNodeFromNeoNode(neoNode, graphModel);
                    subGraph.addNode(graphNode);
                    for (Relationship relation : neoNode.getRelationships(Direction.OUTGOING)) {
                        graphNeighborNode = getGraphNodeFromNeoNode(relation.getEndNode(), graphModel);
                        graphEdge = getGraphEdgeFromNeoRelationship(graphNode, graphNeighborNode, relation, graphModel);
                        subGraph.addNode(graphNeighborNode);
                        subGraph.addEdge(graphEdge);
                    }

                    //Fill Attribute Model
                    peptide = new Peptide(graphNode, subGraph);
                    peptide.setAttributeValue(ID, id);
                    peptide.setAttributeValue(SEQ, seq);
                    peptide.setAttributeValue(LENGHT, seq.length());

                    for (String propertyKey : neoNode.getPropertyKeys()) {
                        if (!(propertyKey.equals(PRO_ID) || propertyKey.equals(PRO_SEQ)
                                || propertyKey.equals(PRO_LENGHT))) {
                            Object value = neoNode.getProperty(propertyKey);
                            if (!attrModel.hasAttribute(propertyKey)) {
                                attr = attrModel.addAttribute(propertyKey, propertyKey, value.getClass());
                            } else {
                                attr = attrModel.getAttribute(propertyKey);
                            }
                            peptide.setAttributeValue(attr, value);
                        }
                    }

                    for (AnnotationType aType : AnnotationType.values()) {
                        attr = attrModel.getAttribute(aType.name());
                        peptide.setAttributeValue(attr, peptide.getAnnotationValues(aType));
                    }
                    attrModel.addPeptide(peptide);
                }
            } finally {
                graphModel.setVisibleView(gView);
                graphModel.getGraph().writeUnlock();
                peptideNodes.close();
                tx.success();
            }
        }
        return attrModel;
    }

    protected ResourceIterator<Node> getPeptides() {
        return graphDb.findNodes(MyLabel.Peptide);
    }

    protected ResourceIterator<Node> getPeptides(List<Node> startNodes) {
        TraversalDescription td = graphDb.traversalDescription().breadthFirst();
        for (MyRelationship edge : MyRelationship.values()) {
            td = td.relationships(edge, Direction.INCOMING);
        }

        ResourceIterator<Node> nodes = td.evaluator(new Evaluator() {

            @Override
            public Evaluation evaluate(Path path) {
                boolean accepted = path.endNode().hasLabel(MyLabel.Peptide);
                return accepted ? Evaluation.INCLUDE_AND_PRUNE : Evaluation.EXCLUDE_AND_CONTINUE;
            }
        })
                .uniqueness(Uniqueness.NODE_GLOBAL)
                .traverse(startNodes)
                .nodes()
                .iterator();
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

    protected void checkColumns(GraphModel graphModel) {
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(PRO_NAME)) {
            nodeTable.addColumn(PRO_NAME, String.class);
        }

        Table edgeTable = graphModel.getEdgeTable();
        if (!edgeTable.hasColumn(PRO_XREF)) {
            edgeTable.addColumn(PRO_XREF, String[].class);
        }
    }

    protected org.gephi.graph.api.Node getGraphNodeFromNeoNode(Node neoNode, GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        String id = String.valueOf(neoNode.getId());
        org.gephi.graph.api.Node graphNode = mainGraph.getNode(id);
        if (graphNode == null) {
            GraphFactory factory = graphModel.factory();
            graphNode = factory.newNode(id);
            if (neoNode.hasProperty(PRO_NAME)) {
                graphNode.setAttribute(PRO_NAME, neoNode.getProperty(PRO_NAME));
            } else {
                graphNode.setAttribute(PRO_NAME, id);
            }
            String label = neoNode.getLabels().iterator().next().name();
            graphNode.setLabel(label);
            graphNode.setSize(GRAPH_NODE_SIZE);

            //Set random position
            graphNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
            graphNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

            //Set color
            graphNode.setR(color.getRed() / 255f);
            graphNode.setG(color.getGreen() / 255f);
            graphNode.setB(color.getBlue() / 255f);
            graphNode.setAlpha(1f);

            mainGraph.addNode(graphNode);
        }
        return graphNode;
    }

    protected org.gephi.graph.api.Edge getGraphEdgeFromNeoRelationship(org.gephi.graph.api.Node startNode, org.gephi.graph.api.Node endNode, Relationship relation, GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        String id = String.valueOf(relation.getId());
        org.gephi.graph.api.Edge graphEdge = mainGraph.getEdge(id);
        if (graphEdge == null) {
            GraphFactory factory = graphModel.factory();
            String relName = relation.getType().name();
            int relType = graphModel.addEdgeType(relName);

            graphEdge = factory.newEdge(id, startNode, endNode, relType, GRAPH_EDGE_WEIGHT, false);
            graphEdge.setLabel(relName);
            graphEdge.setAttribute(PRO_XREF, relation.getProperty(PRO_XREF));

            //Set color
            graphEdge.setR(color.getRed() / 255f);
            graphEdge.setG(color.getGreen() / 255f);
            graphEdge.setB(color.getBlue() / 255f);
            graphEdge.setAlpha(0f);

            mainGraph.addEdge(graphEdge);
        }
        return graphEdge;
    }

}
