package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.model.Workspace;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.RemoveCurrentWorkspace"
)
@ActionRegistration(
        displayName = "#CTL_RemoveWorkspace",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/File/RemoveWorkspace", position = 100),
    @ActionReference(path = "Actions/RemoveWorkspace", position = 100)
})
public class RemoveCurrentWorkspace extends AbstractAction implements LookupListener{
    protected ProjectManager pc;
    protected Lookup.Result<Workspace> lkpResult;

    public RemoveCurrentWorkspace() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        lkpResult = pc.getLookup().lookupResult(Workspace.class);
        lkpResult.addLookupListener(this);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/removeWorkspace.png", false));
        putValue(NAME, NbBundle.getMessage(RemoveCurrentWorkspace.class, "CTL_RemoveWorkspace"));                
        setEnabled(pc.getLookup().lookupAll(Workspace.class).size()>1);
    }        
    
    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = NbBundle.getMessage(RemoveCurrentWorkspace.class, "RemoveCurrentWorkspace.dialog.confirm");
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {
            pc.remove(pc.getCurrentWorkspace());
        }                
    }

    @Override
    public void resultChanged(LookupEvent le) {
        setEnabled(lkpResult.allInstances().size()>1);
    }   

}
