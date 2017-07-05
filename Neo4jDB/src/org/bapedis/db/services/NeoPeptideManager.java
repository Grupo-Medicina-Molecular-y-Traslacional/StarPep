/*
 * To change this license header, choose License Headers in DataProject Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.services;

import java.util.Collection;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.db.dao.PeptideDAOImpl;
import org.bapedis.core.model.Metadata;
import org.bapedis.db.model.NeoPeptideModel;
import org.gephi.graph.api.GraphView;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = NeoPeptideManager.class)
public class NeoPeptideManager {

    protected final PeptideDAOImpl neoModelDAO;
    protected final ProjectManager pm;

    public NeoPeptideManager() {
        neoModelDAO = new PeptideDAOImpl();
        pm = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public void loadNeoPeptides(final Workspace workspace) {
        Collection<? extends Metadata> categories = workspace.getLookup().lookupAll(Metadata.class);
        AttributesModel attrModel = pm.getAttributesModel(workspace);
        if (attrModel != null && attrModel instanceof NeoPeptideModel) {
            NeoPeptideModel oldModel = (NeoPeptideModel) attrModel;
            workspace.remove(oldModel);
            GraphView oldView = oldModel.getCurrentView();
            if (!oldView.isMainView()) {
                neoModelDAO.getGraphModel().destroyView(oldView);
            }
        }
        NeoPeptideModel neoModel = neoModelDAO.getNeoPeptidesBy(categories.toArray(new Metadata[0]));
        workspace.add(neoModel);
    }

}
