/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.netbeans.jmol.displayer;

import java.awt.Point;
import javax.swing.JPanel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.ui.StructureWindowController;
import org.jmol.api.JmolViewer;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViewPerspective;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

/**
 *
 * @author loge
 */
@ServiceProvider(service = StructureWindowController.class)
public class StructureWindowControllerImpl implements StructureWindowController {

    @Override
    public void openStructureWindow(Peptide peptide, String code) {
        MultiViewDescription[] multiviews = new MultiViewDescription[]{new StructureSceneDescription(peptide, code)};
        TopComponent tc = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
        tc.setDisplayName(peptide.getName() + "-" + code);
//        WindowManager.getDefault().findTopComponentID(tc);
        tc.open();
        tc.requestActive();
    }

    @Override
    public JPanel createPanelView(JPanel parent, String code) {
        JmolPanel jmolPanel = new JmolPanel(null, null, parent, 150, 150, "", new Point(50, 50));
        JmolViewer viewer = jmolPanel.getViewer();
        viewer.script(StructureScene.getScript(code));
        //Cartoons
        viewer.script("select protein; cartoons only; color structure; spin on");
//        viewer.script("cartoons only;ssbonds on; select cys; wireframe on; select cys.ca; label %n%r; select; spin on");
        return jmolPanel;
    }

}
