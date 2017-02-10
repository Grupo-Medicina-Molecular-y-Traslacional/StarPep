/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.ui.components.FilterSetupDialog;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AddFilter extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final FilterFactory filterFactory;
    protected final FilterSetupDialog dialog;

    public AddFilter(FilterFactory filterFactory) {
        super(AttributesModel.class);
        this.filterFactory = filterFactory;
        putValue(NAME, filterFactory.getName());
        dialog = new FilterSetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWs = pc.getCurrentWorkspace();
        FilterModel filterModel = currentWs.getLookup().lookup(FilterModel.class);
        if (filterModel == null) {
            filterModel = new FilterModel();
        }
        Filter filter = filterFactory.createFilter();
        FilterSetupUI setupUI = filterFactory.getSetupUI();
        String title = NbBundle.getMessage(AddFilter.class, "FilterSetupDialog.title", filterFactory.getName());
        if (dialog.setup(filter, setupUI, title)) {
            filterModel.addFilter(filter);
            currentWs.add(filterModel);
        }
    }

}
