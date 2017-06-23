/*
 * To change this license header, choose License Headers in DataProject Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.services;

import java.util.Collection;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.dao.NeoPeptideDAO;
import org.bapedis.db.model.BioCategory;
import org.bapedis.db.model.NeoPeptideModel;
import org.gephi.graph.api.GraphView;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = NeoPeptideManager.class)
public class NeoPeptideManager {

    protected final NeoPeptideDAO neoModelDAO;

    public NeoPeptideManager() {
        neoModelDAO = new NeoPeptideDAO();
    }


    public void loadNeoPeptides(final Workspace workspace) {
        Collection<? extends BioCategory> categories = workspace.getLookup().lookupAll(BioCategory.class);
        NeoPeptideModel oldModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        if (oldModel != null) {
            workspace.remove(oldModel);
            GraphView oldView = oldModel.getCurrentView();
            if (!oldView.isMainView())
                neoModelDAO.getGraphModel().destroyView(oldView);
        }
        NeoPeptideModel neoModel = neoModelDAO.getNeoPeptidesBy(categories.toArray(new BioCategory[0]));
        workspace.add(neoModel);
    }


}
