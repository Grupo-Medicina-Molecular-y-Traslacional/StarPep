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
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.RestrictionLevel;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.db.Neo4jDB;
import org.bapedis.db.model.StarPepLabel;
import org.bapedis.db.model.StarPepRelationships;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphFactory;
import org.gephi.graph.api.GraphModel;
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

    private final String PRO_SEQ = "seq";
    private final String PRO_NAME = "name";
    private final String PRO_XREF = "dbRef";

    private final TraversalDescription peptideTraversal, metadataTraversal;

    public PeptideDAOImpl() {
        graphDb = Neo4jDB.getDbService();
        pm = Lookup.getDefault().lookup(ProjectManager.class);

        TraversalDescription td;
        // Peptide traversal from metadata
        td = graphDb.traversalDescription();
        for (StarPepRelationships edge : StarPepRelationships.values()) {
            td = td.relationships(edge, Direction.INCOMING);
        }
        peptideTraversal = td.uniqueness(Uniqueness.NODE_GLOBAL)
                .breadthFirst()
                .evaluator(Evaluators.excludeStartPosition());

        // Metadata traversal from peptide node
        td = graphDb.traversalDescription();
        for (StarPepRelationships edge : StarPepRelationships.values()) {
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

        try (Transaction tx = graphDb.beginTx()) {
            // Get peptides
            ResourceIterator<Node> peptideNodes;
            if (queryModel.countElements() > 0) {
                List<Node> metadataNodes = new LinkedList<>();
                for (Iterator<Metadata> it = queryModel.getMetadataIterator(); it.hasNext();) {
                    Metadata metadata = it.next();
                    metadataNodes.add(graphDb.getNodeById(Long.parseLong(metadata.getID())));
                }
                peptideNodes = getPeptides(metadataNodes, queryModel.getRestriction());
            } else {
                peptideNodes = getPeptides();
            }

            // Write lock
            Peptide peptide;
            org.gephi.graph.api.Node graphNode, graphNeighborNode;
            String id, seq;
            Node neoNode, neoNeighborNode;
            try {
                while (peptideNodes.hasNext()) {
                    neoNode = peptideNodes.next();
                    seq = neoNode.getProperty(PRO_SEQ).toString();
                    // Fill graph
                    graphNode = getOrAddGraphNodeFromNeoNode(neoNode, graphModel);
                    id = (String) graphNode.getId();
                    for (Relationship relation : neoNode.getRelationships(Direction.OUTGOING)) {
                        neoNeighborNode = relation.getEndNode();
                        graphNeighborNode = getOrAddGraphNodeFromNeoNode(neoNeighborNode, graphModel);
                        getOrAddGraphEdgeFromNeoRelationship(graphNode, graphNeighborNode, relation, graphModel);
                    }

                    //Fill Attribute Model
                    peptide = new Peptide(graphNode, graphModel.getGraph());
                    peptide.setAttributeValue(Peptide.ID, Integer.parseInt(id));
                    peptide.setAttributeValue(Peptide.SEQ, seq);
                    peptide.setAttributeValue(Peptide.LENGHT, seq.length());

                    attrModel.addPeptide(peptide);
                }
            } finally {                
                peptideNodes.close();
                tx.success();
            }
        }
        return attrModel;
    }

    protected ResourceIterator<Node> getPeptides() {
        return graphDb.findNodes(StarPepLabel.Peptide);
    }

    protected ResourceIterator<Node> getPeptides(final List<Node> metadataNodes, RestrictionLevel restriction) {
        ResourceIterator<Node> nodes = null;
        Evaluator restrictiveEvaluator;
        if (metadataNodes.size() == 1 || restriction == RestrictionLevel.MATCH_ANY) {

            restrictiveEvaluator = new Evaluator() {
                @Override
                public Evaluation evaluate(Path path) {
                    boolean accepted = path.endNode().hasLabel(StarPepLabel.Peptide);
                    return accepted ? Evaluation.INCLUDE_AND_PRUNE : Evaluation.EXCLUDE_AND_CONTINUE;
                }
            };
            nodes = peptideTraversal.evaluator(restrictiveEvaluator)
                    .traverse(metadataNodes)
                    .nodes()
                    .iterator();
        } else if (metadataNodes.size() > 1 && restriction == RestrictionLevel.MATCH_ALL) {
            final Node[] endNodes = metadataNodes.toArray(new Node[0]);
            final LinkedList<Node> metadataList = new LinkedList<>();
            restrictiveEvaluator = new Evaluator() {
                @Override
                public Evaluation evaluate(Path path) {
                    if (path.endNode().hasLabel(StarPepLabel.Peptide)) {
                        metadataList.clear();
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
            nodes = peptideTraversal.evaluator(restrictiveEvaluator)
                    .traverse(metadataNodes.get(0))
                    .nodes()
                    .iterator();
        }

        return nodes;
    }

    private ResourceIterator<Node> getMetadata(Node peptideNode, Node[] endNodes) {
        ResourceIterator<Node> nodes = metadataTraversal.evaluator(Evaluators.includeWhereEndNodeIs(endNodes))
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

    protected org.gephi.graph.api.Node getOrAddGraphNodeFromNeoNode(Node neoNode, GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        String id = String.valueOf(neoNode.getId());
        org.gephi.graph.api.Node graphNode = mainGraph.getNode(id);
        if (graphNode == null) {
            GraphFactory factory = graphModel.factory();
            graphNode = factory.newNode(id);
            
            String label = neoNode.getLabels().iterator().next().name();
            graphNode.setLabel(label);
            graphNode.setSize(ProjectManager.GRAPH_NODE_SIZE);
            
            if (neoNode.hasProperty(PRO_NAME)) {
                graphNode.setAttribute(ProjectManager.NODE_TABLE_PRO_NAME, neoNode.getProperty(PRO_NAME));
            } else if (label.equals("Peptide")) {
                graphNode.setAttribute(ProjectManager.NODE_TABLE_PRO_NAME, "starPep_" + String.format("%05d", Integer.parseInt(id)));
            }

            //Set random position
            graphNode.setX((float) ((0.01 + Math.random()) * 1000) - 500);
            graphNode.setY((float) ((0.01 + Math.random()) * 1000) - 500);

            //Set color
            graphNode.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
            graphNode.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
            graphNode.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
            graphNode.setAlpha(1f);

            mainGraph.addNode(graphNode);

            //Add parent nodes
            org.gephi.graph.api.Node graphParentNode;
            Node neoParentNode;
            for (Relationship relation : neoNode.getRelationships(StarPepRelationships.is_a, Direction.OUTGOING)) {
                neoParentNode = relation.getEndNode();
                if (!MetadataDAOImpl.ROOT_METADATA.equals(neoParentNode.getProperty(MetadataDAOImpl.PRO_NAME).toString())) {
                    graphParentNode = getOrAddGraphNodeFromNeoNode(neoParentNode, graphModel);
                    getOrAddGraphEdgeFromNeoRelationship(graphNode, graphParentNode, relation, graphModel);
                }
            }

        }
        return graphNode;
    }

    protected Edge getOrAddGraphEdgeFromNeoRelationship(org.gephi.graph.api.Node startNode, org.gephi.graph.api.Node endNode, Relationship relation, GraphModel graphModel) {
        Graph mainGraph = graphModel.getGraph();
        String id = String.valueOf(relation.getId());
        Edge graphEdge = mainGraph.getEdge(id);
        if (graphEdge == null) {
            GraphFactory factory = graphModel.factory();
            String relName = relation.getType().name();
            int relType = graphModel.addEdgeType(relName);

            graphEdge = factory.newEdge(id, startNode, endNode, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
            graphEdge.setLabel(relName);
            if (relation.hasProperty(PRO_XREF)) {
                graphEdge.setAttribute(PRO_XREF, relation.getProperty(PRO_XREF));
            }

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
