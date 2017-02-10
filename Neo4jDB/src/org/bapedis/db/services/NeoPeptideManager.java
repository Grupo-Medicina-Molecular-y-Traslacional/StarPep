/*
 * To change this license header, choose License Headers in DataProject Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.services;

import java.util.Collection;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.dao.NeoPeptideDAO;
import org.bapedis.db.model.BioCategory;
import org.bapedis.db.model.NeoPeptideModel;
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


    public void setNeoPeptidesTo(final Workspace workspace) {
        Collection<? extends BioCategory> categories = workspace.getLookup().lookupAll(BioCategory.class);
        NeoPeptideModel oldModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        PeptideAttribute[] attributes = null;
        if (oldModel != null) {
            workspace.remove(oldModel);
            attributes = oldModel.getAttributes();
        }
        NeoPeptideModel neoModel = neoModelDAO.getNeoPeptidesBy(categories.toArray(new BioCategory[0]), attributes);
        workspace.add(neoModel);
    }


}
