/*
 * To change this license header, choose License Headers in DataProject Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.services;

import java.util.Collection;
import java.util.HashMap;
import javax.swing.SwingUtilities;
import org.bapedis.core.model.Attribute;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.dao.NeoPeptideDAO;
import org.bapedis.db.model.BioCategory;
import org.bapedis.db.model.FilterModel;
import org.bapedis.db.model.NeoNeighbor;
import org.bapedis.db.model.NeoNeighborsModel;
import org.bapedis.db.model.NeoPeptide;
import org.bapedis.db.model.NeoPeptideModel;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = NeoPeptideManager.class)
public class NeoPeptideManager {

    protected final NeoPeptideDAO neoModelDAO;
    protected boolean loadNeighbors;

    public NeoPeptideManager() {
        neoModelDAO = new NeoPeptideDAO();
        loadNeighbors = false;
    }

    public boolean isLoadNeighbors() {
        return loadNeighbors;
    }

    public void setLoadNeighbors(boolean loadNeighbors) {
        this.loadNeighbors = loadNeighbors;
    }

    public void setNeoPeptidesTo(final Workspace workspace, final boolean useFilter) {
        Collection<? extends BioCategory> categories = workspace.getLookup().lookupAll(BioCategory.class);
        FilterModel filterModel = (useFilter) ? workspace.getLookup().lookup(FilterModel.class) : null;
        NeoPeptideModel oldModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        Attribute[] attributes = null;
        if (oldModel != null) {
            workspace.remove(oldModel);
            attributes = oldModel.getAttributes();
        }
        NeoPeptideModel neoModel = neoModelDAO.getNeoPeptidesBy(categories.toArray(new BioCategory[0]), attributes, filterModel);
        workspace.add(neoModel);
        if (loadNeighbors) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    setNeoNeighborsTo(workspace);
                }
            });
        }
    }

    public void setNeoNeighborsTo(Workspace workspace) {
        NeoPeptideModel neoPeptideModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        if (neoPeptideModel != null) {
            NeoNeighborsModel neoNeighborsModel = new NeoNeighborsModel();
            Attribute[] attributes = null;
            NeoNeighborsModel oldModel = workspace.getLookup().lookup(NeoNeighborsModel.class);
            if (oldModel != null) {
                workspace.remove(oldModel);
                attributes = oldModel.getAttributes();
            }
            if (attributes != null) {
                for (Attribute attr : attributes) {
                    neoNeighborsModel.addAttribute(attr);
                }
            }
            HashMap<Long, NeoNeighbor> neighborMap = new HashMap<>();
            Peptide[] peptides = neoPeptideModel.getPeptides();
            for (Peptide peptide : peptides) {
                NeoNeighborsModel neoModel = ((NeoPeptide) peptide).getNeighbors();
                for (NeoNeighbor neighbor : neoModel.getNeighbors()) {
                    if (!neighborMap.containsKey(neighbor.getNeoId())) {
                        neighborMap.put(neighbor.getNeoId(), neighbor);
                        neoNeighborsModel.addNeighbor(neighbor);
                    } else {
                        neighborMap.get(neighbor.getNeoId()).addSourcePeptide((NeoPeptide) peptide);
                    }
                }
                for (Attribute attr : neoModel.getAttributes()) {
                    if (!neoNeighborsModel.hasAttribute(attr.getId())) {
                        neoNeighborsModel.addAttribute(attr);
                    }
                }
            }
            workspace.add(neoNeighborsModel);
        }
    }

}
