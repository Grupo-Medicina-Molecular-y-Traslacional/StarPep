/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.openide.awt.Toolbar;
import org.openide.awt.ToolbarPool;
import org.openide.modules.ModuleInstall;
import org.openide.util.Lookup;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

public class Installer extends ModuleInstall implements WorkspaceEventListener, PropertyChangeListener {

    private ProjectManager pc;

    @Override
    public void restored() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.newProject();
        pc.addWorkspaceEventListener(this);
        workspaceChanged(null, pc.getCurrentWorkspace());
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                Toolbar tb = ToolbarPool.getDefault().findToolbar("Memory");
                if (tb != null) {
                    tb.setVisible(false);
                }

                // Property windows
//                TopComponent tc = WindowManager.getDefault().findTopComponent("properties"); // NOI18N
//                tc.open();
                
                // Navigator windows
                TopComponent tc = WindowManager.getDefault().findTopComponent("navigatorTC"); //NOI18N
                tc.open();
            }
        });
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            oldWs.removePropertyChangeListener(this);
        }
        newWs.addPropertyChangeListener(this);
        setAppName(newWs);
    }

    private void setAppName(final Workspace workspace) {
        WindowManager.getDefault().invokeWhenUIReady(new Runnable() {

            @Override
            public void run() {
                WindowManager.getDefault().getMainWindow().setTitle("Bapedis - " + workspace.getName());
            }
        });
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Workspace.PRO_NAME)) {
            setAppName(pc.getCurrentWorkspace());
        }
    }

}
