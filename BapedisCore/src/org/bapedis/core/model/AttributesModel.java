/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.bridge.AttributeModelBridge;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.event.SwingPropertyChangeSupport;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.GraphView;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * A class that represents an attribute-based data model for peptides.
 *
 * @author loge
 */
public class AttributesModel {

    protected final static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final GraphWindowController graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
    protected final Workspace workspace;
    protected final Map<String, Peptide> peptideMap;
    protected final Map<String, List<MolecularDescriptor>> mdMap;
    protected final Set<PeptideAttribute> displayedColumnsModel;
    private static final int MAX_DISPLAYED_MD_COLUMNS = 10;
    protected List<PeptideNode> nodeList;
    private PeptideNodeContainer container;
    private List<Peptide> filteredPept;
    protected QuickFilter quickFilter;
    public static final String CHANGED_FILTER = "quickFilter";
    public static final String DISPLAY_ATTR_ADDED = "display_attribute_add";
    public static final String DISPLAY_ATTR_REMOVED = "display_attribute_remove";
    public static final String MD_ATTR_ADDED = "md_attribute_add";
    public static final String MD_ATTR_REMOVED = "md_attribute_remove";

    protected final AttributeModelBridgeImpl bridge;
    protected transient final SwingPropertyChangeSupport propertyChangeSupport;
    protected Node rootNode;

    public AttributesModel(Workspace workspace) {
        this.workspace = workspace;
        peptideMap = new LinkedHashMap<>();
        mdMap = new LinkedHashMap<>();
        nodeList = new LinkedList<>();
        container = new PeptideNodeContainer();
        rootNode = new AbstractNode(container);
        propertyChangeSupport = new SwingPropertyChangeSupport(this, true);
        bridge = new AttributeModelBridgeImpl();

        displayedColumnsModel = new LinkedHashSet<>();
        displayedColumnsModel.add(Peptide.ID);
        displayedColumnsModel.add(Peptide.SEQ);
        displayedColumnsModel.add(Peptide.LENGHT);
    }

    public Workspace getOwnerWS() {
        return workspace;
    }

    public Map<String, Peptide> getPeptideMap() {
        return peptideMap;
    }

    public PeptideAttribute[] getDisplayedColumns() {
        return displayedColumnsModel.toArray(new PeptideAttribute[0]);
    }

    public boolean canAddMDColumn() {
        return displayedColumnsModel.size() < MAX_DISPLAYED_MD_COLUMNS;
    }

    public boolean addDisplayedColumn(PeptideAttribute attr) {
        if (attr instanceof MolecularDescriptor && !canAddMDColumn()) {
            return false;
        }
        if (displayedColumnsModel.add(attr)) {
            propertyChangeSupport.firePropertyChange(DISPLAY_ATTR_ADDED, null, attr);
            return true;
        }
        return false;
    }

    public boolean removeDisplayedColumn(PeptideAttribute attr) {
        if (displayedColumnsModel.remove(attr)) {
            propertyChangeSupport.firePropertyChange(DISPLAY_ATTR_REMOVED, attr, null);
            return true;
        }
        return false;
    }

//    private void addNodeTableColumn(PeptideAttribute attr) {
//        Table table = pc.getGraphModel().getNodeTable();
//        if (!table.hasColumn(attr.getId())) {
//            table.addColumn(attr.getId(), attr.getDisplayName(), attr.getType(), Origin.DATA, attr.getDefaultValue(), true);
//        }
//
//        org.gephi.graph.api.Node node;
//        for (Peptide peptide : peptideMap.values()) {
//            node = peptide.getGraphNode();
//            node.setAttribute(attr.getId(), peptide.getAttributeValue(attr));
//        }
//    }
    public Set<String> getMolecularDescriptorKeys() {
        return mdMap.keySet();
    }

    public List<MolecularDescriptor> getMolecularDescriptors(String category) {
        return Collections.unmodifiableList(mdMap.get(category));
    }

    public boolean hasMolecularDescriptors(String category) {
        return mdMap.containsKey(category);
    }

    public void addMolecularDescriptors(String category, List<MolecularDescriptor> features) {
        if (mdMap.containsKey(category)) {
            for (MolecularDescriptor oldAttr : mdMap.get(category)) {
                if (!features.contains(oldAttr)) {
                    delete(oldAttr);
                }
            }
        }
        mdMap.put(category, features);
        propertyChangeSupport.firePropertyChange(MD_ATTR_ADDED, null, category);
    }

    public void deleteAllMolecularDescriptors() {
        Set<String> categories = mdMap.keySet();
        mdMap.clear();
        for (String category : categories) {
            propertyChangeSupport.firePropertyChange(MD_ATTR_REMOVED, category, null);
        }
    }

    public void deleteAllMolecularDescriptors(String category) {
        for (PeptideAttribute attr : mdMap.remove(category)) {
            delete(attr);
        }
        propertyChangeSupport.firePropertyChange(MD_ATTR_REMOVED, category, null);
    }

