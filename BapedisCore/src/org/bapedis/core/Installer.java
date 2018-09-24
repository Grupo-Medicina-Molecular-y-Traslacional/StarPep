/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core;

import org.bapedis.core.project.ProjectManager;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall implements WorkspaceEventListener, PropertyChangeListener {

    private ProjectManager pc;
    private String title;

    @Override
    public void restored() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.newProject();
        pc.addWorkspaceEventListener(this);
        workspaceChanged(null, pc.getCurrentWorkspace());
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                TopComponent tc = WindowManager.getDefault().findTopComponent("PeptideViewerTopComponent"); // NOI18N
                if (tc != null) {
                    tc.open();
                    tc.requestActive();
                }

                // Navigator windows
                tc = WindowManager.getDefault().findTopComponent("navigatorTC"); //NOI18N
                if (tc != null && !tc.isOpened()) {
                    tc.open();
                }
            }
        });
    }

    private String getWindowsTitle() {
        if (title == null) {
            title = WindowManager.getDefault().getMainWindow().getTitle();
        }
        return title;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            oldWs.removePropertyChangeListener(this);
        }
        newWs.addPropertyChangeListener(this);
        setNewWorkspace(newWs);
    }

    private void setNewWorkspace(final Workspace workspace) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {
            @Override
            public void run() {
                WindowManager.getDefault().getMainWindow().setTitle(getWindowsTitle() + " - " + workspace.getName());
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof Workspace) {
            Workspace workspace = (Workspace) evt.getSource();
            if (evt.getPropertyName().equals(Workspace.PRO_NAME)) {
                WindowManager.getDefault().getMainWindow().setTitle(getWindowsTitle() + " - " + workspace.getName());
            }
        }
    }

}
