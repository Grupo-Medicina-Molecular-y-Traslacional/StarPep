/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.project.ProjectManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class RemoveFromQueryModel extends AbstractAction {
    private final Metadata metadata;
    
    public RemoveFromQueryModel(Metadata metadata) {
        this.metadata = metadata;
        putValue(NAME, NbBundle.getMessage(RemoveFromQueryModel.class, "RemoveFromQueryModel.name"));
//            putValue(SMALL_ICON, ImageUtilities.loadImage("org/bapedis/core/resources/remove.png", true));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("QueryExplorerTopComponent");
        tc.open();
        tc.requestActive();
        
        QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
        queryModel.remove(metadata);
    }
}
