/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.DialogDisplayer;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AddFilter extends WorkspaceContextSensitiveAction<AttributesModel> {

    protected final FilterFactory filterFactory;
    protected final SetupDialog dialog;

    public AddFilter(FilterFactory filterFactory) {
        super(AttributesModel.class);
        this.filterFactory = filterFactory;
        putValue(NAME, filterFactory.getName());
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        FilterModel filterModel = pc.getFilterModel();
        if (filterModel.isRunning()) {
            DialogDisplayer.getDefault().notify(filterModel.getOwnerWS().getBusyNotifyDescriptor());
        } else {
            Filter filter = filterFactory.createFilter();
            FilterSetupUI setupUI = filterFactory.getSetupUI();
            if (setupUI == null) {
                filterModel.add(filter);
            } else {
                String title = NbBundle.getMessage(AddFilter.class, "FilterSetupDialog.title", filterFactory.getName());
                if (dialog.setup(setupUI.getEditPanel(filter), setupUI, title)) {
                    filterModel.add(filter);
                }
            }
        }
    }

}
