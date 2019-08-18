/*
 Copyright 2008-2011 Gephi
 Authors : Sebastien Heymann <seb@gephi.org>
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
package org.gephi.statistics.plugin;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.gephi.graph.api.Table;
import org.openide.util.Lookup;

/**
 *
 * @author Sebastien Heymann
 */
public class WeightedDegree implements Algorithm {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final AlgorithmFactory factory;
    public static final String WDEGREE = "weighted degree";
    public static final String WINDEGREE = "internal strength";
    public static final String WOUTDEGREE = "external strength";
    protected Workspace workspace;
    protected GraphModel graphModel;
    protected Graph graph;
    private boolean isCanceled;
    private ProgressTicket progress;
    private GraphVizSetting graphViz;

    public WeightedDegree(AlgorithmFactory factory) {
        this.factory = factory;
    }

    @Override
    public boolean cancel() {
        this.isCanceled = true;
        return true;
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.progress = progressTicket;
        this.workspace = workspace;
        this.progress = progressTicket;
        isCanceled = false;
        graphModel = pc.getGraphModel(workspace);
        graph = graphModel.getGraphVisible();
        graphViz = pc.getGraphVizSetting(workspace);
    }

    @Override
    public void endAlgo() {
        graph = null;
        graphModel = null;
        workspace = null;
        progress = null;

        graphViz.fireChangedGraphView();
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        Table nodeTable = graphModel.getNodeTable();
        if (!nodeTable.hasColumn(WINDEGREE)) {
            nodeTable.addColumn(WINDEGREE, "Internal strength", Double.class, 0.0);
        }
        if (!nodeTable.hasColumn(WOUTDEGREE)) {
            nodeTable.addColumn(WOUTDEGREE, "External strength", Double.class, 0.0);
        }
        if (!nodeTable.hasColumn(WDEGREE)) {
            nodeTable.addColumn(WDEGREE, "Weighted degree", Double.class, 0.0);
        }

        if (nodeTable.hasColumn(Modularity.MODULARITY_CLASS)) {

            //Set default values
            Double defaultValue = new Double(0);
            for (Node node : graphModel.getGraph().getNodes()) {
                node.setAttribute(WINDEGREE, defaultValue);
                node.setAttribute(WOUTDEGREE, defaultValue);
                node.setAttribute(WDEGREE, defaultValue);                
            }

            Node oppositeNode;
            progress.switchToDeterminate(graph.getNodeCount());
            NodeIterable nodesIterable = graph.getNodes();
            for (Node n : nodesIterable) {
                Integer comunity = (Integer) n.getAttribute(Modularity.MODULARITY_CLASS);
                double totalWeight = 0;
                double totalInWeight = 0;
                double totalOutWeight = 0;
                for (Edge e : graph.getEdges(n)) {
                    oppositeNode = graph.getOpposite(n, e);
                    if (oppositeNode.getAttribute(Modularity.MODULARITY_CLASS) != null) {
                        if (comunity.equals(oppositeNode.getAttribute(Modularity.MODULARITY_CLASS))) {
                            totalInWeight += e.getWeight();
                        } else {
                            totalOutWeight += e.getWeight();
                        }
                        totalWeight += totalInWeight + totalOutWeight;
                    }
                }
                n.setAttribute(WINDEGREE, totalInWeight);
                n.setAttribute(WOUTDEGREE, totalOutWeight);
                n.setAttribute(WDEGREE, totalWeight);

                if (isCanceled) {
                    nodesIterable.doBreak();
                    break;
                }
                progress.progress();
            }
        }
    }
}
