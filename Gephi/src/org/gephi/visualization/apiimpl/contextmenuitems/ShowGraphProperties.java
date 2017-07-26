/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.visualization.apiimpl.contextmenuitems;

import java.util.LinkedList;
import java.util.List;
import javax.swing.Icon;
import javax.swing.SwingUtilities;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.nodes.NodeOperation;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Home
 */
@ServiceProvider(service = GraphContextMenuItem.class)
public class ShowGraphProperties implements GraphContextMenuItem {

    protected Node[] nodes;
    protected Graph graph;
    protected final String name = NbBundle.getMessage(ShowGraphProperties.class, "ShowProperties.name");

    @Override
    public void setup(Graph graph, Node[] nodes) {
        this.graph = graph;
        this.nodes = nodes;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getDescription() {
        return null;
    }

    @Override
    public boolean canExecute() {
        return nodes.length > 0;
    }

    @Override
    public void execute() {
        final List<org.openide.nodes.Node> activeNodes = new LinkedList<>();
        for(Node n: nodes){
            activeNodes.add(new NodePropertiesWrapper(n));
        }
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                NodeOperation.getDefault().showProperties(activeNodes.toArray(new org.openide.nodes.Node[0]));
            }
        });

    }

    @Override
    public int getType() {
        return 100;
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public GraphContextMenuItem[] getSubItems() {
        return null;
    }

    @Override
    public Icon getIcon() {
        return null;
    }

    @Override
    public Integer getMnemonicKey() {
        return null;
    }

    @Override
    public int getPosition() {
        return 10;
    }
}
