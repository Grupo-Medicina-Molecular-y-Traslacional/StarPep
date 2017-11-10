
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.model.Workspace;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.RenameWorkspace"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/renameWorkspace.png",
        displayName = "#CTL_RenameWorkspace"
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 1100),
    @ActionReference(path = "Toolbars/Workspace", position = 150)
})
public class RenameWorkspace implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
        Workspace workspace = pc.getCurrentWorkspace();
        String name = workspace.getName();
        DialogDescriptor.InputLine dd = new DialogDescriptor.InputLine("", NbBundle.getMessage(RenameWorkspace.class, "RenameWorkspace.dialog.title"));
        dd.setInputText(name);
        if (DialogDisplayer.getDefault().notify(dd).equals(DialogDescriptor.OK_OPTION) && !dd.getInputText().isEmpty()) {
            name = dd.getInputText();
            workspace.setName(name);
        }
    }

}
