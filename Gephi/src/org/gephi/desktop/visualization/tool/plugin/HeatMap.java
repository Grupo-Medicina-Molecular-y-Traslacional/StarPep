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
package org.gephi.desktop.visualization.tool.plugin;

import java.awt.Color;
import java.text.DecimalFormat;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Icon;
import javax.swing.JPanel;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.graphmining.algorithms.DijkstraShortestPathAlgorithm;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.desktop.visualization.tool.NodeClickEventListener;
import org.gephi.desktop.visualization.tool.Tool;
import org.gephi.desktop.visualization.tool.ToolEventListener;
import org.gephi.desktop.visualization.tool.ToolSelectionType;
import org.gephi.desktop.visualization.tool.ToolUI;
import org.gephi.graph.api.GraphModel;
import org.gephi.ui.utils.GradientUtils.LinearGradient;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Mathieu Bastian
 */
@ServiceProvider(service = Tool.class)
public class HeatMap implements Tool {
    //Architecture

    private ToolEventListener[] listeners;
    private HeatMapPanel heatMapPanel;
    //Settings
    private Color[] gradientColors;
    private float[] gradientPositions;  //All between 0 and 1
    private boolean dontPaintUnreachable = true;

    public HeatMap() {
        //Default settings
        gradientColors = new Color[]{new Color(227, 74, 51), new Color(253, 187, 132), new Color(254, 232, 200)};
        gradientPositions = new float[]{0f, 0.5f, 1f};
    }

    @Override
    public void select() {
    }

    @Override
    public void unselect() {
        listeners = null;
        heatMapPanel = null;
    }

    @Override
    public ToolEventListener[] getListeners() {
        listeners = new ToolEventListener[1];
        listeners[0] = new NodeClickEventListener() {
            @Override
            public void clickNodes(Node[] nodes) {
                try {
                    Node n = nodes[0];
                    Color[] colors;
                    float[] positions;
                    if (heatMapPanel.isUsePalette()) {
                        colors = heatMapPanel.getSelectedPalette().getColors();
                        positions = heatMapPanel.getSelectedPalette().getPositions();
                        dontPaintUnreachable = true;
                    } else {
                        gradientColors = colors = heatMapPanel.getGradientColors();
                        gradientPositions = positions = heatMapPanel.getGradientPositions();
                        dontPaintUnreachable = heatMapPanel.isDontPaintUnreachable();
                    }
                    GraphModel graphModel = Lookup.getDefault().lookup(ProjectManager.class).getGraphModel();
                    Graph graph = graphModel.getGraphVisible();

                    DijkstraShortestPathAlgorithm algorithm = new DijkstraShortestPathAlgorithm(graph, n);
                    algorithm.compute();

                    //Color
                    LinearGradient linearGradient = new LinearGradient(colors, positions);

                    //Algorithm
                    double maxDistance = algorithm.getMaxDistance();
                    if (!dontPaintUnreachable) {
                        maxDistance++;   //+1 to have the maxdistance nodes a ratio<1
                    }
                    if (maxDistance > 0) {
                        for (Entry<Node, Double> entry : algorithm.getDistances().entrySet()) {
                            Node node = entry.getKey();
                            if (!Double.isInfinite(entry.getValue())) {
                                float ratio = (float) (entry.getValue() / maxDistance);
                                Color c = linearGradient.getValue(ratio);
                                node.setColor(c);
                            } else if (!dontPaintUnreachable) {
                                Color c = colors[colors.length - 1];
                                node.setColor(c);
                            }
                        }
                    }
                    Color c = colors[0];
                    n.setColor(c);
                    heatMapPanel.setStatus(NbBundle.getMessage(HeatMap.class, "HeatMap.status.maxdistance", new DecimalFormat("#.##").format(algorithm.getMaxDistance())));
                } catch (Exception e) {
                    Logger.getLogger("").log(Level.SEVERE, "", e);
                }
            }
        };
        return listeners;
    }

    @Override
    public ToolUI getUI() {
        return new ToolUI() {
            @Override
            public JPanel getPropertiesBar(Tool tool) {
                heatMapPanel = new HeatMapPanel(gradientColors, gradientPositions, dontPaintUnreachable);
                return heatMapPanel;
            }

            @Override
            public String getName() {
                return NbBundle.getMessage(HeatMap.class, "HeatMap.name");
            }

            @Override
            public Icon getIcon() {
                return ImageUtilities.loadImageIcon("org/gephi/desktop/visualization/resources/heatmap.png", false);
            }

            @Override
            public String getDescription() {
                return NbBundle.getMessage(HeatMap.class, "HeatMap.description");
            }

            @Override
            public int getPosition() {
                return 150;
            }
        };
    }

    @Override
    public ToolSelectionType getSelectionType() {
        return ToolSelectionType.SELECTION;
    }
}
