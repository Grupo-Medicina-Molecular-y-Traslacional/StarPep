/*
 Copyright 2008-2010 Gephi
 Authors : Helder Suzuki <heldersuzuki@gephi.org>
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
package org.gephi.layout.plugin;

import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.openide.util.Lookup;

/**
 * Base class for layout algorithms.
 *
 * @author Helder Suzuki <heldersuzuki@gephi.org>
 */
public abstract class AbstractLayout implements Algorithm {

    private final AlgorithmFactory layoutBuilder;
    protected GraphModel graphModel;
    private Graph graph;
    protected Integer iterations;
    protected ProgressTicket progressTicket;
    private boolean stopRun;
    private boolean converged;

    public AbstractLayout(AlgorithmFactory layoutBuilder) {
        this.layoutBuilder = layoutBuilder;
        GraphController gc = Lookup.getDefault().lookup(GraphController.class);
        graphModel = gc.getGraphModel();
    }

    public Graph getGraph() {
        return graph;
    }

    public Integer getIterations() {
        return iterations;
    }

    public void setIterations(Integer iterations) {
        this.iterations = iterations;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return layoutBuilder;
    }

    /**
     * Called to initialize the layout (prepare to run).
     */
    public abstract void initLayout();

    /**
     * Run a step in the algorithm, should be called only if canAlgo() returns
     * true.
     */
    public abstract void runLayout();

    /**
     * Called to release resources when the layout is finished
     */
    public abstract void endLayout();

    @Override
    public final void initAlgo() {
        graph = graphModel.getGraphVisible();
        graph.readLock();
        try {
            initLayout();
        } finally {
            graph.readUnlock();
        }
        stopRun = false;
        converged = false;
    }

    @Override
    public final void run() {
        long i = 0;
        while (canAlgo() && !stopRun) {
            graph.readLock();
            try {
                runLayout();
            } finally {
                graph.readUnlock();
            }
            i++;
            if (iterations != null && iterations.longValue() == i) {
                break;
            }
        }
    }

    @Override
    public final void endAlgo() {
        graph.readLock();
        try {
            endLayout();
            for (Node n : graph.getNodes()) {
                n.setLayoutData(null);
            }
        } finally {
            graph.readUnlock();
        }
        graph = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progressTicket = progressTicket;
    }

    /**
     * Tests if the algorithm can run, called before each pass.
     *
     * @return              <code>true</code> if the algorithm can run, <code>
     *                      false</code> otherwise
     */
    private boolean canAlgo() {
        return !isConverged() && graphModel != null;
    }

    public void setConverged(boolean converged) {
        this.converged = converged;
    }

    public boolean isConverged() {
        return converged;
    }
}
