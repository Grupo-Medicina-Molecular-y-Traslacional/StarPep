/*
 Copyright 2008-2013 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2013 Gephi Consortium. All rights reserved.

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

 Portions Copyrighted 2013 Gephi Consortium.
 */
package org.gephi.appearance;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.bapedis.core.model.Workspace;
import org.gephi.appearance.api.AppearanceModel;
import org.gephi.appearance.api.AttributeFunction;
import org.gephi.appearance.api.Function;
import org.gephi.appearance.api.Interpolator;
import org.gephi.appearance.api.Partition;
import org.gephi.appearance.spi.PartitionTransformer;
import org.gephi.appearance.spi.RankingTransformer;
import org.gephi.appearance.spi.SimpleTransformer;
import org.gephi.appearance.spi.Transformer;
import org.gephi.appearance.spi.TransformerUI;
import org.gephi.graph.api.AttributeUtils;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.ColumnObserver;
import org.gephi.graph.api.DirectedGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.ElementIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphObserver;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Index;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.Table;
import org.gephi.graph.api.types.TimeMap;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author mbastian
 */
public class AppearanceModelImpl implements AppearanceModel {

    private final Workspace workspace;
    private final GraphModel graphModel;
    private final Interpolator defaultInterpolator;
    private boolean localScale = false;
    // Transformers
    private final List<Transformer> nodeTransformers;
    private final List<Transformer> edgeTransformers;
    // Transformer UIS
    private final Map<Class, TransformerUI> transformerUIs;

    //Functions
    private final FunctionsModel functions;

    public AppearanceModelImpl(Workspace workspace, GraphView graphView) {
        this.workspace = workspace;
        this.graphModel = graphView.getGraphModel();
        this.defaultInterpolator = Interpolator.LINEAR;

        // Transformers
        this.nodeTransformers = initNodeTransformers();
        this.edgeTransformers = initEdgeTransformers();

        // Transformer UIS
        this.transformerUIs = initTransformerUIs();

        //Functions
        functions = new FunctionsModel(graphModel.getGraph(graphView));
        functions.refreshFunctions();
    }

    @Override
    public Workspace getWorkspace() {
        return workspace;
    }

    @Override
    public boolean isLocalScale() {
        return localScale;
    }

    @Override
    public Function[] getNodeFunctions() {
        return functions.getNodeFunctions();
    }

    @Override
    public Function[] getEdgeFunctions() {
        return functions.getEdgeFunctions();
    }

