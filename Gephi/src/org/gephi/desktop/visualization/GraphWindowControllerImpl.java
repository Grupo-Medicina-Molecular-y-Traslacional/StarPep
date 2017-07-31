/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.gephi.visualization.api.selection.SelectionManager;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
@ServiceProvider(service = GraphWindowController.class)
public class GraphWindowControllerImpl implements GraphWindowController {

    private TopComponent graphWindow;

    public GraphWindowControllerImpl() {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                MultiViewDescription[] multiviews = new MultiViewDescription[2];
                multiviews[0] = new NeoGraphSceneDescription();
                multiviews[1] = new NeoGraphPreViewDescription();
                graphWindow = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
                graphWindow.setDisplayName(NbBundle.getMessage(GraphWindowControllerImpl.class, "CTL_GraphTC"));
            }
        });
    }

    @Override
    public TopComponent getGraphWindow() {
        return graphWindow;
    }

    @Override
    public void openGraphWindow() {
        if (graphWindow != null) {
            if (!graphWindow.isOpened()) {
                graphWindow.open();
            }
            graphWindow.requestActive();
        }
    }

    @Override
    public void selectNode(Node node) {
        openGraphWindow();
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.selectNode(node);
//        sm.setDirectMouseSelection();
    }

    @Override
    public void centerOnNode(Node node) {
        openGraphWindow();
        SelectionManager sm = VizController.getInstance().getSelectionManager();
        sm.centerOnNode(node);
    }

    @Override
    public void closeGraphWindow() {
        if (graphWindow != null) {
            graphWindow.close();
        }
    }

}
