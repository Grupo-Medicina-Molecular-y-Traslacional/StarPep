/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.services;

import java.util.Collection;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.dao.BioCategoryDAO;
import org.bapedis.db.model.Metadata;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = MetadataManager.class)
public class MetadataManager {

    protected final BioCategoryDAO categoryDAO;
    protected final Metadata rootCategory;

    public MetadataManager() {
        categoryDAO = new BioCategoryDAO();
        rootCategory = categoryDAO.getRootBioCategory();
    }

    public Metadata getBioCategory() {
        return rootCategory;
    }

    public void setSelectedCategoriesTo(Workspace workspace, Metadata[] selectedCategories) {
        Collection<? extends Metadata> oldCategories = workspace.getLookup().lookupAll(Metadata.class);
        if (oldCategories != null) {
            for (Metadata oldCategory : oldCategories) {
                workspace.remove(oldCategory);
                oldCategory.setSelected(false);
            }
        }

        for (Metadata selectedCategory : selectedCategories) {
            workspace.add(selectedCategory);
            selectedCategory.setSelected(true);
        }
    }
}
