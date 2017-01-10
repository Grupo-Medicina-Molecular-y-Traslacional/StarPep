/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.ui.actions.WorkspaceContextSensitiveAction;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.filters.spi.FilterFactory;
import org.bapedis.db.filters.spi.FilterSetupUI;
import org.bapedis.db.model.FilterModel;
import org.bapedis.db.model.NeoPeptideModel;
import org.bapedis.db.ui.util.FilterSetupDialog;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class AddFilter extends WorkspaceContextSensitiveAction<NeoPeptideModel> {

    protected final FilterFactory filterFactory;
    protected final FilterSetupDialog dialog;

    public AddFilter(FilterFactory filterFactory) {
        super(NeoPeptideModel.class);
        this.filterFactory = filterFactory;
        putValue(NAME, filterFactory.getName());
        dialog = new FilterSetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Workspace currentWs = pc.getCurrentWorkspace();
        FilterModel filterModel = currentWs.getLookup().lookup(FilterModel.class);
        if (filterModel == null) {
            for (ActionListener a : Utilities.actionsForPath("Actions/EditFilterModel")) {
                if (a instanceof NewFilterModel) {
                    a.actionPerformed(e);
                    filterModel = currentWs.getLookup().lookup(FilterModel.class);
                }
            }
            
        }
        if (filterModel != null) {
            Filter filter = filterFactory.createFilter();
            FilterSetupUI setupUI = filterFactory.getSetupUI();
            String title = NbBundle.getMessage(AddFilter.class, "FilterSetupDialog.title", filterFactory.getName());
            if (dialog.setup(filter, setupUI, title)) {
                filterModel.addFilter(filter);
            }
        }
    }

}
