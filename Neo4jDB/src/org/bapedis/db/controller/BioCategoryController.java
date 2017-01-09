/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.controller;

import java.util.Collection;
import org.bapedis.core.model.Workspace;
import org.bapedis.db.dao.BioCategoryDAO;
import org.bapedis.db.model.BioCategory;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = BioCategoryController.class)
public class BioCategoryController {

    protected final BioCategoryDAO categoryDAO;
    protected final BioCategory rootCategory;

    public BioCategoryController() {
        categoryDAO = Lookup.getDefault().lookup(BioCategoryDAO.class);
        rootCategory = categoryDAO.getRootBioCategory();
    }

    public BioCategory getRootCategory() {
        return rootCategory;
    }

    public void setSelectedCategoriesTo(Workspace workspace, BioCategory[] selectedCategories) {
        Collection<? extends BioCategory> oldCategories = workspace.getLookup().lookupAll(BioCategory.class);
        if (oldCategories != null) {
            for (BioCategory oldCategory : oldCategories) {
                workspace.remove(oldCategory);
                oldCategory.setSelected(false);
            }
        }

        for (BioCategory selectedCategory : selectedCategories) {
            workspace.add(selectedCategory);
            selectedCategory.setSelected(true);
        }
    }
}
