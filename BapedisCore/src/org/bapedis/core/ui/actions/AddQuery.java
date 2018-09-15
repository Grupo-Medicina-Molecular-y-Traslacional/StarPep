/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.ui.components.MetadataSelectorPanel;
import org.bapedis.core.ui.components.SetupDialog;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class AddQuery extends AbstractAction {

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final StarPepAnnotationType type;
    protected final SetupDialog dialog;
    protected final String dialogTitle;
    
    public AddQuery(StarPepAnnotationType type) {
        this.type = type;
        putValue(NAME, NbBundle.getMessage(AddQuery.class, "AddQueryBy.name", type.getLabelName()));
        dialog = new SetupDialog();
        dialogTitle = NbBundle.getMessage(AddFilter.class, "MetadataSelector.title");
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        MetadataSelectorPanel panel = new MetadataSelectorPanel(type);
        if (dialog.setup(panel, panel, dialogTitle)) {
            QueryModel queryModel = pc.getQueryModel();
            List<Metadata> selectedMetada = panel.getSelectedMetadata();
            queryModel.addAll(selectedMetada);
        }
    }

}
