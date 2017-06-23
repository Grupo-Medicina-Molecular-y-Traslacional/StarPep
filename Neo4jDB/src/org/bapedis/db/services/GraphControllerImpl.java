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
package org.bapedis.db.services;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.db.model.NeoPeptide;
import org.bapedis.db.model.NeoPeptideModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Subgraph;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;

/**
 *
 * @author mbastian
 */
public final class GraphControllerImpl implements WorkspaceEventListener, LookupListener, PropertyChangeListener {
    protected final ProjectManager pm;
    protected Lookup.Result<NeoPeptideModel> peptideLkpResult;
    protected final GraphModel graphModel;
    protected GraphView emptyView;

    public GraphControllerImpl() {
        graphModel = GraphModel.Factory.newInstance();
        pm = Lookup.getDefault().lookup(ProjectManager.class);
        pm.addWorkspaceEventListener(this);
        workspaceChanged(null, pm.getCurrentWorkspace());
    }

    public synchronized GraphModel getGraphModel() {
        Workspace currentWorkspace = Lookup.getDefault().lookup(ProjectManager.class).getCurrentWorkspace();
        return getGraphModel(currentWorkspace);
    }

    public synchronized GraphModel getGraphModel(Workspace workspace) {
        NeoPeptideModel peptideModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        if (peptideModel != null) {
            return peptideModel.getGraph().getModel();
        }        
        if (emptyView == null) {
            emptyView = graphModel.createView();
        }
        graphModel.setVisibleView(emptyView);
        return graphModel;
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            FilterModel oldFilterModel = pm.getFilterModel(oldWs);
            if (oldFilterModel != null) {
                oldFilterModel.removePropertyChangeListener(this);
            }
        }

        peptideLkpResult = newWs.getLookup().lookupResult(NeoPeptideModel.class);
        peptideLkpResult.addLookupListener(this);

        FilterModel filterModel = pm.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);
        
        NeoPeptideModel peptideModel = newWs.getLookup().lookup(NeoPeptideModel.class);
        if (peptideModel != null) {
            GraphView graphView = peptideModel.getCurrentView();
            GraphModel model = peptideModel.getGraph().getModel();
            model.setVisibleView(graphView);
        }

    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            updateView();
        } 
    }

    private void updateView() {
        Workspace workspace = pm.getCurrentWorkspace();
        NeoPeptideModel peptideModel = workspace.getLookup().lookup(NeoPeptideModel.class);
        FilterModel filterModel = pm.getFilterModel();

        if (peptideModel != null) {
            Peptide[] peptides = peptideModel.getPeptides();
            Graph graph = peptideModel.getGraph();
            GraphView graphView = graph.getView();

            GraphModel model = graph.getModel();
            GraphView oldView = peptideModel.getCurrentView();
            if (!oldView.isMainView() && oldView != graphView) {
                model.destroyView(oldView);
            }

            if (filterModel.isEmpty()) {
                model.setVisibleView(graphView);
                peptideModel.setCurrentView(graphView);
            } else {
                GraphView newView = model.createView();
                Subgraph subGraph = model.getGraph(newView);
                NeoPeptide neoPeptide;
                List<Node> neighbors;
                List<Edge> edges;
                for (Peptide p : peptides) {
                    neoPeptide = (NeoPeptide) p;
                    subGraph.addNode(neoPeptide.getGraphNode());
                    neighbors = new LinkedList<>();
                    edges = new LinkedList<>();
                    for (Node neighbor : neoPeptide.getNeighbors()) {
                        if (!subGraph.hasNode(neighbor.getId())) {
                            neighbors.add(neighbor);
                        }
                    }
                    subGraph.addAllNodes(neighbors);
                    for (Edge edge : graph.getEdges(neoPeptide.getGraphNode())) {
                        edges.add(edge);
                    }
                    subGraph.addAllEdges(edges);
                }
                model.setVisibleView(newView);
                peptideModel.setCurrentView(newView);
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof FilterModel) {
            updateView();
        }
    }

}
