/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import org.bapedis.core.model.Metadata;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.services.ProjectManager;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author cicese
 */
public class RemoveOthersFromQueryModel extends AbstractAction{
    private final Metadata metadata;

    public RemoveOthersFromQueryModel(Metadata metadata) {
        this.metadata = metadata;
        putValue(NAME, NbBundle.getMessage(RemoveFromQueryModel.class, "RemoveOthersFromQueryModel.name"));
    }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
        for(Metadata m: queryModel.getMetadatas()){
            if (!m.equals(metadata)){
                queryModel.remove(m);
            }
        }
    }
    
}
