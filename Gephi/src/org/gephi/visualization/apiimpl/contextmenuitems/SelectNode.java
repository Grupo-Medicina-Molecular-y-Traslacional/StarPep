/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.visualization.apiimpl.contextmenuitems;

import javax.swing.Icon;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = GraphContextMenuItem.class)
public class SelectNode implements GraphContextMenuItem {

    protected Node[] nodes;
    protected Graph graph;
    protected final String name = NbBundle.getMessage(SelectNode.class, "SelectNode.name");

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
        GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        for (Node node : nodes) {
            graphWC.selectNode(node);
        }        
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
        return 80;
    }

}
