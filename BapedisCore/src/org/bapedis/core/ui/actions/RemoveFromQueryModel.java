/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.services.ProjectManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Home
 */
public class RemoveFromQueryModel extends AbstractAction {
            public RemoveFromQueryModel() {
            putValue(NAME, NbBundle.getMessage(RemoveFromQueryModel.class, "RemoveFromQueryModel.name"));
//            putValue(SMALL_ICON, ImageUtilities.loadImage("org/bapedis/core/resources/remove.png", true));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
//            if (transferable.transferData instanceof Label) {
//                queryModel.remove((Label) transferable.transferData);
//            } else if (transferable.transferData instanceof Metadata) {
//                queryModel.remove((Metadata) transferable.transferData);
//            }
        }
}
