/*
Copyright 2008-2010 Gephi
Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.bapedis.core.task;

import org.netbeans.api.progress.ProgressHandle;
import org.openide.awt.StatusDisplayer;
import org.openide.util.Cancellable;

/**
 *
 * @author Mathieu Bastian
 * Modified by Loge
 */
public final class ProgressTicket {

    private final ProgressHandle handle;
    private String displayName;
    private int progress100 = 0;
    private int progressTotal;
    private int currentUnit = 0;
    private boolean started = false;
    private boolean finished = false;

    public ProgressTicket(String displayName, Cancellable cancellable) {
        handle = ProgressHandle.createHandle(displayName, cancellable);
        this.displayName = displayName;
    }

    /**
     * Finish the task.
     */
    public synchronized void finish() {
        if (handle != null && started && !finished) {
            try {
                handle.finish();
                finished = true;
            } catch (Exception e) {
            }
        }
    }

    /**
     * Finish the task and display a statusbar message
     * @param finishMessage 
     */
    public synchronized void finish(String finishMessage) {
        if (handle != null && started && !finished) {
            try {
                handle.finish();
                finished = true;
            } catch (Exception e) {
            }
            StatusDisplayer.getDefault().setStatusText(finishMessage);
        }
    }

    /**
     * Notify the user about a new completed unit. Equivalent to incrementing workunits by one.
     */
    public synchronized void progress() {
        progress(1);
    }

    /**
     * Notify the user about completed workunits.
     * @param workunit a cumulative number of workunits completed so far
     */
    public synchronized void progress(int workunit) {
        this.currentUnit += workunit;
        if (handle != null) {
            int ratioProgress = (int) (100.0 * currentUnit / progressTotal);
            if (ratioProgress != progress100) {
                progress100 = ratioProgress;
                handle.progress(progress100 <= 100 ? progress100 : 100);
            }
        }
    }

    /**
     * Notify the user about progress by showing message with details.
     * @param message about the status of the task
     */
    public synchronized void progress(String message) {
        if (handle != null) {
            handle.progress(message);
        }
    }

    /**
     * Notify the user about completed workunits and show additional detailed message.
     * @param message details about the status of the task
     * @param workunit a cumulative number of workunits completed so far
     */
    public synchronized void progress(String message, int workunit) {
        currentUnit += workunit;
        if (handle != null) {
            int ratioProgress = (int) (100.0 * currentUnit / progressTotal);
            if (ratioProgress != progress100) {
                progress100 = ratioProgress;
                handle.progress(message, progress100 <= 100 ? progress100 : 100);
            }
        }
    }

    /**
     * Change the display name of the progress task. Use with care, please make sure the changed name is not completely different, or otherwise it might appear to the user as a different task.
     * @param newDisplayName the new display name
     */
    public synchronized void setDisplayName(String newDisplayName) {
        if (handle != null) {
            handle.setDisplayName(newDisplayName);
            this.displayName = newDisplayName;
        }
    }

    /**
     * Returns the current display name.
     * @return the current task's display name
     */
    public synchronized String getDisplayName() {
        return displayName;
    }

    /**
     * Returns the number of current unit.
     * @return the number of current unit
     */    
    public synchronized int getCurrentUnit() {
        return currentUnit;
    }        

    /**
     * Start the progress indication for indeterminate task.
     */
    public synchronized void start() {
        if (handle != null) {
            started = true;
            currentUnit = 0;
            handle.start();
        }
    }

    /**
     * Start the progress indication for a task with known number of steps.
     * @param workunits total number of workunits that will be processed
     */
    public synchronized void start(int workunits) {
        if (handle != null) {
            started = true;
            currentUnit = 0;
            this.progressTotal = workunits;
            handle.start(100);
        }
    }

    /**
     * Currently indeterminate task can be switched to show percentage completed.
     * @param workunits workunits total number of workunits that will be processed
     */
    public synchronized void switchToDeterminate(int workunits) {
        if (handle != null) {
            if (started) {
                this.progressTotal = currentUnit + workunits;
                handle.switchToDeterminate(100);
            } else {
                start(workunits);
            }
        }
    }

    /**
     * Currently determinate task can be switched to indeterminate mode.
     */
    public synchronized void switchToIndeterminate() {
        if (handle != null) {
            handle.switchToIndeterminate();
        }
    }
}