    public void deleteAttribute(MolecularDescriptor attr) {
        String category = attr.getCategory();
        if (mdMap.containsKey(category)) {
            List<MolecularDescriptor> list = mdMap.get(category);
            delete(attr);
            list.remove(attr);
        } else {
            throw new IllegalArgumentException("Unknown molecular descriptor category: " + category);
        }
    }

    private void delete(PeptideAttribute attr) {
        for (PeptideNode pNode : nodeList) {
            pNode.getPeptide().deleteAttribute(attr);
        }
        removeDisplayedColumn(attr);
    }

    public synchronized List<Peptide> getPeptides() {
        if (filteredPept != null) {
            return filteredPept;
        }
        List<Peptide> peptides = new LinkedList<>();
        boolean accepted;
        for (PeptideNode pNode : nodeList) {
            accepted = quickFilter == null ? true : quickFilter.accept(pNode);
            if (accepted) {
                peptides.add(pNode.getPeptide());
            }
        }
        filteredPept = Collections.unmodifiableList(peptides);
        return filteredPept;
    }

    public AttributeModelBridge getBridge() {
        return bridge;
    }

    public Node getRootNode() {
        return rootNode;
    }

    public List<PeptideNode> getNodeList() {
        return Collections.unmodifiableList(nodeList);
    }

    public void addPeptide(Peptide peptide) {
        nodeList.add(new PeptideNode(peptide));
        peptideMap.put(peptide.getID(), peptide);
//        checkDefaultNodeAttributes(peptide);
        filteredPept = null;
    }

//    private void checkDefaultNodeAttributes(Peptide peptide) {
//        org.gephi.graph.api.Node node = peptide.getGraphNode();
//        if (node.getAttribute(Peptide..getDisplayName()) == null) {
//            node.setAttribute(Peptide.ID.getDisplayName(), peptide.getId());
//        }
//    }
    public QuickFilter getQuickFilter() {
        return quickFilter;
    }

    public void setQuickFilter(QuickFilter quickFilter) {
        QuickFilter oldFilter = this.quickFilter;
        this.quickFilter = quickFilter;
        filteredPept = null;
        propertyChangeSupport.firePropertyChange(CHANGED_FILTER, oldFilter, quickFilter);
    }

