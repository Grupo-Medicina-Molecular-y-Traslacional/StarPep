package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.bapedis.core.controller.ProjectController;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.HelpCtx;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.windows.WindowManager;

public final class CloseProject extends SystemAction {

    public void actionPerformed(ActionEvent e) {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        
        if (pc.getProject() != null) {

            //Save ?
            String messageBundle = NbBundle.getMessage(CloseProject.class, "CloseProject_confirm_message");
            String titleBundle = NbBundle.getMessage(CloseProject.class, "CloseProject_confirm_title");
            String saveBundle = NbBundle.getMessage(CloseProject.class, "CloseProject_confirm_save");
            String doNotSaveBundle = NbBundle.getMessage(CloseProject.class, "CloseProject_confirm_doNotSave");
            String cancelBundle = NbBundle.getMessage(CloseProject.class, "CloseProject_confirm_cancel");
            NotifyDescriptor msg = new NotifyDescriptor(messageBundle, titleBundle,
                    NotifyDescriptor.YES_NO_CANCEL_OPTION,
                    NotifyDescriptor.INFORMATION_MESSAGE,
                    new Object[]{saveBundle, doNotSaveBundle, cancelBundle}, saveBundle);
            Object result = DialogDisplayer.getDefault().notify(msg);
            if (result == saveBundle) {
                //pc.saveProject();
            } else if (result == cancelBundle) {
                //
            }

            pc.closeCurrentProject();

            //Actions
//            saveProject = false;
//            saveAsProject = false;
//            projectProperties = false;
//            closeProject = false;
//            newWorkspace = false;
//            deleteWorkspace = false;
//            cleanWorkspace = false;
//            duplicateWorkspace = false;

            //Title bar
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    JFrame frame = (JFrame) WindowManager.getDefault().getMainWindow();
                    String title = frame.getTitle();
                    title = title.substring(0, title.indexOf('-') - 1);
                    frame.setTitle(title);
                }
            });
        }
        
    }

    @Override
    public boolean isEnabled() {
        return Lookup.getDefault().lookup(ProjectController.class).getProject() != null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CloseProject.class, "CTL_CloseProject");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return null;
    }
}
