/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.util.Collection;
import javax.swing.AbstractAction;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author loge
 */
public abstract class WorkspaceContextSensitiveAction<T> extends AbstractAction implements WorkspaceEventListener, LookupListener {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected Lookup.Result<T> lkpResult;
    protected Class<T> contextClass;

    public WorkspaceContextSensitiveAction(Class<T> contextClass) {
        this.contextClass = contextClass;
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (lkpResult != null) {
            lkpResult.removeLookupListener(this);
        }
        
        lkpResult = newWs.getLookup().lookupResult(contextClass);
        lkpResult.addLookupListener(this);
        T context = newWs.getLookup().lookup(contextClass);
        setEnabled(context != null);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends T> context = lkpResult.allInstances();
        setEnabled(!context.isEmpty());
    }

}
