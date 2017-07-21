/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.visualization;

import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Node;
import org.gephi.visualization.VizController;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
@ServiceProvider(service = GraphWindowController.class)
public class GraphWindowControllerImpl implements GraphWindowController {

    private TopComponent graphWindow;

    private void createInstance() {
        if (graphWindow == null) {
            MultiViewDescription[] multiviews = new MultiViewDescription[2];
            multiviews[0] = new NeoGraphSceneDescription();
            multiviews[1] = new NeoGraphPreViewDescription();
            graphWindow = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
            graphWindow.setDisplayName(NbBundle.getMessage(GraphWindowControllerImpl.class, "CTL_GraphTC"));
        }
    }

    @Override
    public void openGraphWindow() {
        createInstance();
        graphWindow.open();
        graphWindow.requestActive();

    }

    @Override
    public void selectNode(Node node) {
        openGraphWindow();
        VizController.getInstance().getSelectionManager().selectNode(node);
    }

    @Override
    public void closeGraphWindow() {
        if (graphWindow != null) {
            graphWindow.close();
        }
    }

}
