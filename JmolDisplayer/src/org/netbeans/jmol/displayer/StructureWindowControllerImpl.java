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
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

/**
 *
 * @author loge
 */
@ServiceProvider(service = StructureWindowController.class)
public class StructureWindowControllerImpl implements StructureWindowController {

    @Override
    public void openStructureWindow(Peptide peptide, String code) {
        JmolTopComponent jtc = new JmolTopComponent();
        jtc.setDisplayName(code);
        jtc.open();
        jtc.requestActive();
        JmolViewer viewer = jtc.panel.getViewer();
        viewer.script(getScript(code));    
        viewer.script("cartoons only;ssbonds on; select cys; wireframe on; select cys.ca; label %n%r; select;");
    }

    @Override
    public JPanel createPanelView(JPanel parent, String code) {
        JmolPanel jmolPanel = new JmolPanel(null, null, parent, parent.getWidth(), parent.getHeight(), "", new Point(50, 50));
        JmolViewer viewer = jmolPanel.getViewer();
        viewer.script(getScript(code));
        viewer.script("cartoons only;ssbonds on; select cys; wireframe on; select cys.ca; label %n%r; select; spin on");
        return jmolPanel;
    }

    private String getScript(String code) {
        return "var xid = _modelTitle; if (xid.length != 4) { xid = '"
                + code
                + "'};load @{'=' + xid}";
    }
}
