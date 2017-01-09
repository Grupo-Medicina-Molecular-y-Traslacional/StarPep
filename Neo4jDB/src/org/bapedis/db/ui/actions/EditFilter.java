/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;
import org.bapedis.core.ui.actions.GlobalContextSensitiveAction;
import org.bapedis.db.controller.FilterFactoryController;
import org.bapedis.db.filters.spi.Filter;
import org.bapedis.db.filters.spi.FilterFactory;
import org.bapedis.db.model.FilterNode;
import org.bapedis.db.ui.FilterExplorerTopComponent;
import org.bapedis.db.ui.util.FilterSetupDialog;
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
        id = "org.bapedis.db.ui.actions.EditFilter"
)
@ActionRegistration(
        displayName = "#CTL_EditFilter",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Actions/EditFilter", position = 200)
})
@NbBundle.Messages("CTL_EditFilter=Edit filter")
public class EditFilter extends GlobalContextSensitiveAction<Filter> {
    protected final FilterFactoryController ffc;
    protected final FilterSetupDialog dialog;    

    public EditFilter() {
        super(Filter.class);
        ffc = Lookup.getDefault().lookup(FilterFactoryController.class);
        String name = NbBundle.getMessage(RemoveFilter.class, "CTL_EditFilter");
        putValue(NAME, name);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/db/resources/edit.png", false));        
        putValue(SHORT_DESCRIPTION, name);
        dialog = new FilterSetupDialog();                
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends Filter> context = lkpResult.allInstances();
        if (!context.isEmpty()){
            Filter filter = context.iterator().next();
            FilterFactory filterFactory = ffc.getBuilder(filter); 
            String title = NbBundle.getMessage(EditFilter.class, "FilterSetupDialog.title", filterFactory.getName());
            if (dialog.setup(filter, filterFactory.getSetupUI(), title)){
                FilterExplorerTopComponent tc  = (FilterExplorerTopComponent) WindowManager.getDefault().findTopComponent("FilterExplorerTopComponent");
                ExplorerManager manager = tc.getExplorerManager();
                Node[] nodes = manager.getRootContext().getChildren().getNodes();
                for(Node node: nodes){
                    FilterNode filterNode = (FilterNode)node;
                    if (filterNode.getFilter().equals(filter)){
                        filterNode.refresh();
                        break;
                    }
                }
            }
        }        
    }            
}
