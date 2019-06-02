/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import org.bapedis.core.spi.alg.ClusteringTag;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
//@ActionID(
//        category = "Tools",
//        id = "org.bapedis.core.ui.actions.ClusteringAction"
//)
//@ActionRegistration(
//        displayName = "#CTL_Clustering",
//        lazy=false
//)
//@ActionReference(path = "Menu/Tools", position = 70)
public class ClusteringAction extends ToolAction {
    
    public ClusteringAction() {
        super( NbBundle.getMessage(NetworkAction.class, "CTL_Clustering"),
                ClusteringTag.class);    
    }   
    
}
