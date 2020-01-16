/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.visualization.apiimpl.contextmenuitems;

import javax.swing.Icon;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.gephi.visualization.spi.GraphContextMenuItem;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = GraphContextMenuItem.class)
public class ClearNodesSelection implements GraphContextMenuItem {

    protected final String name = NbBundle.getMessage(ClearNodesSelection.class, "ClearNodesSelection.name");
    
    @Override
    public void setup(Graph graph, Node[] nodes) {
        
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
        SelectionManager selectionManager = VizController.getInstance().getSelectionManager();
        return selectionManager.isCustomSelection();
    }

    @Override
    public void execute() {
        // Unselect nodes
        SelectionManager selectionManager = VizController.getInstance().getSelectionManager();
        selectionManager.setDirectMouseSelection();
        selectionManager.setDraggingMouseSelection();
        selectionManager.resetSelection();
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
        return ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/mouse.png", false);
    }

    @Override
    public Integer getMnemonicKey() {
        return null;
    }

    @Override
    public int getPosition() {
        return 90;
    }
    
}
