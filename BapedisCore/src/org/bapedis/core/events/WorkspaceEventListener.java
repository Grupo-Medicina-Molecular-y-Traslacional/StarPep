
package org.bapedis.core.events;

import org.bapedis.core.model.Workspace;
import java.util.EventListener;

public interface WorkspaceEventListener extends EventListener {

    /**
     * Notify that a workspace has been changed.
     * @param oldWs the workspace that was changed
     * @param newWs the workspace that was made current workspace
     */
    public void workspaceChanged(Workspace oldWs, Workspace newWs);   

}