    @Override
    public Function getNodeFunction(Column column, Class<? extends Transformer> transformer) {
        for (Function f : functions.getNodeFunctions()) {
            if (f.isAttribute() && f.getTransformer().getClass().equals(transformer)
                    && ((AttributeFunction) f).getColumn().equals(column)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public Function getEdgeFunction(Column column, Class<? extends Transformer> transformer) {
        for (Function f : functions.getEdgeFunctions()) {
            if (f.isAttribute() && f.getTransformer().getClass().equals(transformer)
                    && ((AttributeFunction) f).getColumn().equals(column)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public Function getNodeFunction(AppearanceModel.GraphFunction graphFunction, Class<? extends Transformer> transformer) {
        String id = getNodeId(transformer, graphFunction);
        for (Function f : functions.getNodeFunctions()) {
            if (((FunctionImpl) f).getId().equals(id) && f.getTransformer().getClass().equals(transformer)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public Function getEdgeFunction(AppearanceModel.GraphFunction graphFunction, Class<? extends Transformer> transformer) {
        String id = getEdgeId(transformer, graphFunction);
        for (Function f : functions.getEdgeFunctions()) {
            if (((FunctionImpl) f).getId().equals(id) && f.getTransformer().getClass().equals(transformer)) {
                return f;
            }
        }
        return null;
    }

    @Override
    public synchronized Partition getNodePartition(Column column) {
        return functions.nodeFunctionsModel.getPartition(column);
    }

    @Override
    public Partition getEdgePartition(Column column) {
        return functions.edgeFunctionsModel.getPartition(column);
    }

//    private FunctionsModel refreshFunctions(Graph graph) {
//        synchronized (functionLock) {
//            FunctionsModel m;
//            if (graph.getView().isMainView()) {
//                m = functionsMain;
//            } else {
//                m = functions.get(graph);
//                if (m == null) {
//                    m = new FunctionsModel(graph);
//                    functions.put(graph, m);
//                }
//            }
//
//            //Check and detroy old
//            for (Iterator<Map.Entry<Graph, FunctionsModel>> it = functions.entrySet().iterator();
//                    it.hasNext();) {
//                Map.Entry<Graph, FunctionsModel> entry = it.next();
//                if (entry.getKey().getView().isDestroyed()) {
//                    it.remove();
//                }
//            }
//            return m;
//        }
//    }
    private class NodeFunctionsModel extends ElementFunctionsModel<Node> {

        public NodeFunctionsModel(Graph graph) {
            super(graph);
        }

        @Override
        public Class<? extends Element> getElementClass() {
            return Node.class;
        }

        @Override
        public Iterable<Node> getElements() {
            return graph.getNodes();
        }

        @Override
        public Table getTable() {
            return graph.getModel().getNodeTable();
        }

        @Override
        public Index<Node> getIndex(boolean localScale) {
            return localScale ? graph.getModel().getNodeIndex(graph.getView()) : graph.getModel().getNodeIndex();
        }

        @Override
        public List<Transformer> getTransformers() {
            return nodeTransformers;
        }

        @Override
        public String getIdPrefix() {
            return "node";
        }

        @Override
        public void refreshGraphFunctions() {
            if (!rankings.containsKey(getIdStr(AppearanceModel.GraphFunction.NODE_DEGREE.getId()))) {
                rankings.put(getIdStr(AppearanceModel.GraphFunction.NODE_DEGREE.getId()), new DegreeRankingImpl(graph));
            }
            if (!partitions.containsKey(getIdStr(AppearanceModel.GraphFunction.NODE_TYPE.getId()))) {
                partitions.put(getIdStr(AppearanceModel.GraphFunction.NODE_TYPE.getId()), new NodeTypePartitionImpl(graph));
            } 
            
            if (graph.isDirected()) {
                if (!rankings.containsKey(getIdStr(AppearanceModel.GraphFunction.NODE_INDEGREE.getId()))) {
                    DirectedGraph directedGraph = (DirectedGraph) graph;
                    rankings.put(getIdStr(AppearanceModel.GraphFunction.NODE_INDEGREE.getId()), new InDegreeRankingImpl(directedGraph));
                    rankings.put(getIdStr(AppearanceModel.GraphFunction.NODE_OUTDEGREE.getId()), new OutDegreeRankingImpl(directedGraph));
                }
            } else {
                rankings.remove(getIdStr(AppearanceModel.GraphFunction.NODE_INDEGREE.getId()));
                rankings.remove(getIdStr(AppearanceModel.GraphFunction.NODE_OUTDEGREE.getId()));
            }

            // Degree functions
            RankingImpl degreeRanking = rankings.get(getIdStr(AppearanceModel.GraphFunction.NODE_DEGREE.getId()));
            for (Transformer t : rankingTransformers) {
                String degreeId = getId(t, AppearanceModel.GraphFunction.NODE_DEGREE.getId());

                if (!graphFunctions.containsKey(degreeId)) {
                    String name = NbBundle.getMessage(AppearanceModelImpl.class, "NodeGraphFunction.Degree.name");
                    graphFunctions.put(degreeId, new GraphFunctionImpl(degreeId, name, Node.class, graph, t, getTransformerUI(t), degreeRanking, defaultInterpolator));
                }

                if (graph.isDirected()) {
                    String indegreeId = getId(t, AppearanceModel.GraphFunction.NODE_INDEGREE.getId());
                    String outdegreeId = getId(t, AppearanceModel.GraphFunction.NODE_OUTDEGREE.getId());

                    RankingImpl indegreeRanking = rankings.get(getIdStr(AppearanceModel.GraphFunction.NODE_INDEGREE.getId()));
                    RankingImpl outdegreeRanking = rankings.get(getIdStr(AppearanceModel.GraphFunction.NODE_OUTDEGREE.getId()));
                    if (indegreeRanking != null && outdegreeRanking != null) {
                        if (!graphFunctions.containsKey(indegreeId)) {
                            String inDegreeName = NbBundle.getMessage(AppearanceModelImpl.class, "NodeGraphFunction.InDegree.name");
                            String outDegreeName = NbBundle.getMessage(AppearanceModelImpl.class, "NodeGraphFunction.OutDegree.name");
                            graphFunctions.put(indegreeId, new GraphFunctionImpl(indegreeId, inDegreeName, Node.class, graph, t, getTransformerUI(t), indegreeRanking, defaultInterpolator));
                            graphFunctions.put(outdegreeId, new GraphFunctionImpl(outdegreeId, outDegreeName, Node.class, graph, t, getTransformerUI(t), outdegreeRanking, defaultInterpolator));
                        }
                    } else {
                        graphFunctions.remove(indegreeId);
                        graphFunctions.remove(outdegreeId);
                    }
                }
            }
            
            // Type Function
            for (Transformer t : partitionTransformers) {
                String typeId = getId(t, AppearanceModel.GraphFunction.NODE_TYPE.getId());
                PartitionImpl partition = partitions.get(getIdStr(AppearanceModel.GraphFunction.NODE_TYPE.getId()));
                if (partition != null) {
                    if (!graphFunctions.containsKey(typeId)) {
                        String name = NbBundle.getMessage(AppearanceModelImpl.class, "NodeGraphFunction.Type.name");
                        graphFunctions.put(typeId, new GraphFunctionImpl(typeId, name, Node.class, graph, t, getTransformerUI(t), partition));
                    }
                } else {
                    graphFunctions.remove(typeId);
                }
            }            
        }
    }

    private class EdgeFunctionsModel extends ElementFunctionsModel<Edge> {

        public EdgeFunctionsModel(Graph graph) {
            super(graph);
        }

        @Override
        public Iterable<Edge> getElements() {
            return graph.getEdges();
        }

        @Override
        public Class<? extends Element> getElementClass() {
            return Edge.class;
        }

        @Override
        public Table getTable() {
            return graph.getModel().getEdgeTable();
        }

        @Override
        public Index<Edge> getIndex(boolean localScale) {
            return localScale ? graph.getModel().getEdgeIndex(graph.getView()) : graph.getModel().getEdgeIndex();
        }

        @Override
        public List<Transformer> getTransformers() {
            return edgeTransformers;
        }

        @Override
        public String getIdPrefix() {
            return "edge";
        }

        @Override
        public void refreshGraphFunctions() {
            if (!rankings.containsKey(getIdStr(AppearanceModel.GraphFunction.EDGE_WEIGHT.getId()))) {
                rankings.put(getIdStr(AppearanceModel.GraphFunction.EDGE_WEIGHT.getId()), new EdgeWeightRankingImpl(graph));
            }

            // Weight function
            for (Transformer t : rankingTransformers) {
                String weightId = getId(t, AppearanceModel.GraphFunction.EDGE_WEIGHT.getId());
                RankingImpl ranking = rankings.get(getIdStr(AppearanceModel.GraphFunction.EDGE_WEIGHT.getId()));
                if (!graphFunctions.containsKey(weightId)) {
                    String name = NbBundle.getMessage(AppearanceModelImpl.class, "EdgeGraphFunction.Weight.name");
                    graphFunctions.put(weightId, new GraphFunctionImpl(weightId, name, Edge.class, graph, t, getTransformerUI(t), ranking, defaultInterpolator));
                }
            }
        }
    }

    private class FunctionsModel {

        protected final Graph graph;
        protected final GraphObserver graphObserver;
        protected final NodeFunctionsModel nodeFunctionsModel;
        protected final EdgeFunctionsModel edgeFunctionsModel;

        public FunctionsModel(Graph graph) {
            this.graph = graph;
            graphObserver = graph.getModel().createGraphObserver(graph, false);
            nodeFunctionsModel = new NodeFunctionsModel(graph);
            edgeFunctionsModel = new EdgeFunctionsModel(graph);
        }

        public synchronized Function[] getNodeFunctions() {
            return nodeFunctionsModel.getFunctions();
        }

        public synchronized Function[] getEdgeFunctions() {
            return edgeFunctionsModel.getFunctions();
        }

        protected synchronized void refreshFunctions() {
            graph.readLock();

            try {
                boolean graphHasChanged = graphObserver.isNew() || graphObserver.hasGraphChanged();
                if (graphHasChanged) {
                    nodeFunctionsModel.functions = null;
                    edgeFunctionsModel.functions = null;

                    if (graphObserver.isNew()) {
                        graphObserver.hasGraphChanged();
                    }

                    nodeFunctionsModel.refreshGraphFunctions();
                    edgeFunctionsModel.refreshGraphFunctions();
                }
                nodeFunctionsModel.refreshAttributeFunctions(graphHasChanged);
                edgeFunctionsModel.refreshAttributeFunctions(graphHasChanged);
            } finally {
                graph.readUnlockAll();
            }
        }
    }

    private abstract class ElementFunctionsModel<T extends Element> {

        protected final Graph graph;
        protected final Map<Column, ColumnObserver> columnObservers;
        protected final Map<String, SimpleFunctionImpl> simpleFunctions;
        protected final Map<String, GraphFunctionImpl> graphFunctions;
        protected final Map<String, AttributeFunctionImpl> attributeFunctions;
        protected final Map<String, PartitionImpl> partitions;
        protected final Map<String, RankingImpl> rankings;
        protected final List<RankingTransformer> rankingTransformers;
        protected final List<PartitionTransformer> partitionTransformers;
        protected Function[] functions;

        protected ElementFunctionsModel(Graph graph) {
            this.graph = graph;
            simpleFunctions = new HashMap<>();
            graphFunctions = new HashMap<>();
            attributeFunctions = new HashMap<>();
            columnObservers = new HashMap<>();
            partitions = new HashMap<>();
            rankings = new HashMap<>();

            // Init simple
            initSimpleFunctions();

            //Init transformers
            rankingTransformers = initRankingTransformers();
            partitionTransformers = initPartitionTransformers();
        }

        public Function[] getFunctions() {
            if (functions == null) {
                List<Function> list = new LinkedList<>();
                list.addAll(simpleFunctions.values());
                list.addAll(graphFunctions.values());
                list.addAll(attributeFunctions.values());
                functions = list.toArray(new Function[0]);
            }
            return functions;
        }

        public abstract Iterable<T> getElements();

        public abstract Table getTable();

        public abstract Index<T> getIndex(boolean localScale);

        public abstract List<Transformer> getTransformers();

        public abstract String getIdPrefix();

        public abstract void refreshGraphFunctions();

        public abstract Class<? extends Element> getElementClass();

        public Partition getPartition(Column column) {
            return partitions.get(getIdCol(column));
        }

        public void refreshAttributeFunctions(boolean graphHasChanged) {
            Set<Column> columns = new HashSet<>();
            for (Column column : getTable()) {
                if (!column.isProperty()) {
                    columns.add(column);
                }
            }

            //Clean
            for (Iterator<Map.Entry<Column, ColumnObserver>> itr = columnObservers.entrySet().iterator(); itr.hasNext();) {
                Map.Entry<Column, ColumnObserver> entry = itr.next();
                if (!columns.contains(entry.getKey())) {
                    rankings.remove(getIdCol(entry.getKey()));
                    partitions.remove(getIdCol(entry.getKey()));
                    for (Transformer t : getTransformers()) {
                        attributeFunctions.remove(getId(t, entry.getKey()));
                    }
                    itr.remove();
                    if (!entry.getValue().isDestroyed()) {
                        entry.getValue().destroy();
                    }
                }
            }

            //Get columns to be refreshed
            Set<Column> toRefreshColumns = new HashSet<>();
            for (Column column : columns) {
                if (!columnObservers.containsKey(column)) {
                    columnObservers.put(column, column.createColumnObserver(false));
                    toRefreshColumns.add(column);
                } else if (columnObservers.get(column).hasColumnChanged() || graphHasChanged) {
                    toRefreshColumns.add(column);
                }
            }

            //Refresh ranking and partitions
            for (Column column : toRefreshColumns) {
                RankingImpl ranking = rankings.get(getIdCol(column));
                PartitionImpl partition = partitions.get(getIdCol(column));
                if (ranking == null && partition == null) {
                    if (isPartition(graph, column)) {
                        if (column.isIndexed()) {
                            partition = new AttributePartitionImpl(column, getIndex(false));
                        } else {
                            partition = new AttributePartitionImpl(column, graph);
                        }
                        partitions.put(getIdCol(column), partition);
                    }
                    if (isRanking(graph, column)) {
                        if (column.isIndexed()) {
                            ranking = new AttributeRankingImpl(column, graph, getIndex(localScale));
                        } else {
                            ranking = new AttributeRankingImpl(column, graph, null);
                        }
                        rankings.put(getIdCol(column), ranking);
                    }
                }
            }

            //Ranking functions
            for (Column col : toRefreshColumns) {
                RankingImpl ranking = rankings.get(getIdCol(col));
                if (ranking != null) {
                    for (Transformer t : rankingTransformers) {
                        String id = getId(t, col);
                        if (!attributeFunctions.containsKey(id)) {
                            attributeFunctions.put(id, new AttributeFunctionImpl(id, graph, col, t, getTransformerUI(t), ranking, defaultInterpolator));
                        }
                    }
                }
            }

            //Partition functions
            for (Column col : toRefreshColumns) {
                PartitionImpl partition = partitions.get(getIdCol(col));
                if (partition != null) {
                    for (Transformer t : partitionTransformers) {
                        String id = getId(t, col);
                        if (!attributeFunctions.containsKey(id)) {
                            attributeFunctions.put(id, new AttributeFunctionImpl(id, graph, col, t, getTransformerUI(t), partition));
                        }
                    }
                }
            }
        }

        private void initSimpleFunctions() {
            for (Transformer transformer : getTransformers()) {
                if (transformer instanceof SimpleTransformer) {
                    String id = getId(transformer, "simple");
                    simpleFunctions.put(id, new SimpleFunctionImpl(id, getElementClass(), graph, transformer, getTransformerUI(transformer)));
                }
            }
        }

        protected TransformerUI getTransformerUI(Transformer transformer) {
            return transformerUIs.get(transformer.getClass());
        }

        private List<RankingTransformer> initRankingTransformers() {
            List<RankingTransformer> res = new LinkedList<>();
            for (Transformer t : getTransformers()) {
                if (t instanceof RankingTransformer) {
                    res.add((RankingTransformer) t);
                }
            }
            return res;
        }

        private List<PartitionTransformer> initPartitionTransformers() {
            List<PartitionTransformer> res = new LinkedList<>();
            for (Transformer t : getTransformers()) {
                if (t instanceof PartitionTransformer) {
                    res.add((PartitionTransformer) t);
                }
            }
            return res;
        }

        protected String getId(Transformer transformer, Column column) {
            return getIdPrefix() + "_" + transformer.getClass().getSimpleName() + "_column_" + column.getId();
        }

        protected String getId(Transformer transformer, String suffix) {
            return getIdPrefix() + "_" + transformer.getClass().getSimpleName() + "_" + suffix;
        }

        protected String getIdStr(String suffix) {
            return getIdPrefix() + "_" + suffix;
        }
    }

    protected String getIdCol(Column column) {
        return (AttributeUtils.isNodeColumn(column) ? "node" : "edge") + "_column_" + column.getId();
    }

    protected String getNodeId(Class<? extends Transformer> transformer, AppearanceModel.GraphFunction graphFunction) {
        return "node_" + transformer.getSimpleName() + "_" + graphFunction.getId();
    }

    protected String getEdgeId(Class<? extends Transformer> transformer, AppearanceModel.GraphFunction graphFunction) {
        return "edge_" + transformer.getSimpleName() + "_" + graphFunction.getId();
    }

    private boolean isPartition(Graph graph, Column column) {
        if (column.isDynamic()) {
            if (!column.isNumber()) {
                return true;
            }
            ElementIterable<? extends Element> iterable = AttributeUtils.isNodeColumn(column) ? graph.getNodes() : graph.getEdges();
            for (Element el : iterable) {
                TimeMap val = (TimeMap) el.getAttribute(column);
                if (val != null) {
                    Object[] va = val.toValuesArray();
                    for (Object v : va) {
                        if (v != null) {
                            iterable.doBreak();
                            return true;
                        }
                    }
                }
            }

            return false;
        } else if (column.isIndexed()) {
            if (!column.isNumber()) {
                return true;
            }
            Index index;
            if (AttributeUtils.isNodeColumn(column)) {
                index = graphModel.getNodeIndex(graph.getView());
            } else {
                index = graphModel.getEdgeIndex(graph.getView());
            }
            return index.countValues(column) > 0;
        } else {
            return false;
        }
    }

    private boolean isRanking(Graph graph, Column column) {
        if (column.isDynamic() && column.isNumber()) {
            ElementIterable<? extends Element> iterable = AttributeUtils.isNodeColumn(column) ? graph.getNodes() : graph.getEdges();
            for (Element el : iterable) {
                if (el.getAttribute(column, graph.getView()) != null) {
                    iterable.doBreak();
                    return true;
                }
            }
        } else if (!column.isDynamic() && !column.isArray() && column.isIndexed() && column.isNumber()) {
            Index index;
            if (AttributeUtils.isNodeColumn(column)) {
                index = localScale ? graphModel.getNodeIndex(graph.getView()) : graphModel.getNodeIndex();
            } else {
                index = localScale ? graphModel.getEdgeIndex(graph.getView()) : graphModel.getEdgeIndex();
            }
            if (index.countValues(column) > 0) {
                return true;
            }
        }
        return false;
    }

    public void setLocalScale(boolean localScale) {
        this.localScale = localScale;
    }

    protected GraphModel getGraphModel() {
        return graphModel;
    }

    private Map<Class, TransformerUI> initTransformerUIs() {
        //Index UIs
        Map<Class, TransformerUI> uis = new HashMap<>();

        for (TransformerUI ui : Lookup.getDefault().lookupAll(TransformerUI.class)) {
            Class transformerClass = ui.getTransformerClass();
            if (transformerClass == null) {
                throw new NullPointerException("Transformer class can' be null");
            }
            if (uis.containsKey(transformerClass)) {
                throw new RuntimeException("A Transformer can't be attach to multiple TransformerUI");
            }
            uis.put(transformerClass, ui);
        }
        return uis;
    }

    private List<Transformer> initNodeTransformers() {
        List<Transformer> res = new ArrayList<>();
        for (Transformer transformer : Lookup.getDefault().lookupAll(Transformer.class)) {
            if (transformer.isNode()) {
                res.add(transformer);
            }
        }
        return res;
    }

    private List<Transformer> initEdgeTransformers() {
        List<Transformer> res = new ArrayList<>();
        for (Transformer transformer : Lookup.getDefault().lookupAll(Transformer.class)) {
            if (transformer.isEdge()) {
                res.add(transformer);
            }
        }
        return res;
    }
}