    public void addQuickFilterChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_FILTER, listener);
    }

    public void removeQuickFilterChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_FILTER, listener);
    }

    public void addDisplayColumnChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(DISPLAY_ATTR_ADDED, listener);
        propertyChangeSupport.addPropertyChangeListener(DISPLAY_ATTR_REMOVED, listener);
    }

    public void removeDisplayColumnChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(DISPLAY_ATTR_ADDED, listener);
        propertyChangeSupport.removePropertyChangeListener(DISPLAY_ATTR_REMOVED, listener);
    }

    public void addMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(MD_ATTR_ADDED, listener);
        propertyChangeSupport.addPropertyChangeListener(MD_ATTR_REMOVED, listener);
    }

    public void removeMolecularDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(MD_ATTR_ADDED, listener);
        propertyChangeSupport.removePropertyChangeListener(MD_ATTR_REMOVED, listener);
    }

    private class PeptideNodeContainer extends Index.ArrayChildren {

        @Override
        protected List<Node> initCollection() {
            List<Node> nodes = new ArrayList<>(nodeList.size());
            for (PeptideNode node : nodeList) {
                nodes.add(node);
            }
            return nodes;
        }

        public void refreshNodes() {
            refresh();
        }

    }

    private class AttributeModelBridgeImpl implements AttributeModelBridge {

        @Override
        public void copyTo(AttributesModel attrModel, List<String> peptideIDs) {
            if (workspace.equals(attrModel.workspace)) {
                interCopy(attrModel, peptideIDs);
            } else {
                intraCopy(attrModel, peptideIDs);
            }
        }

        private void interCopy(AttributesModel attrModel, List<String> peptideIDs) {
            attrModel.mdMap.putAll(mdMap);

            for (PeptideAttribute attr : displayedColumnsModel) {
                if (!attr.isVolatil()) {
                    attrModel.displayedColumnsModel.add(attr);
                }
            }

            if (peptideIDs != null) {
                for (String id : peptideIDs) {
                    if (!peptideMap.containsKey(id)) {
                        throw new IllegalArgumentException("Invalid peptide id: " + id);
                    }
                    attrModel.addPeptide(peptideMap.get(id));
                }
                attrModel.filteredPept = null;
            }
        }

        private void intraCopy(AttributesModel attrModel, List<String> peptideIDs) {
            try {
                copyMdMapTo(attrModel);
                copyDisplayedColumnsTo(attrModel);
                copyDataTo(attrModel, peptideIDs);
            } catch (CloneNotSupportedException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        private void copyMdMapTo(AttributesModel attrModel) throws CloneNotSupportedException {
            String key;
            List<MolecularDescriptor> currentValue;
            List<MolecularDescriptor> newValue;
            for (Map.Entry<String, List<MolecularDescriptor>> entry : mdMap.entrySet()) {
                key = entry.getKey();
                currentValue = entry.getValue();
                newValue = new LinkedList<>();
                for (MolecularDescriptor md : currentValue) {
                    newValue.add((MolecularDescriptor) md.clone());
                }
                attrModel.mdMap.put(key, newValue);
            }
        }

        private void copyDisplayedColumnsTo(AttributesModel attrModel) throws CloneNotSupportedException {
            for (PeptideAttribute attr : displayedColumnsModel) {
                attrModel.displayedColumnsModel.add((PeptideAttribute) attr.clone());
            }
        }

        private void copyGraphTo(GraphModel targetGraphModel, Graph targetVisibleGraph) {
            GraphVizSetting graphViz = pc.getGraphVizSetting(workspace);
            GraphModel currentGraphModel = pc.getGraphModel(workspace);
            Graph visibleCurrentGraph = pc.getGraphVisible(workspace);
            currentGraphModel.getGraph().readLock();
            try {
                org.gephi.graph.api.Node[] nodes = currentGraphModel.getGraph().getNodes().toArray();
                targetGraphModel.bridge().copyNodes(nodes);
                Graph targetGraph = targetGraphModel.getGraph();

                //Nodes
                List<org.gephi.graph.api.Node> nodesToAdd = new LinkedList<>();
                for (org.gephi.graph.api.Node node : targetGraph.getNodes()) {
                    if (visibleCurrentGraph.hasNode(node.getId())) {
                        nodesToAdd.add(node);
                    }
                }

                //Edges
                List<Edge> edgesToAdd = new LinkedList<>();
                int relType = currentGraphModel.getEdgeType(ProjectManager.GRAPH_EDGE_SIMALIRITY);
                for (Edge edge : targetGraph.getEdges()) {
                    if (visibleCurrentGraph.hasEdge(edge.getId())
                            && targetGraph.hasNode(edge.getSource().getId())
                            && targetGraph.hasNode(edge.getTarget().getId())) {
                        if (relType != -1 && edge.getType() == relType) {
                            if (edge.getWeight() >= graphViz.getSimilarityThreshold()) {
                                edgesToAdd.add(edge);
                            }
                        } else {
                            edgesToAdd.add(edge);
                        }
                    }
                }
                if (!nodesToAdd.isEmpty()) {
                    targetVisibleGraph.addAllNodes(nodesToAdd);
                }
                if (!edgesToAdd.isEmpty()) {
                    targetVisibleGraph.addAllEdges(edgesToAdd);
                }
//                
//                List<Edge> edgesToRemove = new LinkedList<>();
//                for (Edge edge : targetGraph.getEdges()) {
//                    if (!visibleCurrentGraph.hasEdge(edge.getId())) {
//                        edgesToRemove.add(edge);
//                    }
//                }
//
//                if (!edgesToRemove.isEmpty()) {
//                    targetGraph.removeAllEdges(edgesToRemove);
//                }
            } finally {
                currentGraphModel.getGraph().readUnlockAll();
            }
        }

        private void copyDataTo(AttributesModel attrModel, List<String> peptideIDs) throws CloneNotSupportedException {
            Workspace targetWorkspace = attrModel.getOwnerWS();
            GraphModel targetGraphModel = pc.getGraphModel(targetWorkspace);
            Graph targetVisibleGraph = pc.getGraphVisible(targetWorkspace);
            
            Peptide currentPeptide, targetPeptide;
            org.gephi.graph.api.Node currentNode, targetNode;

            if (peptideIDs != null) {
                //Copy graph  
                copyGraphTo(targetGraphModel, targetVisibleGraph);

                //Copy peptides
                Map<PeptideAttribute, Object> currAttrsValue, targetAttrsValue;
                for (String id : peptideIDs) {
                    if (!peptideMap.containsKey(id)) {
                        throw new IllegalArgumentException("Invalid peptide id: " + id);
                    }

                    if (!attrModel.peptideMap.containsKey(id)) {
                        currentPeptide = peptideMap.get(id);
                        currentNode = currentPeptide.getGraphNode();
                        currAttrsValue = currentPeptide.attrsValue;

                        targetNode = targetGraphModel.getGraph().getNode(currentNode.getId());
                        targetPeptide = new Peptide(targetNode, targetGraphModel.getGraph());
                        targetAttrsValue = targetPeptide.attrsValue;

                        for (Map.Entry<PeptideAttribute, Object> entry : currAttrsValue.entrySet()) {
                            PeptideAttribute key = entry.getKey();
                            Object value = entry.getValue();
                            targetAttrsValue.put((PeptideAttribute) key.clone(), value);
                        }

                        attrModel.addPeptide(targetPeptide);
                    } else {
                        throw new IllegalArgumentException("Duplicated peptide id: " + id);
                    }
                    attrModel.filteredPept = null;
                }
            }
        }
    }

}
