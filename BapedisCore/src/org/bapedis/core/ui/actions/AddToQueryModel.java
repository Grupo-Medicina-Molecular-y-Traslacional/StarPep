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
import org.bapedis.core.spi.data.MetadataDAO;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class AddToQueryModel extends AbstractAction {

    private final static MetadataDAO metadataDAO = Lookup.getDefault().lookup(MetadataDAO.class);
    private final String metadataName;

    public AddToQueryModel(String metadataName) {
        this.metadataName = metadataName;
        putValue(NAME, NbBundle.getMessage(AddToQueryModel.class, "AddToQueryModel.name"));
//            putValue(SMALL_ICON, ImageUtilities.loadImage("org/bapedis/core/resources/add.png", true));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent tc = WindowManager.getDefault().findTopComponent("QueryExplorerTopComponent");
        tc.open();
        tc.requestActive();
        Metadata metadata = null;
        QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
        queryModel.add(metadata);
    }

}
