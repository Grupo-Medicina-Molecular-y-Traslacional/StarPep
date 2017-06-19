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

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

/**
 * Portable long-task executor, that supports synchronous and asynchronous
 * execution, progress, cancellation and error management.
 * <p>
 * Note that only one task can be executed by the executor at one time.
 *
 * @author Mathieu Bastian
 * @see LongTask
 */
public final class TaskExecutor {
//    number of seconds to wait after a cancel request    

    private final long interruptDelay = 500;
    private final ThreadPoolExecutor executor;
    private LongTaskListener listener;
    private LongTaskErrorHandler defaultErrorHandler;

    /**
     * Creates a new task executor.
     *
     */
    public TaskExecutor() {
        int numberOfCPUs = Runtime.getRuntime().availableProcessors();
        this.executor = new ThreadPoolExecutor(0, numberOfCPUs + 1, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(), new NamedThreadFactory());
    }

    /**
     * Execute a long task with cancel and progress support. Task can be
     * <code>null</code>. In this case <code>runnable</code> will be executed
     * normally, but without cancel and progress support.
     *
     * @param runnable the runnable to be executed
     * @param taskName the name of the task, is displayed in the status bar if
     * available
     * @param errorHandler error handler for exception retrieval during
     * execution
     * @throws NullPointerException if <code>runnable</code> * or
     * <code>taskName</code> is null
     * @throws IllegalStateException if a task is still executing at this time
     */
    public synchronized void execute(final Runnable runnable, String taskName, LongTaskErrorHandler errorHandler) {
        if (runnable == null || taskName == null) {
            throw new NullPointerException();
        }

        if (runnable instanceof LongTask) {
            RunningLongTask runningLongtask = new RunningLongTask(runnable, taskName, errorHandler);
            runningLongtask.future = executor.submit(runningLongtask);
        } else {
            executor.submit(runnable);
        }

    }

    /**
     * Execute a long task with cancel and progress support. Task can be
     * <code>null</code>. In this case <code>runnable</code> will be executed
     * normally, but without cancel and progress support.
     *
     * @param runnable the runnable to be executed
     * @throws NullPointerException if <code>runnable</code> is null
     * @throws IllegalStateException if a task is still executing at this time
     */
    public synchronized void execute(Runnable runnable) {
        execute(runnable, "", null);
    }

    /**
     * Set the listener to this executor. Only a unique listener can be set to
     * this executor. The listener is called when the task terminates normally.
     *
     * @param listener a listener for this executor
     */
    public void setLongTaskListener(LongTaskListener listener) {
        this.listener = listener;
    }

    /**
     * Set the default error handler. Use error handlers to get errors and
     * exceptions thrown during tasks execution.
     *
     * @param errorHandler the default error handler
     */
    public void setDefaultErrorHandler(LongTaskErrorHandler errorHandler) {
        if (errorHandler != null) {
            this.defaultErrorHandler = errorHandler;
        }
    }

    private synchronized void finished(RunningLongTask runningLongTask) {        
        runningLongTask.progress.finish();
        if (listener != null) {
            LongTask task = (LongTask) runningLongTask.runnable;
            listener.taskFinished(task);
        }
    }

    /**
     * Inner class for associating a task to its Future instance
     */
    private class RunningLongTask implements Runnable {

        private final String taskName;
        private final Runnable runnable;
        private Future<?> future;
        private final ProgressTicket progress;
        private LongTaskErrorHandler errorHandler;

        public RunningLongTask(Runnable runnable, String taskName, LongTaskErrorHandler errorHandler) {
            this.runnable = runnable;
            this.taskName = taskName;
            this.errorHandler = errorHandler;
            this.progress = new ProgressTicket(taskName, new Cancellable() {
                @Override
                public boolean cancel() {
                    RunningLongTask.this.cancel();
                    return true;
                }

            });
            ((LongTask) runnable).setProgressTicket(progress);
        }

        @Override
        public void run() {
            progress.setDisplayName(taskName);
            progress.start();
            try {
                runnable.run();
                finished(this);
            } catch (Exception e) {
                LongTaskErrorHandler err = errorHandler;
                finished(this);
                if (err != null) {
                    err.fatalError(e);
                } else if (defaultErrorHandler != null) {
                    defaultErrorHandler.fatalError(e);
                } else {
                    Logger.getLogger("").log(Level.SEVERE, "", e);
                }
            }
        }

        public void cancel() {
            boolean isCancelled = ((LongTask) runnable).cancel();
            if (isCancelled) {
                finished(this);
            } else {
                Timer cancelTimer = new Timer(taskName + "_cancelTimer");
                cancelTimer.schedule(new InterruptTimerTask(cancelTimer, this), interruptDelay);
                cancelTimer = null;
            }
        }
    }

    /**
     * Inner class for naming the executor service thread
     */
    private class NamedThreadFactory implements ThreadFactory {
//        the name of the executor, used to recognize threads by names

        private final String name;

        private NamedThreadFactory() {
            this.name = NbBundle.getMessage(TaskExecutor.class, "TaskExecutor.name");
        }

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, name);
        }
    }

    private class InterruptTimerTask extends TimerTask {
        Timer cancelTimer;
        private final RunningLongTask runningLongTask;

        public InterruptTimerTask(Timer cancelTimer, RunningLongTask runningLongTask) {
            this.cancelTimer = cancelTimer;
            this.runningLongTask = runningLongTask;
        }

        @Override
        public void run() {
            if (runningLongTask.future != null) {
                runningLongTask.future.cancel(true);
            }
            finished(runningLongTask);
            cancelTimer.cancel();
            cancelTimer = null;
        }
    }
}
