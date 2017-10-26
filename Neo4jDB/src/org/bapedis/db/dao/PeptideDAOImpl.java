/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.dao;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.RestrictionLevel;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.db.Neo4jDB;
import org.bapedis.db.model.MyLabel;
import org.bapedis.db.model.MyRelationship;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Subgraph;
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
    
    private final TraversalDescription peptideTraversal, metadataTraversal;

    public PeptideDAOImpl() {
        graphDb = Neo4jDB.getDbService();
        pm = Lookup.getDefault().lookup(ProjectManager.class);

        TraversalDescription td;
        // Peptide traversal from metadata
        td = graphDb.traversalDescription();
        for (MyRelationship edge : MyRelationship.values()) {
            td = td.relationships(edge, Direction.INCOMING);
        }
        peptideTraversal = td.uniqueness(Uniqueness.NODE_GLOBAL)
                .breadthFirst()
                .evaluator(Evaluators.excludeStartPosition());

        // Metadata traversal from peptide node
        td = graphDb.traversalDescription().breadthFirst();
        for (MyRelationship edge : MyRelationship.values()) {
            td = td.relationships(edge, Direction.OUTGOING);
        }
        metadataTraversal = td.uniqueness(Uniqueness.NODE_GLOBAL)
                .breadthFirst()
                .evaluator(Evaluators.excludeStartPosition());
//                .evaluator(new Evaluator() {
//                    @Override
//                    public Evaluation evaluate(Path path) {
//                        boolean isMetadata = !path.endNode().hasLabel(MyLabel.Peptide);
//                        return isMetadata ? Evaluation.INCLUDE_AND_CONTINUE : Evaluation.EXCLUDE_AND_CONTINUE;
//                    }
//                });
    }

    @Override
    public AttributesModel getPeptides(QueryModel queryModel, GraphModel graphModel) {
        AttributesModel attrModel = new AttributesModel();
        attrModel.addAttribute(ID);
        attrModel.addAttribute(SEQ);
        attrModel.addAttribute(LENGHT);

        try (Transaction tx = graphDb.beginTx()) {
            // Get peptides
            ResourceIterator<Node> peptideNodes;
            if (queryModel.countElements() > 0) {
                List<Node> metadataNodes = new LinkedList<>();
                for (Iterator<Metadata> it = queryModel.getMetadataIterator(); it.hasNext();) {
                    Metadata metadata = it.next();
                    metadataNodes.add(graphDb.getNodeById(Long.valueOf(metadata.getUnderlyingNodeID())));
                }
                peptideNodes = getPeptides(metadataNodes, queryModel.getRestriction());
            } else {
                peptideNodes = getPeptides();
            }

            // Write lock
            graphModel.getGraph().writeLock();
            GraphView graphDBView = null;
            GraphView csnView = null;

            Peptide peptide;
            org.gephi.graph.api.Node graphNode, graphNeighborNode;
            org.gephi.graph.api.Edge graphEdge;
            PeptideAttribute attr;
            String id, seq;
            try {
                graphDBView = graphModel.createView();
                Subgraph subGraphDB = graphModel.getGraph(graphDBView);

                csnView = graphModel.createView();
                Subgraph subGraphCSN = graphModel.getGraph(csnView);

                while (peptideNodes.hasNext()) {
                    Node neoNode = peptideNodes.next();
                    id = neoNode.getProperty(PRO_ID).toString();
                    seq = neoNode.getProperty(PRO_SEQ).toString();
                    // Fill graph
                    graphNode = getGraphNodeFromNeoNode(neoNode, graphModel);
                    subGraphDB.addNode(graphNode);
                    subGraphCSN.addNode(graphNode);
                    for (Relationship relation : neoNode.getRelationships(Direction.OUTGOING)) {
                        graphNeighborNode = getGraphNodeFromNeoNode(relation.getEndNode(), graphModel);
                        graphEdge = getGraphEdgeFromNeoRelationship(graphNode, graphNeighborNode, relation, graphModel);
                        subGraphDB.addNode(graphNeighborNode);
                        subGraphDB.addEdge(graphEdge);
                    }

                    //Fill Attribute Model
                    peptide = new Peptide(graphNode, graphModel.getGraph());
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
                    attrModel.addPeptide(peptide);
                }
            } finally {
                //Write unlock
                graphModel.getGraph().writeUnlock();
                attrModel.setCsnView(csnView);
                attrModel.setGraphDBView(graphDBView);
                peptideNodes.close();
                tx.success();
            }
        }
        return attrModel;
    }

    protected ResourceIterator<Node> getPeptides() {
        return graphDb.findNodes(MyLabel.Peptide);
    }

    protected ResourceIterator<Node> getPeptides(final List<Node> metadataNodes, RestrictionLevel restriction) {
        Evaluator restrictiveEvaluator = null;

        if (restriction == RestrictionLevel.MATCH_ANY) {
            restrictiveEvaluator = new Evaluator() {
                @Override
                public Evaluation evaluate(Path path) {
                    boolean accepted = path.endNode().hasLabel(MyLabel.Peptide);
                    return accepted ? Evaluation.INCLUDE_AND_PRUNE : Evaluation.EXCLUDE_AND_CONTINUE;
                }
            };
        } else if (restriction == RestrictionLevel.MATCH_ALL) {
            final Node[] endNodes = metadataNodes.toArray(new Node[0]);
            restrictiveEvaluator = new Evaluator() {
                @Override
                public Evaluation evaluate(Path path) {
                    if (path.endNode().hasLabel(MyLabel.Peptide)) {
                        LinkedList<Node> metadataList = new LinkedList<>();
                        try (ResourceIterator<Node> nodes = getMetadata(path.endNode(), endNodes)) {
                            while (nodes.hasNext()) {
                                metadataList.add(nodes.next());
                            }
                        }
                        boolean accepted = metadataList.containsAll(metadataNodes);
                        return accepted ? Evaluation.INCLUDE_AND_PRUNE : Evaluation.EXCLUDE_AND_PRUNE;
                    }
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                }
            };
        }

        ResourceIterator<Node> nodes = peptideTraversal.evaluator(restrictiveEvaluator)
                .traverse(metadataNodes)
                .nodes()
                .iterator();

        return nodes;
    }

    private ResourceIterator<Node> getMetadata(Node peptideNode, Node[] metadataNodes) {
        ResourceIterator<Node> nodes = metadataTraversal.evaluator(Evaluators.pruneWhereEndNodeIs(metadataNodes))
                .traverse(peptideNode)
                .nodes()
                .iterator();

        return nodes;
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

    protected org.gephi.graph.api.Node getGraphNodeFromNeoNode(Node neoNode, GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        String id = String.valueOf(neoNode.getId());
        org.gephi.graph.api.Node graphNode = mainGraph.getNode(id);
        if (graphNode == null) {
            GraphFactory factory = graphModel.factory();
            graphNode = factory.newNode(id);
            if (neoNode.hasProperty(PRO_NAME)) {
                graphNode.setAttribute(ProjectManager.NODE_TABLE_PRO_NAME, neoNode.getProperty(PRO_NAME));
            } else {
                graphNode.setAttribute(ProjectManager.NODE_TABLE_PRO_NAME, id);
            }
            String label = neoNode.getLabels().iterator().next().name();
            graphNode.setLabel(label);
            graphNode.setSize(ProjectManager.GRAPH_NODE_SIZE);

            //Set random position
            graphNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
            graphNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

            //Set color
            graphNode.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
            graphNode.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
            graphNode.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
            graphNode.setAlpha(1f);

            mainGraph.addNode(graphNode);
        }
        return graphNode;
    }

    protected Edge getGraphEdgeFromNeoRelationship(org.gephi.graph.api.Node startNode, org.gephi.graph.api.Node endNode, Relationship relation, GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        String id = String.valueOf(relation.getId());
        Edge graphEdge = mainGraph.getEdge(id);
        if (graphEdge == null) {
            GraphFactory factory = graphModel.factory();
            String relName = relation.getType().name();
            int relType = graphModel.addEdgeType(relName);

            graphEdge = factory.newEdge(id, startNode, endNode, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
            graphEdge.setLabel(relName);
            graphEdge.setAttribute(PRO_XREF, relation.getProperty(PRO_XREF));

            //Set color
            graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
            graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
            graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
            graphEdge.setAlpha(0f);

            mainGraph.addEdge(graphEdge);
        }
        return graphEdge;
    }

}
