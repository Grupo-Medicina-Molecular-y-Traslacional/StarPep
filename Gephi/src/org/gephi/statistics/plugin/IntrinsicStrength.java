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

import java.util.Arrays;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Sebastien Heymann
 */
public class IntrinsicStrength implements Algorithm {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final AlgorithmFactory factory;

    public static final String STRENGTH = "intrinsicstrength";
    public static final String STRENGTH_TITLE = "Intrinsic Strength (IS)";

    public static final String RANKING_BY_STRENGTH = "rankingByintrinsicstrength";
    public static final String RANKING_BY_STRENGTH_TITLE = "Ranking by IS";

    protected Workspace workspace;
    protected GraphModel graphModel;
    protected Graph graph;
    private boolean isCanceled;
    private ProgressTicket progress;
    private GraphVizSetting graphViz;

    public IntrinsicStrength(AlgorithmFactory factory) {
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
        if (!nodeTable.hasColumn(STRENGTH)) {
            nodeTable.addColumn(STRENGTH, STRENGTH_TITLE, Double.class, 0.0);
            nodeTable.addColumn(RANKING_BY_STRENGTH, RANKING_BY_STRENGTH_TITLE, Integer.class, -1);
        }

        if (nodeTable.hasColumn(PeptideAttribute.CLUSTER_ATTR.getId())) {

            //Set default values
            Double defaultValue = new Double(0);
            for (Node node : graphModel.getGraph().getNodes()) {
                if (nodeTable.hasColumn(STRENGTH)) {
                    node.setAttribute(STRENGTH, defaultValue);
                }
            }

            Node oppositeNode;
            progress.switchToDeterminate(graph.getNodeCount());
            Node[] nodes = graph.getNodes().toArray();
            for (Node n : nodes) {
                if (isCanceled) {
                    return;
                }
                Integer comunity = (Integer) n.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId());
                double totalWeight = 0;
                double internalWeight = 0;
                double externalWeight = 0;
                for (Edge e : graph.getEdges(n)) {
                    oppositeNode = graph.getOpposite(n, e);
                    if (oppositeNode.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId()) != null) {
                        if (comunity.equals(oppositeNode.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId()))) {
                            internalWeight += e.getWeight();
                        } else {
                            externalWeight += e.getWeight();
                        }
                    }
                }
                totalWeight += internalWeight + externalWeight;
                n.setAttribute(STRENGTH, totalWeight == 0 ? 0 : internalWeight / totalWeight);
                progress.progress();
            }

            if (!isCanceled) {
                Arrays.parallelSort(nodes, new RankComparator(STRENGTH));
                for (int i = 0; i < nodes.length; i++) {
                    nodes[i].setAttribute(RANKING_BY_STRENGTH, i + 1);
                }
            }

        }
    }
}
