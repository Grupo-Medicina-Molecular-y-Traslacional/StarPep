/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.FilterFactory;
import org.bapedis.core.model.FilterNode;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.filters.FilterSetupUI;
import org.bapedis.core.ui.FilterExplorerTopComponent;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Edit",
        id = "org.bapedis.core.ui.actions.EditFilter"
)
@ActionRegistration(
        displayName = "#CTL_EditFilter",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilter", position = 100)
})
@NbBundle.Messages("CTL_EditFilter=Edit filter")
public class EditFilter extends GlobalContextSensitiveAction<Filter> {

    protected final ProjectManager pm;
    protected final SetupDialog dialog;

    public EditFilter() {
        super(Filter.class);
        pm = Lookup.getDefault().lookup(ProjectManager.class);
        String name = NbBundle.getMessage(EditFilter.class, "CTL_EditFilter");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/edit.png", false));
        putValue(SHORT_DESCRIPTION, name);
        dialog = new SetupDialog();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends Filter> context = lkpResult.allInstances();
        if (!context.isEmpty()) {
            Filter filter = context.iterator().next();
            FilterFactory filterFactory = filter.getFactory();
            String title = NbBundle.getMessage(EditFilter.class, "FilterSetupDialog.title", filterFactory.getName());
            FilterSetupUI setupUI = filterFactory.getSetupUI();
            if (setupUI != null && dialog.setup(setupUI.getEditPanel(filter), setupUI, title)) {
                FilterExplorerTopComponent tc = (FilterExplorerTopComponent) WindowManager.getDefault().findTopComponent("FilterExplorerTopComponent");
                ExplorerManager manager = tc.getExplorerManager();
                Node[] nodes = manager.getRootContext().getChildren().getNodes();
                for (Node node : nodes) {
                    FilterNode filterNode = (FilterNode) node;
                    if (filterNode.getFilter().equals(filter)) {
                        filterNode.refresh();
                        break;
                    }
                }
                FilterModel filterModel = pm.getFilterModel();
                filterModel.fireEditedEvent(filter);
            }
        }
    }

}
