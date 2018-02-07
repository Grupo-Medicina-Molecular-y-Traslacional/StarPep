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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Portable long-task executor, that supports synchronous and asynchronous
 * execution, progress, cancellation and error management.
 * <p>
 * Note that only one task can be executed by the executor at one time.
 *
 * @author Mathieu Bastian
 * @see LongTask
 */
@ServiceProvider(service = AlgorithmExecutor.class)
public final class AlgorithmExecutor {

    private final ProjectManager pc;
    private final ThreadPoolExecutor executor;
    private final List<AlgoExecutor> taskList;
    private final AlgorithmListener defaultAlgoListener;
    private final AlgorithmErrorHandler defaultErrorHandler;

    /**
     * Creates a new task executor.
     *
     */
    public AlgorithmExecutor() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        int numberOfCPUs = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = numberOfCPUs + 1;
        this.executor = new ThreadPoolExecutor(0, maximumPoolSize, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        taskList = Collections.synchronizedList(new LinkedList<>());
        defaultAlgoListener = new AlgorithmListenerImpl();
        defaultErrorHandler = new AlgorithmErrorHandlerImpl();
    }

    public AlgorithmListener getDefaultAlgoListener() {
        return defaultAlgoListener;
    }

    public AlgorithmErrorHandler getDefaultErrorHandler() {
        return defaultErrorHandler;
    }        

    /**
     * Execute an algorithm with cancel and progress support.
     *
     * @param workspace the workspace containing the algorithm
     * @param algorithm the algorithm to be executed
     * @param listener the listener to this executor. The listener is called
     * when the task is finished.
     * @param errorHandler error handler for exception retrieval during
     * execution
     * @throws NullPointerException if <code>algorithm</code> is null
     */
    public synchronized void execute(Workspace workspace, final Algorithm algorithm, AlgorithmListener listener, AlgorithmErrorHandler errorHandler) {
        AlgoExecutor runnable = new AlgoExecutor(workspace, algorithm, listener, errorHandler);
        runnable.ticket.start();
        runnable.ticket.progress(NbBundle.getMessage(AlgorithmExecutor.class, "AlgorithmExecutor.task.submitted"));
        runnable.future = executor.submit(runnable);
        taskList.add(runnable);
    }

    /**
     * Execute an algorithm with cancel and progress support.
     *
     * @param algorithm the algorithm to be executed
     * @param listener the listener to this executor. The listener is called
     * when the task is finished.
     * @param errorHandler error handler for exception retrieval during
     * execution. Use error handlers to get errors and exceptions thrown during
     * tasks execution.
     * @throws NullPointerException if <code>algorithm</code> is null
     */
    public synchronized void execute(Algorithm algorithm, AlgorithmListener listener, AlgorithmErrorHandler errorHandler) {
        execute(pc.getCurrentWorkspace(), algorithm, listener, errorHandler);
    }

    /**
     * Execute an algorithm with cancel and progress support.
     *
     * @param algorithm the algorithm to be executed
     * @throws NullPointerException if <code>algorithm</code> is null
     */
    public synchronized void execute(Algorithm algorithm) {
        execute(algorithm, defaultAlgoListener, defaultErrorHandler);
    }

    /**
     * Cancel an algorithm.
     *
     * @param algorithm the algorithm to be cancelled
     * @return {@code false} if the algorithm could not be cancelled, typically
     * because it has already completed normally; {@code true} otherwise
     * @throws NullPointerException if <code>algorithm</code> is null
     */
    public synchronized boolean cancel(Algorithm algorithm) {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        for (AlgoExecutor runnable : taskList) {
            if (runnable.algorithm.equals(algorithm)) {
                return runnable.cancel();
            }
        }
        return false;
    }

    /**
     * Inner class for associating a runnable to its Future instance
     */
    private class AlgoExecutor implements Runnable {

        private final Algorithm algorithm;
        private Future<?> future;
        private final ProgressTicket ticket;
        private final AlgorithmListener listener;
        private final AlgorithmErrorHandler errorHandler;
        private final AtomicBoolean running;
        private final Workspace workspace;

        public AlgoExecutor(Workspace workspace, Algorithm algorithm, AlgorithmListener listener, AlgorithmErrorHandler errorHandler) {
            this.workspace = workspace;
            this.algorithm = algorithm;
            this.listener = listener;
            this.errorHandler = errorHandler;
            this.running = new AtomicBoolean(false);
            String taskName = NbBundle.getMessage(AlgorithmExecutor.class, "AlgorithmExecutor.task.name", workspace.getName(), algorithm.getFactory().getName());
            this.ticket = new ProgressTicket(taskName, new Cancellable() {
                @Override
                public boolean cancel() {
                    return AlgoExecutor.this.cancel();
                }

            });
        }

        @Override
        public void run() {
            running.set(true);
            ticket.progress(NbBundle.getMessage(AlgorithmExecutor.class, "AlgorithmExecutor.task.running"));
            pc.reportRunningTask(algorithm.getFactory().getName(), workspace);
            try {
                algorithm.initAlgo(workspace, ticket);
                algorithm.run();
            } catch (Throwable e) {
                if (errorHandler != null) {
                    errorHandler.fatalError(e);
                } else {
                    Logger.getLogger(AlgoExecutor.class.getName()).log(Level.SEVERE, "", e);
                }
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                pc.reportError(errors.toString(), workspace);
            } finally {
                finish();
            }
        }

        public boolean cancel() {
            ticket.progress(NbBundle.getMessage(AlgorithmExecutor.class, "AlgorithmExecutor.task.canceling"));
            boolean isCancelled = algorithm.cancel();
            if (!running.get()) {
                isCancelled = future.cancel(true);
                if (isCancelled) {
                    finish();
                }
            }
            return isCancelled;
        }

        private void finish() {
            if (taskList.remove(this)) {
                algorithm.endAlgo();
                ticket.finish();
                if (listener != null) {
                    listener.algorithmFinished(algorithm);
                }
                pc.reportFinishedTask(algorithm.getFactory().getName(), workspace);
            }
        }
    }

    private class AlgorithmListenerImpl implements AlgorithmListener {

        @Override
        public void algorithmFinished(Algorithm algo) {
            AlgorithmModel algoModel;
            for (Iterator<? extends Workspace> it = pc.getWorkspaceIterator(); it.hasNext();) {
                Workspace workspace = it.next();
                algoModel = pc.getAlgorithmModel(workspace);
                if (algoModel.getSelectedAlgorithm() != null && algoModel.getSelectedAlgorithm().equals(algo)) {
                    algoModel.setRunning(false);
                    if (pc.getCurrentWorkspace() != workspace) {
                        String txt = NbBundle.getMessage(AlgorithmExecutor.class, "Workspace.notify.finishedTask", algo.getFactory().getName());
                        pc.workspaceChangeNotification(txt, workspace);
                    }
                }
            }
        }

    }

    private class AlgorithmErrorHandlerImpl implements AlgorithmErrorHandler {

        @Override
        public void fatalError(Throwable t) {
            Exceptions.printStackTrace(t);
        }

    }

}
