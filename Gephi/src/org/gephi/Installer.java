/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi;

import com.jogamp.opengl.GLProfile;
import org.gephi.visualization.VizController;
import org.openide.modules.ModuleInstall;

public class Installer extends ModuleInstall {

    @Override
    public void restored() {
        //Init JOGL, recommended
        GLProfile.initSingleton();
        VizController instance = VizController.getInstance();
        instance.initInstances();
    }

    @Override
    public void uninstalled() {
        super.uninstalled(); //To change body of generated methods, choose Tools | Templates.
        VizController instance = VizController.getInstance();
        instance.destroy();
    }

}
