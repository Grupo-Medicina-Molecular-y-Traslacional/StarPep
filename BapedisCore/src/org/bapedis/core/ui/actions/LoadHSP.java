/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;
import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class LoadHSP extends AbstractAction {

    private final ProjectManager pc;

    public LoadHSP() {
        putValue(NAME, "Load HSP");
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        GraphModel graphModel = pc.getGraphModel();
        Graph graph = pc.getGraphVisible();
        Node node1, node2;
        Edge edge;
        JFileChooser chooser = new JFileChooser();
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(f)))) {
                String line;
                String id1, id2;
                StringTokenizer tokenizer1;
                StringTokenizer tokenizer2;
                while ((line = br.readLine()) != null) {
                    tokenizer1 = new StringTokenizer(line, ":");
                    id1 = tokenizer1.nextToken().trim();
                    node1 = graph.getNode(id1);
                    if (node1 != null) {
                        tokenizer2 = new StringTokenizer(tokenizer1.nextToken(), " ");
                        while (tokenizer2.hasMoreTokens()) {
                            id2 = tokenizer2.nextToken().trim();
                            node2 = graph.getNode(id2);
                            if (node2 != null) {
                                edge = createSimilarityEdge(graphModel, node1, node2, 0f);
                                graph.writeLock();
                                try {
                                    graph.addEdge(edge);
                                } finally {
                                    graph.writeUnlock();
                                }
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
    
    public static Edge createSimilarityEdge(GraphModel graphModel, Node node1, Node node2, Float score) {
        int relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        // Create Edge
        String id = String.format("%s-%s", node1.getId(), node2.getId());
        Edge graphEdge = graphModel.factory().newEdge(id, node1, node2, relType, ProjectManager.GRAPH_EDGE_WEIGHT, false);
        graphEdge.setLabel(ProjectManager.GRAPH_EDGE_SIMALIRITY);

        //Set color
        graphEdge.setR(ProjectManager.GRAPH_NODE_COLOR.getRed() / 255f);
        graphEdge.setG(ProjectManager.GRAPH_NODE_COLOR.getGreen() / 255f);
        graphEdge.setB(ProjectManager.GRAPH_NODE_COLOR.getBlue() / 255f);
        graphEdge.setAlpha(0f);

        // Add edge to main graph
        Graph mainGraph = graphModel.getGraph();
        mainGraph.writeLock();
        try {
            mainGraph.addEdge(graphEdge);
            graphEdge.setWeight(score);
        } finally {
            mainGraph.writeUnlock();
        }

        return graphEdge;
    }    

}
