/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi;

import com.jogamp.opengl.GLProfile;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.visualization.VizController;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        //Init JOGL, recommended
        GLProfile.initSingleton();
        VizController instance = VizController.getInstance();
        instance.initInstances();
//        final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
//        if (graphWC != null) {
//            WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
//                @Override
//                public void run() {
//                    TopComponent tc = graphWC.getGraphWindow();
//                    if (tc != null) {
//                        tc.open();
//                    }
//                }
//            });
//        }
    }

    @Override
    public void uninstalled() {
        super.uninstalled(); //To change body of generated methods, choose Tools | Templates.
        VizController instance = VizController.getInstance();
        instance.destroy();
    }

}
