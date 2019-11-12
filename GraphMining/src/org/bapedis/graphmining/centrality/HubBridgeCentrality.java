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
package org.bapedis.graphmining.centrality;

import org.bapedis.graphmining.model.NodeCentralityComparator;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.GraphVizSetting;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class HubBridgeCentrality extends AbstractCentrality {

    protected static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final AlgorithmFactory factory;

    public static final String HUB_BRIDGE = "hubBridge";
    public static final String HUB_BRIDGE_TITLE = "Hub-Bridge Centrality (HB)";

    public static final String RANKING_BY_HUB_BRIDGE = "rankingByHubBridge";
    public static final String RANKING_BY_HUB_BRIDGE_TITLE = "Ranking by HB";

    private GraphVizSetting graphViz;
    private int relType;
    private final NotifyDescriptor errorND;
    private double[] nodeHubBridge;

    public HubBridgeCentrality(AlgorithmFactory factory) {
        super(factory);
        this.factory = factory;
        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(HubBridgeCentrality.class, "HubBridgeCentrality.errorND"), NotifyDescriptor.ERROR_MESSAGE);
    }

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket);

        Table nodeTable = graphModel.getNodeTable();
        graphViz = pc.getGraphVizSetting(workspace);
        relType = graphModel.addEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
        if (!nodeTable.hasColumn(PeptideAttribute.CLUSTER_ATTR.getId())) {
            DialogDisplayer.getDefault().notify(errorND);
            cancel();
        } else {
            nodeHubBridge = new double[graph.getNodeCount()];
        }
    }

    @Override
    public void endAlgo() {
        boolean fireEvent = false;

        if (!isCanceled.get()) {
            Table nodeTable = graphModel.getNodeTable();
            if (!nodeTable.hasColumn(HUB_BRIDGE)) {
                nodeTable.addColumn(HUB_BRIDGE, HUB_BRIDGE_TITLE, Double.class, 0.0);
                nodeTable.addColumn(RANKING_BY_HUB_BRIDGE, RANKING_BY_HUB_BRIDGE_TITLE, Integer.class, -1);
                fireEvent = true;
            }

            //Set default values
            Double defaultValue = new Double(0);
            Integer defaultRanking = -1;
            for (Node node : graphModel.getGraph().getNodes()) {
                if (nodeTable.hasColumn(HUB_BRIDGE)) {
                    node.setAttribute(HUB_BRIDGE, defaultValue);
                    node.setAttribute(RANKING_BY_HUB_BRIDGE, defaultRanking);
                }
            }
            
            //Save values
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].setAttribute(HUB_BRIDGE, nodeHubBridge[i]);
            }

            Arrays.parallelSort(nodes, new NodeCentralityComparator(HUB_BRIDGE));
            for (int i = 0; i < nodes.length; i++) {
                nodes[i].setAttribute(RANKING_BY_HUB_BRIDGE, i + 1);
            }
        }

        super.endAlgo();
        nodeHubBridge = null;

        if (fireEvent) {
            graphViz.fireChangedGraphView();
        }
        graphViz = null;
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
    protected void calculateCentrality() {
        if (!isCanceled.get()) {
            progress.switchToDeterminate(3 * graph.getNodeCount());

            Node oppositeNode;
            String id;
            int community;

            //1 - Mapping communities
            Map<Integer, Set<Node>> mapC = new HashMap<>();
            Set<Node> members;
            for (Node node : nodes) {
                if (isCanceled.get()) {
                    return;
                }
                community = (int) node.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId());
                if (mapC.containsKey(community)) {
                    members = mapC.get(community);
                } else {
                    members = new HashSet<>();
                    mapC.put(community, members);
                }
                members.add(node);
                progress.progress();
            }

            //2 - Mapping neighboring community
            Map<String, Set<Integer>> mapNC = new HashMap<>();
            Set<Integer> communities;
            for (Node node : nodes) {
                if (isCanceled.get()) {
                    return;
                }
                id = (String) node.getId();
                if (mapNC.containsKey(id)) {
                    communities = mapNC.get(id);
                } else {
                    communities = new HashSet<>();
                    mapNC.put(id, communities);
                }
                for (Edge e : graph.getEdges(node, relType)) {
                    oppositeNode = graph.getOpposite(node, e);
                    community = (int) oppositeNode.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId());
                    communities.add(community);
                }
                progress.progress();
            }

            //-------compute the HUB_BRIDGE centrality    
            double internalStrength, externalStrength;
            double measure;
            Node node;
            for (int i = 0; i < nodes.length; i++) {
                node = nodes[i];
                id = (String) node.getId();
                if (isCanceled.get()) {
                    return;
                }
                community = (int) node.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId());
                internalStrength = 0;
                externalStrength = 0;
                for (Edge e : graph.getEdges(node, relType)) {
                    oppositeNode = graph.getOpposite(node, e);
                    if (oppositeNode.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId()) != null) {
                        if (community == (int) oppositeNode.getAttribute(PeptideAttribute.CLUSTER_ATTR.getId())) {
                            internalStrength += e.getWeight();
                        } else {
                            externalStrength += e.getWeight();
                        }
                    }
                }
                measure = mapC.get(community).size() * internalStrength + mapNC.get(id).size() * externalStrength;
                nodeHubBridge[i] = measure;
                progress.progress();
            }

        }
    }

}
