package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.project.ProjectManager;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.CleanProject"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/clean.gif",
        displayName = "#CTL_CleanProject"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1320),
    @ActionReference(path = "Toolbars/File", position = 300)
})
public class CleanProject implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        String msg = NbBundle.getMessage(CleanProject.class, "CleanProject.dialog.confirm");
        NotifyDescriptor nd = new NotifyDescriptor.Confirmation(msg, NotifyDescriptor.YES_NO_OPTION);
        if (DialogDisplayer.getDefault().notify(nd) == NotifyDescriptor.YES_OPTION) {
            ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
            pc.clean();
        }
    }

}
