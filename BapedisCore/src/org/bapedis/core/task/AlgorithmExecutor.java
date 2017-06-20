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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bapedis.core.spi.algo.Algorithm;
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
public final class AlgorithmExecutor {

    private final ThreadPoolExecutor executor;
    private final List<AlgoExecutor> taskList;

    /**
     * Creates a new task executor.
     *
     */
    public AlgorithmExecutor() {
        int numberOfCPUs = Runtime.getRuntime().availableProcessors();
        int maximumPoolSize = numberOfCPUs + 1;
        this.executor = new ThreadPoolExecutor(0, maximumPoolSize, 15, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>());
        taskList = new LinkedList<>();
    }

    /**
     * Execute an algorithm with cancel and progress support.
     *
     * @param algorithm the algorithm to be executed
     * @param taskName the name of the task, is displayed in the status bar if
     * available
     * @param listener the listener to this executor. The listener is called
     * when the task is finished.
     * @param errorHandler error handler for exception retrieval during
     * execution
     * @throws NullPointerException if <code>algorithm</code> is null
     */
    public synchronized void execute(final Algorithm algorithm, String taskName, AlgorithmListener listener, AlgorithmErrorHandler errorHandler) {
        if (algorithm == null) {
            throw new NullPointerException();
        }
        AlgoExecutor runnable = new AlgoExecutor(algorithm, taskName, listener, errorHandler);
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
        execute(algorithm, algorithm.getFactory().getName(), listener, errorHandler);
    }

    /**
     * Execute an algorithm with cancel and progress support.
     *
     * @param algorithm the algorithm to be executed
     * @throws NullPointerException if <code>algorithm</code> is null
     */
    public synchronized void execute(Algorithm algorithm) {
        execute(algorithm, null, null);
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

    private synchronized void finished(AlgoExecutor runnable) {
        runnable.progress.finish();
        if (runnable.listener != null) {
            runnable.listener.algorithmFinished(runnable.algorithm);
        }
        taskList.remove(runnable);
    }

    /**
     * Inner class for associating a runnable to its Future instance
     */
    private class AlgoExecutor implements Runnable {

        private final String taskName;
        private final Algorithm algorithm;
        private Future<?> future;
        private final ProgressTicket progress;
        private final AlgorithmListener listener;
        private final AlgorithmErrorHandler errorHandler;

        public AlgoExecutor(Algorithm algorithm, String taskName, AlgorithmListener listener, AlgorithmErrorHandler errorHandler) {
            this.algorithm = algorithm;
            this.taskName = taskName;
            this.listener = listener;
            this.errorHandler = errorHandler;
            this.progress = new ProgressTicket(taskName, new Cancellable() {
                @Override
                public boolean cancel() {
                    return AlgoExecutor.this.cancel();
                }

            });
            algorithm.setProgressTicket(progress);
            progress.start();            
            progress.setDisplayName(NbBundle.getMessage(AlgorithmExecutor.class, "AlgorithmExecutor.task.submitted", taskName));
        }

        @Override
        public void run() {
            progress.setDisplayName(NbBundle.getMessage(AlgorithmExecutor.class, "AlgorithmExecutor.task.running", taskName));
            try {
                algorithm.run();
            } catch (Throwable e) {
                if (errorHandler != null) {
                    errorHandler.fatalError(e);
                } else {
                    Logger.getLogger("").log(Level.SEVERE, "", e);
                }
            } finally {
                finished(this);
            }
        }

        public boolean cancel() {
            boolean isCancelled = algorithm.cancel();
            if (!isCancelled) {
                if (future != null) {
                    isCancelled = future.cancel(true);
                }
            }
            return isCancelled;
        }
    }

}
